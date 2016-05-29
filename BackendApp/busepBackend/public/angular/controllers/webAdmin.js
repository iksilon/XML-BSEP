(function() {
	var app = angular.module('mainApp');
	
	app.controller('WebAdminCtrl', function($scope, $rootScope, $http) {
		$scope.newUser = {email:'', role:'', pass:'', passConfirm:''};
		$scope.userRoles = ['Predsednik', 'Odbornik', 'Web admin'];
		
		$scope.addUser = function() {
			$http.post('/user/create', {user:$scope.newUser})
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
