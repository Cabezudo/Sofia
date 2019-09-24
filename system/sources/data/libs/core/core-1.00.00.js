/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global fetch */
/* global postData */

'use strict';
const Core = {
  onloadFunctions: [],
  messagesContainer: null,
  EVENT_TIME_DELAY: 300,
  screenBlockerDiv: null,
  addMessage: message => {
    if (Core.messagesContainer) {
      Core.trigger(Core.messagesContainer, 'add', message);
    }
  },
  addOnloadFunction: (func) => {
    Core.onloadFunctions.push(func);
  },
  cleanMessagesContainer: () => {
    Core.removeChilds(Core.messagesContainer);
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
//    if (!element.style.display) {
//      element.setAttribute('lastDisplay', element.style.display);
//    }
//    element.style.display = 'none';
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
        return true;
      default:
        return false;
    }
  },
  isNotLogged: () => {
    console.log(`variables.user: ${variables.user}`);
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
          Core.screenBlockerDiv.style.background = "gray";
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
  sendGet: (url, origin, messageId) => {
    if (!origin) {
      throw new Error(`Invalid origin for sendGet: ${origin}`);
    }
    fetch(url)
            .then(function (response) {
              response.json().then(jsonData => {
                jsonData.messageId = messageId;
                Core.trigger(origin, 'response', jsonData);
              });
            })
            ;
  },
  sendPost: (url, origin, messageId, formObject) => {
    fetch(url, {
      method: "POST",
      mode: "no-cors",
      cache: "no-cache",
      credentials: "same-origin",
      headers: {
        "Content-Type": "application/json"
      },
      redirect: "follow",
      referrer: "no-referrer",
      body: JSON.stringify(formObject)
    })
            .then(function (response) {
              response.json().then(jsonData => {
                jsonData.messageId = messageId;
                Core.trigger(origin, 'response', jsonData);
              });
            })
            ;
  },
  setMessagesContainer: target => {
    Core.messagesContainer = typeof target === 'string' ? document.getElementById(target) : target;
  },
  show: (id) => {
    const element = typeof id === 'string' ? document.getElementById(id) : id;
    element.hidden = false;
//    const display = element.getAttribute('lastDisplay');
//    if (display) {
//      element.style.display = display;
//    } else {
//      element.style.display = '';
//    }
  },
  trigger: (target, eventName, message) => {
    const event = new CustomEvent(eventName, {detail: message});
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
};
