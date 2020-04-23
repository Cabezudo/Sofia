/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global fetch */
/* global postData */
/* global variables */

'use strict';
const Core = {
  messagesContainer: null,
  requestId: 0,
  onloadFunctions: [],
  setPageFunctions: [],
  screenBlockerDiv: null,
  loaderDiv: null,
  pageParameters: new URLSearchParams(location.search),
  lastSection: null,
  showMessage: (messageObject) => {
    if (Core.messagesContainer !== null) {
      Core.trigger(Core.messagesContainer, 'showMessage', messageObject);
    } else {
      throw new Error('No messages container defined.');
    }
  },
  addOnloadFunction: (func) => {
    Core.onloadFunctions.push(func);
  },
  addSetFunction: (func) => {
    Core.setPageFunctions.push(func);
  },
  changeSection: section => {
    if (Core.lastSection !== null) {
      Core.hide(Core.lastSection);
      Core.show(section);
    }
  },
  cleanMessagesContainer: () => {
    if (Core.messagesContainer) {
      Core.trigger(Core.messagesContainer, 'cleanMessages');
    }
  },
  getNextRequestId: () => {
    return Core.requestId++;
  },
  getRequestId: () => {
    return Core.requestId;
  },
  getURLParameterByName: (name, url) => {
    if (!url) {
      url = window.location.href;
    }
    name = name.replace(/[\[\]]/g, '\\$&');
    const regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)');
    const results = regex.exec(url);
    if (!results) {
      return null;
    }
    if (!results[2]) {
      return '';
    }
    return decodeURIComponent(results[2].replace(/\+/g, ' '));
  },
  hide: (id) => {
    const element = typeof id === 'string' ? document.getElementById(id) : id;
    element.hidden = true;
    Core.trigger(element, 'hide');
//    if (!element.style.display) {
//      element.setAttribute('lastDisplay', element.style.display);
//    }
//    element.style.display = 'none';
  },
  inFocus: element => {
    return element === document.activeElement;
  },
  isEnter: event => {
    return event.key === 'Enter';
  },
  isFunction: v => {
    return Object.prototype.toString.call(v) === '[object Function]';
  },
  isLogged: () => {
    return !Core.isNotLogged();
  },
  isModifierKey: event => {
    const key = event.key;
    switch (key) {
      case "Alt":
      case "AltGraph":
      case "CapsLock":
      case "Control":
      case "Fn":
      case "FnLock":
      case "Hyper":
      case "Meta":
      case "NumLock":
      case "ScrollLock":
      case "Shift":
      case "Super":
      case "Symbol":
      case "SymbolLock":
        return true;
      default:
        return false;
    }
  },
  isNavigationKey: event => {
    const key = event.key;
    switch (key) {
      case 'Up':
      case 'ArrowUp':
      case 'Right':
      case 'ArrowRight':
      case 'Down':
      case 'ArrowDown':
      case 'Left':
      case 'ArrowLeft':
      case 'End':
      case 'Home':
      case 'PageDown':
      case 'PageUp':
      case 'Tab':
        return true;
      default:
        return false;
    }
  },
  isNotLogged: () => {
    return variables.user === null;
  },
  isRightLeft: event => {
    return (typeof event === 'object' && event.button === 0);
  },
  isRightClick: event => {
    return (typeof event === 'object' && event.button === 2);
  },
  isTouchStart: event => {
    return true;
  },
  isVisible: element => {
    return !element.hidden;
  },
  removeChilds: element => {
    while (element.firstChild) {
      element.removeChild(element.firstChild);
    }
  },
  screenBlocker: {
    create: () => {
      if (!Core.screenBlockerDiv) {
        Core.screenBlockerDiv = document.getElementById('screenBlocker');
        if (!Core.screenBlockerDiv) {
          Core.screenBlockerDiv = document.createElement("div");
          Core.screenBlockerDiv.id = 'screenBlocker';
          Core.screenBlockerDiv.style.position = "absolute";
          Core.screenBlockerDiv.style.top = "0px";
          Core.screenBlockerDiv.style.width = "100vw";
          Core.screenBlockerDiv.style.height = "100vh";
          Core.screenBlockerDiv.style.backgroundColor = "gray";
          Core.screenBlockerDiv.style.opacity = ".7";
          Core.screenBlockerDiv.focus();
          document.body.appendChild(Core.screenBlockerDiv);
        }
      } else {
        Core.screenBlockerDiv.style.display = "block";
      }
    },
    block: () => {
      Core.screenBlocker.create();
      Core.screenBlockerDiv.style.display = "block";
    },
    unblock: (options) => {
      Core.screenBlocker.create();
      Core.screenBlockerDiv.style.display = "none";
      if (options && options.focus) {
        options.focus.focus();
      }
    }
  },
  sendGet: (url, origin) => {
    if (!origin) {
      throw new Error(`Invalid origin for sendGet: ${origin}`);
    }
    const requestId = Core.getNextRequestId();
    fetch(url, {
      headers: {
        "Content-Type": "application/json",
        "RequestId": requestId
      }
    })
            .then(function (response) {
              const headers = response.headers;
              const requestId = parseInt(headers.get('RequestId'));
              response.json().then(jsonData => {
                jsonData.requestId = requestId;
                Core.trigger(origin, 'response', jsonData);
              });
            })
            ;
    return {requestId};
  },
  sendPost: (url, origin, formObject) => {
    if (!origin) {
      throw new Error(`Invalid origin for sendGet: ${origin}`);
    }
    const requestId = Core.getNextRequestId();
    fetch(url, {
      method: "POST",
      cache: "no-cache",
      credentials: "same-origin",
      headers: {
        "Content-Type": "application/json",
        "RequestId": requestId
      },
      redirect: "follow",
      body: JSON.stringify(formObject)
    })
            .then(function (response) {
              const headers = response.headers;
              const requestId = parseInt(headers.get('RequestId'));
              response.json().then(jsonData => {
                jsonData.requestId = requestId;
                Core.trigger(origin, 'response', jsonData);
              });
            })
            ;
    return {requestId};
  },
  sendPut: (url, origin, formObject) => {
    if (!origin) {
      throw new Error(`Invalid origin for sendGet: ${origin}`);
    }
    const requestId = Core.getNextRequestId();
    fetch(url, {
      method: "PUT",
      cache: "no-cache",
      credentials: "same-origin",
      headers: {
        "Content-Type": "application/json",
        "RequestId": requestId
      },
      redirect: "follow",
      body: JSON.stringify(formObject)
    })
            .then(function (response) {
              const headers = response.headers;
              const requestId = parseInt(headers.get('RequestId'));
              response.json().then(jsonData => {
                jsonData.requestId = requestId;
                Core.trigger(origin, 'response', jsonData);
              });
            })
            ;
    return {requestId};
  },
  setDefaultMessage: message => {
    if (Core.messagesContainer !== null) {
      Core.trigger(Core.messagesContainer, 'setDefaultMessage', message);
    } else {
      throw new Error('No messages container defined.');
    }
  },
  setMessagesContainer: target => {
    Core.messagesContainer = typeof target === 'string' ? document.getElementById(target) : target;
    console.log(`Core : setMessagesContainer : Set to ${Core.messagesContainer.id}`);
  },
  show: (id) => {
    const element = typeof id === 'string' ? document.getElementById(id) : id;
    element.hidden = false;
    Core.trigger(element, 'show');
//    const display = element.getAttribute('lastDisplay');
//    if (display) {
//      element.style.display = display;
//    } else {
//      element.style.display = '';
//    }
  },
  trigger: (target, eventName, detail) => {
    console.log(`Core : trigger : Event ${eventName} to ${target.id} using data ${JSON.stringify(detail)}`);
    const event = new CustomEvent(eventName, {detail});
    target.dispatchEvent(event);
  },
  validateById: (id) => {
    if (id === null) {
      throw new Error(`You must specify a valid id: ${id}`);
    }
    const element = document.getElementById(id);
    if (element === null) {
      throw new Error(`Can't find the element with the id ${id}.`);
    }
    return element;
  },
  validateElement: (element) => {
    if (element === null) {
      throw new Error(`The parameter element is null.`);
    }
    if (!element.tagName) {
      throw new Error(`The node is not an element.`);
    }
    return element;
  },
  validateIdOrElement: (id, element) => {
    if (id === null && element === null) {
      throw new Error(`You must specify an id or element.`);
    }
    if (id !== null && element !== null && element.id !== id) {
      throw new Error(`The element and the id don't belong to the same element.`);
    }
    if (id !== null) {
      return Core.validateById(id);
    }
    if (element !== null) {
      return Core.validateElement(element);
    }
  }
};

window.onload = () => {
  Core.onloadFunctions.forEach(func => {
    func();
  });
  Core.setPageFunctions.forEach(func => {
    func();
  });
  if (Core.pageParameters.has('section')) {
    Core.changeSection(Core.pageParameters.get('section'));
  }
  document.body.style.opacity = "1";
};
