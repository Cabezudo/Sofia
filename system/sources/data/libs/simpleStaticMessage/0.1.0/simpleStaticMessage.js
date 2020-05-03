/*
 * Created on: 29/10/2019
 * Author:     Esteban Cabezudo
 */

/* global Core */

const simpleStaticMessage = ({ id = null, element = null, onShow = null } = {}) => {
  let
          defaultMessage,
          messageContainer;
  const validateOptions = () => {
    element = Core.validateIdOrElement(id, element);
  };
  const createGUI = () => {
    element.className = 'simpleStaticMessage';
    Core.setMessagesContainer(element);
    defaultMessage = element.innerHTML;
    Core.removeChilds(element);
    messageContainer = document.createElement('div');
    messageContainer.innerText = defaultMessage;
    element.appendChild(messageContainer);
  };
  const clearMessage = () => {
    console.log('simpleStaticMessage : clearMessage.');
    messageContainer.innerText = defaultMessage;
    element.classList.remove('red');
    element.classList.remove('green');
  };
  const assignTriggers = () => {
    element.addEventListener('cleanMessages', () => {
      clearMessage();
    });
    element.addEventListener('setDefaultMessage', event => {
      defaultMessage = event.detail;
      console.log(`simpleStaticMessage : trigger: setDefaultMessage : ${defaultMessage}`);
      messageContainer.innerText = defaultMessage;
    });
    element.addEventListener('showMessage', event => {
      const payload = event.detail;
      console.log(`simpleStaticMessage : trigger : showMessage : ${JSON.stringify(payload)}`);
      switch (payload.status) {
        case 'ERROR':
          messageContainer.innerText = payload.message;
          element.classList.remove('ok');
          element.classList.remove('message');
          element.classList.add('error');
          break;
        case 'OK':
          messageContainer.innerText = payload.message;
          element.classList.remove('error');
          element.classList.remove('message');
          element.classList.add('ok');
          break;
        case 'MESSAGE':
          messageContainer.innerText = payload.message;
          element.classList.remove('ok');
          element.classList.remove('error');
          element.classList.add('message');
          break;
        default:
          throw new Error(`Invalid status: ${payload.status}`);
      }
      if (Core.isFunction(onShow)) {
        onShow();
      }
    });
  };
  validateOptions();
  createGUI();
  assignTriggers();
  return element;
};
