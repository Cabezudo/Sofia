/*
 * Created on: 18/06/2020
 * Author:     Esteban Cabezudo
 */

/* global Core */



const simpleSICEditor = ({ id = null, element = null, height = null, autoFormat = true } = {}) => {
  let editor;

  const validateOptions = () => {
    if (element === null && id === null) {
      throw Error('You must define a property id or a property element.');
    }
  };
  const highligth = () => {
    const text = editor.innerText || editor.textContent;
    let html = text.replace('resize', '<span class="function">resize</span>');
    html = html.replace('width', '<span class="parameter">width</span>');
    html = html.replace('height', '<span class="parameter">height</span>');

    const {selection, caretOffset} = Core.ContentEditable.getCaretOffset(editor);
    console.log(caretOffset);

    editor.innerHTML = html;

    if (caretOffset) {
      selection.removeAllRanges();

      const range = Core.ContentEditable.setCaretOffset(editor.parentNode, caretOffset);
      range.collapse(false);
      selection.addRange(range);
    }
  };

  const format = () => {
    const getSpaces = tabs => {
      let out = '';
      for (let i = 0; i < tabs; i += 1) {
        out += '  ';
      }
      return out;
    };
    const text = editor.innerText || editor.textContent;

    const {selection, startOffset} = Core.ContentEditable.getOffsets();

    let tabs = 0;
    let formatedText = '';
    let isBlankLine = true;
    for (let i = 0; i < text.length; i++) {
      const char = text.charAt(i);
      switch (char) {
        case '\n':
          break;
        case ' ':
          if (isBlankLine) {
            break;
          }
          formatedText += char;
          break;
        case '(':
          tabs++;
          formatedText += char;
          formatedText += '\n';
          isBlankLine = true;
          formatedText += getSpaces(tabs);
          break;
        case ')':
          tabs--;
          formatedText += '\n';
          isBlankLine = true;
          formatedText += getSpaces(tabs);
          formatedText += char;
          break;
        case ',':
          formatedText += char;
          formatedText += '\n';
          isBlankLine = true;
          formatedText += getSpaces(tabs);
          break;
        default:
          formatedText += char;
          isBlankLine = false;
      }
    }
    ;

    editor.innerHTML = formatedText;

    if (startOffset) {
      selection.removeAllRanges();
      const range = Core.ContentEditable.setCaretOffset(editor.parentNode, startOffset);
      range.collapse(false);
      selection.addRange(range);
    }
  }
  ;
  const createGUI = () => {
    if (element === null) {
      element = Core.validateById(id);
    }
    element.className = 'simpleSICEditor';
    element.style.height = height;
    editor = document.createElement('div');
    editor.innerHTML = element.innerHTML;
    editor.setAttribute('spellcheck', "false");
    editor.setAttribute('contenteditable', "true");

    Core.removeChilds(element);
    element.appendChild(editor);
    if (autoFormat) {
      format();
      highligth();
    }
  };
  const assignTriggers = () => {
    element.addEventListener('response', event => {
    });
    element.addEventListener("keyup", event => {
      if (Core.isModifierKey(event) || Core.isNavigationKey(event)) {
        return;
      }
//      format();
      highligth();
    });
  };
  validateOptions();
  createGUI();
  assignTriggers();
}
;