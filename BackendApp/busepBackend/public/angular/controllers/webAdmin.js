(function() {
	var app = angular.module('mainApp');
	
	app.controller('WebAdminCtrl', function($scope, $rootScope, $http, $window) {
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
							//dodat korisnik
						},
						function(reason) {
							console.log(reason.data);
						});
		};
		$scope.deleteUser = function() {
			
		}
	});
}());
