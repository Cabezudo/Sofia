/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

class CrossButton {
  constructor( { onClick = null, onResponse = null } = {})  {
    this.element = document.createElement('div');
    const validateOptions = () => {
    };
    const createGUI = () => {
      this.element.innerHTML = '<div class="arm"></div><div class="arm"></div>';
      this.element.className = 'crossButton';
      new Link({
        element: this.element,
        onClick: () => {
          if (Core.isFunction(onClick)) {
            onClick();
          }
        }
      });

    };
    const assignTriggers = () => {
      this.element.addEventListener('response', event => {
        if (Core.isFunction(onResponse)) {
          onResponse(event);
        }
      });

    };
    validateOptions();
    createGUI();
    assignTriggers();
  }
  getElement() {
    return this.element;
  }
}
;
