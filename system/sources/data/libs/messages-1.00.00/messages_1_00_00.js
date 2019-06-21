/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

const messages_1_00_00 = ({ id = null, element = null } = {}) => {

  const validateOptions = () => {
    element = Core.validateIdOrElement(id, element);
  };

  const createGUI = () => {
    element.className = 'messages_1_00_00';
    Core.setMessagesContainer(element);
  };

  const assignTriggers = () => {
    element.addEventListener('add', event => {
      const message = event.detail;
      const messageContainer = document.createElement('div');
      messageContainer.innerText = message.message;
      switch (message.status) {
        case 'OK':
        case 'VALID':
          messageContainer.className = 'green';
          break;
        case 'ERROR':
        case 'INVALID':
          messageContainer.className = 'red';
          break;
      }
      element.appendChild(messageContainer);
    });
  };
  validateOptions();
  createGUI();
  assignTriggers();

  return element;
};
