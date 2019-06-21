/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

const linkTo_1_00_00 = ({ onClick = null, element = null } = {}) => {
  const validateOptions = () => {
    if (onClick === null) {
      throw Error('You must define a url or function in a property onClick.');
    }
    if (element === null) {
      throw Error('You must define a property element.');
    }
  };
  const createGUI = () => {
    element.classList.add('linkTo-1_00_00');
    element.onclick = () => {
      if (typeof onClick === 'function') {
        onClick(this);
      } else {
        document.location.href = onClick;
      }
    };
  };
  const assignTriggers = () => {
  };
  validateOptions();
  createGUI();
  assignTriggers();
}
;