(function() {
	var app = angular.module('mainApp');

	app.config(function($routeProvider){ //, $locationProvider
		$routeProvider
		.when("/", {
			templateUrl: "angular/routes/main.html",
			controller: "IndexCtrl"			
		})
		.when("/main", {
			templateUrl: "angular/routes/main.html",
			controller: "IndexCtrl"
		})
		.when("/index", {
			templateUrl: "angular/routes/main.html",
			controller: "IndexCtrl"
		})
		.when("/home", {
			templateUrl: "angular/routes/main.html",
			controller: "IndexCtrl"
		})
		.when("/odbornici", {
			templateUrl: "angular/routes/odbornici.html",
			controller: "OdborniciCtrl"
		})
		.when("/akti/vazeci", {
			templateUrl: "angular/routes/aktiVazeci.html",
			controller: "AktiCtrl"
		})
		.when("/akti/proc", {
			templateUrl: "angular/routes/aktiProc.html",
			controller: "AktiCtrl"
		})
		.when("/login", {
			templateUrl: "angular/routes/login.html",
			controller: "LoginCtrl"
		})
		.when("/search", {
			templateUrl: "angular/routes/search.html",
			controller: "SearchCtrl"
		})
		.when("/cp/odbornik", {
			templateUrl: "angular/routes/panelOdbornik.html",
			controller: "PanelCtrl"
		})
		.when("/cp/highlord", {
			templateUrl: "angular/routes/panelHighlord.html",
			controller: "PanelCtrl"
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