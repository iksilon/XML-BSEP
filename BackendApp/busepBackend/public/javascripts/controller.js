var app = angular.module('mainApp', ['ngMaterial', 'ngMessages', 'ngRoute', 'ngCookies']);

app.config(function($routeProvider){
    $routeProvider
    .when('/', {
        templateUrl: 'Application/index.html'
    })
    .when('/login', {
        templateUrl:'Login/show.html'
    })
    .when('/home', {
        resolve: {
            "check": function($window, $rootScope){
                if(!$rootScope.loggedIn) {
                	$window.location.href = '/';
                }
            }
        },
        templateUrl:'Application/index.html'
    })
    .otherwise({
        redirectTo: 'Application/index.html'
    });
});

app.controller('indexCtrl', function($scope, $rootScope, $http) {
	$scope.test = "TEST";
	
	$scope.alertMe = function() {
		$http.get('/test').then(
				function(response){
					alert(response.data);
				},
				function(reason){
					alert("failed");
				}
		);
	}
});

app.controller('loginCtrl', function($scope, $window, $rootScope){
	$scope.username = "";
	$scope.password = "";
	
	$scope.submit = function() {
		if($scope.username == 'admin' && $scope.password == 'admin') {
			$rootScope.loggedIn = true;
			$window.location.href = 'home';
		} else {
			alert('Pogrešno uneseno korisničko ime ili lozinka! Pokušajte ponovo.');
		}
   };
});