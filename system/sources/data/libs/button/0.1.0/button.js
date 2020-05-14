/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

const button = ({ id = null, element = null, type = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  const validateOptions = () => {
    element = Core.validateIdOrElement(id, element);
    id = element.id;
    if (type === null) {
      throw Error('You must specify a button type.');
    }
  };
  const createGUI = () => {
    element.className = 'button';
    element.classList.add(`${type}Button`);
    if (!enabled) {
      element.setAttribute('disabled', true);
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
      element.removeAttribute('disabled');
    });
    element.addEventListener('disabled', () => {
      element.setAttribute('disabled', true);
    });
    element.addEventListener('toggle', () => {
      element.setAttribute('disabled', !getAttribute('disabled'));
    });
    element.addEventListener('click', event => {
      if (element.getAttribute('disabled')) {
        return;
      }
      if (event.button === 0 && Core.isFunction(onClick)) {
        element.setAttribute('disabled', true);
        onClick();
      }
    });
    element.addEventListener('response', event => {
      if (Core.isFunction(onResponse)) {
        onResponse(event);
      }
    });
  };
  validateOptions();
  createGUI();
  assignTriggers();
  return element;
};
const redButton = ({ id = null, element = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return button({id: id, element: element, type: 'red', text: text, enabled: enabled, onClick: onClick, onResponse: onResponse});
};
const orangeButton = ({ id = null, element = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return button({id: id, element: element, type: 'orange', text: text, enabled: enabled, onClick: onClick, onResponse: onResponse});
};
const blueButton = ({ id = null, element = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return button({id: id, element: element, type: 'blue', text: text, enabled: enabled, onClick: onClick, onResponse: onResponse});
};
const greenButton = ({ id = null, element = null, text = null, enabled = true, onClick = null, onResponse = null } = {}) => {
  return button({id: id, element: element, type: 'green', text: text, enabled: enabled, onClick: onClick, onResponse: onResponse});
};