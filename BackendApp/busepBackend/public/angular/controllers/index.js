(function() {
	var app = angular.module('mainApp', ['ngMaterial', 'ngMessages', 'ngRoute', 'ngCookies']);
	
	app.controller('IndexCtrl', function($scope, $rootScope, $http) {
		$rootScope.mainPage = true;
	});
}());