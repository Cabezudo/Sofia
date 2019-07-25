/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

const inputEMailValidator_1_00_00 = ({ element = null, onValid = null, onNotValid = null, onKeyPress = null } = {}) => {
  let verificationTimer;
  let messageId = 0;

  const getNextMessageId = () => {
    return ++messageId;
  };

  const validateOptions = () => {
    if (element === null) {
      throw Error('You must define an element to apply the validator.');
    }
  };
  const createGUI = () => {
    element.className = 'inputEMailValidator_1_00_00';
    if (element.value && element.value.length > 0) {
      sendVerificationRequest();
    }
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
      verificationTimer = setTimeout(sendVerificationRequest, Core.EVENT_TIME_DELAY);
    });
  };
  const sendVerificationRequest = () => {
    messageId = getNextMessageId();
    Core.sendGet(`/api/v1/mail/validate/${element.value}`, element, messageId);
  };

  validateOptions();
  createGUI();
  assignTriggers();
}
;