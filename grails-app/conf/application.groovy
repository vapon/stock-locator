// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'stock.locator.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'stock.locator.UserRole'
grails.plugin.springsecurity.authority.className = 'stock.locator.Role'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
        [pattern: '/', access: ['permitAll']],
        [pattern: '/error', access: ['permitAll']],
        [pattern: '/logout', access: ['permitAll']],
        [pattern: '/index', access: ['permitAll']],
        [pattern: '/index.gsp', access: ['permitAll']],
        [pattern: '/product/index', access: ['ROLE_ADMIN', 'ROLE_USER']],
        [pattern: '/product/save', access: ['ROLE_ADMIN', 'ROLE_USER']],
        [pattern: '/product/show**', access: ['ROLE_ADMIN', 'ROLE_USER']],
        [pattern: '/product/create', access: ['ROLE_ADMIN']],
        [pattern: '/product/importProducts', access: ['ROLE_ADMIN']],
        [pattern: '/product/getImportSample', access: ['ROLE_ADMIN', 'ROLE_USER']],
        [pattern: '/product/exportProducts', access: ['ROLE_ADMIN', 'ROLE_USER']],
        [pattern: '/product/upload', access: ['ROLE_ADMIN']],
        [pattern: '/product/edit', access: ['ROLE_ADMIN']],
        [pattern: '/product/delete', access: ['ROLE_ADMIN']],
        [pattern: '/shutdown', access: ['permitAll']],
        [pattern: '/assets/**', access: ['permitAll']],
        [pattern: '/**/js/**', access: ['permitAll']],
        [pattern: '/**/css/**', access: ['permitAll']],
        [pattern: '/**/images/**', access: ['permitAll']],
        [pattern: '/**/favicon.ico', access: ['permitAll']]
]

grails.plugin.springsecurity.filterChain.chainMap = [
        [pattern: '/assets/**', filters: 'none'],
        [pattern: '/**/js/**', filters: 'none'],
        [pattern: '/**/css/**', filters: 'none'],
        [pattern: '/**/images/**', filters: 'none'],
        [pattern: '/**/favicon.ico', filters: 'none'],
        [pattern: '/**', filters: 'JOINED_FILTERS']
]

