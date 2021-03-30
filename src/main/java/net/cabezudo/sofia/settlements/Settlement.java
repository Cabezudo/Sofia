package net.cabezudo.sofia.settlements;

import net.cabezudo.sofia.cities.City;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.municipalities.Municipality;
import net.cabezudo.sofia.zones.Zone;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class Settlement {

  private final int id;
  private final SettlementType type;
  private final City city;
  private final Municipality municipality;
  private final Zone zone;
  private final String name;

  public Settlement(int id, SettlementType type, City city, Municipality municipality, Zone zone, String name) {
    this.id = id;
    this.type = type;
    this.city = city;
    this.municipality = municipality;
    this.zone = zone;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public SettlementType getType() {
    return type;
  }

  public City getCity() {
    return city;
  }

  public Municipality getMunicipality() {
    return municipality;
  }

  public Zone getZone() {
    return zone;
  }

  public String getName() {
    return name;
  }

  public Object toJSONTree() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  public Object toWebListTree(Language language) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
