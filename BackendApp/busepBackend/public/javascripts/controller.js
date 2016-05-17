var app = angular.module('mainApp', ['ngMaterial', 'ngMessages', 'ngRoute', 'ngCookies']);

app.controller('indexCtrl', function($scope, $rootScope, $http) {
	$scope.test = "TEST";
	
	$scope.alertMe = function() {
		$http.get('/test/IT/LIVES').then(
				function(response){
					alert(response.data);
				},
				function(reason){
					alert("failed");
				}
		);
	}
});

app.controller('loginCtrl', function($scope, $window, $http, $rootScope){
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