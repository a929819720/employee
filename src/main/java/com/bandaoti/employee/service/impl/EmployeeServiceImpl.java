package com.bandaoti.employee.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.bandaoti.employee.ControllerException;
import com.bandaoti.employee.ReturnCode;
import com.bandaoti.employee.dao.EmployeeMapper;
import com.bandaoti.employee.entity.Employee;
import com.bandaoti.employee.entity.EmployeeExample;
import com.bandaoti.employee.service.EmployeeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
@Service
public class EmployeeServiceImpl implements EmployeeService {
	@Autowired private EmployeeMapper empMapper;
	public static final List<Integer> empStatus=Arrays.asList(1,2);
	
	@Override
	public Employee addEmployee(Employee emp) throws ControllerException {
		if((!StringUtils.isEmpty(emp.getCode())&&getEmployeeByCode(emp.getCode())!=null)
				||(!StringUtils.isEmpty(emp.getEmail())&&getEmployeeByEmail(emp.getEmail())!=null)
				||(!StringUtils.isEmpty(emp.getMobile())&&getEmployeeByMobile(emp.getMobile())!=null)
				||(!StringUtils.isEmpty(emp.getIdcard())&&getEmployeeByIdcard(emp.getIdcard())!=null)
				){
			throw new ControllerException(ReturnCode.ACCOUNT_EXIST_ERROR);
		}
		emp.setStatus(1);
		empMapper.insertSelective(emp);
		return emp;
	}

	@Override
	public Boolean delEmployeeByMobile(String mobile) {
		EmployeeExample employeeExample=new EmployeeExample();
		employeeExample.createCriteria().andMobileEqualTo(mobile);
		if(empMapper.deleteByExample(employeeExample)==1){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public Boolean delEmployeeByIdcard(String idCard) {
		EmployeeExample employeeExample=new EmployeeExample();
		employeeExample.createCriteria().andIdcardEqualTo(idCard);
		if(empMapper.deleteByExample(employeeExample)==1){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public Boolean delEmployeeByEmail(String email) {
		EmployeeExample employeeExample=new EmployeeExample();
		employeeExample.createCriteria().andEmailEqualTo(email);
		if(empMapper.deleteByExample(employeeExample)==1){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public Boolean updateEmployee(Employee emp) {
		EmployeeExample example=new EmployeeExample();
		EmployeeExample.Criteria criteria=example.createCriteria();
		if(emp.getId()!=null){
			empMapper.updateByPrimaryKeySelective(emp);
			return true;
		}
		if(!StringUtil.isEmpty(emp.getMobile())){
			criteria.andMobileEqualTo(emp.getMobile());
		}else if(!StringUtil.isEmpty(emp.getEmail())){
			criteria.andEmailEqualTo(emp.getEmail());
		}else if(!StringUtil.isEmpty(emp.getIdcard())){
			criteria.andIdcardEqualTo(emp.getIdcard());
		}else {
			return false;
		}
		empMapper.updateByExampleSelective(emp, example);
		return true;
	}

	@Override
	public Employee getEmployeeByMobile(String mobile) {
		EmployeeExample employeeExample=new EmployeeExample();
		employeeExample.createCriteria().andStatusIn(empStatus).andMobileEqualTo(mobile);
		List<Employee> list=empMapper.selectByExample(employeeExample);
		if(list.size()==1)return list.get(0);
		return null;
	}

	@Override
	public Employee getEmployeeByIdcard(String idCard) {
		EmployeeExample employeeExample=new EmployeeExample();
		employeeExample.createCriteria().andStatusIn(empStatus).andIdcardEqualTo(idCard);
		List<Employee> list=empMapper.selectByExample(employeeExample);
		if(list.size()==1)return list.get(0);
		return null;
	}

	@Override
	public Employee getEmployeeByEmail(String email) {
		EmployeeExample employeeExample=new EmployeeExample();
		employeeExample.createCriteria().andStatusIn(empStatus).andEmailEqualTo(email);
		List<Employee> list=empMapper.selectByExample(employeeExample);
		if(list.size()==1)return list.get(0);
		return null;
	}

	@Override
	public Employee getEmployeeByCode(String code) {
		EmployeeExample employeeExample=new EmployeeExample();
		employeeExample.createCriteria().andStatusIn(empStatus).andCodeEqualTo(code);
		List<Employee> list=empMapper.selectByExample(employeeExample);
		if(list.size()==1)return list.get(0);
		return null;
	}

	@Override
	public Employee getEmployee(Integer id) {
		return empMapper.selectByPrimaryKey(id);
	}

	@Override
	public PageInfo<Employee> getEmployee(Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum,pageSize);
		EmployeeExample example=new EmployeeExample();
		example.createCriteria().andStatusIn(empStatus);
		List<Employee> emps=empMapper.selectByExample(example);
		System.out.println(emps.size());
		return new PageInfo<Employee>(emps);
	}

	@Override
	public Boolean delEmployeeByEmpId(Integer id) {
		empMapper.deleteByPrimaryKey(id);
		return true;
	}

}
