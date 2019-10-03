/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

const editableElement_1_00 = ({ element = null, id = null, uri = null, field = null, value = null, onValid = null, onNotValid = null } = {}) => {
  let inputElement, lastValue, validationTimer;

  const validateOptions = () => {
    if (element === null && id === null) {
      throw Error('You must define a property id or a property element.');
    }
    if (uri === null) {
      throw Error('You must set a web services URI.');
    }
    if (field === null) {
      throw Error('You must add a record field to associate.');
    }
  };
  const createGUI = () => {
    if (element === null) {
      element = Core.validateById(id);
    }
    element.classList.add('editableElement-1_00');
    inputElement = document.createElement('input');
    inputElement.setAttribute('type', 'text');
    inputElement.value = value;
    lastValue = value;
    element.appendChild(inputElement);
    element.inputElement = inputElement;
    inputElement.data = {uri, field};
  };
  const assignTriggers = () => {
    element.addEventListener('response', event => {
      const data = event.detail;

      if (data.messageId === Core.getRequestId()) {
        Core.cleanMessagesContainer();
        const messages = event.detail.messages;
        element.classList.remove('error');
        messages.forEach(message => {
          Core.addMessage(message);
          if (message.status === 'ERROR') {
            element.classList.add('error');
          }
          if (message.status === 'VALID') {
            if (Core.isFunction(onValid)) {
              onValid();
            }
          } else {
            if (Core.isFunction(onNotValid)) {
              onNotValid();
            }
          }
        });
      }
    });
    const update = event => {
      if (Core.isModifierKey(event) || Core.isNavigationKey(event)) {
        return;
      }
      if (lastValue !== inputElement.value) {
        lastValue = inputElement.value;
        if (validationTimer) {
          clearTimeout(validationTimer);
        }
        const sendValidationRequest = event => {
          const inputElement = event.srcElement;
          const data = {
            field: inputElement.data.field,
            value: inputElement.value
          };
          Core.sendPut(uri, inputElement, data);
        };
        validationTimer = setTimeout(sendValidationRequest(event), Core.EVENT_TIME_DELAY);
      }
    };
    sendData = event => {
      const source = event.srcElement;
      const data = source.data;
      console.log(data);
    };
    element.addEventListener("keyup", event => {
      update(event);
    });
  };
  validateOptions();
  createGUI();
  assignTriggers();
}
;