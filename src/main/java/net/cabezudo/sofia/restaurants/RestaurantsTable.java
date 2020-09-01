package net.cabezudo.sofia.restaurants;

import net.cabezudo.sofia.addresses.AddressesTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.26
 */
public class RestaurantsTable {

  public static final String DATABASE = "hayQueComer";
  public static final String NAME = "restaurants";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`subdomain` VARCHAR(30) NOT NULL, "
          + "`imageName` VARCHAR(50) NOT NULL, "
          + "`name` VARCHAR(50) NOT NULL, "
          + "`location` VARCHAR(20), " // To differentiate one from the other
          + "`typeId` INT, "
          + "`priceRange` INT(1), "
          + "`currencyCode` CHAR(3) NOT NULL, "
          + "`shippingCost` DECIMAL(9,6) DEFAULT 0, "
          + "`minDeliveryTime` INT(3), "
          + "`maxDeliveryTime` INT(3), "
          + "`score` INT(1), "
          + "`longitude` DECIMAL(9,6), "
          + "`latitude` DECIMAL(8,6), "
          + "`addressId` INT(10), "
          + "PRIMARY KEY (`id`), "
          + "FOREIGN KEY (`typeId`) REFERENCES " + RestaurantTypesTable.NAME + "(`id`), "
          + "FOREIGN KEY (`addressId`) REFERENCES " + AddressesTable.DATABASE + "." + AddressesTable.NAME + "(`id`), "
          + "UNIQUE INDEX `iSubdomain` (`subdomain`), "
          + "UNIQUE INDEX `iName` (`name`)"
          + ") "
          + "CHARACTER SET = UTF8";

  private RestaurantsTable() {
    // Nothing to do here. Utility classes should not have public constructors.
  }
}
