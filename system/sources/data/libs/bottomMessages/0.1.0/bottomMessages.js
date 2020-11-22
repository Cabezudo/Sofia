/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

const bottomMessages = ({ id = null, element = null } = {}) => {
  let messageContainer;
  const validateOptions = () => {
    element = Core.validateIdOrElement(id, element);
  };

  const createGUI = () => {
    element.className = 'bottomMessages';
    Core.setMessagesContainer(element);
    messageContainer = document.createElement('div');
    element.appendChild(messageContainer);
  };

  const assignTriggers = () => {
    element.addEventListener('clearMessages', () => {
      Core.removeChilds(messageContainer);
    });
    element.addEventListener('add', event => {
      messageContainer.style.opacity = 1;
      const {detail} = event;
      const {data} = detail;

      messageContainer.innerText = data.message;
      switch (data.status) {
        case 'OK':
          messageContainer.className = 'green';
          break;
        case 'ERROR':
          messageContainer.className = 'red';
          break;
      }
      setTimeout(() => {
        messageContainer.style.opacity = 0;
      }, 6000);
    }
    );
  };
  validateOptions();
  createGUI();
  assignTriggers();

  return element;
};
