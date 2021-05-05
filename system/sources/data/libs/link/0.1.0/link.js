/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */


class Link {
  constructor( { onClick = null, id = null, element = null, key = null, parameters = null } = {}) {
    console.log(`Create Link`);
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
      console.log('Link :: createGUI :: Using element ', element);
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
      if (this.key !== null && this.key.length > 0) {
        const value = Core.getText(this.key, this.parameters);
        console.log(`Link :: setLanguage :: Set text ${value} for`, this.element);
        this.setText(value);
        return;
      }
      if (this.id !== null && this.id.length > 0) {
        const value = Core.getText(this.id, this.parameters);
        console.log(`Link :: setLanguage :: Set text ${value} for`, this.element);
        this.setText(value);
        return;
      }
      console.log('Link :: setLanguage :: No key nor id to set text for link.');
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