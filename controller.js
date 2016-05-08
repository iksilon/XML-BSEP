var app = angular.module('testApp',['ngRoute']);

app.config(function($routeProvider){
    $routeProvider
    .when('/', {
        templateUrl: 'login.html'
    })
    .when('/home', {
        resolve: {
            "check": function($location, $rootScope){
                if(!$rootScope.loggedIn) {
                    $location.path('/')
                }
            }
        },
        templateUrl:'home.html'
    })
    .otherwise({
        redirectTo: '/'
    });
});

app.controller('loginCtrl', function($scope, $location, $rootScope){
   $scope.submit = function() {
        if($scope.username == 'admin' && $scope.password == 'admin') {
            $rootScope.loggedIn = true;
            $location.path('/home');
        } else {
            alert('Pogresno uneseni parametri! Pokusajte ponovo.');
       }
   };
});