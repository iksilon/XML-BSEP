(function() {
	var app = angular.module('mainApp');
	
	app.controller('AktiProcCtrl', function($scope, $http, $window, $rootScope, $mdToast, $sce, amandmanSvc){
		$rootScope.mainPage = false;
		
		$scope.user = $rootScope.user;
		
		$scope.showDocAmendments = false;
		$scope.acts = [];
		$scope.amendments = [];
		$http.get('/acts/proc')
			.then(
					function(response) {
						$scope.acts = response.data;
					},
					function(reason) {
						$mdToast.show({
							template: '<md-toast>Greška pri preuzimanju akata u proceduri</md-toast>',
							hideDelay: 3000,
							position: 'top right',
							parent: '#toastParent'
						});
					}
			);

		$scope.selectedDoc = {uri:''};
		$scope.docViewPath = undefined;//$sce.trustAsHtml('<md-content xmlns:pro=\"http:\/\/www.ftn.uns.ac.rs\/propisi\" layout-align=\"center\"> <md-content layout-align=\"center\" layout=\"row\"> <h1>Testiraje-transformacije<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\')\">Izmeni<\/md-button> <br> <\/span> <\/h1> <\/md-content> <h2> Deo 1<br>Supe<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\')\">Izmeni<\/md-button> <br> <\/span> <\/h2> <h3>1<br>Pilece<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\')\">Izmeni<\/md-button> <br> <\/span> <\/h3> <h4>1<br> <span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\')\">Izmeni<\/md-button> <br> <\/span> <\/h4> <h5>A<br>sa lukom<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\')\">Izmeni<\/md-button> <br> <\/span> <\/h5> <h6> \u00C4\u0152lan 1<br>Priprema<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\')\">Izmeni<\/md-button> <br> <\/span> <\/h6> <span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[1]\')\">Izmeni<\/md-button> <br> <\/span>Potrebni sastojci su:<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[2]\')\">Izmeni<\/md-button> <br> <\/span> <p> <span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[2]\/Tacka[1]\')\">Izmeni<\/md-button> <br> <\/span>serpa<\/p> <p> <span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[2]\/Tacka[2]\')\">Izmeni<\/md-button> <br> <\/span>supa<\/p> <p> <span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[2]\/Tacka[3]\')\">Izmeni<\/md-button> <br> <\/span> <p> <span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[2]\/Tacka[3]\/Podtacka[1]\')\">Izmeni<\/md-button> <br> <\/span>ponestalo<\/p> <p> <span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[2]\/Tacka[3]\/Podtacka[2]\')\">Izmeni<\/md-button> <br> <\/span>mi<\/p> <p> <span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[2]\/Tacka[3]\/Podtacka[3]\')\">Izmeni<\/md-button> <br> <\/span> <p>inspiracije<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[2]\/Tacka[3]\/Podtacka[3]\/Alineja[1]\')\">Izmeni<\/md-button> <br> <\/span> <\/p> <p>za<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[2]\/Tacka[3]\/Podtacka[3]\/Alineja[2]\')\">Izmeni<\/md-button> <br> <\/span> <\/p> <p>sastojke<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[2]\/Tacka[3]\/Podtacka[3]\/Alineja[3]\')\">Izmeni<\/md-button> <br> <\/span> <\/p> <\/p> <\/p> <h6> \u00C4\u0152lan 2<br>Jedenje<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[2]\')\">Izmeni<\/md-button> <br> <\/span> <\/h6> <h5>B<br>sa krompirom<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[2]\')\">Izmeni<\/md-button> <br> <\/span> <\/h5> <h4>2<br>Bistre<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[2]\')\">Izmeni<\/md-button> <br> <\/span> <\/h4> <h6> \u00C4\u0152lan <br> <span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[2]\/Clan[1]\')\">Izmeni<\/md-button> <br> <\/span> <\/h6> <h3>2<br>Govedje<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[2]\')\">Izmeni<\/md-button> <br> <\/span> <\/h3> <h2> Deo 2<br>Testa<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[2]\')\">Izmeni<\/md-button> <br> <\/span> <\/h2> <\/md-content> ');
		$scope.showDocView = false;
		$scope.docSelected = function(doc) {
			$http.post('/acts/get', [doc.uri, doc.uriHash])
				.then(
						function(response) {
							$scope.docViewPath = response.data.path;
							$scope.showDocView = true;
							amandmanSvc.selectedAct = doc;
							$scope.selectedDoc = doc;
						},
						function(reason) {
							$mdToast.show({
								template: '<md-toast>Greška pri preuzimanju odabranog akta</md-toast>',
								hideDelay: 3000,
								position: 'top left',
								parent: '#docView'
							});
							$scope.docViewPath = undefined;
							amandmanSvc.selectedAct = undefined;
							$scope.showDocView = false;
							$scope.showDocAmendments = false;
							$scope.amendments = [];
							$scope.selectedDoc = {uri:''};
						}
				);
		};
		
//		$http.post('/acts/amdmnt', [doc.uri, doc.uriHash])
		$http.get('/acts/amend')
			.then(
					function(rspns) {
						$scope.amendments = rspns.data;
						$scope.showDocAmendments = true;
					},
					function(reason) {
						$scope.amendments = [];
						$scope.showDocAmendments = false;
						
						
					}
			);
		
		$scope.elementEdit = function(xpath) {
			amandmanSvc.editingPath = xpath;
			$scope.showDocView = false;
			$window.location.href = '#/odbornik';
		};
		
		$scope.docCancelProcedure = function(doc, docType) {
			$http.post('/acts/proc/cancel', [doc.uri, doc.uriHash])
				.then(
						function(response) {
							if(docType === 'act') {
								var amdmToRemove = response.data; // lista amandmana [{amdm1attrs}, {amdm2attrs}...]
								$scope.amendments.forEach(function(amdm) {
									amdmsToRemove.forEach(function(rAmdm) {
										if(amdm.uri === rAmdm.uri) {
											amdm.remove = true;
										}
									});
								});
								
								$scope.amendments.filter(function(amdm) {
									if(amdm.remove) {
										return false; //ukloni element
									}
									
									return true;
								});
								
								$scope.acts.splice($scope.acts.indexOf(doc), 1);
								$mdToast.show({
									template: '<md-toast>Akt uklonjen iz procedure</md-toast>',
									hideDelay: 3000,
									position: 'top right',
									parent: '#toastParent'
								});
							}
							else if(docType === 'amdm') {
								$scope.amendments.splice($scope.amendments.indexOf(doc), 1);
								$mdToast.show({
									template: '<md-toast>Amandman uklonjen iz procedure</md-toast>',
									hideDelay: 3000,
									position: 'top right',
									parent: '#toastParentAmdm'
								});
							}
						},
						function(reason) {
							if(docType === 'act') {
								$mdToast.show({
									template: '<md-toast>Uklanjanje akt iz procedure neuspešno</md-toast>',
									hideDelay: 3000,
									position: 'top right',
									parent: '#toastParent'
								});
							}
							else if(docType === 'amdm') {
								$mdToast.show({
									template: '<md-toast>Uklanjanje amandmana iz procedure neuspešno</md-toast>',
									hideDelay: 3000,
									position: 'top right',
									parent: '#toastParentAmdm'
								});
							}
						}
				);
		}
   });
}());