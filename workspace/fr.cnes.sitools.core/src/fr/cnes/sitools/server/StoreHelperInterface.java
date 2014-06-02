package fr.cnes.sitools.server;

import java.util.Map;

import org.restlet.Context;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.exception.SitoolsException;

public interface StoreHelperInterface {

  /**
   * Initializes the context with default stores
   * 
   * @param context
   *          a Restlet {@link Context}. It must contains the global {@link SitoolsSettings}
   * @return the map of initial context
   * @throws SitoolsException
   *           if an error occured while creating the stores
   */
  public abstract Map<String, Object> initContext(Context context) throws SitoolsException;

}