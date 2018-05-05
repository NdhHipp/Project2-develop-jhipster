(function() {
    'use strict';

    angular
        .module('vtravelApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('poster', {
            parent: 'entity',
            url: '/poster?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'vtravelApp.poster.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/poster/posters.html',
                    controller: 'PosterController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('poster');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('poster-detail', {
            parent: 'poster',
            url: '/poster/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'vtravelApp.poster.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/poster/poster-detail.html',
                    controller: 'PosterDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('poster');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Poster', function($stateParams, Poster) {
                    return Poster.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'poster',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }],
                //
                findiddesc: ['Poster', function(Poster){
                    return Poster.findiddesc().$promise;
                }],
            }
        })
        .state('poster-detail.edit', {
            parent: 'poster-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/poster/poster-dialog.html',
                    controller: 'PosterDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Poster', function(Poster) {
                            return Poster.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('poster.new', {
            parent: 'poster',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/poster/poster-dialog.html',
                    controller: 'PosterDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                title: null,
                                image: null,
                                imageContentType: null,
                                recommend: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('poster', null, { reload: 'poster' });
                }, function() {
                    $state.go('poster');
                });
            }]
        })
        .state('poster.edit', {
            parent: 'poster',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/poster/poster-dialog.html',
                    controller: 'PosterDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Poster', function(Poster) {
                            return Poster.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('poster', null, { reload: 'poster' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('poster.delete', {
            parent: 'poster',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/poster/poster-delete-dialog.html',
                    controller: 'PosterDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Poster', function(Poster) {
                            return Poster.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('poster', null, { reload: 'poster' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
