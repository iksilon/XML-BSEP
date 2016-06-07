(function() {
	var app = angular.module('mainApp');
	
	app.controller('LoginCtrl', function($scope, $window, $http, $rootScope){
		$rootScope.mainPage = false;
		$scope.formData = {username:'', funnyString:''};
		
		$scope.loginFormSubmit = function() {
			$http.get('/login/' + $scope.formData.username + '/' + sha256($scope.formData.funnyString))
				.then(
						function(response) {
							$rootScope.user = response.data;
							$window.location.href = '#/';
						},
						function(reason) {
							console.log(reason.data);
						}
				);
//			var time = new Date().getTime(); // UTC
//			var shaTime = sha256(time.toString());
//			$http.post('/login/' + $scope.formData.username + '/' + sha256($scope.formData.funnyString), {timestamp:time, timestampHash:shaTime})
//				.then(
//						function(response) {
//							$rootScope.user = response.data;
//							$window.location.href = '#/';
//						},
//						function(reason) {
//							console.log(reason.data);
//						}
//				);
	   };
	});
}());