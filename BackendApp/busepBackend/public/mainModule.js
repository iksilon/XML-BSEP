(function() {
	var app = angular.module('mainApp', ['ngMaterial', 'ngMessages', 'ngRoute']);

	app.factory('timestampInterceptor', function($rootScope, $window) {  
	    var timestampInterceptor = {
	    		request: function(request) {
	    			var time = new Date().getTime();
    				request.headers.timestamp = time; // UTC
    				request.headers.timestampHash = sha256(time.toString());
    				if($rootScope.user) {
    					request.headers.username = $rootScope.user.username;
    					request.headers.msgNum = ++$rootScope.user.msgNum;
    				}
    				if($window.sessionStorage.token) {
    					request.headers.Authorization = 'Bearer ' + $window.sessionStorage.token;
    				}
    				
	    			return request;
	    		},
	    		response: function(response) {
	    			if(response.data.token) {
	    				$window.sessionStorage.token = response.data.token;
	    			}
	    			
	    			return response;
	    		}
	    };

	    return timestampInterceptor;
	})
	.config(function($routeProvider, $httpProvider, $locationProvider){ //, $locationProvider
		$httpProvider.interceptors.push('timestampInterceptor');
		$routeProvider
		.when("/", {
			templateUrl: "angular/routes/odbornici.html",
			controller: "OdborniciCtrl"
//			templateUrl: "angular/routes/main.html",
//			controller: "IndexCtrl"			
		})
		.when("/main", {
			templateUrl: "angular/routes/odbornici.html",
			controller: "OdborniciCtrl"
//			templateUrl: "angular/routes/main.html",
//			controller: "IndexCtrl"
		})
		.when("/index", {
			templateUrl: "angular/routes/odbornici.html",
			controller: "OdborniciCtrl"
//			templateUrl: "angular/routes/main.html",
//			controller: "IndexCtrl"
		})
		.when("/home", {
			templateUrl: "angular/routes/odbornici.html",
			controller: "OdborniciCtrl"
//			templateUrl: "angular/routes/main.html",
//			controller: "IndexCtrl"
		})
		.when("/odbornici", {
			templateUrl: "angular/routes/odbornici.html",
			controller: "OdborniciCtrl"
		})
		.when("/akti/vazeci", {
			templateUrl: "angular/routes/aktiVazeci.html",
			controller: "AktiVazCtrl"
		})
		.when("/akti/proc", {
			templateUrl: "angular/routes/aktiProc.html",
			controller: "AktiProcCtrl"
		})
		.when("/login", {
			templateUrl: "angular/routes/login.html",
			controller: "LoginCtrl"
		})
		.when("/search", {
			templateUrl: "angular/routes/search.html",
			controller: "SearchCtrl"
		})
		.when("/odbornik", {
			templateUrl: "angular/routes/panelOdbornik.html",
			controller: "OdbornikCtrl"
		})
		.when("/highlord", {
			templateUrl: "angular/routes/panelHighlord.html",
			controller: "PredsednikCtrl"
		})
		.when("/wa", {
			templateUrl: "angular/routes/panelWebAdmin.html",
			controller: "WebAdminCtrl"
		})
		.otherwise({
			redirectTo:"/"
		});
		
//		$locationProvider.html5Mode(true);
	})
	.run(function($rootScope, $http, $window) {		
		$rootScope.userMenuChecks = {
				isPredsednik: function(userRole) {
					if(userRole !== 'Predsednik') {
						return false;
					}
					return true;
				},
				isOdbornik: function(userRole) {
					if(userRole !== 'Odbornik') {
						return false;
					}
					
					return true;
				},
				isAdmin: function(userRole) {
					if(userRole !== 'Web Admin') {
						return false;
					}
					return true;
				}};
		
		if($window.sessionStorage.token) {
			$http.post('/login/token', [$window.sessionStorage.token, $window.sessionStorage.username])
				.then(
						function(response) {
							$rootScope.user = {};
							$rootScope.user.username = response.data.username;
							$rootScope.user.role = response.data.role;
							$rootScope.user.msgNum = response.data.msgNum;
							$window.sessionStorage.username = $rootScope.user.username;
						},
						function(reason) {
							delete $window.sessionStorage.token;
							delete $window.sessionStorage.username;
						}
				);
		}
		
		$rootScope.sekund = {
				click: function() {
					$http.post('/xml/submit/archive')
						.then(
								function(response) {
									alert("ASD");
								},
								function(reason) {
									alert("e jebiga ne moze");
								}
						);
				}
		};
		
		$rootScope.logout = function() {
			$http.get('/logout/' + $rootScope.user.username)
				.then(
						function(response) {
							$rootScope.user = null;
							delete $window.sessionStorage.token;
							$window.location.href = '#/login';
						},
						function(reason) {
							//delete $window.sessionStorage.token;
						});
		};
	});
}());