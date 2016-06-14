(function() {
	var app = angular.module('mainApp');
	
	app.controller('LatestActsCtrl', function($scope, $window, $http, $rootScope, $mdToast){
		$rootScope.mainPage = false;

		$scope.recentActs = [
		                     {title:'template act 1'},
		                     {title:'template act 2'},
		                     {title:'There\'s nothing like a little bit of "Ryuu ga waga teki wo kurau" to help you focus!'}
                     ];
		$http.get('/acts/10')
		.then(
				function(response) {
					$scope.recentActs = response.data;
				},
				function(reason) {
					$mdToast.show({
						template: '<md-toast>Greška pri preuzimanju dokumenata</md-toast>',
						hideDelay: 3000,
						position: 'top right',
						parent: '#toastParentActs'
					});
				}
		);
   });
}());