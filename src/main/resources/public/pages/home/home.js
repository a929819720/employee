bdtApp.controller('homeController', function($scope, $stateParams, UserService, HttpService, ModalService) {
	$scope.showLogin = false;
	$scope.userInfo = {};
	$scope.tag = $stateParams.code;
	if ($scope.tag == null || $scope.tag == '')
		$scope.tag = 'emp';
	UserService.getUser().then(function(res) {
		$scope.userInfo = res;
	});
	$scope.isAdmin = false;
	$scope.roleAll = [];
	$scope.emps = [];
	$scope.currentPage = 1; // 当前页
	$scope.delEmp = function(emp) {
		ModalService.yesno({
			title : '提示',
			content : '确定删除员工:' + emp.name + '吗？'
		}, function() {
			HttpService.post("/employee/deleteEmployee", emp).then(function(res) {
				$scope.emps = [];
				$scope.getEmployee();
			})
		}, function() {

		})
	}
	$scope.getEmployee = function() {
		HttpService.post("/employee/findEmployee", {
			pageNum : $scope.currentPage
		}).then(function(res) {
			if (res.code == 0) {
				for (var i = 0; i < res.data.list.length; i++) {
					var data = res.data.list[i];
					$scope.emps.push(data);
				}
				$scope.isLastPage = res.data.isLastPage;
			} else {
				ModalService.toastr.error("网络错误");
			}
		});
	}
	// 获取所有角色，和用户角色
	$scope.getRoles = function() {
		HttpService.post("/role/findRole", {
			pageNum : $scope.currentPage
		}).then(function(res) {
			if (res.code == 0) {
				var roles = res.data.roles;
				var myRoles = res.data.myRoles;
				$scope.isLastPage = roles.isLastPage;
				var myRoleStatus = {};
				for (var i = 0; i < myRoles.length; i++) {
					if (myRoles[i].code == 'admin')
						$scope.isAdmin = true;
					myRoleStatus[myRoles[i].code] = myRoles[i].myStatus;
				}
				for (var i = 0; i < roles.list.length; i++) {
					var data = roles.list[i];
					data.myStatus = myRoleStatus[data.code];
					if (data.myStatus == null || data.status == 0) {
						data.btnTitle = "申请";
					} else if (data.myStatus == 1) {
						data.btnTitle = "已有";
					} else if (data.myStatus == 2) {
						data.btnTitle = "申请中"
					} else if (data.myStatus == 3) {
						data.btnTitle = "重新申请";
					}
					$scope.roleAll.push(data);
				}
			} else {
				ModalService.toastr.error("网络错误");
			}
		});
	}
	// 加载更多
	$scope.loadMoreRole = function() {
		$scope.currentPage += 1;
		if ($scope.tag == 'role') {
			$scope.getRoles();
		} else if ($scope.tag == 'emp') {
			$scope.getEmployee();
		}
	}
	// 申请权限
	$scope.reqRole = function(role) {
		var list = $scope.roleAll;
		HttpService.post("/employee/getEmployeeByRole", {
			roleCode : 'admin'
		}).then(function(res) {
			if (res.code == 0) {
				ModalService.selectList({
					title : '请选择申请人',
					listTitle : [ {
						title : '姓名',
						code : 'name'
					}, {
						title : '手机号',
						code : 'mobile'
					}, ],
					list : res.data.list
				}, function success(res) {
					if (res.id != null) {
						HttpService.post("/role/reqRole", {
							roleId : role.id,
							empId : res.id
						}).then(function(res) {
							ModalService.toastr.success("申请成功");
							$scope.roleAll = [];
							$scope.currentPage = 1; // 当前页
							$scope.getRoles();
						}).then(function(res) {
							ModalService.toastr.error(res.msg);
						});
					}
				});
			} else {
				ModalService.toastr.error(res.msg);
			}
		})
	}
	if ($scope.tag == 'role') {
		$scope.getRoles();
	}
	if ($scope.tag == 'emp') {
		$scope.getEmployee();
	}
});