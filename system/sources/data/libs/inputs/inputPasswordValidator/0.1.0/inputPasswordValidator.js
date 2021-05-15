/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

class InputPasswordValidator {
  constructor( { element = null, placeholderKey = null, placeholderParameters = null, onValid = null, onNotValid = null, onKeyPress = null } = {}) {
    this.requestId = 0;
    this.verificationTimer;

    const validateOptions = () => {
      if (element === null) {
        throw Error('You must define an element to apply the validator.');
      }
    };
    const setTexts = () => {
      element.placeholder = Core.getText(placeholderKey, placeholderParameters);
    };
    const createGUI = () => {
      element.className = 'inputPasswordValidator';
      Core.addOnSetLanguageFunction(setTexts);
    };
    const assignTriggers = () => {
      element.addEventListener('response', event => {
        const {detail} = event;
        const {data} = detail;

        const element = event.srcElement;
        if (this.requestId === detail.requestId) {
          detail.elementId = element.id;
          if (detail.status === 'ERROR') {
            Core.showMessage(data);
            element.classList.add('error');
          }
          if (data.status === 'OK') {
            element.classList.remove('error');
            Core.showMessage(data);
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
        this.verificationTimer = setTimeout(sendVerificationRequest, 300);
      });
    };
    const sendVerificationRequest = () => {
      const response = Core.sendPost(`/api/v1/password/validate`, element, {password: btoa(element.value)});
      this.requestId = response.requestId;
    };
    validateOptions();
    createGUI();
    assignTriggers();
  }
}
;