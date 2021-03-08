/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

const editableField = ({ element = null, id = null, disabled = false, optionListURL = null, validationURI = null, updateURI = null, field = null, defaultValue = null, onValid = null, onNotValid = null, onUpdate = null, onKeyDown = null, placeholder = '' } = {}) => {
  let inputElement, listContainerElement, lastValue, lastValueSaved, validationTimer, saveTimer, requestId = 0;

  const validateOptions = () => {
    if (element === null && id === null) {
      throw Error('You must define a property id or a property element.');
    }
    if (validationURI === null) {
      throw Error('You must set a web services validationURI.');
    }
    if (updateURI === null) {
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
    element.classList.add('editableField');
    inputElement = document.createElement('input');
    if (disabled) {
      inputElement.setAttribute('disabled', true);
    }
    inputElement.setAttribute('type', 'text');
    inputElement.setAttribute('spellcheck', 'false');
    inputElement.setAttribute('placeholder', placeholder);

    inputElement.value = defaultValue;
    lastValue = defaultValue;
    lastValueSaved = defaultValue;
    element.appendChild(inputElement);
    element.inputElement = inputElement;
    inputElement.data = {validationURI, field};

    if (optionListURL !== null) {
      listContainerElement = document.createElement('DIV');
      listContainerElement.className = 'optionList';
      listContainerElement.innerHTML = '<div>Loading data...</div>';
      Core.sendGet(optionListURL, listContainerElement);
      var style = window.getComputedStyle(inputElement, null);
      listContainerElement.style.width = style.getPropertyValue("width");
      element.appendChild(listContainerElement);
    }
  };
  const sendValidationRequest = event => {
    const inputElement = event.srcElement;
    const uri = validationURI.replace('{value}', inputElement.value);
    const response = Core.sendGet(uri, inputElement);
    requestId = response.requestId;
  };
  const sendUpdateRequest = () => {
    if (lastValueSaved === inputElement.value) {
      return;
    }
    lastValueSaved = inputElement.value;
    // TODO put a function to return the text  for the actual language
    Core.showMessage({status: "OK", message: `Saving ${field}...`});
    const data = {
      field: inputElement.data.field,
      value: inputElement.value
    };
    const response = Core.sendPut(updateURI, inputElement, data);
  };
  const assignTriggers = () => {
    element.addEventListener("keydown", event => {
      if (Core.isFunction(onKeyDown)) {
        onKeyDown(event);
      }
    });
    element.addEventListener('enabled', () => {
      element.removeAttribute('disabled');
    });
    element.addEventListener('disabled', () => {
      element.setAttribute('disabled', true);
    });
    element.addEventListener('toggle', () => {
      element.setAttribute('disabled', !getAttribute('disabled'));
    });
    inputElement.addEventListener('focusin', event => {
      listContainerElement.style.display = 'block';
      sendUpdateRequest(event);
    });
    inputElement.addEventListener('focusout', event => {
      listContainerElement.style.display = 'none';
      sendUpdateRequest(event);
    });
    inputElement.addEventListener('response', event => {
      const {detail} = event;
      const {data} = detail;

      if (data.status === 'OK') {
        if (data.type === 'UPDATE') {
          Core.showMessage({status: "OK", message: data.message});
          fieldUpdated = true;
          if (Core.isFunction(onUpdate)) {
            onUpdate();
          }
        }
      }
      if (requestId === detail.requestId) {
        element.classList.remove('error');
        Core.showMessage(data);
        if (data.status === 'ERROR') {
          element.classList.add('error');
        }
        if (data.status === 'OK') {
          if (data.type === 'VALIDATION') {
            saveTimer = setTimeout(event => {
              sendUpdateRequest(event);
            }, 4000);
            if (Core.isFunction(onValid)) {
              onValid();
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
    console.log(listContainerElement);
    if (listContainerElement) {
      listContainerElement.addEventListener('response', event => {
        const {detail} = event;
        const {data} = detail;
        Core.removeChilds(listContainerElement);
        data.list.forEach(elementData => {
          const item = document.createElement('DIV');
          item.innerHTML = elementData.name;
          listContainerElement.appendChild(item);
        });
      });
    }
  };
  validateOptions();
  createGUI();
  assignTriggers();
}
;