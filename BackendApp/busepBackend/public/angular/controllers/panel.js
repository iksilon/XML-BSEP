(function() {
	var app = angular.module('mainApp');

	//TODO mozda bolje kao servis, koji koriste (trenutno nepostojeci) kontroleri za odbornika i predsednika
	//radi lakse provere da li je u pitanju korisnik koji ne treba da ima pristup ovom delu aplikacije
	app.controller('PanelCtrl', function($scope, $window, $http, $rootScope) {
		$rootScope.mainPage = false;
		
		var user = $rootScope.user; // cuvacemo u Java Web Token (JWT),
		// POST-om saljemo na server, server skonta koji je user,
		// i vrati username i sta god vec treba

//		(function() {
//			if ($rootScope.user.role != 0 && $rootScope.user.role != 1) {
//				$window.location.href = "#/";
//			}
//		}());

		if (user == undefined || user == null) {
			$window.location.href = "#/";
		}

		$scope.aktData = [{
								'tip' : '',
								'oznaka' : '',
								'ustanova' : '',
								'odgLice' : '',
								'datum' : new Date()
							},
							[], //delovi
							[]]; //clanovi
		$scope.amandman = {
			'title' : '',
			'content' : ''
		};

		$scope.aktTabs = [];
		var deoId = 1;
		var clanId = 1;
		$http.get('/get/panelIds/' + user.username)
			.then(
					function(response) {
						deoId = response.data.panelDeoId;
						clanId = response.data.panelClanId;
					},
					function(reason) {
						// greska o neuspesnom loadovanju sacuvanog stanja
					}
			);
		$scope.addPreambulaDeo = function() {
			var incrDeoFail = false;
			$http.post('/set/deoId/' + user.username)
				.then(
						function(response) {
							// uspesno uvecane vrednosti
						},
						function(reason) {
							incrDeoFail = true;
						}
				);
			if (incrDeoFail) {
				// poruka greske
				return;
			}			
			$scope.aktTabs.push({
				label : "Deo " + deoId++,
				deo : true,
				preambula : '',
				naziv : '',
				oznaka : '',
				deoNaziv : '',
				glavaOznaka : '',
				glavaNaziv : '',
				glavaOdeljak : '',
				obrazlozenje : ''
			});
			$scope.aktData[1].push($scope.actTabs[$scope.aktTabs.length - 1]);
		};
		$scope.addPreambulaClan = function() {
			var incrClanFail = false;
			$http.post('/set/clanId/' + user.username)
				.then(
						function(response) {
							// uspesno uvecane vrednosti
						}, function(reason) {
							incrClanFail = true;
						}
				);
			if (incrClanFail) {
				// poruka greske
				return;
			}
			$scope.aktTabs.push({
				label : "Član " + clanId++,
				deo : false,
				preambula : '',
				naziv : '',
				oznaka : '',
				deoNaziv : '',
				glavaOznaka : '',
				glavaNaziv : '',
				glavaOdeljak : '',
				obrazlozenje : ''
			});
			$scope.aktData[2].push($scope.aktTabs[$scope.aktTabs.length - 1]);
		};

		$scope.predloziAkt = function() {
			$http.post('/acts/new', $scope.aktData)
				.then(
						function(response) {
							// neka poruka za uspesno predlaganje
							// resetovanje svih inputa ($scope.akt.tip = ''; ili neki
							// angularskiji nacin)
						}, function(reason) {
							// poruka o neuspehu, zasto blabla
						}
				);
		};
		$scope.predloziAmandman = function() {

		};
	});
}());