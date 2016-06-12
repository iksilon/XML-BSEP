(function() {
	var app = angular.module('mainApp');
	
	app.controller('OdborniciCtrl', function($scope, $http, $mdToast) {
		$scope.odbList = [];
		$scope.predList = [];
		$http.get('/users/odbornici')
			.then(
				function(response) {
					$scope.odbList = response.data;
				},
				function(reason) {
					$mdToast.show({
						template: '<md-toast>Greška pri preuzimanju liste odbornika</md-toast>',
						hideDelay: 3000,
						position: 'top',
						parent: '#toastParent2'
					});
				}
			);
		$http.get('/users/highlord')
			.then(
				function(response) {
					$scope.predList = response.data;
				},
				function(reason) {
					$mdToast.show({
						template: '<md-toast>Greška pri preuzimanju liste predsednika</md-toast>',
						hideDelay: 3000,
						position: 'top',
						parent: '#toastParent1'
					});
				}
			);
	});
}());