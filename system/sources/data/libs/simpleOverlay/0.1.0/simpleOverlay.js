/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

const simpleOverlay = ({ id = null, scrollElementId = null, scrollElement = null } = {}) => {
  let
          overlay,
          lastOverflowValue;

  const validateOptions = () => {
    if (id !== null && id.length === 0) {
      throw Error(`Invalid element id: ${id}`);
    }
    if (scrollElementId !== null) {
      scrollElement = Core.validateById(scrollElementId);
    }
  };
  const createGUI = () => {
    overlay = document.createElement('DIV');
    overlay.className = 'simpleOverlay';
    document.body.appendChild(overlay);
  };
  const assignTriggers = () => {
  };
  this.show = () => {
    overlay.style.display = 'block';
    if (scrollElement) {
      lastOverflowValue = scrollElement.style.overflow;
      scrollElement.style.overflow = 'hidden';
    }
  };
  this.hide = () => {
    overlay.style.display = 'none';
    if (scrollElement) {
      scrollElement.style.overflow = lastOverflowValue;
    }
  };
  this.setContent = content => {
    if (Core.isDIV(content)) {
      Core.removeChilds(overlay);
      overlay.appendChild(content);
      return;
    }
    overlay.innerHTML = content;
  };
  validateOptions();
  createGUI();
  assignTriggers();
  return this;
}
;
