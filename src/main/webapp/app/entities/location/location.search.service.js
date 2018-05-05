(function() {
    'use strict';

    angular
        .module('vtravelApp')
        .factory('LocationSearch', LocationSearch);

    LocationSearch.$inject = ['$resource'];

    function LocationSearch($resource) {
        var resourceUrl =  'api/_search/locations/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
