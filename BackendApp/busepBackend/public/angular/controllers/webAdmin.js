(function() {
	var app = angular.module('mainApp');
	
	app.controller('WebAdminCtrl', function($scope, $rootScope, $http, $window, $mdToast) {
		$rootScope.mainPage = false;
		
		if(!$rootScope.user || $rootScope.user.role !== 'Web Admin') {
			$window.location.href = '#/';
		}
		
		$scope.newUser = {name:'', lastName:'', username:'', role:'', password:'', passConfirm:''};
		$scope.userRoles = ['Predsednik', 'Odbornik', 'Web admin'];
		
		$scope.addUser = function() {
			var data = [$scope.newUser.name, $scope.newUser.lastName, 
			            $scope.newUser.username, $scope.newUser.role, sha256($scope.newUser.password)];
			
			$http.post('/user/create', data)
				.then(
						function(response) {
							$scope.newUser = {name:'', lastName:'', username:'', role:'', password:'', passConfirm:''};
							$mdToast.show({
								template: '<md-toast>Korisnik dodat</md-toast>',
								hideDelay: 3000,
								position: 'top right',
								parent: '#toastParent'
							});
						},
						function(reason) {
							$mdToast.show({
								template: '<md-toast>Greška pri dodavanju korisnika</md-toast>',
								hideDelay: 3000,
								position: 'top right',
								parent: '#toastParent'
							});
						});
		};
		
		$scope.deleteUser = function() {
			
		};
		
		$scope.cancelAdd = function() {
			$scope.newUser = {name:'', lastName:'', username:'', role:'', password:'', passConfirm:''};
		};
	});
}());
