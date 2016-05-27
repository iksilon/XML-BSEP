(function() {
	var app = angular.module('mainApp', ['ngMaterial', 'ngMessages', 'ngRoute', 'ngCookies']);
	
	app.controller('IndexCtrl', function($scope, $rootScope, $http) {
		$rootScope.user = {empty:true};
		$http.get('/test/loggedUserTest')
			.then(function(response) {
				$rootScope.user = response.data;
			}, function(reason) {
				console.log(reason.data);
			});
	});
}());