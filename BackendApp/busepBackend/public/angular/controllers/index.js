(function() {
	var app = angular.module('mainApp');
	
	app.controller('IndexCtrl', function($scope, $rootScope, $http) {
		$scope.test = "TEST";
		
		$scope.alertMe = function() {
			$http.get('/test/IT/LIVES').then(
					function(response){
						alert(response.data);
					},
					function(reason){
						alert("failed");
					}
			);
		}
	});
}());