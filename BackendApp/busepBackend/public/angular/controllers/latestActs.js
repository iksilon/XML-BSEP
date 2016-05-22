(function() {
	var app = angular.module('mainApp');
	
	app.controller('LatestActsCtrl', function($scope, $window, $http, $rootScope){
		$scope.recentActs = [
		                     {'title':'template act 1'},
		                     {'title':'template act 2'},
		                     {'title':'There\'s nothing like a little bit of "Ryuu ga waga teki wo kurau" to help you focus!'},
                     ];
   });
}());