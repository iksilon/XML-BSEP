(function() {
	var app = angular.module('mainApp');
	
	app.controller('WebAdminCtrl', function($scope, $rootScope, $http) {
		$rootScope.mainPage = false;
		
		$scope.newUser = {email:'', role:'', pass:'', passConfirm:''};
		$scope.userRoles = ['Predsednik', 'Odbornik', 'Web admin'];
		
		$scope.addUser = function() {			
			$http.get('/user/create/' + $scope.newUser.email + "/" +$scope.newUser.role + "/" + sha256($scope.newUser.pass))
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
