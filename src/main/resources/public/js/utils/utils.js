"use strict";

function getQueryString(name)
{
    var vars = window.location.search.substring(1).split("&");
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        if(pair[0] == name) {
            return pair[1];
        }
    }
    return null;
}

function urlencode(data) {
    var params = new URLSearchParams();
    for (var key in data) {
        // 不拼接null和undefined
        // 因null与"null"易混淆
        if (data[key] !== null && data[key] !== undefined) {
            params.append(key, data[key]);
        }
    }
    return params.toString();

}

function getFormParas($selector) {
    var paras = $selector.serializeArray();

    var result = {};
    paras.forEach(function(item) {
        result[item.name] = item.value;
    });
    return result;
}

function gotoUrl(url, data, notNewWindow) {
    var queryString = "";
    if (data) {
        queryString = "?" + $.param(data);
    }

    if (notNewWindow) {
        window.location.href = url + queryString;
    } else {
        window.open(url + queryString);
    }
}

window.template = {};

template.mount = function(slotSelector, templateSelector, data) {
    var template = $(templateSelector).html();
    Mustache.parse(template);   // optional, speeds up future uses
    var rendered = Mustache.render(template, data);
    var $slot = $(slotSelector);
    $slot.html(rendered);
};

function treeDFSForeach(tree, callback) {
    if (!(tree instanceof Array)) {
        return;
    }
    tree.forEach(node => {
        callback(node);
        treeDFSForeach(node.children, callback);
    });
}

function treeFind(nodes, predicate) {
    if (!(nodes instanceof Array)) {
        return null;
    }
    for (const node of nodes) {
        if (predicate(node)) {
            return node;
        }
        const r = treeFind(node.children, predicate);
        if (r != null) {
            return r;
        }
    }
}

function treePath(tree, dstNode) {
    let paths = [];
    let dfs;
    dfs = function (nodes) {
        if (!(nodes instanceof Array)) {
            return false;
        }

        for (const n of nodes) {
            paths.push(n);
            if (n === dstNode) {
                return true;
            }

            if (dfs(n.children)) {
                return true;
            }
            paths.pop(n);
        }
    };
    dfs(tree);
    return paths;
}

// Thanks for https://stackoverflow.com/questions/105034/create-guid-uuid-in-javascript
function uuidv4() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}
