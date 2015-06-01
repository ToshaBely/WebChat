'use strict';

var nowID;
var appState = {
    mainUrl : 'http://localhost:8080/WebChat'
};
var url = appState.mainUrl + '?first=true';

function createMes(textNew, authorNew) {
    return {
        text: textNew,
        author: authorNew,
        id: uniqueID()
    };
}

function uniqueID() {
    var date = Date.now();
    var random = Math.random() * Math.random();

    return Math.floor(date * random).toString();
}

function run() {
    initDialog();
    url = appState.mainUrl + '?first=false';
    restore();

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
        $.ajax({
            method: "POST",
            url: appState.mainUrl,
            data: JSON.stringify(item)
        });
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
    spanElem.classList.add("author");
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
    divItem.setAttribute("onmouseout", "hideButtons(this)");

    return divItem;
}

function deleteUIMessage(messageList) {
    if (messageList != null) {
        for (var i = 0; i < messageList.length; i++) {
            var id = messageList[i].id;
            var item = document.getElementById(id);
            item.parentNode.removeChild(item);
        }
    }
}

function changeUIMessage(messageList) {
    if (messageList != null) {
        for (var i = 0; i < messageList.length; i++) {
            var id = messageList[i].id;
            var item = document.getElementById(id);
            item.getElementsByClassName("textMessage")[0].innerHTML = messageList[i].text;

        }
    }
}

function funBtnDelete(elem) {
    var parent = elem.parentNode;
    $.ajax({
        method: "DELETE",
        url: appState.mainUrl,
        data: JSON.stringify({id: parent.id, text: "", author: "not important"})
    });
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

    $.ajax({
        method: "PUT",
        url: appState.mainUrl,
        data: JSON.stringify({id: nowID, text: text.value, author: document.getElementById("name").value})
    });

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

function setServerStatusBad () {
    if ($("#serverStatus").hasClass("serverGoodStatus")) {
        $("#serverStatus").removeClass("serverGoodStatus").addClass("serverBadStatus").html("Server is not available!");
    }
}

function setServerStatusGood() {
    if ($("#serverStatus").hasClass("serverBadStatus")) {
        $("#serverStatus").removeClass("serverBadStatus").addClass("serverGoodStatus").html("Server is available!");
    }
}

function initDialog () {
    $.get(url, function(data, textStatus, xhr) {

        if(xhr.status != 200) {
            if (xhr.status != 304) {
                continueWithError('Error on the server side, response ' + xhr.status);
                setServerStatusBad();
                return;
            } else {
                setServerStatusGood();
            }
        } else {
            setServerStatusGood();
            writeAll(data.messages);
        }
    });
}

function restore() {
    $.ajax({url: url, success: function(data, textStatus) {
        setServerStatusGood();
        if (data.action == "ADD") {
            writeAll(data.messages);
        } else if (data.action == "DELETE") {
            deleteUIMessage(data.messages);
        } else if (data.action == "CHANGE") {
            changeUIMessage(data.messages);
        }
    },
        error: function() {
                    setServerStatusBad();
        }, dataType: "json", complete: restore, timeout: 300000});
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

function continueWithError(strErr) {
    console.log(strErr);
}
