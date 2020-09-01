package net.cabezudo.sofia.restaurants;

import java.math.BigDecimal;
import java.util.Currency;
import net.cabezudo.sofia.addresses.Address;
import net.cabezudo.sofia.core.money.Money;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.27
 */
public class Restaurant {

  private final int id;
  private Company company;
  private final String subdomain;
  private final String imageName;
  private final String name;
  private String location;
  private final RestaurantType type;
  private int priceRange;
  private final Currency currency;
  private Money shippingCost;
  private int minDeliveryTime;
  private int maxDeliveryTime;
  private int score;
  private BigDecimal longitude;
  private BigDecimal latitude;
  private Address address;
  private BusinessHours businessHours;

  public Restaurant(String subdomain, String image, String name, RestaurantType type, Currency currency) {
    this(0, subdomain, image, name, type, currency);
  }

  public Restaurant(int id, String subdomain, String image, String name, RestaurantType type, Currency currency) {
    this.id = id;
    this.subdomain = subdomain;
    this.imageName = image;
    this.name = name;
    this.type = type;
    this.currency = currency;
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

  public void setLocation(String location) {
    this.location = location;
  }

  public RestaurantType getType() {
    return type;
  }

  public void setPriceRange(int priceRange) {
    this.priceRange = priceRange;
  }

  public int getPriceRange() {
    return priceRange;
  }

  public Currency getCurrency() {
    return currency;
  }

  public void setShippingCost(Money shippingCost) {
    this.shippingCost = shippingCost;
  }

  public Money getShippingCost() {
    return shippingCost;
  }

  public void setDeliveryTime(int deliveryTime) {
    this.minDeliveryTime = deliveryTime;
    this.maxDeliveryTime = deliveryTime;
  }

  public void setMinDeliveryTime(int minDeliveryTime) {
    this.minDeliveryTime = minDeliveryTime;
  }

  public int getMinDeliveryTime() {
    return minDeliveryTime;
  }

  public void setMaxDeliveryTime(int maxDeliveryTime) {
    this.maxDeliveryTime = maxDeliveryTime;
  }

  public int getMaxDeliveryTime() {
    return maxDeliveryTime;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getScore() {
    return score;
  }

  public void setLongitude(BigDecimal longitude) {
    this.longitude = longitude;
  }

  public BigDecimal getLongitude() {
    return longitude;
  }

  public void setLatitude(BigDecimal latitude) {
    this.latitude = latitude;
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

  public void setBusinessHours(BusinessHours businessHours) {
    this.businessHours = businessHours;
  }

  public BusinessHours getBusinessHours() {
    return businessHours;
  }

}
