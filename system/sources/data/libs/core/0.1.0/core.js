/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */
/* global fetch */
/* global postData */
/* global variables */
/* global Node */
/* global templateVariables */

'use strict';
const Core = {
  messagesContainer: null,
  requestId: 0,
  resizeTimer: null,
  onSetLanguageFunctions: [],
  onCreateFunctions: [],
  onLoadFunctions: [],
  onResizeFunctions: [],
  setPageFunctions: [],
  scrollElements: [],
  onScrollFunctions: [],
  testFunctions: [],
  screenBlockerDiv: null,
  loaderDiv: null,
  queryParameters: new URLSearchParams(location.search),
  lastSection: null,
  showMessage: (messageObject) => {
    if (Core.messagesContainer !== null) {
      Core.trigger(Core.messagesContainer, 'showMessage', messageObject);
    } else {
      throw new Error('No messages container defined.');
    }
  },
  addOnSetLanguageFunction: (func) => {
    Core.onSetLanguageFunctions.push(func);
  },
  addOnCreateFunction: (func) => {
    Core.onCreateFunctions.push(func);
  },
  addOnloadFunction: (func) => {
    Core.onLoadFunctions.push(func);
  },
  addOnResizeFunction: (func) => {
    Core.onResizeFunctions.push(func);
  },
  addOnScrollElement: (element) => {
    Core.scrollElements.push(element);
    element.addEventListener('scroll', e => {
      Core.onScrollFunctions.forEach(func => {
        func();
      });
    });
  },
  addOnScrollFunction: (func) => {
    Core.onScrollFunctions.push(func);
  },
  addSetFunction: (func) => {
    Core.setPageFunctions.push(func);
  },
  addTexts: texts => {
    variables.texts = Object.assign(variables.texts, texts);
    Core.onSetLanguageFunctions.forEach(func => {
      func(variables.site.language);
    });
  },
  getURLForLanguage: language => {
    let pathname = window.location.pathname.toLowerCase();
    if (pathname.endsWith('/')) {
      pathname += 'index.html';
    }
    const pageName = pathname.replace('.html', '').replaceAll('/', '.');
    return `/api/v1/sites/${variables.site.id}/pages/${pageName}/texts/${language}`;
  },
  changeLanguageTo: language => {
    Core.sendGet(Core.getURLForLanguage(language), response => {
      const texts = response.data;
      variables.site.language = language;
      Core.addTexts(texts);
    });
  },
  loadLanguage: parameter => {
    Core.sendGet(Core.getURLForLanguage(variables.site.language), response => {
      const texts = response.data;
      if (Core.isString(parameter)) {
        variables.site.language = parameter;
      }
      Core.addTexts(texts);
      if (Core.isFunction(parameter)) {
        parameter();
      }
    });
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
  createOverlay: id => {
    const overlay = document.createElement('DIV');
    overlay.id = id;
    overlay.className = 'overlay';
  },
  disable: element => {
    Core.trigger(element, 'disabled');
  },
  enable: element => {
    Core.trigger(element, 'enabled');
  },
  getGeoLocation: async () => {
    if (navigator.geolocation) {
      try {
        const getCurrentPositionPromiseFunction = (resolve, reject) => {
          navigator.geolocation.getCurrentPosition(resolve, reject);
        };
        const locationData = await new Promise(getCurrentPositionPromiseFunction);
        return {
          status: "OK",
          message: "OK",
          code: 0,
          data: locationData
        };
      } catch (error) {
        return {
          status: "ERROR",
          message: error.message,
          code: error.code,
          data: null
        };
      }
    } else {
      console.log("*** get end without support");
      return {
        status: "NOT_SUPPORTED",
        message: "Geolocation is not supported by this browser.",
        code: 0,
        data: null
      }
      ;
    }
  },
  getNextRequestId: () => {
    return Core.requestId++;
  }
  ,
  getRequestId: () => {
    return Core.requestId;
  },
  getText: (key, values) => {
    if (!key) {
      console.trace();
      throw new Error(`Missing parameter key.`);
    }
    let text = variables.texts[key];
    if (!text) {
      throw new Error(`Key not found searching texts: ${key}.`);
    }
    if (values) {
      if (Core.isArray(values)) {
        let i = 0;
        values.forEach(value => {
          text = text.replaceAll(`{${i}}`, value);
          i++;
        });
      } else {
        throw new Error('The second parameter MUST be an array');
      }
    }
    return text;
  },
  getTimezoneOffset: () => {
    return  (new Date()).getTimezoneOffset();
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
    Core.trigger(window, 'hide', {id});
//    if (!element.style.display) {
//      element.setAttribute('lastDisplay', element.style.display);
//    }
//    element.style.display = 'none';
  },
  inFocus: element => {
    return element === document.activeElement;
  },
  isArray: v => {
    return Object.prototype.toString.call(v) === '[object Array]';
  },
  isEnter: event => {
    return event.key === 'Enter';
  },
  isDIV: o => {
    return o !== null && o.tagName === 'DIV';
  },
  isObject: v => {
    return Object.prototype.toString.call(v) === '[object Object]';
  },
  isString: v => {
    return Object.prototype.toString.call(v) === '[object String]';
  },
  isTab: event => {
    return event.key === 'Tab';
  },
  isFunction: v => {
    return Object.prototype.toString.call(v) === '[object Function]';
  },
  isVisibleInScreen: element => {
    // TODO verificar cuando la imagen se debería de ver en la pantalla pero es mas grande y agregar el viewport
    const elementBounding = element.getBoundingClientRect();
    const viewPortTop = 0;
    const viewPortRight = window.innerWidth;
    const viewPortBottom = window.innerHeight;
    const viewPortLeft = 0;
    const pointInside = (x, y) => {
      return x >= viewPortLeft && x <= viewPortRight && y >= viewPortTop && y <= viewPortBottom;
    };
    const top = elementBounding.top;
    const right = elementBounding.right;
    const bottom = elementBounding.bottom;
    const left = elementBounding.left;
    if (pointInside(right, top))
      return true;
    if (pointInside(left, top))
      return true;
    if (pointInside(right, bottom))
      return true;
    if (pointInside(left, bottom))
      return true;
    return;
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
  sendGet: (url, targetElement) => {
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
              response.text().then(text => {
                let jsonData;
                try {
                  if (!text) {
                    throw new Error(`Empty response.`);
                  }
                  jsonData = {
                    requestId,
                    data: JSON.parse(text)
                  };
                  do {
                    if (targetElement) {
                      if (Core.isFunction(targetElement)) {
                        targetElement(jsonData);
                        break;
                      } else {
                        Core.trigger(targetElement, 'response', jsonData);
                        break;
                      }
                    }
                  } while (false);
                } catch (error) {
                  console.trace();
                  console.log(`%cCore : sendGet : ${error.message}\n${text}`, 'color: red');
                }
              })
                      ;
            })
            ;
    return {requestId}
    ;
  },
  sendDelete: (url, targetElement) => {
    const requestId = Core.getNextRequestId();
    fetch(url, {
      method: "DELETE",
      cache: "no-cache",
      credentials: "same-origin",
      headers: {
        "Content-Type": "application/json",
        "RequestId": requestId
      },
      redirect: "follow"
    })
            .then(function (response) {
              const headers = response.headers;
              const requestId = parseInt(headers.get('RequestId'));
              response.text().then(text => {
                let jsonData;
                try {
                  if (!text) {
                    throw new Error(`Empty response.`);
                  }
                  jsonData = {
                    requestId,
                    data: JSON.parse(text)
                  };
                  do {
                    if (Core.isFunction(targetElement)) {
                      targetElement(jsonData);
                      break;
                    }
                    if (targetElement) {
                      Core.trigger(targetElement, 'response', jsonData);
                      break;
                    }
                  } while (false);
                } catch (error) {
                  console.log(`%cCore : sendDelete : ${error.message}\n${text}`, 'color: red');
                }
              })
                      ;
            })
            ;
    return {requestId}
    ;
  },
  sendPost: (url, targetElement, data) => {
    const requestId = Core.getNextRequestId();
    let body;
    if (typeof data === 'object') {
      body = JSON.stringify(data);
    } else {
      body = data;
    }

    fetch(url, {
      method: "POST",
      cache: "no-cache",
      credentials: "same-origin",
      headers: {
        "Content-Type": "application/json",
        "RequestId": requestId
      },
      redirect: "follow",
      body
    })
            .then(function (response) {
              if (response.status === 200) {
                const headers = response.headers;
                const requestId = parseInt(headers.get('RequestId'));
                response.text().then(text => {
                  let jsonData;
                  try {
                    if (!text) {
                      throw new Error(`Empty response.`);
                    }
                    jsonData = {
                      requestId,
                      data: JSON.parse(text)
                    };
                    do {
                      if (Core.isFunction(targetElement)) {
                        targetElement(jsonData);
                        break;
                      }
                      if (targetElement) {
                        Core.trigger(targetElement, 'response', jsonData);
                        break;
                      }
                    } while (false);
                  } catch (error) {
                    console.log(`%cCore : sendPost : ${error.message}\n${text}`, 'color: red');
                  }
                });
              }
            })
            ;
    return {requestId};
  },
  sendPut: (url, targetElement, data) => {
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
      body: JSON.stringify(data)
    })
            .then(function (response) {
              const headers = response.headers;
              const requestId = parseInt(headers.get('RequestId'));
              response.text().then(text => {
                let jsonData;
                try {
                  if (!text) {
                    throw new Error(`Empty response.`);
                  }
                  jsonData = {
                    requestId,
                    data: JSON.parse(text)
                  };
                  do {
                    if (Core.isFunction(targetElement)) {
                      targetElement(jsonData);
                      break;
                    }
                    if (targetElement) {
                      Core.trigger(targetElement, 'response', jsonData);
                      break;
                    }
                  } while (false);
                } catch (error) {
                  console.log(`%cCore : sendPut : ${error.message}\n${text}`, 'color: red');
                }
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
  setSessionMessage: message => {
    console.log(`*** ${JSON.stringify(message)}`);
    Core.sendPost(`/api/v1/messages/session`, null, message);
  },
  setTextsFor: values => {
    console.log(`Core : setTextsFor : Set text for ${values}`);
    values.forEach(o => {
      if (Object.prototype.toString.call(o) === '[object String]') {
        const id = o;
        const element = document.getElementById(id);
        if (element) {
          const text = variables.texts[id];
          if (text) {
            element.innerHTML = text;
          } else {
            console.warn(`No text found for the key ${id}`);
          }
        } else {
          console.warn(`No element found with the id '${id}'`);
        }
      }
      if (Object.prototype.toString.call(o) === '[object Object]') {
        const object = o;
        if (Core.isFunction(object.setText)) {
          const id = object.getId();
          const text = variables.texts[id];
          if (text) {
            object.setText(text);
          } else {
            console.warn(`No text found for the key ${id}`);
          }
        }
      }
    });
  },
  show: (id) => {
    const element = typeof id === 'string' ? document.getElementById(id) : id;
    element.hidden = false;
    Core.trigger(element, 'show');
    Core.trigger(window, 'show', {id});
  },
  tests: {
    add: testFunction => {
      Core.testFunctions.push(testFunction);
    }
  },
  trigger: (targetElement, eventName, detail) => {
    console.log(`Core : trigger : Event ${eventName} to ${targetElement.id} ${detail === undefined ? 'with no data' : `using data ${JSON.stringify(detail)}`}.`);
    const event = new CustomEvent(eventName, {detail});
    targetElement.dispatchEvent(event);
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
    if (!id && !element) {
      throw new Error(`You must specify an id or element.`);
    }
    if (id && element && element.id !== id) {
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
window.onresize = () => {
  resizeTimer = setTimeout(() => {
    clearTimeout(resizeTimer);
    Core.onResizeFunctions.forEach(func => {
      func();
    });
  }, 500);
};

const pageLoaded = () => {
  console.log(`Core :: pageLoaded :: Page loaded.`);
  Core.onCreateFunctions.forEach(func => {
    func();
  });
  Core.onLoadFunctions.forEach(func => {
    console.log(`Run on load function ${func.name}`);
    func();
  });
  Core.setPageFunctions.forEach(func => {
    func();
  });
  console.log(`Core :: pageLoaded :: templateVariables: `, templateVariables);

  console.log(`Core :: startPage`);
  if (Core.queryParameters.has('section')) {
    Core.changeSection(Core.queryParameters.get('section'));
  }
  Core.testFunctions.forEach(func => {
    func();
  });

  const loadLanguageCallBack = () => {
    console.log(`Core :: startPage :: loadLanguageCallBack`);
    document.body.style.opacity = "1";
    if (variables.message !== null) {
      Core.showMessage(variables.message);
    }
  };
  Core.loadLanguage(loadLanguageCallBack);
};

window.addEventListener("load", pageLoaded, false);
