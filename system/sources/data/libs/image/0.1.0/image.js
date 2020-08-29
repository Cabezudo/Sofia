/*
 * Created on: 30/10/2018
 * Author:     Esteban Cabezudo
 */

/* global Core */

const image = ({ id = null, url = null } = {}) => {
  let element;
  let loaded = false;

  const validateOptions = () => {
    if (id === null && url === null) {
      throw Error('You must define a property id and a property url.');
    }
  };

  const createGUI = () => {
    element = Core.validateById(id);

    const loadImage = () => {
      if (loaded) {
        return;
      }
      if (Core.isInsideViewport(element)) {
        loaded = true;
        const newImage = new Image();
        newImage.onload = () => {
          element.style.backgroundImage = `url('${url}')`;
        };
        newImage.src = url;
      }
    };
    loadImage();

    if (loaded === false) {
      window.addEventListener('scroll', e => {
        loadImage();
      });
    }
  };

  validateOptions();
  createGUI();
  return element;
};
