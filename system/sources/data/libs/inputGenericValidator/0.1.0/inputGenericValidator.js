/*
 * Created on: 26/02/2020
 * Author:     Esteban Cabezudo
 */

/* global Core */

const inputGenericValidator = ({ element = null, id = null, getValidationURL = null, onValid = null, onNotValid = null, onKeyPress = null, onFocus = null } = {}) => {
  let verificationTimer;
  let requestId = 0;

  const validateOptions = () => {
    if (element === null && id === null) {
      throw Error('You must define a property id or a property element.');
    }
    if (getValidationURL === null && Core.isFunction(getValidationURL)) {
      throw Error('You must set a function that returns a validation url.');
    }
  };
  const createGUI = () => {
    if (element === null) {
      element = Core.validateById(id);
    }
    element.className = 'inputGenericValidator';
  };
  const assignTriggers = () => {
    element.addEventListener('response', event => {
      const data = event.detail;
      const element = event.srcElement;
      if (requestId === data.requestId) {
        const data = event.detail;
        data.elementId = element.id;
        if (data.status === 'ERROR') {
          element.classList.add('error');
          if (Core.isFunction(onNotValid)) {
            onNotValid(data);
          }
        }
        if (data.status === 'OK') {
          element.classList.remove('error');
          if (Core.isFunction(onValid)) {
            onValid(data);
          }
        }
      }
    });
    element.addEventListener("focus", event => {
      if (Core.isFunction(onFocus)) {
        onFocus(event);
      }
    });
    element.addEventListener("keyup", event => {
      if (Core.isModifierKey(event) || Core.isNavigationKey(event)) {
        return;
      }
      if (verificationTimer !== undefined) {
        clearTimeout(verificationTimer);
      }
      verificationTimer = setTimeout(() => {
        sendValidationRequest(element);
      }, 500);
    });
    element.addEventListener("type", event => {
      element.value = event.detail;
      sendValidationRequest(element);
    });
  };
  const sendValidationRequest = element => {
    if (element.value !== null && element.value !== undefined) {
      const name = element.value.trim();
      console.log(`inputGenericValidator : sendValidationRequest : send GET with ${getValidationURL()}`);
      const response = Core.sendGet(getValidationURL(), element);
      requestId = response.requestId;
    }
  };

  validateOptions();
  createGUI();
  assignTriggers();
}
;