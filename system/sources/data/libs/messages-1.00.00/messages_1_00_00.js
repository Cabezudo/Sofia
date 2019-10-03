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
      const payload = event.detail;
      const messageContainer = document.createElement('div');
      messageContainer.innerText = payload.message;
      switch (payload.status) {
        case 'OK':
          messageContainer.className = 'green';
          break;
        case 'ERROR':
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
