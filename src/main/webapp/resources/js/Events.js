'use strict';

//var messageList = [];
var nowID;

function createMes(textNew, authorNew) {
    return {
        text: textNew,
        author: authorNew,
        id: uniqueID()
    };
}

function createIdJSON(id_) {
    return {
        id: id_
    }
}

function uniqueID() {
    var date = Date.now();
    var random = Math.random() * Math.random();

    return Math.floor(date * random).toString();
}

var appState = {
    mainUrl : 'http://localhost:8080/WebChat',
    token : 'TE11EN',
    version: 0
};

function run() {
    restore(writeAll);

    var name = document.getElementById("btnName");
    name.addEventListener("click", function () {
        var name = document.getElementById("inputName").value;
        document.getElementById("inputName").value = '';
        document.getElementById("name").value = name;
    });

    var send = document.getElementById("btnSend");
    send.addEventListener("click", function() {
        if (!document.getElementById("inputText").value) {
            return;
        }

        if (!document.getElementById("name").value) {
            alert ("Enter name!");
            return;
        }


        var item = createMes(document.getElementById("inputText").value, document.getElementById("name").value);
        document.getElementById("inputText").value = '';
        doPost (appState.mainUrl, JSON.stringify(item), null, null);
        document.getElementById("history").scrollTop = 99999999;
    });

    var btn = document.getElementById("btnChangeMessage");
    btn.addEventListener("click", changeMessage, false);
    document.getElementById("history").scrollTop = 99999999;
}

function writeUIMessage(elem) {
    var divItem = document.createElement("div");
    divItem.setAttribute("id", elem.id);

    var spanElem = document.createElement("span");
    spanElem.classList.add("author")
    spanElem.textContent = elem.author + ': ';

    var change = document.createElement("i");
    var del = document.createElement("i");
    var btnDel = document.createElement("button");
    var btnChange = document.createElement("button");

    btnDel.classList.add("btn");
    btnDel.classList.add("deleteMessage");
    btnDel.setAttribute("type", "button");

    btnDel.setAttribute("onclick", "funBtnDelete(this)");
    btnDel.classList.add("setInvisible");

    btnChange.classList.add("btn");
    btnChange.classList.add("changeMessage");
    btnChange.setAttribute("type", "button");

    btnChange.setAttribute("onclick", "funBtnChange(this)");
    btnChange.classList.add("setInvisible");

    del.classList.add("glyphicon");
    del.classList.add("glyphicon-remove");
    change.classList.add("glyphicon");
    change.classList.add("glyphicon-cog");

    btnDel.appendChild(del);
    btnChange.appendChild(change);

    var text = document.createElement("pre");
    text.textContent = elem.text;
    text.classList.add("textMessage");

    divItem.appendChild(spanElem);
    divItem.appendChild(btnDel);
    divItem.appendChild(btnChange);
    divItem.appendChild(text);
    divItem.setAttribute("onmouseover", "showButtons(this)");
    divItem.setAttribute("onmouseout", "hideButtons(this)")

    return divItem;
}

function funBtnDelete(elem) {
    var parent = elem.parentNode;
    doDelete(appState.mainUrl, JSON.stringify({id: parent.id, text: "", author: "not important"}), null, null);
}

function funBtnChange(elem) {
    var item = elem.parentNode;
    var i = 1;
    var mes = item.firstChild;
    while (mes.nodeName == "#text" || !mes.classList.contains("textMessage")) {
        mes = item.childNodes[i++];
    }
    document.getElementById("inputText").value = mes.firstChild.textContent;
    var btn = document.getElementById("btnChangeMessage");
    btn.classList.remove("setInvisible");
    var send = document.getElementById("btnSend");
    send.classList.add("setInvisible");
    nowID = item.id;
}

function changeMessage() {
    var text = document.getElementById("inputText");

    while (!text.value) {
        alert ("Enter some text!");
        return;
    }

    doPut(appState.mainUrl,JSON.stringify({id: nowID, text: text.value, author: document.getElementById("name").value}), null, null);

    var btn = document.getElementById("btnChangeMessage");
    btn.classList.add("setInvisible");
    var send = document.getElementById("btnSend");
    send.classList.remove("setInvisible");

    text.value = '';
}

function showButtons (msg) {
    if (document.getElementById("name").value + ": " == msg.getElementsByClassName("author")[0].innerHTML) {
        msg.getElementsByClassName("deleteMessage")[0].classList.remove("setInvisible");
        msg.getElementsByClassName("changeMessage")[0].classList.remove("setInvisible");
    }
}

function hideButtons (msg) {
    msg.getElementsByClassName("deleteMessage")[0].classList.add("setInvisible");
    msg.getElementsByClassName("changeMessage")[0].classList.add("setInvisible");
}

function restore(continueWith) {
    var url = appState.mainUrl + '?token=' + appState.token + '&version=' + appState.version;

    doGet(url, function (responseText) {
        console.assert(responseText != null);

        var response = JSON.parse(responseText);
        if (appState.version != response.version) {
            document.getElementById('history').innerHTML = '';
            appState.version = response.version;
        }
        appState.token = response.token;
        continueWith && continueWith(response.messages);
        });
    setTimeout(function() {
        restore(continueWith);
    }, 1000);
}

function writeAll(messageList) {
    var flag = false;
    if (messageList != null) {
        if (messageList.length != 0) {
            flag = true;
        }
        for (var i = 0; i < messageList.length; i++) {
            if (messageList[i].text != '') {
                document.getElementById("history").appendChild(writeUIMessage(messageList[i]));
            }
        }
        if (flag) {
            document.getElementById("history").scrollTop = 99999999;
        }
    }
}

// "начинка" ajax

function defaultErrorHandler(message) {
    console.error(message);
    //output(message);
}

function doGet(url, continueWith, continueWithError) {
    ajax('GET', url, null, continueWith, continueWithError);
}

function doPost(url, data, continueWith, continueWithError) {
    ajax('POST', url, data, continueWith, continueWithError);
}

function doPut(url, data, continueWith, continueWithError) {
    ajax('PUT', url, data, continueWith, continueWithError);
}

function doDelete(url, data, continueWith, continueWithError) {
    ajax('DELETE', url, data, continueWith, continueWithError);
}

function isError(text) {
    if(text == "")
        return false;
    var obj;

    try {
        obj = JSON.parse(text);
    } catch(ex) {
        return true;
    }

    return !!obj.error;
}

function ajax(method, url, data, continueWith, continueWithError) {
    var xhr = new XMLHttpRequest();

    continueWithError = continueWithError || defaultErrorHandler;
    xhr.open(method || 'GET', url, true);

    xhr.onload = function () {
        if (xhr.readyState !== 4)
            return;

        if(xhr.status != 200) {
            continueWithError('Error on the server side, response ' + xhr.status);
            return;
        }

        if(isError(xhr.responseText)) {
            continueWithError('Error on the server side, response ' + xhr.responseText);
            return;
        }

        continueWith(xhr.responseText);
    };

    xhr.ontimeout = function () {
        ontinueWithError('Server timed out !');
    };

    xhr.onerror = function (e) {
        var errMsg = 'Server connection error !\n'+
            '\n' +
            'Check if \n'+
            '- server is active\n'+
            '- server sends header "Access-Control-Allow-Origin:*"';

        continueWithError(errMsg);
    };

    xhr.send(data);
}

window.onerror = function(err) {
    //output(err.toString());
}
