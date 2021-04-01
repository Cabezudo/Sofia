package net.cabezudo.sofia.core.company;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.01
 */
public class CompanySubdomainManager {

  private static final CompanySubdomainManager INSTANCE = new CompanySubdomainManager();

  private final Map<String, Integer> map = new TreeMap<>();

  private CompanySubdomainManager() {
    //Utility classes should not have public constructors
  }

  public static CompanySubdomainManager getInstance() {
    return INSTANCE;
  }

  public void add(String subdomain, int id) {
    map.put(subdomain, id);
  }

  public Integer get(String subdomain) {
    return map.get(subdomain);
  }

}
