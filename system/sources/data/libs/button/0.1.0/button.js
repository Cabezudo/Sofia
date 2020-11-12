/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

class Button {
  constructor(p = { id = null, element = null, type = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) {
    this.enabled = p.enabled;

    const validateOptions = () => {
      this.element = Core.validateIdOrElement(p.id, p.element);
      if (p.type === null) {
        throw Error('You must specify a button type.');
      }
    };
    const createGUI = () => {
      this.element.className = 'button';
      this.element.classList.add(`${p.type}Button`);
      if (!p.enabled) {
        this.element.setAttribute('disabled', true);
      }
      if (p.text !== null) {
        this.element.innerHTML = p.text;
      } else {
        if (this.element.innerHTML === '') {
          this.element.innerHTML = 'Action';
        }
      }
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
        if (event.button === 0 && Core.isFunction(p.onClick)) {
          p.onClick();
        }
      });
      this.element.addEventListener('response', event => {
        this.enable();
        if (Core.isFunction(p.onResponse)) {
          p.onResponse(event);
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
const redButton = ({ id = null, element = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return new Button({id: id, element: element, type: 'red', text: text, enabled: enabled, onClick: onClick, onResponse: onResponse});
};
const orangeButton = ({ id = null, element = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return new Button({id: id, element: element, type: 'orange', text: text, enabled: enabled, onClick: onClick, onResponse: onResponse});
};
const blueButton = ({ id = null, element = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return new Button({id: id, element: element, type: 'blue', text: text, enabled: enabled, onClick: onClick, onResponse: onResponse});
};
const greenButton = ({ id = null, element = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return new Button({id: id, element: element, type: 'green', text: text, enabled: enabled, onClick: onClick, onResponse: onResponse});
};
const blackButton = ({ id = null, element = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return new Button({id: id, element: element, type: 'black', text: text, enabled: enabled, onClick: onClick, onResponse: onResponse});
};