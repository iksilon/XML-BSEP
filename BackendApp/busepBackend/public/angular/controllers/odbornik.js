(function() {
	var app = angular.module('mainApp');

	app.controller('OdbornikCtrl', function($scope, $window, $rootScope, $mdToast, amandmanSvc) {
		$rootScope.mainPage = false;
		
		var user = $rootScope.user;
		if (!user || user.role !== "Odbornik" && user.role !== "Predsednik") {
			$window.location.href = "#/";
		}

		$scope.doc = amandmanSvc.selectedAct;
		$scope.path = amandmanSvc.editingPath;
		$scope.selectedTabIdx = 0;
		$scope.checkAvailability = function() {
			if((!$scope.doc || !$scope.path) && $scope.selectedTabIdx === 1) {
				$scope.selectedTabIdx = 0;
				$mdToast.show({
					template: '<md-toast>Morate prvo odabrati akt iz menija \"AKTI U PROCEDURI\"</md-toast>',
					hideDelay: 3000,
					position: 'top left',
					parent: '#toastTab'
				});
			}
		};
		
		if($scope.doc && $scope.path) {
			$scope.selectedTabIdx = 1;
		}
		
		
		
	});
}());