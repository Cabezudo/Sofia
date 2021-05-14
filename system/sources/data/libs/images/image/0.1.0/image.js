/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

const image = ({ id = null, element = null, src = null } = {}) => {
  let loaded = false;

  const validateOptions = () => {
    if (id === null && element === null && src === null) {
      throw Error('You must define a property id or element and a property url.');
    }
  };

  const createGUI = () => {
    if (id !== null) {
      element = Core.validateById(id);
    }

    const loadImage = () => {
      if (loaded) {
        return;
      }
      if (Core.isVisibleInScreen(element)) {
        loaded = true;
        const newImage = new Image();
        newImage.onload = () => {
          element.style.backgroundImage = `url('${src}')`;
        };
        newImage.src = src;
      }
    };
    loadImage();

    if (loaded === false) {
      window.addEventListener('show', e => {
        loadImage();
      });
      Core.addOnResizeFunction(e => {
        loadImage();
      });
      window.addEventListener('scroll', e => {
        loadImage();
      });
    }
  };

  validateOptions();
  createGUI();
  return element;
};
