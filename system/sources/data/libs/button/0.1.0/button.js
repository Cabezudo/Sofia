/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

class Button {
  constructor( { id = null, element = null, type = null, key = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) {
    this.enabled = enabled;

    const validateOptions = () => {
      this.element = Core.validateIdOrElement(id, element);
      if (type === null) {
        throw Error('You must specify a button type.');
      }
    };
    const setText = () => {
      this.text = text;
      this.key = key;
      if (text !== null) {
        this.element.innerHTML = text;
        return;
      }
      if (key !== null) {
        this.element.innerHTML = Core.getText(this.key, this.parameters);
        return;
      }
      if (this.element.innerHTML === '') {
        this.element.innerHTML = 'Action';
      }
    }
    ;
    const createGUI = () => {
      this.element.className = 'button';
      this.element.classList.add(`${type}Button`);
      if (!enabled) {
        this.element.setAttribute('disabled', true);
      }
      Core.addOnSetLanguageFunction(setText);
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