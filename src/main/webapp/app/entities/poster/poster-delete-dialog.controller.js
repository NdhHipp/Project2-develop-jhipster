(function() {
    'use strict';

    angular
        .module('vtravelApp')
        .controller('PosterDeleteController',PosterDeleteController);

    PosterDeleteController.$inject = ['$uibModalInstance', 'entity', 'Poster'];

    function PosterDeleteController($uibModalInstance, entity, Poster) {
        var vm = this;

        vm.poster = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Poster.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
