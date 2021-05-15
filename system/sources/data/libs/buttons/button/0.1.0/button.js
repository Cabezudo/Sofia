/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

class Button {
  constructor( { id = null, element = null, type = null, key = null, parameters = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) {
    this.enabled = enabled;
    this.element = element;

    const validateOptions = () => {
      this.element = Core.validateIdOrElement(id, this.element);
      this.id = this.element.id;
      if (type === null) {
        throw Error('Button :: setLanguage :: You must specify a button type.');
      }
    };
    const setLanguage = () => {
      if (text !== null) {
        this.element.innerHTML = text;
        console.log(`Button :: setLanguage :: Set text '${text}' from text for button ${id}`);
        return;
      }
      if (key !== null) {
        const text = Core.getText(key, parameters);
        console.log(`Button :: setLanguage :: Set text '${text}' from key for button ${id}`);
        this.element.innerHTML = text;
        return;
      }
      if (this.id !== null) {
        const text = Core.getText(id, parameters);
        console.log(`Button :: setLanguage :: Set text '${text}' from id for button ${id}`);
        this.element.innerHTML = text;
        return;
      }
      if (this.element.innerHTML === '') {
        this.element.innerHTML = 'Action';
      }
      console.log(`Button :: setLanguage :: No text to set`);
    }
    ;
    const createGUI = () => {
      console.log(`Button :: createGUI :: Create button using`, element);
      this.element.className = 'button';
      this.element.classList.add(`${type}Button`);
      if (!enabled) {
        this.element.setAttribute('disabled', true);
      }
      Core.addOnSetLanguageFunction(setLanguage);
    };
    const assignTriggers = () => {
      this.element.addEventListener('enabled', () => {
        this.enable();
      });
      this.element.addEventListener('disabled', () => {
        this.disable();
      });
      this.element.addEventListener('toggle', () => {
        this.toggle();
      });
      this.element.addEventListener('click', event => {
        if (this.element.getAttribute('disabled')) {
          return;
        }
        this.disable();
        if (event.button === 0 && Core.isFunction(onClick)) {
          onClick();
        }
      });
      this.element.addEventListener('response', event => {
        this.enable();
        if (Core.isFunction(onResponse)) {
          onResponse(event);
        }
      });
    };
    validateOptions();
    createGUI();
    assignTriggers();
  }
  enable() {
    this.element.removeAttribute('disabled');
  }

  disable() {
    this.element.setAttribute('disabled', true);
  }

  toggle() {
    this.element.setAttribute('disabled', !getAttribute('disabled'));
  }
}
const redButton = ({ id = null, element = null, key = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return new Button({id: id, element: element, type: 'red', key: key, text: text, enabled: enabled, onClick: onClick, onResponse: onResponse});
};
const orangeButton = ({ id = null, element = null, key = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return new Button({id: id, element: element, type: 'orange', key: key, text: text, enabled: enabled, onClick: onClick, onResponse: onResponse});
};
const blueButton = ({ id = null, element = null, key = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return new Button({id: id, element: element, type: 'blue', key: key, text: text, enabled: enabled, onClick: onClick, onResponse: onResponse});
};
const greenButton = ({ id = null, element = null, key = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return new Button({id: id, element: element, type: 'green', key: key, text: text, enabled: enabled, onClick: onClick, onResponse: onResponse});
};
const blackButton = ({ id = null, element = null, key = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return new Button({id: id, element: element, type: 'black', key: key, text: text, enabled: enabled, onClick: onClick, onResponse: onResponse});
};