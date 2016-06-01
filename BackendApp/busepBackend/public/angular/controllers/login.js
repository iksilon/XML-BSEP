(function() {
	var app = angular.module('mainApp');
	
	app.controller('LoginCtrl', function($scope, $window, $http, $rootScope){
		$rootScope.mainPage = false;
		$scope.formData = {username:'', funnyString:''};
		
//		alert(sha256("đčćžšљњшこつきめ"));
		$scope.loginFormSubmit = function() {
			$http.get('/login/' + $scope.formData.username + '/' + sha256($scope.formData.funnyString))
				.then(
						function(response) {
//							$http.get('/test/loggedUserTest')
//							.then(
//									function(response) {
										$rootScope.user = response.data;
										$window.location.href = '#/';
//									},
//									function(reason) {
//										console.log(reason.data);
//							});
						},
						function(reason) {
							console.log(reason.data);
						}
				);
	   };
	});
}());