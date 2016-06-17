(function() {
	var app = angular.module('mainApp');
	
	app.controller('AktiProcCtrl', function($scope, $http, $window, $rootScope, $mdToast, $sce, amandmanSvc){
		$rootScope.mainPage = false;
		
		$scope.user = $rootScope.user;
		
		$scope.showDocAmendments = false;
		$scope.acts = [{uri:'Dresiranje pseta', uriHash:'shshsh', username:'p@a.com'},
		               {uri:'Hranjenje mačeta', uriHash:'shshsh', username:'o@a.com'},
		               {uri:'Odlazak u budizam', uriHash:'shshsh', username:'p@a.com'}];
		$scope.amendments = [];
		$http.get('/acts/proc')
			.then(
					function(response) {
						//TODO ODKOMENTARISI
//						$scope.acts = response.data;
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

//		$scope.docView = $sce.trustAsHtml('<md-content xmlns:pro=\"http:\/\/www.ftn.uns.ac.rs\/propisi\" layout-align=\"center\"> <md-content layout-align=\"center\" layout=\"row\"> <h1>Testiraje-transformacije<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\')\">Izmeni<\/md-button> <br> <\/span> <\/h1> <\/md-content> <h2> Deo 1<br>Supe<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\')\">Izmeni<\/md-button> <br> <\/span> <\/h2> <h3>1<br>Pilece<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\')\">Izmeni<\/md-button> <br> <\/span> <\/h3> <h4>1<br> <span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\')\">Izmeni<\/md-button> <br> <\/span> <\/h4> <h5>A<br>sa lukom<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\')\">Izmeni<\/md-button> <br> <\/span> <\/h5> <h6> \u00C4\u0152lan 1<br>Priprema<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\')\">Izmeni<\/md-button> <br> <\/span> <\/h6> <span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[1]\')\">Izmeni<\/md-button> <br> <\/span>Potrebni sastojci su:<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[2]\')\">Izmeni<\/md-button> <br> <\/span> <p> <span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[2]\/Tacka[1]\')\">Izmeni<\/md-button> <br> <\/span>serpa<\/p> <p> <span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[2]\/Tacka[2]\')\">Izmeni<\/md-button> <br> <\/span>supa<\/p> <p> <span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[2]\/Tacka[3]\')\">Izmeni<\/md-button> <br> <\/span> <p> <span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[2]\/Tacka[3]\/Podtacka[1]\')\">Izmeni<\/md-button> <br> <\/span>ponestalo<\/p> <p> <span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[2]\/Tacka[3]\/Podtacka[2]\')\">Izmeni<\/md-button> <br> <\/span>mi<\/p> <p> <span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[2]\/Tacka[3]\/Podtacka[3]\')\">Izmeni<\/md-button> <br> <\/span> <p>inspiracije<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[2]\/Tacka[3]\/Podtacka[3]\/Alineja[1]\')\">Izmeni<\/md-button> <br> <\/span> <\/p> <p>za<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[2]\/Tacka[3]\/Podtacka[3]\/Alineja[2]\')\">Izmeni<\/md-button> <br> <\/span> <\/p> <p>sastojke<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[1]\/Stav[2]\/Tacka[3]\/Podtacka[3]\/Alineja[3]\')\">Izmeni<\/md-button> <br> <\/span> <\/p> <\/p> <\/p> <h6> \u00C4\u0152lan 2<br>Jedenje<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[1]\/Clan[2]\')\">Izmeni<\/md-button> <br> <\/span> <\/h6> <h5>B<br>sa krompirom<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[1]\/Pododeljak[2]\')\">Izmeni<\/md-button> <br> <\/span> <\/h5> <h4>2<br>Bistre<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[2]\')\">Izmeni<\/md-button> <br> <\/span> <\/h4> <h6> \u00C4\u0152lan <br> <span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[1]\/Odeljak[2]\/Clan[1]\')\">Izmeni<\/md-button> <br> <\/span> <\/h6> <h3>2<br>Govedje<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[1]\/Glava[2]\')\">Izmeni<\/md-button> <br> <\/span> <\/h3> <h2> Deo 2<br>Testa<span> <md-button type=\"button\" class=\"md-raised md-primary\" ng-click=\"elementEdit(\'\/Propis[1]\/Deo[2]\')\">Izmeni<\/md-button> <br> <\/span> <\/h2> <\/md-content> ');
		$scope.showDocView = false;
		$scope.docSelected = function(doc) {
			$http.post('/acts/get', [doc.uri, doc.uriHash])
				.then(
						function(response) {
							$http.post('/acts/amdmnt', [doc.uri, doc.uriHash])
								.then(
										function(response) {
											$scope.amendments = response.data;
											$scope.showDocAmendments = true;
										},
										function(reason) {
											$scope.amendments = [];
											$scope.showDocAmendments = false;
											
											
										}
								);
//							docView = $sce.trustAsHtml(response.data);
							$scope.showDocView = true;
							amandmanSvc.selectedAct = doc;
						},
						function(reason) {
							$mdToast.show({
								template: '<md-toast>Greška pri preuzimanju odabranog akta</md-toast>',
								hideDelay: 3000,
								position: 'top right',
								parent: '#docView'
							});
							$scope.docView = $sce.trustAsHtml('');
							amandmanSvc.selectedAct = undefined;
							$scope.showDocView = false;
							$scope.showDocAmendments = false;
							$scope.amendments = [];
						}
				);
		};
		
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