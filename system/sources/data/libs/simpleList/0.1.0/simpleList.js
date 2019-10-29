/* global Core, fetch */

/*
 * Created on: 14/05/2019
 * Author:     Esteban Cabezudo
 *
 */

const simpleList = async ({ id = null, source = null, filterInputElement = null, cellMaker = null, onClick = null, notLoggedURL = '/' } = {}) => {
  const LIST_CLASS_NAME = "simpleList";
  const SCROLL_TIME_TO_FETCH = 200;
  const RESIZE_TIME_TO_CALCULATE = 200;
  const MESSAGE_BASE_CLASS = "messageBaseClass";
  const MESSAGE_CLASS_MESSAGE = "message";
  const MESSAGE_CLASS_WARNING = "warning";
  const MESSAGE_CLASS_ERROR = "error";
  const NUMBER_OF_OFFSET_RECORDS = 10;
  const STATUS_ATTRIBUTE_NAME = "status";
  const STATUS_PENDING = "pending";
  const STATUS_COMPLETED = "completed";
  const MESSAGE_LOADING_DATA = "Cargando datos para crear lista.";
  const MESSAGE_CREATING_LIST = "Creando lista.";
  const MESSAGE_I_CAN_NOT_CREATE_THE_LIST = "Ha sucedido un error al cargar la lista. Intente mas tarde por favor.";
  const MESSAGE_NO_RECORD_FOUND = "No hay registros en la base de datos";
  const RELOAD_LIST_EVENT_TIME_DELAY = 800;
  let numberOfRecordsPerReading;
  let resizeTimer;
  let lastFilterInputValue = '';
  let reloadTimer;
  let fetchTimer;
  let listElement;
  let tableContainer;
  let filterInput = null;
  let table;
  let tableBody;
  let totalRecords;
  let bodyHeight;
  let rowHeight;
  let firstVisibleRowNumber;
  let lastVisibleRowNumber;
  let visibleRowsNumber;
  let tableScroll;
  const calculateValues = () => {
    bodyHeight = tableBody.offsetHeight;
    const firstRow = tableBody.firstChild;
    rowHeight = firstRow.offsetHeight;
    const tableContainerHeight = tableContainer.offsetHeight;
    visibleRowsNumber = parseInt(tableContainerHeight / rowHeight);
    tableScroll = tableContainer.scrollTop;
    firstVisibleRowNumber = Math.round(tableScroll / rowHeight);
    lastVisibleRowNumber = firstVisibleRowNumber + visibleRowsNumber;
  };
  const loadAllData = () => {
    loadData(firstVisibleRowNumber - NUMBER_OF_OFFSET_RECORDS);
    loadData(lastVisibleRowNumber + NUMBER_OF_OFFSET_RECORDS);
  };
  const loadData = async rowNumber => {
    if (rowNumber < 0) {
      rowNumber = 0;
    }
    const page = Math.floor(rowNumber / numberOfRecordsPerReading);
    const milestoneId = page * numberOfRecordsPerReading;
    if (milestoneId > totalRecords) {
      return;
    }
    const rowId = getRowId(milestoneId);
    const rowElement = document.getElementById(rowId);
    const state = rowElement.getAttribute(STATUS_ATTRIBUTE_NAME);
    if (state === null) {
      fetchData(rowElement, milestoneId);
    }
  };
  const fetchData = (rowElement, start) => {
    rowElement.setAttribute(STATUS_ATTRIBUTE_NAME, STATUS_PENDING);
    let url = `${source}?offset=${start}`;
    return fetch(url)
            .then(response => {
              if (response.ok) {
                return response.json();
              } else {
                return null;
              }
            })
            .then(data => {
              if (data === null) {
                createErrorMessage(MESSAGE_I_CAN_NOT_CREATE_THE_LIST);
                return;
              }
              data.list.forEach(elementData => {
                const rowId = getRowId(elementData.row);
                const tableRow = document.getElementById(rowId);
                addTableData(tableRow, elementData);
                if (Core.isFunction(onClick)) {
                  tableRow.className = 'clickable';
                  tableRow.onclick = event => {
                    onClick(event, elementData);
                  };
                }
              });
              rowElement.setAttribute(STATUS_ATTRIBUTE_NAME, STATUS_COMPLETED);
            });
  };
  const showRecord = recordNumber => {
    newScroll = recordNumber * rowHeight - bodyHeight / 2;
    tableContainer.scrollTop = newScroll;
    tableBodyScroll = newScroll;
  };
  const createMessage = message => {
    abstractMessageCreator(message, MESSAGE_CLASS_MESSAGE);
  };
  const createErrorMessage = message => {
    abstractMessageCreator(message, MESSAGE_CLASS_ERROR);
  };
  const abstractMessageCreator = (message, ...className) => {
    const divMessage = document.createElement("DIV");
    divMessage.classList.add(MESSAGE_BASE_CLASS);
    className.forEach(className => {
      divMessage.classList.add(className);
    });
    Core.removeChilds(tableContainer);
    tableContainer.appendChild(divMessage);
    const messageTextNode = document.createTextNode(message);
    divMessage.appendChild(messageTextNode);
  };
  const addEmptyRow = (rowId) => {
    const tableRow = document.createElement("TR");
    tableRow.id = getRowId(rowId);
    if (Core.isFunction(cellMaker)) {
      const innerHTML = cellMaker();
      tableRow.innerHTML = innerHTML;
    } else {
      removeChilds(tableRow);
      const tableData = document.createElement("TD");
      tableRow.appendChild(tableData);
      const textNode = document.createTextNode('\u00A0');
      tableData.appendChild(textNode);
    }
    tableBody.appendChild(tableRow);
  };
  const addTableData = (tableRow, row) => {
    if (Core.isFunction(cellMaker)) {
      const innerHTML = cellMaker(row);
      tableRow.innerHTML = innerHTML;
    } else {
      removeChilds(tableRow);
      Object.keys(row).forEach(field => {
        if (field === 'row' || field === 'id') {
          return;
        }
        const tableData = document.createElement("TD");
        tableRow.appendChild(tableData);
        const textNode = document.createTextNode(`${row[field]}`);
        tableData.appendChild(textNode);
      });
    }
  };
  const getRowId = rowId => {
    return `${id}:${rowId}`;
  };
  const removeChilds = element => {
    while (element.firstChild) {
      element.removeChild(element.firstChild);
    }
  };
  const validateOptions = () => {
    listElement = Core.validateById(id);
    if (filterInputElement !== null) {
      filterInput = Core.validateElement(filterInputElement);
      if (filterInput.tagName !== 'INPUT') {
        throw new Error(`Can't use a ${filterInput.tagName} as filter input.`);
      }
    }
  };
  const createGUI = async() => {
    tableContainer = document.createElement('DIV');
    listElement.className = LIST_CLASS_NAME;
    listElement.appendChild(tableContainer);
//    onresize(() => {
//      if (resizeTimer) {
//        clearTimeout(resizeTimer);
//      }
//      resizeTimer = setTimeout(() => {
//        calculateValues();
//      }, RESIZE_TIME_TO_CALCULATE);
//    });
    createMessage(MESSAGE_LOADING_DATA);
    loadTable();
  };
  const loadTable = () => {
    let url = source;
    if (filterInput !== null && filterInput.value !== lastFilterInputValue) {
      lastFilterInputValue = filterInput.value;
      url += `?filters=${encodeURIComponent(filterInput.value)}`;
    }
    tableContainer.scrollTop = 0;

    fetch(`${url}`)
            .then(response => {
              if (response.ok) {
                return response.json();
              } else {
                return null;
              }
            })
            .then(data => {
              if (data === null) {
                createErrorMessage(MESSAGE_I_CAN_NOT_CREATE_THE_LIST);
                return;
              }

              if (data.status === 'NOT_LOGGED_IN') {
                document.location.replace(notLoggedURL);
              }

              totalRecords = data.totalRecords;
              const filters = data.filters;
              if (filterInput !== null) {
                filterInput.value = filters;
                lastFilterInputValue = filters;
              }
              if (totalRecords === 0) {
                createMessage(MESSAGE_NO_RECORD_FOUND);
                return;
              }
              numberOfRecordsPerReading = data.pageSize;
              removeChilds(tableContainer);
              createMessage(MESSAGE_CREATING_LIST);
              table = document.createElement("TABLE");
              tableBody = document.createElement("TBODY");
              table.appendChild(tableBody);
              removeChilds(tableContainer);
              tableContainer.appendChild(table);
              tableContainer.onscroll = event => {
                calculateValues();
                if (fetchTimer) {
                  clearTimeout(fetchTimer);
                }
                fetchTimer = setTimeout(() => {
                  loadAllData();
                }, SCROLL_TIME_TO_FETCH);
              };
              for (let i = 0; i < totalRecords; i++) {
                addEmptyRow(i);
              }
              calculateValues();
              loadAllData();
            });
  };
  const assignTriggers = () => {
    if (filterInput !== null) {
      filterInput.addEventListener('setFilter', event => {
        const data = event.detail;
        filterInput.value = data;
        loadTable();
      });
      filterInput.addEventListener("keyup", event => {
        if (Core.isModifierKey(event) || Core.isNavigationKey(event)) {
          return;
        }
        if (reloadTimer) {
          clearTimeout(reloadTimer);
        }
        reloadTimer = setTimeout(loadTable, RELOAD_LIST_EVENT_TIME_DELAY);
      });
    }
  };
  validateOptions();
  assignTriggers();
  createGUI();
};
