/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

class EditableField {
  constructor( {
  element = null,
          id = null,
          disabled = false,
          optionListURL = null,
          validationURI = null,
          updateURI = null,
          field = null,
          defaultValue = null,
          onValid = null,
          onNotValid = null,
          onUpdate = null,
          onKeyDown = null,
          placeholder = null
  } = {}) {
    this.isValueInCatalog = false;
    this.isOptionsVisible = false;
    let
            inputElement,
            listContainerElement,
            lastValue,
            lastValueSaved,
            serverTimer,
            requestId = 0
            ;
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
      inputElement.setAttribute('placeholder', placeholder !== null ? placeholder : '');
      inputElement.value = defaultValue;
      lastValue = defaultValue;
      lastValueSaved = defaultValue;
      element.appendChild(inputElement);
      element.inputElement = inputElement;
      inputElement.data = {validationURI, field};
      if (optionListURL !== null) {
        listContainerElement = document.createElement('DIV');
        listContainerElement.className = 'optionList';
        listContainerElement.innerHTML = '<div>' + Core.getText('editableField.loadingData') + '</div>';
        Core.sendGet(optionListURL, listContainerElement);
        var style = window.getComputedStyle(inputElement, null);
        listContainerElement.style.width = style.getPropertyValue("width");
        element.appendChild(listContainerElement);
      }
    };
    const sendValidationRequest = () => {
      const uri = validationURI.replace('{value}', inputElement.value);
      const response = Core.sendGet(uri, inputElement);
      requestId = response.requestId;
    };
    const getServerValidation = () => {
      if (lastValue !== inputElement.value) {
        if (serverTimer) {
          clearTimeout(serverTimer);
        }
        lastValue = inputElement.value;
        serverTimer = setTimeout(() => {
          sendValidationRequest();
        }, 400);
      }
    };
    const fillOptions = () => {
      if (!listContainerElement) {
        return;
      }
      Core.removeChilds(listContainerElement);
      this.isValueInCatalog = false;
      this.optionsData.list.forEach(elementData => {
        const name = elementData.name;
        const inputValue = inputElement.value;
        if (name.includes(inputValue) && name !== inputValue) {
          const item = document.createElement('DIV');
          item.addEventListener("click", () => {
            inputElement.value = item.innerHTML;
            checkForValueInCatalog();
            colorInputValue();
            updateValueOnServer();
          });
          item.innerHTML = name;
          listContainerElement.appendChild(item);
        }
        if (name === inputValue) {
          this.isValueInCatalog = true;
        }
      });
    };
    const updateValueOnServer = () => {
      const value = inputElement.value;
      console.log(`Update server with ${value}`);
      if (lastValueSaved === value) {
        console.log(`Update NOT sent. The last value ${lastValueSaved} are the same than the actual`);
        return;
      }

      if ((optionListURL !== null && !this.isValueInCatalog)) {
        console.log(`Update NOT sent. The ${inputElement.value} value IS NOT in the cataloge`);
        return;
      }
      if (this.saveTimer) {
        clearTimeout(this.saveTimer);
      }
      this.saveTimer = setTimeout(event => {
        sendUpdateRequest(event);
      }, 2000);
      const sendUpdateRequest = () => {
        console.log(`Send update with the value ${inputElement.value} for the field ${field}`);
        const message = Core.getText('editableField.saving', field);
        Core.showMessage({status: "OK", message});
        let data = {};
        data[inputElement.data.field] = inputElement.value;
        Core.sendPut(updateURI, inputElement, data);
      };
    };
    const colorInputValue = () => {
      if (this.isValueInCatalog) {
        inputElement.classList.remove('valueNotFound');
      } else {
        inputElement.classList.add('valueNotFound');
      }
    };
    const checkForValueInCatalog = () => {
      const list = this.optionsData.list;
      const value = inputElement.value;
      this.isValueInCatalog = false;
      for (let item of list) {
        if (item.name === value) {
          this.isValueInCatalog = true;
          break;
        }
      }
    };
    const showOptions = () => {
      if (listContainerElement && listContainerElement.childElementCount > 0) {
        listContainerElement.style.display = 'block';
        this.isOptionsVisible = true;
      } else {
        hideOptions();
      }
    };
    const hideOptions = () => {
      if (listContainerElement) {
// TODO use opacity for disapear the opcions and put the none in the last step of the animation
        listContainerElement.style.display = 'none';
        this.isOptionsVisible = false;
      }
    };
    const assignTriggers = () => {
// TODO quitar estos eventos y usar un método
      element.addEventListener('enabled', () => {
        element.removeAttribute('disabled');
      });
      element.addEventListener('disabled', () => {
        element.setAttribute('disabled', true);
      });
      element.addEventListener('toggle', () => {
        element.setAttribute('disabled', !getAttribute('disabled'));
      });
      inputElement.addEventListener('focusin', () => {
        fillOptions();
        showOptions();
      });
      inputElement.addEventListener('focusout', () => {
        // TODO use opacity for disapear the options in order to capture the posible click and put the none in the last step of the animation. Remove the timeout.
        if (this.isOptionsVisible) {
          setTimeout(() => {
            hideOptions();
            colorInputValue();
          }, 400);
        } else {
          updateValueOnServer();
        }
      });
      inputElement.addEventListener("keyup", event => {
        if (Core.isModifierKey(event) || Core.isNavigationKey(event)) {
          return;
        }
        getServerValidation();
        fillOptions();
        showOptions();
        checkForValueInCatalog();
        if (this.isValueInCatalog) {
          updateValueOnServer();
        }
        colorInputValue();
      });
      inputElement.addEventListener("keydown", event => {
        if (Core.isFunction(onKeyDown)) {
          onKeyDown(event);
        }
      });
      inputElement.addEventListener('response', event => {
        const {detail} = event;
        const {data} = detail;
        console.log(detail);
        if (data.status === 'OK') {
          if (data.type === 'UPDATE') {
            const message = Core.getText('editableField.saved', [placeholder, inputElement.value]);
            Core.showMessage({status: "OK", message: message});
            lastValueSaved = inputElement.value;
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
              if (optionListURL === null) {
                updateValueOnServer();
              } else {
                checkForValueInCatalog();
              }
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
      if (listContainerElement) {
        listContainerElement.addEventListener('response', event => {
          const {detail} = event;
          const {data} = detail;
          this.optionsData = data;
          fillOptions();
        });
      }
    };
    validateOptions();
    createGUI();
    assignTriggers();
  }

}
