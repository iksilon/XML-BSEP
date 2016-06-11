(function() {
	var app = angular.module('mainApp');
	
	app.controller('LoginCtrl', function($scope, $window, $http, $rootScope, $cookies, $mdToast){
		$rootScope.mainPage = false;
		$scope.formData = {username:'', funnyString:''};
		
		$scope.loginFormSubmit = function() {
			$http.post('/login', [$scope.formData.username, sha256($scope.formData.funnyString)])
				.then(
						function(response) {
							$rootScope.user = response.data;
							
							var now = new Date(),
						    exp = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 2);
							$cookies.putObject("user", $rootScope.user, {"expires":exp, "path":"/"}); //za sad se cuva u cookie
							$mdToast.hide();
							$window.location.href = '#/';
						},
						function(reason) {
							$mdToast.show({
								template: '<md-toast>Prijava neuspešna</md-toast>',
								hideDelay: 3000,
								position: 'top',
								parent: '#toastParent'
							});
						}
				);
			$mdToast.show({
				template: '<md-toast>Prijava u toku</md-toast>',
				hideDelay: 0,
				position: 'top',
				parent: '#toastParent'
			});
	   };
	});
}());