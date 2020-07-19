package net.cabezudo.sofia.addresses;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class AddressManager {

  private static AddressManager INSTANCE;

  public static AddressManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new AddressManager();
    }
    return INSTANCE;
  }

  public AddressDataList list(String streetName, Integer postalCode, String settlementName, String cityName, String municipalityName, String stateName) {
    AddressDataList addressDataList = new AddressDataList();
    return addressDataList;
  }
}
