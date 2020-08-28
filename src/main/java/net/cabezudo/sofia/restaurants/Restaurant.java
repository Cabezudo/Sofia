package net.cabezudo.sofia.restaurants;


import java.math.BigDecimal;
import net.cabezudo.sofia.addresses.Address;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.27
 */
public class Restaurant {

  private Company company;
  private final String name;
  private String location;
  private String type;
  private int priceRange;
  private int deliveryTime;
  private BigDecimal longitude;
  private BigDecimal latitude;
  private Address address;
  private BusinessHours businessHours;

  public Restaurant(String name) {
    this.name = name;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getPriceRange() {
    return priceRange;
  }

  public void setPriceRange(int priceRange) {
    this.priceRange = priceRange;
  }

  public int getDeliveryTime() {
    return deliveryTime;
  }

  public void setDeliveryTime(int deliveryTime) {
    this.deliveryTime = deliveryTime;
  }

  public BigDecimal getLongitude() {
    return longitude;
  }

  public void setLongitude(BigDecimal longitude) {
    this.longitude = longitude;
  }

  public BigDecimal getLatitude() {
    return latitude;
  }

  public void setLatitude(BigDecimal latitude) {
    this.latitude = latitude;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public BusinessHours getBusinessHours() {
    return businessHours;
  }

  public void setBusinessHours(BusinessHours businessHours) {
    this.businessHours = businessHours;
  }

}
