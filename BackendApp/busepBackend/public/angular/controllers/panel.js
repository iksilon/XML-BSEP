(function() {
	var app = angular.module('mainApp');

	app.controller('PanelCtrl', function($scope, $window, $http, $rootScope) {
		var user = $rootScope.user; // cuvacemo u Java Web Token (JWT),
		// POST-om saljemo na server, server skonta koji je user,
		// i vrati username i sta god vec treba

//		(function() {
//			if ($rootScope.user.role != 0 && $rootScope.user.role != 1) {
//				$window.location.href = "#/";
//			}
//		}());

		if (user == undefined) {
			user = {};
			user.username = 'highlord';
		}

		$scope.akt = {
			'tip' : '',
			'oznaka' : '',
			'ustanova' : '',
			'odgLice' : '',
			'datum' : new Date()
		};
		$scope.amandman = {
			'title' : '',
			'content' : ''
		};

		$scope.aktTabs = [];
		var deoId = 1;
		var clanId = 1;
		$http.get('/get/panelIds/' + user.username).then(function(response) {
			deoId = response.panelDeoId;
			clanId = response.panelClanId;
		}, function(reason) {
			// greska o neuspesnom loadovanju sacuvanog stanja
		});
		$scope.addPreambulaDeo = function() {
			var incrDeoFail = false;
			$http.post('/set/deoId/' + user.username)
				.then(
						function(response) {
							// uspesno uvecane vrednosti
						},
						function(reason) {
							incrDeoFail = true;
				});
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
		};
		$scope.addPreambulaClan = function() {
			var incrClanFail = false;
			$http.post('/set/clanId/' + user.username).then(function(response) {
				// uspesno uvecane vrednosti
			}, function(reason) {
				incrClanFail = true;
			});
			if (incrClanFail) {
				// poruka greske
				return;
			}
			$scope.aktTabs.push({
				label : "Članing Tatum " + clanId++,
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
		};

		$scope.predloziAkt = function() {
			$http.post('/acts/new', $scope.akt).then(function(response) {
				// neka poruka za uspesno predlaganje
				// resetovanje svih inputa ($scope.akt.tip = ''; ili neki
				// angularskiji nacin)
			}, function(reason) {
				// poruka o neuspehu, zasto blabla
			});
		};
		$scope.predloziAmandman = function() {

		};
	});
}());