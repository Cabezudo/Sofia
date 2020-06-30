/*
 * Created on: 18/06/2020
 * Author:     Esteban Cabezudo
 */

/* global Core */
/* global Node */



const simpleSICEditor = ({ id = null, element = null, height = null, autoFormat = true } = {}) => {
  let editor, messages;
  const validateOptions = () => {
    if (element === null && id === null) {
      throw Error('You must define a property id or a property element.');
    }
  };
  const getCaretOffset = node => {
    let length = 0;
    const doc = node.ownerDocument || node.document;
    const win = doc.defaultView || doc.parentWindow;
    if (win.getSelection) {
      const selection = win.getSelection();
      if (selection.rangeCount) {
        const range = selection.getRangeAt(0);
        const targetNode = range.endContainer;
        let end = false;
        const get = (node, tabs) => {
          if (end) {
            return;
          }
          const childs = node.childNodes;
          for (let i = 0; i < childs.length; i++) {
            if (end) {
              break;
            }
            const child = childs[i];
            if (child.nodeType === Node.TEXT_NODE) {
              if (child === targetNode) {
                length += range.endOffset;
                end = true;
                break;
              }
              length += child.nodeValue.length;
            } else {
              if (child.tagName === 'DIV') {
                if (child === targetNode) {
                  length += 1;
                  end = true;
                  break;
                }
                length += 1;
              }
              get(child, tabs + 1);
            }
          }
        };
        get(node, 0);
      }
    }
    return length;
  };

  const setCaretOffset = (node, chars) => {
    const set = (node, tabs) => {
      const childs = node.childNodes;
      for (let i = 0; i < childs.length; i++) {
        const child = childs[i];
        lastChild = child;
        if (child.nodeType === Node.TEXT_NODE) {
          const length = child.nodeValue.length;
          if (length >= remaining) {
            return {node: child, offset: remaining};
          }
          remaining -= length;
        } else {
          if (child.tagName === 'DIV') {
            remaining -= 1;
            if (remaining === 0) {
              return {node: child, offset: remaining};
            }
          }
        }
        const result = set(child, tabs++);
        if (result) {
          return result;
        }
      }
    };

    let lastChild;
    let remaining = chars;
    const doc = node.ownerDocument || node.document;
    const win = doc.defaultView || doc.parentWindow;
    if (window.getSelection) {
      const selection = win.getSelection();
      if (selection.rangeCount) {
        selection.removeAllRanges();
        const result = set(node, chars, 0);
        const newNode = result.node;
        const offset = result.offset;
        const range = document.createRange();
        range.selectNode(newNode);
        range.setStart(newNode, offset);
        range.setEnd(newNode, offset);
        selection.addRange(range);
      }
    }
  };

  const writeOnRange = (node, text) => {
    const doc = node.ownerDocument || node.document;
    const win = doc.defaultView || doc.parentWindow;
    if (window.getSelection) {
      const selection = win.getSelection();
      if (selection.rangeCount) {
        const range = selection.getRangeAt(0);
        range.deleteContents();
        const node = document.createTextNode('  ');
        range.insertNode(node);
        range.collapse(false);
      }
    }
  };

  const toText = node => {
    const getText = (node, tabs) => {
      tabs++;
      let text = '';
      const childs = node.childNodes;
      childs.forEach(node => {
        if (node.nodeType === Node.TEXT_NODE) {
          text += node.nodeValue;
        } else {
          text += getText(node, 0);
          if (node.tagName === 'DIV') {
            text += '\n';
          }
        }
      });
      return text;
    };
    const text = getText(node);
    return text;
  };
  const highligth = () => {
    const code = toText(editor);
    Core.sendPost('/api/v1/sic/tokens/compile', editor, {code});
  };
  const format = () => {
  }
  ;
  const createGUI = () => {
    if (element === null) {
      element = Core.validateById(id);
    }
    element.className = 'simpleSICEditor';
    element.style.height = height;
    editor = document.createElement('div');
    editor.innerHTML = `<div>${element.innerHTML}</div>`;
    editor.setAttribute('spellcheck', "false");
    editor.setAttribute('contenteditable', "true");
    Core.removeChilds(element);
    element.appendChild(editor);

    messages = document.createElement('div');
    messages.className = 'messages';
    element.appendChild(messages);

    if (autoFormat) {
      format();
      highligth();
    }
  };

  const assignTriggers = () => {
    editor.addEventListener('response', event => {
      const data = event.detail;
      const tokens = data.tokens;
      let html = '';
      let line = '';
      tokens.forEach(token => {
        if (token.value === '\n') {
          if (line === '') {
            line = '<br>';
          }
          html += `<div>${line}</div>`;
          line = '';
        } else {
          let clazz;
          if (token.error) {
            if (token.class === 'none') {
              clazz = `error`;
            } else {
              clazz = `${token.class} error`;
            }
          } else {
            clazz = token.class;
          }
          if (clazz && clazz !== "none") {
            line += `<span class="${clazz}">${token.value}</span>`;
          } else {
            line += token.value;
          }
        }
      });
      const offset = getCaretOffset(editor);

      editor.innerHTML = html;

      if (offset) {
        setCaretOffset(editor, offset);
      }

      Core.removeChilds(messages);
      data.messages.forEach(item => {
        const messageContainer = document.createElement('div');
        messageContainer.innerHTML = `${item.message} Line ${item.position.line}, row ${item.position.row}`;
        messages.appendChild(messageContainer);
      });
    });
    element.addEventListener("keydown", event => {
      if (Core.isTab(event)) {
        writeOnRange(editor, '  ');
        event.preventDefault();
      }
      if (Core.isModifierKey(event) || Core.isNavigationKey(event)) {
        return;
      }
    });
    element.addEventListener("keyup", event => {
      if (Core.isModifierKey(event) || Core.isNavigationKey(event)) {
        return;
      }
      highligth();
//      format();
    });
  };
  validateOptions();
  createGUI();
  assignTriggers();
}
;