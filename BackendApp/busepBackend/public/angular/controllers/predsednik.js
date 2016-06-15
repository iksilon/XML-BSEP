(function() {
	var app = angular.module('mainApp');

	//TODO mozda bolje kao servis, koji koriste (trenutno nepostojeci) kontroleri za odbornika i predsednika
	//radi lakse provere da li je u pitanju korisnik koji ne treba da ima pristup ovom delu aplikacije
	app.controller('PredsednikCtrl', function($scope, $http, $window, $rootScope, $mdToast) {
		$rootScope.mainPage = false;
		
		var user = $rootScope.user; // cuvacemo u Java Web Token (JWT),
		// POST-om saljemo na server, server skonta koji je user,
		// i vrati username i sta god vec treba

		if (!user || user.role !== "Predsednik") {
			$window.location.href = "#/";
		}
		
		$scope.docList = [{title: "Dresiranje pseta"}, {title: "Hranjenje mačeta"}, {title: "Odlazak u budizam"}];
		$http.get('/acts/proc')
			.then(
					function(response) {
						$scope.docList = response.data;
					},
					function(reason) {
						$mdToast.show({
							template: '<md-toast>Greška pri preuzimanju dokumenata</md-toast>',
							hideDelay: 3000,
							position: 'top right',
							parent: '#toastParent'
						});
					}
			);

		$scope.docAccept = function(doc) {
			//TODO http zahtev, pa ovo u success funkciju
//			$http.post('')
			$mdToast.show({
				template: '<md-toast>Dokument \'' + doc.title + '\' prihvaćen</md-toast>',
				hideDelay: 3000,
				position: 'top right',
				parent: '#toastParent'
			});
		};

		$scope.docReject = function(doc) {
			//TODO http zahtev, pa ovo u success funkciju
			$scope.docList.splice($scope.docList.indexOf(doc), 1);
			$mdToast.show({
				template: '<md-toast>Dokument \'' + doc.title + '\' odbijen</md-toast>',
				hideDelay: 3000,
				position: 'top right',
				parent: '#toastParent'
			});
		};
	});
}());