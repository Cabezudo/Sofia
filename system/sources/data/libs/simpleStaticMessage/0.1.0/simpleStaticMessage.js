/*
 * Created on: 29/10/2019
 * Author:     Esteban Cabezudo
 */

/* global Core */

const simpleStaticMessage = ({ id = null, element = null } = {}) => {
  let messageContainer;
  const validateOptions = () => {
    element = Core.validateIdOrElement(id, element);
  };
  const createGUI = () => {
    element.className = 'simpleStaticMessage';
    Core.setMessagesContainer(element);
    const defaultMessage = element.innerHTML;
    messageContainer = document.createElement('div');
    messageContainer.innerText = defaultMessage;
  };
  const assignTriggers = () => {
    element.addEventListener('clearMessages', () => {
      Core.removeChilds(element);
    });
    element.addEventListener('set', event => {
      const payload = event.detail;
      switch (payload.status) {
        case 'ERROR':
          messageContainer.className = 'red';
          break;
        case 'MESSAGE':
          messageContainer.className = 'message';
          break;
        default:
          throw new Error(`Invalid status: ${payload.status}`);
      }
      messageContainer.innerText = payload.message;
    });
  };
  validateOptions();
  createGUI();
  assignTriggers();
  return element;
};
