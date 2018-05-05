(function() {
    'use strict';

    angular
        .module('vtravelApp')
        .controller('PosterDialogController', PosterDialogController);

    PosterDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Poster', 'Location'];

    function PosterDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Poster, Location) {
        var vm = this;

        vm.poster = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.locations = Location.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.poster.id !== null) {
                Poster.update(vm.poster, onSaveSuccess, onSaveError);
            } else {
                Poster.save(vm.poster, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('vtravelApp:posterUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setImage = function ($file, poster) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        poster.image = base64Data;
                        poster.imageContentType = $file.type;
                    });
                });
            }
        };

    }
})();
