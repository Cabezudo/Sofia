/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

const crossButton = ({ onClick = null, onResponse = null } = {}) => {
  const element = document.createElement('div');
  const validateOptions = () => {
  };
  const createGUI = () => {
    element.innerHTML = '<div class="arm"></div><div class="arm"></div>';
    element.className = 'crossButton';
    linkTo({
      element: element,
      onClick: () => {
        if (Core.isFunction(onClick)) {
          onClick();
        }
      }
    });

  };
  const assignTriggers = () => {
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
