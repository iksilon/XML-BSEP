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
	.directive("xmlEditor", function() {
		return {
			restrict: 'E',
//			scope: {
//				schemaName: '=schema',
//				docName: '=doc',
//				rootElement: '=root'
//			},
			link: function(scope, elem, attrs) {
				if(attrs.doc == undefined || attrs.doc == null) {
					attrs.doc = attrs.schema;
				}
				if(attrs.root == undefined || attrs.root == null) {
					attrs.root = attrs.schema;
				}
				
				var extractor = new Xsd2Json(attrs.schema + '.xsd', {'schemaURI':'/xml-schema/', 'rootElement': attrs.root});

				$(elem).xmlEditor({
					confirmExitWhenUnsubmitted: true,
					documentTitle : attrs.doc,
					ajaxOptions: {
						xmlUploadPath: '/xml/submit',
						xmlRetrievalPath: '/xml/' + attrs.doc + '.xml'
					},
					libPath: "/javascripts/jquery.xmleditor-master/lib/",
					schema : extractor.xsdManager.originatingRoot
				});
				
//				scope.root = elem;
		    }
		}
	});
}());