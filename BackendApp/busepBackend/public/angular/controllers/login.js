(function() {
	var app = angular.module('mainApp');
	
	app.controller('LoginCtrl', function($scope, $window, $http, $rootScope){
		$scope.formData = {username:'', funnyString:''};
		
//		alert(sha256("đčćžšљњшこつきめ"));
		$scope.loginFormSubmit = function() {
			$http.get('/login/' + $scope.formData.username + '/' + sha256($scope.formData.funnyString))
				.then(
						function(response) {
							$window.location.href = '#/';
						},
						function(reason) {
							alert(reason.data);
						}
				);
	   };
	});
}());