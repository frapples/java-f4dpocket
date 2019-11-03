const RouteView = httpVueLoader("./src/layouts/RouteView.vue");
const MenuView = httpVueLoader("./src/layouts/MenuView.vue");
const ExceptionPage = httpVueLoader("./src/pages/exception/ExceptionPage.vue");
const ProjectConfig = httpVueLoader("./src/pages/webproject/ProjectConfig.vue");

const Page404 = Vue.component('Page404', {
    components: { ExceptionPage },
    template: '<exception-page type="404"></exception-page>'
});

window.globalModules.router = new VueRouter({
    routes: [
        {
            path: '/',
            name: '主页',
            component: MenuView,
            invisible: true,
            children: [
                {
                    path: '/webproject',
                    name: 'WEB项目代码生成',
                    component: RouteView,
                    icon: 'dashboard',
                    children: [
                        {
                            path: '/webproject/project-config',
                            name: '项目配置',
                            component: ProjectConfig,
                            icon: 'none'
                        },
                        {
                            path: '/webproject/mybatis-generate',
                            name: 'mybatis代码生成',
                            component: Page404,
                            icon: 'none'
                        }
                    ]
                },
            ]
        }
    ]
});
