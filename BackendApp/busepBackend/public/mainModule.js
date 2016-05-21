(function() {
	var app = angular.module('mainApp', ['ngMaterial', 'ngMessages', 'ngRoute', 'ngCookies']);

	app.config(function($routeProvider){ //, $locationProvider
		$routeProvider
		.when("/", {
			templateUrl: "main.html",
			controller: "IndexCtrl"			
		})
		.when("/main", {
			templateUrl: "main.html",
			controller: "IndexCtrl"
		})
		.when("/index", {
			templateUrl: "main.html",
			controller: "IndexCtrl"
		})
		.when("/odbornici", {
			templateUrl: "angular/includes/odbornici.html",
			controller: "OdborniciCtrl"
		})
		.when("/akti/vazeci", {
			templateUrl: "angular/includes/aktiVazeci.html",
			controller: "AktiCtrl"
		})
		.when("/akti/proc", {
			templateUrl: "angular/includes/aktiProc.html",
			controller: "AktiCtrl"
		})
		.when("/login", {
			templateUrl: "angular/includes/login.html",
			controller: "LoginCtrl"
		})
		.when("/search", {
			templateUrl: "angular/includes/search.html",
			controller: "SearchCtrl"
		})
		.otherwise({
			redirectTo:"/"
		});
	})
	.directive('goClick', function($location) {
		return function(scope, element, attrs) {
			var path;

			attrs.$observe('goClick', function(val) {
				path = val;
			});

			element.bind('click', function() {
				scope.$apply(function() {
					$location.path(path);
				});
			});
		};
	});;
}());