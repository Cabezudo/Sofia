package net.cabezudo.sofia.core.data;

import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.configuration.ConfigurationException;
import net.cabezudo.sofia.core.exceptions.DataConversionException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.05
 */
public interface DataCreator {

  void create() throws ClusterException, ConfigurationException, DataConversionException;
}
