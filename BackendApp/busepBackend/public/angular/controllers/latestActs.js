(function() {
	var app = angular.module('mainApp');
	
	app.controller('LatestActsCtrl', function($scope, $window, $http, $rootScope){
		$rootScope.mainPage = false;
		
//		$http.get('/documents/10')
//			.then(
//					function(response) {
//						$scope.recentActs = response.data;
//					},
//					function(reason) {
//						// sta god
//					}
//			);
		$scope.recentActs = [
		                     {title:'template act 1'},
		                     {title:'template act 2'},
		                     {title:'There\'s nothing like a little bit of "Ryuu ga waga teki wo kurau" to help you focus!'}
                     ];
   });
}());