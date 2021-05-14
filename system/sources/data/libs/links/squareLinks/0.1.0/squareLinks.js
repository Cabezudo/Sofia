/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */
/* global templateVariables */

class SquareLinks {
  constructor( { id = null, element = null } = {}) {
    console.log(`Create Square links`);
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
    };
    const createGUI = () => {
      console.log('SquareLinks :: createGUI :: Using element ', this.element);
      this.data = templateVariables.squareLinks;
      this.element.className = 'squareLinks';
      this.titleContainerDiv = document.createElement('DIV');
      this.titleContainerDiv.className = 'titleContainer';
      this.element.appendChild(this.titleContainerDiv);
      this.titleDiv = document.createElement('DIV');
      this.titleDiv.className = 'title';
      this.titleContainerDiv.appendChild(this.titleDiv);
      const contentDiv = document.createElement('DIV');
      contentDiv.className = 'content';
      this.element.appendChild(contentDiv);
      this.data.links.forEach(item => {
        const squareDiv = document.createElement('DIV');
        contentDiv.appendChild(squareDiv);
        squareDiv.className = 'square';

        let squareContentDiv;
        if (item.link) {
          squareContentDiv = document.createElement('A');
          squareContentDiv.href = item.link;
        } else {
          squareContentDiv = document.createElement('DIV');
        }
        squareDiv.appendChild(squareContentDiv);
        if (item.link) {
          squareContentDiv.className = 'squareContent link';
        } else {
          squareContentDiv.className = 'squareContent';
        }

        const titleDiv = document.createElement('DIV');
        squareContentDiv.appendChild(titleDiv);
        item.titleDiv = titleDiv;
        titleDiv.className = 'title';

        const descriptionDiv = document.createElement('DIV');
        squareContentDiv.appendChild(descriptionDiv);
        item.descriptionDiv = descriptionDiv;
        descriptionDiv.className = 'description';
      });
      setLanguage();
    };
    const setLanguage = () => {
      this.titleDiv.innerHTML = Core.getText(this.data.title);
      this.data.links.forEach(item => {
        item.titleDiv.innerHTML = Core.getText(item.title);
        item.descriptionDiv.innerHTML = Core.getText(item.description);
      });
    };
    const assignTriggers = () => {
    };
    validateOptions();
    createGUI();
    assignTriggers();
    Core.addOnSetLanguageFunction(setLanguage);
    return this;
  }
}
