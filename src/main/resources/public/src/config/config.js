/****  httpVueLoader 配置 ****/
// Thanks for https://github.com/FranckFreiburger/http-vue-loader/issues/38
const useBabel = false;

if (useBabel) {
    const esBabalTransformer = script => {
        console.log("loading");
        console.log(script);
        const s = Babel.transform(script, { presets: ['es2017', 'stage-3'] }).code;
        console.log("loading end");
        console.log(s);
        return s;
    };
    httpVueLoader.langProcessor.javascript = esBabalTransformer;
    httpVueLoader.langProcessor.js = esBabalTransformer;
}

/**** vue vueRouter ***/
Vue.use(VueRouter);


/****  bus 配置 ****/
Vue.use(function (Vue) {
    Vue.prototype.$bus = new Vue();
});


function httpWithJsonRpc(serviceName, data) {
    return axios.post("/api/json-rpc", {
        "jsonrpc": "2.0",
        "method": serviceName,
        "params": data,
        "id": uuidv4()
    }).then(function (response) {
        if (response.data.error) {
            antd.notification.error({
                message: 'API调用错误',
                description: JSON.stringify(response.data.error),
                duration: null
            });
            return Promise.reject(response.data);
        } else {
            return Promise.resolve(response.data);
        }
    });
}

/****  axios 配置 ****/
axios.interceptors.request.use(function (config) {
    return config;
}, function (error) {
    return Promise.reject(error);
});

axios.interceptors.response.use(function (response) {
    // console.log(response);
    const data = response.data;
    if (false) {
        antd.notification.error({
            message: 'API调用错误',
            description: JSON.stringify(error),
            duration: null
        });
        return Promise.reject(data);
    } else {
        return response;
    }
}, function (error) {
    antd.notification.error({
        message: '网络错误',
        description: JSON.stringify(error),
        duration: null
    });
    // Do something with response error
    return Promise.reject(error);
});

window.globalModules = {};

