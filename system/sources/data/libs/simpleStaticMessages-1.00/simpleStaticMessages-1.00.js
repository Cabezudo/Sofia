/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

const simpleStaticMessages_1_00 = ({ id = null, element = null } = {}) => {
  let messageContainer;
  let messages = {};

  const validateOptions = () => {
    element = Core.validateIdOrElement(id, element);
  };
  const createGUI = () => {
    element.className = 'simpleStaticMessages_1_00';
    Core.setMessagesContainer(element);
  };
  const assignTriggers = () => {
    element.addEventListener('clearMessages', () => {
      Core.removeChilds(element);
    });
    element.addEventListener('add', event => {
      const payload = event.detail;
      if (payload.elementId === undefined) {
        throw new Error('Undefined elementId property for message container.');
      }
      if (payload.status === 'ERROR') {
        messages[payload.elementId] = payload;
      } else {
        delete messages[payload.elementId];
      }
      if (Object.keys(messages).length > 0) {
        messageContainer = document.createElement('div');
        for (var key in messages) {
          messageContainer.className = 'red';
          messageContainer.innerText = messages[key].message;
          break;
        }
        element.appendChild(messageContainer);
      } else {
        Core.removeChilds(element);
      }
    });
  };
  validateOptions();
  createGUI();
  assignTriggers();
  return element;
};
