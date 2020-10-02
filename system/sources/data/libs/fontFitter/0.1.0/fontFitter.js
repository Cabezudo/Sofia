/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

const fontFitter = ({id = null, element = null, fontSize = null} = {}) => {
  const fitFontOnElement = (element, fontSize) => {
    const running = element.getAttribute('running');
    if (running === 'true') {
      return;
    }
    element.setAttribute('running', true);
    const child = element.firstChild;
    child.style.fontSize = `${fontSize}px`;
    const color = child.style.color;
    child.style.color = 'transparent';
    const checkFont = () => {
      if (element.clientHeight < child.scrollHeight && fontSize > 2) {
        fontSize--;
        child.style.fontSize = `${fontSize}px`;
        setTimeout(checkFont, 50);
      } else {
        element.setAttribute('running', false);
        child.style.color = color;
      }
    };
    checkFont();
  };
  const validateOptions = () => {
    if (id === null) {
      this.element = Core.validateIdOrElement(id, element);
    } else {
      this.element = element;
    }
    if (this.element === null) {
      throw Error('You must specify an id or an element.');
    }
    if (fontSize === null) {
      throw Error('You must specify a fontSize parameter.');
    }
  };
  const createGUI = () => {
  };
  const assignTriggers = () => {
    Core.addOnResizeFunction(() => {
      fitFontOnElement(element, fontSize);
    });
    window.addEventListener('show', e => {
      fitFontOnElement(element, fontSize);
    });
  };

  validateOptions();
  createGUI();
  assignTriggers();

  fitFontOnElement(element, fontSize);
};
