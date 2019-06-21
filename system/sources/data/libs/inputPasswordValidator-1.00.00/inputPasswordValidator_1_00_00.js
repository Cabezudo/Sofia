/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

const inputPasswordValidator_1_00_00 = ({ element = null, onValid = null, onNotValid = null, onKeyPress = null } = {}) => {
  let messageId = 0;
  let verificationTimer;

  const getNextMessageId = () => {
    return ++messageId;
  };

  const validateOptions = () => {
    if (element === null) {
      throw Error('You must define an element to apply the validator.');
    }
  };
  const createGUI = () => {
    element.className = 'inputPasswordValidator_1_00_00';
  };
  const assignTriggers = () => {
    element.addEventListener('response', event => {
      const data = event.detail;

      if (data.messageId === messageId) {
        Core.cleanMessagesContainer();
        const messages = event.detail.messages;
        element.classList.remove('error');
        messages.forEach(message => {
          Core.addMessage(message);
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
      const messageId = getNextMessageId();
      if (verificationTimer) {
        clearTimeout(verificationTimer);
      }
      verificationTimer = setTimeout(sendVerificationRequest, Core.EVENT_TIME_DELAY);
    });
  };
  const sendVerificationRequest = () => {
    Core.sendPost(`/api/v1/password/validate`, element, messageId, {password: btoa(element.value)});
  };
  validateOptions();
  createGUI();
  assignTriggers();
}
;