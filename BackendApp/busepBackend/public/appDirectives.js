(function() {
	var app = angular.module('mainApp');

	app.directive('goClick', function($location) {
		return function(scope, element, attrs) {
			var path;

			attrs.$observe('goClick', function(val) {
				path = val;
			});

			element.bind('click', function() {
				scope.$apply(function() {
					$location.path(path);
				});
			});
		};
	})
	.directive("compareTo", function() {
	    return {
	    	require: "ngModel",
	        scope: {
	            otherModelValue: "=compareTo"
	        },
	        link: function(scope, element, attributes, ngModel) {
	             
	            ngModel.$validators.compareTo = function(modelValue) {
	                return modelValue == scope.otherModelValue;
	            };
	 
	            scope.$watch("otherModelValue", function() {
	                ngModel.$validate();
	            });
	        }
	    };
	})
	.directive("xmlEditor", function($http) {
		return {
//			restrict: 'AE',
//			scope: {
//				xmlEditorScope: '=xmlEditor'
//			},
//			template: '<div id="xml_editor"></div>', //<script>$("#xml_editor").xmlEditor({documentTitle : "Novi akt",ajaxOptions: {xmlRetrievalPath: "/xml/akt.xml"},schema : "/xml-schema/akt.json"});</script>
			link: function(scope, elem, attrs) {
				var extractor = new Xsd2Json("Propis.xsd", {"schemaURI":"/xml-schema/", "rootElement":"Propis"});
				var xmlSchema = undefined;
				$http.get('/xml-schema/akt.json')
					.then(
							function(response) {
								xmlSchema = response.data;
								console.log(xmlSchema); //provera
							},
							function(reason) { //failed
								xmlSchema = extractor.xsdManager.originatingRoot;
								console.log(xmlSchema); //provera
							}
					);
				$(elem).xmlEditor({
//					confirmExitWhenUnsubmitted: true,
//					documentTitle : "Novi akt",
//					ajaxOptions: {
//						xmlUploadPath: "/xml/submit",
//						xmlRetrievalPath: "/xml/akt.xml"
//					},
					schema : xmlSchema
				});  //extractor.xsdManager.originatingRoot //"/xml-schema/akt.json"
				
				scope.root = elem;
		    }
		}
	});
}());