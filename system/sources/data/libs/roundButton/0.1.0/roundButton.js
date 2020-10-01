/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

const roundButton = ({id = null, element = null, type = null, enabled = true, onClick = null, onResponse = null} = {}) => {
  const validateOptions = () => {
    this.element = Core.validateIdOrElement(id, element);
    if (type === null) {
      throw Error('You must specify a button type.');
    }
  };
  const createGUI = () => {
    switch (type) {
      case 'cross':
        this.element.className = "roundButton crossButton";
        this.element.innerHTML = `<div class="drawContainer"><div class="patty"></div><div class="patty"></div></div`;
        break;
      case 'add':
        this.element.className = "roundButton addButton";
        this.element.innerHTML = `<div class="drawContainer"><div class="patty"></div><div class="patty"></div></div`;
        break;
      case 'remove':
        this.element.className = "roundButton removeButton";
        this.element.innerHTML = `<div class="drawContainer"><div class="patty"></div></div></div`;
        break;
      default:
        throw new Error(`Invalid button Type: ${type}`);
    }

    if (!enabled) {
      this.element.setAttribute('disabled', true);
    }
  };
  const assignTriggers = () => {
    this.element.addEventListener('click', event => {
      if (this.element.getAttribute('disabled')) {
        return;
      }
      if (event.button === 0 && Core.isFunction(onClick)) {
        this.element.setAttribute('disabled', true);
        onClick();
      }
    });
  };
  this.disable = () => {
    this.element.setAttribute('disabled', true);
  };
  this.enable = () => {
    this.element.removeAttribute('disabled');
  };

  validateOptions();
  createGUI();
  assignTriggers();
  return this;
};
const crossRoundButton = ({ id = null, element = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return roundButton({id: id, element: element, type: 'cross', enabled: enabled, onClick: onClick, onResponse: onResponse});
};
const addRoundButton = ({ id = null, element = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return roundButton({id: id, element: element, type: 'add', enabled: enabled, onClick: onClick, onResponse: onResponse});
};
const removeRoundButton = ({ id = null, element = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return roundButton({id: id, element: element, type: 'remove', enabled: enabled, onClick: onClick, onResponse: onResponse});
};