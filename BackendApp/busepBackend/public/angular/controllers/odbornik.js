(function() {
	var app = angular.module('mainApp');

	//TODO mozda bolje kao servis, koji koriste (trenutno nepostojeci) kontroleri za odbornika i predsednika
	//radi lakse provere da li je u pitanju korisnik koji ne treba da ima pristup ovom delu aplikacije
	app.controller('OdbornikCtrl', function($window, $rootScope) {
		$rootScope.mainPage = false;
		
		var user = $rootScope.user; // cuvacemo u Java Web Token (JWT),
		// POST-om saljemo na server, server skonta koji je user,
		// i vrati username i sta god vec treba
		if (!user || user.role !== "Odbornik" && user.role !== "Predsednik") {
			$window.location.href = "#/";
		}
	});
}());