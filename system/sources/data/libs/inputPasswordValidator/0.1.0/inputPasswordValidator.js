/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

const inputPasswordValidator = ({ element = null, onValid = null, onNotValid = null, onKeyPress = null } = {}) => {
  let requestId = 0;
  let verificationTimer;

  const validateOptions = () => {
    if (element === null) {
      throw Error('You must define an element to apply the validator.');
    }
  };
  const createGUI = () => {
    element.className = 'inputPasswordValidator';
  };
  const assignTriggers = () => {
    element.addEventListener('response', event => {
      const data = event.detail;

      const element = event.srcElement;
      if (requestId === data.requestId) {
        Core.cleanMessagesContainer();
        data.elementId = element.id;
        if (data.status === 'ERROR') {
          Core.addMessage(data);
          element.classList.add('error');
        }
        if (data.status === 'OK') {
          element.classList.remove('error');
          Core.addMessage(data);
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
      verificationTimer = setTimeout(sendVerificationRequest, 300);
    });
  };
  const sendVerificationRequest = () => {
    const response = Core.sendPost(`/api/v1/password/validate`, element, {password: btoa(element.value)});
    requestId = response.requestId;
  };
  validateOptions();
  createGUI();
  assignTriggers();
}
;