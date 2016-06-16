(function() {
	var app = angular.module('mainApp');

	//TODO mozda bolje kao servis, koji koriste (trenutno nepostojeci) kontroleri za odbornika i predsednika
	//radi lakse provere da li je u pitanju korisnik koji ne treba da ima pristup ovom delu aplikacije
	app.controller('PredsednikCtrl', function($scope, $http, $window, $rootScope, $mdToast) {
		$rootScope.mainPage = false;
		
		var user = $rootScope.user;
		if (!user || user.role !== "Predsednik") {
			$window.location.href = "#/";
		}
		
		$scope.showVoteInput = false;
		$scope.votes = {'for': undefined, against: undefined};
		$scope.voting = false;
		$scope.equalVotes = false;
		
		$scope.docList = [{title: "Dresiranje pseta", uri:'', uriHash:'', voted: false, acceptable: false, equalVotes: false},
		                  {title: "Hranjenje mačeta", uri:'', uriHash:'', voted: false, acceptable: false, equalVotes: false},
		                  {title: "Odlazak u budizam", uri:'', uriHash:'', voted: false, acceptable: false, equalVotes: false}];
		
		$http.get('/acts/proc')
			.then(
					function(response) {
//						$scope.docList = response.data;
//						$scope.docList.forEach(function(doc) {
//							doc.voted = false;
//							doc.acceptable = false;
//							doc.equalVotes = false;
//						});
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
		
		$scope.docRez = function(doc) {
			$scope.voting = true;
			$scope.showVoteInput = true;
			$scope.selectedDoc = doc;
		};
		
		var calculatePercents = function() {
			var total = $scope.votes['for'] + $scope.votes.against;
			var percentFor = 100 * $scope.votes['for'] / total;
			var percentAgainst = 100 * $scope.votes.against / total;
			var shaPercentFor = sha256(percentFor.toString());
			var shaPercentAgainst = sha256(percentAgainst.toString());
			
			return [percentFor, percentAgainst, shaPercentFor, shaPercentAgainst];
		};
		
		var acceptDoc = function(doc, percentFor, percentAgainst, shaFor, shaAgainst, shaPercentFor, shaPercentAgainst) {
			$http.post('/xml/submit/final', [$scope.votes['for'], $scope.votes.against, percentFor, percentAgainst,
			                                 shaFor, shaAgainst, shaPercentFor, shaPercentAgainst,
			                                 doc.uri, doc.uriHash])
				.then(
						function(response) {
							$mdToast.show({
								template: '<md-toast>Dokument prihvaćen</md-toast>',
								hideDelay: 3000,
								position: 'top left',
								parent: '#voteToastParent'
							});
							
							$scope.docList.splice($scope.docList.indexOf($scope.selectedDoc), 1);
							$scope.voting = false;
							$scope.votes = {'for': undefined, against: undefined};
						},
						function(reason) {
							$scope.selectedDoc.acceptable = true;
							$mdToast.show({
								template: '<md-toast>Greška tokom prihvatanja dokumenta. Možete pokušati prihvatiti klikom na dugme pored naziva dokumenta.</md-toast>',
								hideDelay: 3000,
								position: 'top left',
								parent: '#voteToastParent'
							});
						}
				);
		};
		
		var refuseDoc = function(doc, percentFor, percentAgainst, shaFor, shaAgainst, shaPercentFor, shaPercentAgainst) {
			$http.post('/xml/refuse', [$scope.votes['for'], $scope.votes.against, percentFor, percentAgainst,
			                           shaFor, shaAgainst, shaPercentFor, shaPercentAgainst,
			                           doc.uri, doc.uriHash])
				.then(
						function(response) {
							$mdToast.show({
								template: '<md-toast>Dokument odbijen</md-toast>',
								hideDelay: 3000,
								position: 'top left',
								parent: '#voteToastParent'
							});
							
							$scope.docList.splice($scope.docList.indexOf($scope.selectedDoc), 1);
							$scope.voting = false;
							$scope.votes = {'for': undefined, against: undefined};
						},
						function(reason) {
							$scope.selectedDoc.acceptable = false;
							$mdToast.show({
								template: '<md-toast>Greška tokom odbijanja dokumenta. Možete pokušati odbiti klikom na dugme pored naziva dokumenta.</md-toast>',
								hideDelay: 3000,
								position: 'top left',
								parent: '#voteToastParent'
							});
						}
				);
		};
		
		$scope.voteFormSubmit = function() {
			$scope.selectedDoc.voted = true;
			$scope.showVoteInput = false;
			
			var shaFor = sha256($scope.votes['for'].toString());
			var shaAgainst = sha256($scope.votes.against.toString());
			
			var total = $scope.votes['for'] + $scope.votes.against;
			var percents = calculatePercents();
			var percentFor = percents[0];
			var percentAgainst = percents[1];
			var shaPercentFor = percents[2];
			var shaPercentAgainst = percents[3];
			
			if(percentFor === 100 || percentFor > percentAgainst) {
//				$scope.selectedDoc.state = 1; // must accept
				acceptDoc($scope.selectedDoc, percentFor, percentAgainst, shaFor, shaAgainst, shaPercentFor, shaPercentAgainst);
			}
			
			if(percentAgainst === 100 || percentAgainst > percentFor) {
				refuseDoc($scope.selectedDoc, percentFor, percentAgainst, shaFor, shaAgainst, shaPercentFor, shaPercentAgainst);
			}
			
			if(percentFor === percentAgainst) {
				$scope.showVoteInput = false;
				$scope.selectedDoc.equalVotes = true;
			}
			
//			if(percentFor >== percentAgainst) {
//				$scope.acceptable = true;
//			}			
		};

		$scope.docAccept = function(doc) {
			var shaFor = sha256($scope.votes['for'].toString());
			var shaAgainst = sha256($scope.votes.against.toString());
			var percents = calculatePercents();

			if(doc !== $scope.selectedDoc) {
				return false;
			}
			acceptDoc(doc, percents[0], percents[1], shaFor, shaAgainst, percents[2], percents[3]);
		};

		$scope.docReject = function(doc) {
			var percents = calculatePercents();
			var shaFor = sha256($scope.votes['for'].toString());
			var shaAgainst = sha256($scope.votes.against.toString());
			
			if(doc !== $scope.selectedDoc) {
				return false;
			}
			refuseDoc(doc, percents[0], percents[1], shaFor, shaAgainst, percents[2], percents[3]);
		};
		
		$scope.cancelVote = function() {
			$scope.showVoteInput = false;
			$scope.votes = {'for': undefined, against: undefined};
			$scope.voting = false;
			$scope.equalVotes = false;
			
			$scope.docList.forEach(function(doc) {
//				doc.voted = false;
//				doc.acceptable = false;
			});
		}
	});
}());