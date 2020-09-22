package net.cabezudo.sofia.restaurants;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.addresses.Address;
import net.cabezudo.sofia.core.money.Money;
import net.cabezudo.sofia.core.schedule.BusinessHours;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.27
 */
public class Restaurant implements Comparable<Restaurant> {

  private final int id;
  private Company company;
  private final String subdomain;
  private final String imageName;
  private final String name;
  private final String location;
  private final RestaurantType type;
  private final int priceRange;
  private final Currency currency;
  private final Money shippingCost;
  private final DeliveryRange deliveryRange;
  private final Integer score;
  private final Integer numberOfVotes;
  private final BigDecimal longitude;
  private final BigDecimal latitude;
  private final BusinessHours businessHours;
  private Address address;

  Restaurant(int id, String subdomain, String imageName, String name, String location, RestaurantType type, int priceRange, Currency currency, Money shippingCost, DeliveryRange deliveryRange, Integer score, Integer numberOfVotes, BigDecimal latitude, BigDecimal longitude, BusinessHours businessHours) {
    this.id = id;
    this.subdomain = subdomain;
    this.imageName = imageName;
    this.name = name;
    this.location = location;
    this.type = type;
    this.priceRange = priceRange;
    this.currency = currency;
    this.shippingCost = shippingCost;
    this.deliveryRange = deliveryRange;
    this.score = score;
    this.numberOfVotes = numberOfVotes;
    this.latitude = latitude;
    this.longitude = longitude;
    this.businessHours = businessHours;
  }

  public int getId() {
    return id;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public String getSubdomain() {
    return subdomain;
  }

  public String getImageName() {
    return imageName;
  }

  public String getName() {
    return name;
  }

  public String getLocation() {
    return location;
  }

  public RestaurantType getType() {
    return type;
  }

  public int getPriceRange() {
    return priceRange;
  }

  public Currency getCurrency() {
    return currency;
  }

  public Money getShippingCost() {
    return shippingCost;
  }

  public DeliveryRange getDeliveryRange() {
    return deliveryRange;
  }

  public Integer getScore() {
    return score;
  }

  public Integer getNumberOfVotes() {
    return numberOfVotes;
  }

  public BigDecimal getLongitude() {
    return longitude;
  }

  public BigDecimal getLatitude() {
    return latitude;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public Address getAddress() {
    return address;
  }

  public BusinessHours getBusinessHours() {
    return businessHours;
  }

  public String toJSON() {
    return toJSONTree().toString();
  }

  public JSONObject toJSONTree() {
    JSONObject jsonRestaurant = new JSONObject();
    jsonRestaurant.add(new JSONPair("id", id));
    jsonRestaurant.add(new JSONPair("company", company == null ? null : company.toJSONTree()));
    jsonRestaurant.add(new JSONPair("subdomain", subdomain));
    jsonRestaurant.add(new JSONPair("imageName", imageName));
    jsonRestaurant.add(new JSONPair("name", name));
    jsonRestaurant.add(new JSONPair("location", location));
    jsonRestaurant.add(new JSONPair("type", type.toJSONTree()));
    jsonRestaurant.add(new JSONPair("priceRange", priceRange));
    jsonRestaurant.add(new JSONPair("currency", currency.getNumericCode()));
    jsonRestaurant.add(new JSONPair("shippingCost", shippingCost.toJSONTree()));
    jsonRestaurant.add(new JSONPair("deliveryRange", deliveryRange.toJSONTree()));
    jsonRestaurant.add(new JSONPair("score", score));
    jsonRestaurant.add(new JSONPair("numberOfVotes", numberOfVotes));
    jsonRestaurant.add(new JSONPair("longitude", longitude));
    jsonRestaurant.add(new JSONPair("latitude", latitude));
    jsonRestaurant.add(new JSONPair("address", address == null ? null : address.toJSONTree()));
    return jsonRestaurant;
  }

  @Override
  public int compareTo(Restaurant r) {
    boolean thisIsOpen = getBusinessHours() != null && getBusinessHours().isOpen() != null && getBusinessHours().isOpen();
    boolean otherIsOpen = r.getBusinessHours() != null && r.getBusinessHours().isOpen() != null && r.getBusinessHours().isOpen();
    if (thisIsOpen && otherIsOpen) {
      return this.name.compareTo(r.getName());
    }
    if (thisIsOpen) {
      return -1;
    }
    if (otherIsOpen) {
      return 1;
    }
    return this.name.compareTo(r.getName());
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Restaurant) {
      Restaurant r = (Restaurant) o;
      return this.compareTo(r) == 0;
    }
    return false;
  }

  @Override
  public int hashCode() {
    boolean isOpen = getBusinessHours() != null && getBusinessHours().isOpen() != null && getBusinessHours().isOpen();
    int hash = 7;
    hash = 37 * hash + Objects.hashCode(isOpen);
    hash = 37 * hash + Objects.hashCode(this.businessHours);
    return hash;
  }

}
