(function() {
    'use strict';
    angular
        .module('vtravelApp')
        .factory('Poster', Poster);

    Poster.$inject = ['$resource'];

    function Poster ($resource) {
        var resourceUrl =  'api/posters/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' },

            //
            'findiddesc':{
                method: 'GET',
                url: 'api/poster/findiddesc',
                isArray: true,
                transformResponse: function(data) {
                    if (data){
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            }
        });
    }
})();
