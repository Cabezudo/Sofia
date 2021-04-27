/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */


class Link {
  constructor( { onClick = null, id = null, element = null, key = null, parameters = null } = {}) {
    this.key = key;
    this.parameters = parameters;
    this.id = id;
    this.element = element;

    const validateOptions = () => {
      if (id !== null) {
        this.element = Core.validateById(id);
        this.id = id;
      } else {
        if (element === null) {
          throw Error('You must define a property id or a property element.');
        }
        this.id = this.element.id;
      }
      if (onClick === null) {
        throw Error('You must define a url or function in a property onClick.');
      }
    };
    const createGUI = () => {
      this.element.classList.add('linkTo');
      const html = this.element.innerHTML;
      this.setText(html);
      this.element.onclick = () => {
        if (typeof onClick === 'function') {
          onClick(this);
        } else {
          document.location.href = onClick;
        }
      };
    };
    const setLanguage = () => {
      console.log('this.element: ', this.element);

      if (this.key) {
        console.log('this.key: ', this.key);
        this.setText(Core.getText(this.key, this.parameters));
        return;
      }
      if (this.id) {
        console.log('this.id: ', this.id);
        this.setText(Core.getText(this.id, this.parameters));
        return;
      }
    };
    const assignTriggers = () => {
    };
    validateOptions();
    createGUI();
    assignTriggers();
    Core.addOnSetLanguageFunction(setLanguage);
    return this;
  }

  setText(value) {
    this.element.innerHTML = `<div>${value}</div>`;
  }

  getId() {
    return this.element.id;
  }
}
;