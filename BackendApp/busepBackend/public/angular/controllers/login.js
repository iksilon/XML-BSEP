(function() {
	var app = angular.module('mainApp');
	
	app.controller('LoginCtrl', function($scope, $window, $http, $rootScope){
		$scope.formData = {username:'', funnyString:''};
		
		$scope.loginFormSubmit = function() {
			$http.get('/login/' + $scope.formData.username + '/' + $scope.formData.funnyString)
				.then(
						function(response) {
							$window.location.href = '#/';
						},
						function(reason) {
							alert(reason.data);
						}
				);
	   };
	   
	   $rootScope.logout = function() {
		   $http.get('/logout/' + $rootScope.user.username)
		   	.then(
		   			function(response) {
		   				$rootScope.user = {empty:true};
		   			},
		   			function(reason) {
		   				console.error(reason.data);
		   			});
	   }
	});
}());