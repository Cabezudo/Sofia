/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

const editableField_1_00 = ({ element = null, id = null, validationURI = null, storeURI = null, field = null, defaultValue = null, onValid = null, onNotValid = null, onUpdate = null } = {}) => {
  let inputElement, saveButton, lastValue, validationTimer, saveTimer, requestId = 0;

  const validateOptions = () => {
    if (element === null && id === null) {
      throw Error('You must define a property id or a property element.');
    }
    if (validationURI === null) {
      throw Error('You must set a web services validationURI.');
    }
    if (storeURI === null) {
      throw Error('You must set a web services storeURI.');
    }
    if (field === null) {
      throw Error('You must add a record field to associate.');
    }
  };
  const createGUI = () => {
    if (element === null) {
      element = Core.validateById(id);
    }
    element.classList.add('editableField-1_00');
    inputElement = document.createElement('input');
    inputElement.setAttribute('type', 'text');
    inputElement.value = defaultValue;
    lastValue = defaultValue;
    element.appendChild(inputElement);
    element.inputElement = inputElement;
    inputElement.data = {validationURI, field};

    saveButton = document.createElement('div');
    saveButton.innerHTML = 'Save';
    element.appendChild(saveButton);
  };
  const sendValidationRequest = event => {
    const inputElement = event.srcElement;
    const data = {
      field: inputElement.data.field,
      value: inputElement.value
    };
    const uri = validationURI.replace('{value}', inputElement.value);
    const response = Core.sendGet(uri, inputElement, data);
    requestId = response.requestId;
  };
  const sendUpdateRequest = event => {
    saveButton.innerHTML = 'Save';
    saveButton.style.opacity = 0;
    console.log(event);
  };
  const assignTriggers = () => {
    inputElement.addEventListener('response', event => {
      const data = event.detail;
      if (requestId === data.requestId) {
        Core.cleanMessagesContainer();
        element.classList.remove('error');
        Core.addMessage(data);
        if (data.status === 'ERROR') {
          element.classList.add('error');
        }
        if (data.status === 'OK') {
          if (data.type === 'VALIDATION') {
            saveButton.style.opacity = 1;
            saveTimer = setTimeout(event => {
              sendUpdateRequest(event);
            }, 5000);
            if (Core.isFunction(onValid)) {
              onValid();
            }
          }
          if (data.type === 'UPDATE') {
            saveButton.style.opacity = 0;
            if (Core.isFunction(onUpdate)) {
              onUpdate();
            }
          }
        } else {
          if (Core.isFunction(onNotValid)) {
            onNotValid();
          }
        }
      }
    });
    const update = event => {
      if (Core.isModifierKey(event) || Core.isNavigationKey(event)) {
        return;
      }
      if (lastValue !== inputElement.value) {
        if (saveTimer) {
          clearTimeout(saveTimer);
        }
        lastValue = inputElement.value;
        if (validationTimer) {
          clearTimeout(validationTimer);
        }
        validationTimer = setTimeout(() => {
          sendValidationRequest(event);
        }, 400);
      }
    };
    sendData = event => {
      const source = event.srcElement;
      const data = source.data;
    };
    element.addEventListener("keyup", event => {
      update(event);
    });
  };
  validateOptions();
  createGUI();
  assignTriggers();
}
;