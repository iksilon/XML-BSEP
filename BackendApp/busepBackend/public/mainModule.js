(function() {
	var app = angular.module('mainApp', ['ngMaterial', 'ngMessages', 'ngRoute', 'ngCookies']);

	app.factory('timestampInterceptor', function($rootScope) {  
	    var timestampInterceptor = {
	    		request: function(request) {
	    			var time = new Date().getTime();
    				request.headers.timestamp = time; // UTC
    				request.headers.timestampHash = sha256(time.toString());
    				if($rootScope.user != undefined && $rootScope.user != null) {
    					request.headers.username = $rootScope.user.username;
    					request.headers.msgNum = ++$rootScope.user.msgNum;
    				}
    				
	    			return request;
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
	.run(function($rootScope, $cookies, $http, $window) {		
		$rootScope.userMenuChecks = {
				isPredsednik: function(userRole) {
					if(userRole != 1) {
						return false;
					}
					return true;
				},
				isOdbornik: function(userRole) {
					if(userRole != 2) {
						return false;
					}
					
					return true;
				},
				isAdmin: function(userRole) {
					if(userRole != 3) {
						return false;
					}
					return true;
				}};
		
		$rootScope.demoFs = {
				submitXML: function() {
					$http.post('/xml/submit')
						.then(
								function(response) {
									console.log('xml submitted');
								},
								function(reason) {
									console.error('xml not submitted');
								}
						);
				},
				encrXML: function() {
					$http.get('/encry')
							.then(
									function(response) {
										console.log('xml transferred');
									},
									function(reason) {
										console.error('xml not encrypted');
									}
							);
				},
				decrXML: function() {
					$http.get('/decry')
							.then(
									function(response) {
										console.log('xml decrypted');
									},
									function(reason) {
										console.error('xml not decrypted');
									}
							);
				}
		};
		
		var user = $cookies.getObject('user'); //za sad se cuva u cookie
		if(user != undefined || user != null) {
			$http.post('/loginCheck', user)
				.then(
						function(response) {
							$rootScope.user = response.data;
							var now = new Date(),
						    exp = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 2);
							$cookies.putObject("user", $rootScope.user, {"expires":exp, "path":"/"}); //za sad se cuva u cookie
//							$http.get('/msgnum')
//								.then(
//										function(response) {
//											$rootScope.user.msgNum = response.data;
//										},
//										function(reason) {
//											$window.location.href = "#/";
//										}
//								);
						},
						function(reason) {
							$rootScope.user = undefined;
							$cookies.remove("user");
						}
				);
		};
		
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
							$cookies.remove("user");
							$window.location.href = '#/login';
						},
						function(reason) {
							console.error(reason.data);
						});
		};
	});
}());