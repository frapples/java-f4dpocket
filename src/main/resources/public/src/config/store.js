const setting = {
    namespaced: true,
    state: {
        isMobile: false,
        theme: 'dark',
        layout: 'side',
        systemName: 'F4d Pocket',
        logoUrl: 'img/vue-antd-logo.png',
        copyright: '2017 Frapples',
        footerLinks: [
            /*
            {link: 'https://pro.ant.design', name: 'Pro首页'},
            {link: 'https://github.com/iczer/vue-antd-admin', icon: 'github'},
            {link: 'https://ant.design', name: 'Ant Design'}
             */
        ],
        multipage: true
    },
    mutations: {
        setDevice (state, isMobile) {
            state.isMobile = isMobile
        },
        setTheme (state, theme) {
            state.theme = theme
        },
        setLayout (state, layout) {
            state.layout = layout
        },
        setMultipage (state, multipage) {
            state.multipage = multipage
        }
    }
};

function getLocalStorage(key, defaultValue) {
    let v = localStorage.getItem(key);
    return _.isNil(v) ? defaultValue : JSON.parse(v);
}

function setLocalStorage(key, v) {
    localStorage.setItem(key, JSON.stringify(v));
}

function getProjectList() {
    let list = localStorage.getItem("f4dpocket/projectList");
    return _.isNil(list) ? [] : JSON.parse(list);
}


const autocode = {

    namespaced: true,
    state: {
        projects: getLocalStorage("f4dpocket/projectList", []),
        authors: getLocalStorage("f4dpocket/authors", []),
    },

    mutations: {
        setProjects(state, projects) {
            setLocalStorage("f4dpocket/projectList", projects);
            state.projects = projects;
        },

        setAuthors(state, authors) {
            setLocalStorage("f4dpocket/authors", authors);
            state.authors = authors;
        }
    }

};

window.globalModules.store = new Vuex.Store({
    modules: {
        setting,
        autocode
    }
});
