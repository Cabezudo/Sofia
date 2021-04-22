package net.cabezudo.sofia.addresses;

import net.cabezudo.sofia.cities.CityList;
import net.cabezudo.sofia.municipalities.MunicipalityList;
import net.cabezudo.sofia.postalcodes.PostalCodeList;
import net.cabezudo.sofia.states.StateList;
import net.cabezudo.sofia.streets.StreetList;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.07
 */
public class AddressDataList {

  private final StreetList streetList = new StreetList();
  private final PostalCodeList postalCodeList = new PostalCodeList();
  private final CityList cityList = new CityList();
  private final MunicipalityList municipalityList = new MunicipalityList();
  private final StateList stateList = new StateList();

}
