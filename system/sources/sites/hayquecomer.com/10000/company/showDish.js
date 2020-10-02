const showDish = (event, dish) => {
  let quantity = 1;
  let total;
  const setValues = () => {
    total = quantity * dish.price.cost;
    document.getElementById('quantityOnButton').innerHTML = quantity;
    document.getElementById('totalCost').innerHTML = `${dish.price.currency} ${total}`;
  };
  let description = dish.description;
  if (description === null) {
    description = '';
  }
  const content = `
            <div id="overlayContainer">
              <div id="overlayContent">
                <div id="dishContainer">
                  <div id="dishImage">
                    <div id="closeButton" class="closeButton"></div>
                  </div>
                  <div id="dishInfo">
                    <div id="dishTitle">${dish.name}</div>
                    <div id="dishDescription">${description}</div>
                    <div id="options"></div>
                  </div>
                  <div id="controlPanelContainer">
                    <div id="controlPanel">
                      <div id="buttonContainer">
                        <div id="removeButton"></div>
                        <div id="totalCost"></div>
                        <div id="addButton"></div>
                      </div>
                      <div id="addToOrder">
                        <div id="addToOrderText">Agregar <span id="quantityOnButton"></span> a la orden</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>`;
  overlay.setContent(content);
  // TODO ocultar con ESC
  crossRoundButton({
    id: 'closeButton',
    onClick: () => {
      overlay.hide();
    }
  });
  const removeButton = removeRoundButton({
    id: 'removeButton',
    onClick: () => {
      if (quantity > 1) {
        quantity--;
        setValues();
      }
      removeButton.enable();
    }
  });
  const addButton = addRoundButton({
    id: 'addButton',
    onClick: () => {
      if (quantity < 20) {
        quantity++;
        setValues();
      }
      addButton.enable();
    }
  });
  linkTo({
    id: 'addToOrder',
    onClick: () => {
      alert(`Add ${quantity} ${dish.name} to the order.`);
      overlay.hide();
    }
  });
  overlay.show();
  const dishImage = document.getElementById('dishImage');
  if (dish.imageName === null) {
    dishImage.className = 'noDishImage';
  } else {
    dishImage.className = 'dishImage';
    image({element: dishImage, src: `/images/${dish.imageName}?resize(height=400)`});
  }
  setValues();
};
