(function() {
    'use strict';

    angular
        .module('vtravelApp')
        .controller('PosterDetailController', PosterDetailController);

    PosterDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Poster', 'Location', 'findiddesc'];

    function PosterDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Poster, Location, findiddesc) {
        var vm = this;

        //
        vm.findiddesc = findiddesc;
        
        vm.poster = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('vtravelApp:posterUpdate', function(event, result) {
            vm.poster = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
