/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

const button_1_00_00 = ({ id = null, element = null, type = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  const DISABLED_CLASS = 'disabledButton_1_00_00';

  const validateOptions = () => {
    element = Core.validateIdOrElement(id, element);
    id = element.id;
    if (type === null) {
      throw Error('You must specify a button type.');
    }
  };
  const createGUI = () => {
    element.className = 'button_1_00_00';
    element.classList.add(`${type}Button_1_00_00`);
    if (!enabled) {
      element.classList.add(DISABLED_CLASS);
    }
    if (text !== null) {
      element.innerHTML = text;
    } else {
      if (element.innerHTML === '') {
        element.innerHTML = 'Action';
      }
    }
  };
  const assignTriggers = () => {
    element.addEventListener('enabled', () => {
      element.classList.remove(DISABLED_CLASS);
    });
    element.addEventListener('disabled', () => {
      element.classList.add(DISABLED_CLASS);
    });
    element.addEventListener('toggle', () => {
      element.classList.toggle(DISABLED_CLASS);
    });
    element.addEventListener('click', event => {
      if (element.classList.contains(DISABLED_CLASS)) {
        return;
      }
      if (event.button === 0 && Core.isFunction(onClick)) {
        element.classList.add(DISABLED_CLASS);
        onClick();
      }
    });
    element.addEventListener('response', event => {
      if (Core.isFunction(onResponse)) {
        onResponse(event);
      }
      element.classList.remove(DISABLED_CLASS);
    });
  };
  validateOptions();
  createGUI();
  assignTriggers();
  return element;
};
const redButton_1_00_00 = ({ id = null, element = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return button_1_00_00({id: id, element: element, type: 'red', text: text, enabled: enabled, onClick: onClick, onResponse: onResponse});
};
const orangeButton_1_00_00 = ({ id = null, element = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return button_1_00_00({id: id, element: element, type: 'orange', text: text, enabled: enabled, onClick: onClick, onResponse: onResponse});
};
const blueButton_1_00_00 = ({ id = null, element = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return button_1_00_00({id: id, element: element, type: 'blue', text: text, enabled: enabled, onClick: onClick, onResponse: onResponse});
};
const greenButton_1_00_00 = ({ id = null, element = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return button_1_00_00({id: id, element: element, type: 'green', text: text, enabled: enabled, onClick: onClick, onResponse: onResponse});
};