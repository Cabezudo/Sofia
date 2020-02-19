/*
 * Created on: 29/10/2019
 * Author:     Esteban Cabezudo
 */

/* global Core */

const simpleStaticMessage = ({ id = null, element = null } = {}) => {
  const
          REMOVE_MESSAGE_TIME_DELAY = 3000;
  let
          defaultMessage,
          messageContainer,
          messageRemoverTimer;

  const validateOptions = () => {
    element = Core.validateIdOrElement(id, element);
  };
  const createGUI = () => {
    element.className = 'simpleStaticMessage';
    Core.setMessagesContainer(element);
    defaultMessage = element.innerHTML;
    Core.removeChilds(element);
    messageContainer = document.createElement('div');
    messageContainer.innerText = defaultMessage;
    element.appendChild(messageContainer);
  };
  const assignTriggers = () => {
    element.addEventListener('cleanMessages', () => {
      console.log('Clear messages.');
      messageContainer.innerText = '';
    });
    element.addEventListener('showMessage', event => {
      console.log('Show message.');
      const payload = event.detail;
      switch (payload.status) {
        case 'ERROR':
          element.classList.remove('green');
          element.classList.add('red');
          break;
        case 'OK':
          element.classList.remove('red');
          element.classList.add('green');
          break;
        default:
          throw new Error(`Invalid status: ${payload.status}`);
      }
      messageContainer.innerText = payload.message;
      if (messageRemoverTimer) {
        clearTimeout(messageRemoverTimer);
      }
      messageRemoverTimer = setTimeout(() => {
        element.className = 'simpleStaticMessage';
        messageContainer.innerText = defaultMessage;
      }, REMOVE_MESSAGE_TIME_DELAY);
    });
  };
  validateOptions();
  createGUI();
  assignTriggers();
  return element;
};
