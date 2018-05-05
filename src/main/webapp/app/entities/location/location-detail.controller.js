(function() {
    'use strict';

    angular
        .module('vtravelApp')
        .controller('LocationDetailController', LocationDetailController);

    LocationDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Location', 'Poster'];

    function LocationDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Location, Poster) {
        var vm = this;

        vm.location = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('vtravelApp:locationUpdate', function(event, result) {
            vm.location = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
