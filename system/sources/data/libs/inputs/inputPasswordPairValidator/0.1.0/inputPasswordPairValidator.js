/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

const inputPasswordPairValidator = ({ element = null, repetitionElement = null, onValid = null, onNotValid = null, onKeyPress = null } = {}) => {

  const validateOptions = () => {
    if (element === null) {
      throw Error('You must define a input password element to apply the validator.');
    }
    if (repetitionElement === null) {
      throw Error('You must define a input password repetitionElement to apply the validator.');
    }
  };
  const createGUI = () => {
    element.className = 'inputPasswordPairValidator';
    repetitionElement.className = 'inputPasswordPairValidator';
  };
  const assignTriggers = () => {
    element.addEventListener('response', event => {
      const {detail} = event;
      const {data} = detail;

      const messages = data.messages;
      element.classList.remove('error');
      messages.forEach(message => {
        Core.showMessage(message);
        if (message.type === 'ERROR') {
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
    });
    element.addEventListener("keypress", event => {
      if (Core.isFunction(onKeyPress)) {
        onKeyPress(event);
      }
    });
    repetitionElement.addEventListener("keypress", event => {
      if (Core.isFunction(onKeyPress)) {
        onKeyPress(event);
      }
    });
    const keyUpEvent = event => {
      if (Core.isModifierKey(event) || Core.isNavigationKey(event)) {
        return;
      }
      sendVerificationRequest();
    };
    element.addEventListener("keyup", event => {
      keyUpEvent(event);
    });
    repetitionElement.addEventListener("keyup", event => {
      keyUpEvent(event);
    });
  };
  const sendVerificationRequest = () => {
    Core.sendPost(`/api/v1/password/pair/validate`, element, {password: btoa(element.value), repetitionPassword: btoa(repetitionElement.value)});
  };
  validateOptions();
  createGUI();
  assignTriggers();
}
;