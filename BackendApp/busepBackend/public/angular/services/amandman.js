(function() {
	var app = angular.module('mainApp');
	
	app.service('amandmanSvc', function($mdToast, $http) {
		var self = this;
		
		self.selectedAct = undefined;
		self.editingPath = '';
	});
}());