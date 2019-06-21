const WEB_SOCKET_URL = null;

const onloadFunctionList = [];
const onresizeFunctionList = [];

let webSocketURL;
let webSocketAvailable = false;

const webSocketSupport =
  ("WebSocket" in window && window.WebSocket != undefined) || ("MozWebSocket" in window && navigator.userAgent.indexOf("Android") === -1);

const onload = functionToRun => {
  onloadFunctionList.push(functionToRun);
};

const onresize = functionToRun => {
  onresizeFunctionList.push(functionToRun);
};

const openWebsocket = () => {
  if (WEB_SOCKET_URL === null) {
    const location = window.location;
    let port = "";
    if (location.port !== "") {
      port = `:${location.port}`;
    }
    webSocketURL = `wss://${location.host}${port}`;
  } else {
    webSocketURL = WEB_SOCKET_URL;
  }

  try {
    socket = new WebSocket(webSocketURL);
    socket.onopen = () => {};
    socket.onerror = error => {
      webSocketAvailable = false;
    };
    socket.onmessage = message => {
      console.info("Message: %o", message.data);
    };
    webSocketAvailable = socket.readyState === WebSocket.OPEN;
  } catch (e) {
    webSocketAvailable = false;
  }
};

window.onresize = event => {
  onresizeFunctionList.forEach(functionToRun => {
    functionToRun(event);
  });
};

window.onload = event => {
  openWebsocket();

  onloadFunctionList.forEach(functionToRun => {
    functionToRun(event);
  });
};
