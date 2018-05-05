(function() {
    'use strict';

    angular
        .module('vtravelApp')
        .factory('PosterSearch', PosterSearch);

    PosterSearch.$inject = ['$resource'];

    function PosterSearch($resource) {
        var resourceUrl =  'api/_search/posters/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
