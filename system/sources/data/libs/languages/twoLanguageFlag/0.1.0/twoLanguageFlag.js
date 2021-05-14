/*
 * Created on: 11/05/2021
 * Author:     Esteban Cabezudo
 */

/* global Core */
/* global variables */

class TwoLanguageFlag {
  constructor( { id = null, element = null, data = null } = {}) {
    const language = variables.site.language;
    if (data === null) {
      this.data = {"languages": [
          {
            "twoLetterCode": "es",
            "image": "/images/flags/ES.png"
          },
          {
            "twoLetterCode": "en",
            "image": "/images/flags/US.png"
          }
        ]
      };
    } else {
      this.data = data;
    }
    const setFlag = () => {
      this.data.languages.forEach(language => {
        if (variables.site.language === language.twoLetterCode) {
          language.element.style.display = 'none';
        } else {
          language.element.style.display = 'block';
        }
      });
    };
    const validateOptions = () => {
      if (id !== null) {
        this.element = Core.validateById(id);
        this.id = id;
      } else {
        if (element === null) {
          throw Error('You must define a property id or a property element.');
        } else {
          this.element = element;
          this.id = this.element.id;
        }
      }
    };
    const createGUI = () => {
      console.log('TwoLanguageFlag :: createGUI :: Create flag using ', this.element);
      this.element.className = 'languageFlagContainer';
      this.data.languages.forEach(language => {
        const flagElement = document.createElement('DIV');
        flagElement.className = 'languageFlag';
        language.element = flagElement;
        flagElement.style.backgroundImage = `url('${language.image}')`;
        flagElement.addEventListener('click', () => {
          Core.sendGet(`/changeLanguage?${language.twoLetterCode}`, response => {
            Core.changeLanguageTo(language.twoLetterCode);
            setFlag();
          });
        });
        this.element.appendChild(flagElement);
      });
      setFlag();
    };
    validateOptions();
    createGUI();
    Core.addOnSetLanguageFunction(setFlag);
  }
}


