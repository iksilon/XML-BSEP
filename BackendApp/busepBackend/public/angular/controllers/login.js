(function() {
	var app = angular.module('mainApp');
	
	app.controller('LoginCtrl', function($scope, $window, $http, $rootScope, $mdToast){
		$rootScope.mainPage = false;
		$scope.formData = {username:'', funnyString:''};
		
		$scope.loginFormSubmit = function() {			
			$mdToast.show({
				template: '<md-toast>Prijava u toku</md-toast>',
				hideDelay: 0,
				position: 'top',
				parent: '#toastParent'
			});
			
			$http.post('/login', [$scope.formData.username, sha256($scope.formData.funnyString)])
				.then(
						function(response) {
							$rootScope.user = {};
							$rootScope.user.username = response.data.username;
							$rootScope.user.role = response.data.role;
							$rootScope.user.msgNum = response.data.msgNum;
							$window.sessionStorage.username = $rootScope.user.username;
							
							$mdToast.hide();
							$window.location.href = '#/';
						},
						function(reason) {
							$mdToast.hide();
							
							$mdToast.show({
								template: '<md-toast>Prijava neuspešna</md-toast>',
								hideDelay: 3000,
								position: 'top',
								parent: '#toastParent'
							});

							delete $window.sessionStorage.token;
							delete $window.sessionStorage.username;
						}
				);
	   };
	});
}());