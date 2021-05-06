/*
 * Created on: 26/02/2020
 * Author:     Esteban Cabezudo
 */

/* global Core */

class InputGenericValidator {
  constructor(
  { element = null, id = null, placeholderKey = null, placeholderParameters = null, getValidationURL = null, onValid = null, onNotValid = null, onEnter = null, onFocus = null } = {}) {
    this.placeholderKey = placeholderKey;
    this.placeholderParameters = placeholderParameters;
    this.verificationTimer;
    this.requestId = 0;
    const validateOptions = () => {
      if (element === null && id === null) {
        throw Error('You must define a property id or a property element.');
      }
      if (getValidationURL === null && Core.isFunction(getValidationURL)) {
        throw Error('You must set a function that returns a validation url.');
      }
    };
    const setTexts = () => {
      element.placeholder = Core.getText(this.placeholderKey, this.placeholderParameters);
    };
    const createGUI = () => {
      if (element === null) {
        element = Core.validateById(id);
      }
      element.className = 'inputGenericValidator';
      Core.addOnSetLanguageFunction(setTexts);
    };
    const assignTriggers = () => {
      element.addEventListener('response', event => {
        const {detail} = event;
        const {data} = detail;
        const element = event.srcElement;
        if (this.requestId === detail.requestId) {
          const {detail} = event;
          const {data} = detail;
          detail.elementId = element.id;
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
      element.addEventListener("keydown", event => {
        console.log('InputGenericValidator :: Enter pressed.');
        if (Core.isEnter(event)) {
          if (Core.isFunction(onEnter)) {
            onEnter();
          }
        }
      });
      element.addEventListener("keyup", event => {
        if (Core.isModifierKey(event) || Core.isNavigationKey(event)) {
          return;
        }
        if (this.verificationTimer !== undefined) {
          clearTimeout(this.verificationTimer);
        }
        this.verificationTimer = setTimeout(() => {
          sendValidationRequest(element);
        }, 500);
      });
      element.addEventListener("type", event => {
        const {detail} = event;
        element.value = detail;
        sendValidationRequest(element);
      });
    };
    const sendValidationRequest = element => {
      if (element.value !== null && element.value !== undefined) {
        const name = element.value.trim();
        console.log(`InputGenericValidator :: sendValidationRequest :: Send GET with ${getValidationURL()}`);
        const response = Core.sendGet(getValidationURL(), element);
        this.requestId = response.requestId;
      }
    };
    validateOptions();
    createGUI();
    assignTriggers();
  }
}
;