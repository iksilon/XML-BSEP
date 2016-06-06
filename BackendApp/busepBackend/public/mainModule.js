(function() {
	var app = angular.module('mainApp');

	app.factory('timestampInterceptor', function($rootScope, $http) {  
	    var timestampInterceptor = {
	    		response: function(response) {
	    			// server validirao da je sve ok
	    			if(response.data.ok != undefined && response.data.ok != null) {
	    				return response;
	    			}
	    			
	    			// first time receiving server response for any action
	    			if($rootScope.lastMsgNum >= response.data.msgNum || $rootScope.lastTimestamp >= response.data.timestamp) {
	    				response.data = null;
	    				response.config.invalid = true;
	    				return response;
	    			}
	    			
	    			$rootScope.lastMsgNum = response.data.msgNum;
	    			$rootScope.lastTimestamp = response.data.timestamp;
//	    			$http.post('/respawnschck', {msgNum:$rootScope.lastMsgNum, timestamp:new Date().getTime()})
//	    				.then(
//	    						function(resp) {
//	    							return response;
//	    						},
//	    						function(reason) {
//	    							return response;
//	    						});
	    			
	    			return response;
	    		}
	    };

	    return timestampInterceptor;
	})
	.config(function($routeProvider, $httpProvider){ //, $locationProvider
		//$httpProvider.interceptors.push('timestampInterceptor');
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
		.when("/cp/wa", {
			templateUrl: "angular/routes/panelWebAdmin.html",
			controller: "WebAdminCtrl"
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
	})
	.directive("compareTo", function() {
	    return {
	    	require: "ngModel",
	        scope: {
	            otherModelValue: "=compareTo"
	        },
	        link: function(scope, element, attributes, ngModel) {
	             
	            ngModel.$validators.compareTo = function(modelValue) {
	                return modelValue == scope.otherModelValue;
	            };
	 
	            scope.$watch("otherModelValue", function() {
	                ngModel.$validate();
	            });
	        }
	    };
	})
	.run(function($rootScope, $cookies, $http, $window) {
		$rootScope.lastMsgNum = 0;
		$rootScope.lastTimestamp = null;
		
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
		
		$rootScope.user = null;
		
		$rootScope.logout = function() {
			$http.get('/logout/' + $rootScope.user.username)
				.then(
						function(response) {
							$rootScope.user = null;
							$window.location.href = '#/login';
						},
						function(reason) {
							console.error(reason.data);
						});
		}
	});;
}());