(function() {
	var app = angular.module('mainApp');
	
	app.controller('LoginCtrl', function($scope, $window, $http, $rootScope){
		$scope.username = "";
		$scope.funnyString = "";
		
		$scope.submit = function() {
			$http.post('/login/' + $scope.username + '/' + $scope.funnyString, {'uname':$scope.username, 'pwd':$scope.funnyString})
				.then(
						function(response) {
							$window.location.href = '/home';
						},
						function(reason) {
							alert('bad login');
						}
				);
	   };
	});
}());