/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

class EditableImage {
  constructor( { element = null, id = null, imageURL = null, uploadURL = '/api/v1/images', onUpload = null } = {}) {
    const DRAG_HERE = "Drag the files here";
    const self = this;
    this.element = element;
    this.id = id;
    this.imageURL = imageURL;
    console.log(uploadURL);

    const validateOptions = () => {
      if (uploadURL === null) {
        throw Error('Undefined uploadURL property for editableImage');
      }
      this.element = Core.validateIdOrElement(self.id, self.element);
    };
    const setMessage = message => {
      this.inputMessageElement.innerHTML = message;
    };
    const createGUI = () => {
      self.uploadElement = document.createElement('DIV');
      self.uploadElement.classList.add('editableImage');
      this.element.appendChild(self.uploadElement);
      this.inputMessageElement = document.createElement('DIV');
      this.inputMessageElement.classList.add('message');
      self.uploadElement.appendChild(this.inputMessageElement);
      this.throbber = document.createElement('DIV');
      this.throbber.classList.add('throbber');
      self.uploadElement.appendChild(this.throbber);
      this.formElement = document.createElement('FORM');
      this.formElement.id = `upload:${this.element.id}`;
      this.formElement.setAttribute('action', `${uploadURL}`);
      this.formElement.setAttribute('method', `POST`);
      this.formElement.setAttribute('enctype', 'multipart/form-data');
      self.uploadElement.appendChild(this.formElement);
      this.formElement.style.display = 'none';
      this.inputElement = document.createElement('INPUT');
      this.inputElement.setAttribute('type', 'file');
      this.inputElement.setAttribute('name', 'file');
      this.formElement.appendChild(this.inputElement);
      this.inputElement.style.display = 'none';
      this.submitElement = document.createElement('INPUT');
      this.submitElement.setAttribute('type', 'submit');
      this.submitElement.setAttribute('value', 'Send file');
      this.submitElement.style.display = 'none';
      this.formElement.appendChild(this.submitElement);
      if (self.imageURL !== null) {
        self.uploadElement.style.backgroundImage = `url('${self.imageURL}')`;
      }
      setMessage(DRAG_HERE);

      const fileDragHover = () => {
        event.stopPropagation();
        event.preventDefault();
        setMessage('Drop here');
      };
      const fileDragLeave = () => {
        event.stopPropagation();
        event.preventDefault();
        setMessage(DRAG_HERE);
      };
      const fileSelectHandler = event => {
        setMessage(DRAG_HERE);
        event.stopPropagation();
        event.preventDefault();
        var images = event.target.files || event.dataTransfer.files;
        const image = images[0];
        console.log(image);
        const filename = image.name;
        fileUpload(image, filename);
      };

      const updateThrobber = percentage => {
      };

      const showThrobber = () => {
      };

      const fileUpload = (file, filename) => {
        console.log(filename);
        showThrobber();
        const reader = new FileReader();
        this.xhr = new XMLHttpRequest();
        const self = this;

        this.xhr.upload.addEventListener('progress', function (event) {
          if (event.lengthComputable) {
            const percentage = Math.round((event.loaded * 100) / event.total);
            updateThrobber(percentage);
          }
        }, false);

        this.xhr.upload.addEventListener('load', function (event) {
          updateThrobber(100);
        }, false);
        this.xhr.addEventListener('load', function (event) {
          const response = JSON.parse(event.currentTarget.response);
          if (Core.isFunction(onUpload)) {
            onUpload(response);
          }
          console.log('**** ' + response);
          self.uploadElement.style.backgroundImage = `url('/images/${response.data.filePath}')`;
        }, false);
        this.xhr.addEventListener('error', function (event) {
          setMessage(event);
        }, false);
        this.xhr.open('POST', uploadURL);
        var formData = new FormData();
        formData.append("file", file);
        reader.onload = () => {
          self.xhr.send(formData);
        };
        reader.readAsBinaryString(file);
      };

      self.uploadElement.addEventListener('change', fileSelectHandler, false);
      self.uploadElement.addEventListener('dragover', fileDragHover, false);
      self.uploadElement.addEventListener('dragleave', fileDragLeave, false);
      self.uploadElement.addEventListener('drop', fileSelectHandler, false);
    };
    const assignTriggers = () => {
    };
    validateOptions();
    createGUI();
    assignTriggers();
  }
}
