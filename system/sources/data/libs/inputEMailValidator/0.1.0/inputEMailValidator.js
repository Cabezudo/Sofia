/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

class InputEMailValidator {
  constructor( { element = null, onValid = null, onNotValid = null, onKeyPress = null } = {}) {
    this.verificationTimer;
    this.requestId = 0;
    const validateOptions = () => {
      if (element === null) {
        throw Error('You must define an element to apply the validator.');
      }
    };
    const createGUI = () => {
      element.className = 'inputEMailValidator';
      element.setAttribute('autocomplete', 'off');
      element.setAttribute('autocorrect', 'off');
      element.setAttribute('autocapitalize', 'off');
      element.setAttribute('spellcheck', 'false');
      if (element.value && element.value.length > 0) {
        sendValidationRequest(element);
      }
    };
    const assignTriggers = () => {
      element.addEventListener('response', event => {
        const {detail} = event;
        const {data} = detail;
        const element = event.srcElement;
        if (this.requestId === detail.requestId) {
          detail.elementId = element.id;
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
        if (this.verificationTimer) {
          clearTimeout(this.verificationTimer);
        }
        this.verificationTimer = setTimeout(sendValidationRequest(element), Core.EVENT_TIME_DELAY);
      });
    };
    const sendValidationRequest = element => {
      const response = Core.sendGet(`/api/v1/mails/${element.value}/validate`, element);
      this.requestId = response.requestId;
    };
    validateOptions();
    createGUI();
    assignTriggers();
  }
}
;