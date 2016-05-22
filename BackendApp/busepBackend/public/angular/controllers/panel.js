(function() {
	var app = angular.module('mainApp');
	
	app.controller('PanelCtrl', function($scope, $window, $http, $rootScope){
		$scope.akt = {'tip':'', 'oznaka':'', 'ustanova':'', 'odgLice':'', 'datum': new Date()};		
		$scope.amandman = {'title':'', 'content':''};

		$scope.aktTabs = [];
		var deoId = 1;
		var clanId = 1;
		$scope.addPreambulaDeo = function() {
			$scope.aktTabs.push({
				label:"Deo " + deoId++,
				deo:true,
				preambula:'',
				naziv:'',
				oznaka:'',
				deoNaziv:'',
				glavaOznaka:'',
				glavaNaziv:'',
				glavaOdeljak:'',
				obrazlozenje:''
			});
		};
		$scope.addPreambulaClan = function() {
			$scope.aktTabs.push({
				label:"Član " + clanId++,
				deo:false,
				preambula:'',
				naziv:'',
				oznaka:'',
				deoNaziv:'',
				glavaOznaka:'',
				glavaNaziv:'',
				glavaOdeljak:'',
				obrazlozenje:''
			});
		};
		
//		$scope.formInputs = [];
//		var prmblId = 1;
//		$scope.addPreambulaDeo = function() {
//			var prmblCurrent = 'Preambula ' + prmblId++;
//			$scope.formInputs.push({label:prmblCurrent, content:'', required:false});
//			$scope.formInputs.push({label:'Naziv', content:'', required:true});
//			$scope.formInputs.push({label:'Deo', content:'', required:true});
//			$scope.formInputs.push({label:'Obrazloženje', content:'', required:true});
//		};
//		$scope.addPreambulaClan = function() {
//			var prmblCurrent = 'Preambula ' + prmblId++;
//			$scope.formInputs.push({label:prmblCurrent, content:'', required:false});
//			$scope.formInputs.push({label:'Naziv', content:'', required:true});
//			$scope.formInputs.push({label:'Član', content:'', required:true});
//			$scope.formInputs.push({label:'Obrazloženje', content:'', required:true});			
//		};
		
		$scope.predloziAkt = function() {
			$http.post('/acts/new', $scope.akt)
				.then(function(response) {
					//neka poruka za uspesno predlaganje
					//resetovanje svih inputa ($scope.akt.tip = ''; ili neki angularskiji nacin)
				}, function(reason) {
					//poruka o neuspehu, zasto blabla
				});
		};
		
		$scope.predloziAmandman = function() {
			
		};
   });
}());