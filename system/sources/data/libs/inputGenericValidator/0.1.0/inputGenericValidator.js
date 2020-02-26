/*
 * Created on: 26/02/2020
 * Author:     Esteban Cabezudo
 */

/* global Core */

const inputGenericValidator = ({ element = null, id = null, getValidationURL = null, onValid = null, onNotValid = null, onKeyPress = null } = {}) => {
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
    if (element.value && element.value.length > 0) {
      sendValidationRequest(element);
    }
  };
  const assignTriggers = () => {
    element.addEventListener('response', event => {
      const data = event.detail;

      const element = event.srcElement;
      if (requestId === data.requestId) {
        const data = event.detail;
        data.elementId = element.id;
        Core.showMessage(data);
        if (data.status === 'ERROR') {
          element.classList.add('error');
        }
        if (data.status === 'OK') {
          element.classList.remove('error');
          if (Core.isFunction(onValid)) {
            onValid();
          }
        } else {
          if (Core.isFunction(onNotValid)) {
            onNotValid();
          }
        }
      }
    });
    element.addEventListener("keypress", event => {
      if (Core.isFunction(onKeyPress)) {
        onKeyPress(event);
      }
    });
    element.addEventListener("keyup", event => {
      if (Core.isModifierKey(event) || Core.isNavigationKey(event)) {
        return;
      }
      if (verificationTimer) {
        clearTimeout(verificationTimer);
      }
      verificationTimer = setTimeout(sendValidationRequest(element), Core.EVENT_TIME_DELAY);
    });
  };
  const sendValidationRequest = element => {
    const response = Core.sendGet(getValidationURL(), element);
    requestId = response.requestId;
  };

  validateOptions();
  createGUI();
  assignTriggers();
}
;