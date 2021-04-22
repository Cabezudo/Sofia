package net.cabezudo.sofia.geography;

import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.configuration.ConfigurationException;
import net.cabezudo.sofia.core.data.DataCreator;
import net.cabezudo.sofia.core.exceptions.DataConversionException;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.languages.InvalidTwoLettersCodeException;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.languages.LanguageManager;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.05
 */
public class AdministrativeDivisionCreator implements DataCreator {

  @Override
  public void create() throws ClusterException, ConfigurationException, DataConversionException {
    try {
      Language en = LanguageManager.getInstance().get("en");
      Language es = LanguageManager.getInstance().get("es");

      AdministrativeDivision af = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.CONTINENT, "AF", null, null);
      AdministrativeDivisionManager.getInstance().add(af, en, "Africa");
      AdministrativeDivisionManager.getInstance().add(af, es, "África");

      AdministrativeDivision an = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.CONTINENT, "AN", null, null);
      AdministrativeDivisionManager.getInstance().add(an, en, "Antarctica");
      AdministrativeDivisionManager.getInstance().add(an, es, "Antártida");

      AdministrativeDivision as = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.CONTINENT, "AS", null, null);
      AdministrativeDivisionManager.getInstance().add(as, en, "Asia");
      AdministrativeDivisionManager.getInstance().add(as, es, "Asia");

      AdministrativeDivision eu = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.CONTINENT, "EU", null, null);
      AdministrativeDivisionManager.getInstance().add(eu, en, "Europe");
      AdministrativeDivisionManager.getInstance().add(eu, es, "Europa");

      AdministrativeDivision na = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.CONTINENT, "NA", null, null);
      AdministrativeDivisionManager.getInstance().add(na, en, "North america");
      AdministrativeDivisionManager.getInstance().add(na, es, "Norteamérica");

      AdministrativeDivision mx = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.COUNTRY, "MX", 114686, na);
      AdministrativeDivisionManager.getInstance().add(mx, en, "Mexico");
      AdministrativeDivisionManager.getInstance().add(mx, es, "México");

      AdministrativeDivision agu = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "AGU", 2610002, mx);
      AdministrativeDivisionManager.getInstance().add(agu, es, "Aguascalientes");

      AdministrativeDivision bcn = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "BCN", 2589601, mx);
      AdministrativeDivisionManager.getInstance().add(bcn, es, "Baja California");

      AdministrativeDivision bcs = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "BCS", 2589611, mx);
      AdministrativeDivisionManager.getInstance().add(bcs, es, "Baja California Sur");

      AdministrativeDivision cam = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "CAM", 2568834, mx);
      AdministrativeDivisionManager.getInstance().add(cam, es, "Campeche");

      AdministrativeDivision chp = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "CHP", 2556679, mx);
      AdministrativeDivisionManager.getInstance().add(chp, es, "Chiapas");

      AdministrativeDivision chh = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "CHH", 1673425, mx);
      AdministrativeDivisionManager.getInstance().add(chh, es, "Chihuahua");

      AdministrativeDivision cmx = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "CMX", 1376330, mx);
      AdministrativeDivisionManager.getInstance().add(cmx, es, "Ciudad de México");

      AdministrativeDivision coa = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "COA", 1661524, mx);
      AdministrativeDivisionManager.getInstance().add(coa, es, "Coahuila");

      AdministrativeDivision col = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "COL", 2340912, mx);
      AdministrativeDivisionManager.getInstance().add(col, es, "Colima");

      AdministrativeDivision dur = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "DUR", 2399740, mx);
      AdministrativeDivisionManager.getInstance().add(dur, es, "Durango");

      AdministrativeDivision gua = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "GUA", 2340909, mx);
      AdministrativeDivisionManager.getInstance().add(gua, es, "Guanajuato");

      AdministrativeDivision gro = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "GRO", 2439316, mx);
      AdministrativeDivisionManager.getInstance().add(gro, es, "Guerrero");

      AdministrativeDivision hid = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "HID", 1376490, mx);
      AdministrativeDivisionManager.getInstance().add(hid, es, "Hidalgo");

      AdministrativeDivision jal = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "JAL", 2340910, mx);
      AdministrativeDivisionManager.getInstance().add(jal, es, "Jalisco");

      AdministrativeDivision mex = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "MEX", 1376489, mx);
      AdministrativeDivisionManager.getInstance().add(mex, es, "México");

      AdministrativeDivision mic = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "MIC", 2340636, mx);
      AdministrativeDivisionManager.getInstance().add(mic, es, "Michoacán");

      AdministrativeDivision mor = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "MOR", 1376332, mx);
      AdministrativeDivisionManager.getInstance().add(mor, es, "Morelos");

      AdministrativeDivision nay = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "NAY", 7695827, mx);
      AdministrativeDivisionManager.getInstance().add(nay, es, "Nayarit");

      AdministrativeDivision nle = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "NLE", 1661523, mx);
      AdministrativeDivisionManager.getInstance().add(nle, es, "Nuevo León");

      AdministrativeDivision oax = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "OAX", 2529822, mx);
      AdministrativeDivisionManager.getInstance().add(oax, es, "Oaxaca");

      AdministrativeDivision pue = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "PUE", 1376491, mx);
      AdministrativeDivisionManager.getInstance().add(pue, es, "Puebla");

      AdministrativeDivision que = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "QUE", 2340903, mx);
      AdministrativeDivisionManager.getInstance().add(que, es, "Querétaro");

      AdministrativeDivision roo = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "ROO", 2614434, mx);
      AdministrativeDivisionManager.getInstance().add(roo, es, "Quintana Roo");

      AdministrativeDivision slp = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "SLP", 4086617, mx);
      AdministrativeDivisionManager.getInstance().add(slp, es, "San Luis Potosí");

      AdministrativeDivision sin = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "SIN", 2455086, mx);
      AdministrativeDivisionManager.getInstance().add(sin, es, "Sinaloa");

      AdministrativeDivision son = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "SON", 1673426, mx);
      AdministrativeDivisionManager.getInstance().add(son, es, "Sonora");

      AdministrativeDivision tab = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "TAB", 2556680, mx);
      AdministrativeDivisionManager.getInstance().add(tab, es, "Tabasco");

      AdministrativeDivision tam = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "TAM", 2415518, mx);
      AdministrativeDivisionManager.getInstance().add(tam, es, "Tamaulipas");

      AdministrativeDivision tla = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "TLA", 1375274, mx);
      AdministrativeDivisionManager.getInstance().add(tla, es, "Tlaxcala");

      AdministrativeDivision ver = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "VER", 2415761, mx);
      AdministrativeDivisionManager.getInstance().add(ver, es, "Veracruz");

      AdministrativeDivision yuc = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "YUC", 2614435, mx);
      AdministrativeDivisionManager.getInstance().add(yuc, es, "Yucatán");

      AdministrativeDivision zac = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.STATE, "ZAC", 2399704, mx);
      AdministrativeDivisionManager.getInstance().add(zac, es, "Zacatecas");

      AdministrativeDivision oc = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.CONTINENT, "OC", null, null);
      AdministrativeDivisionManager.getInstance().add(oc, en, "Oceania");
      AdministrativeDivisionManager.getInstance().add(oc, es, "Oceanía");

      AdministrativeDivision sa = AdministrativeDivisionManager.getInstance().add(AdministrativeDivisionType.CONTINENT, "SA", null, null);
      AdministrativeDivisionManager.getInstance().add(sa, en, "South america");
      AdministrativeDivisionManager.getInstance().add(sa, es, "Sur américa");

    } catch (InvalidTwoLettersCodeException e) {
      throw new SofiaRuntimeException(e);
    }
  }
}
