package fr.cnes.sitools.logging.java.util.logging;

import java.io.IOException;
import java.util.logging.FileHandler;

/**
 * Specific FileHandler for Service access log. Only useful when using sitools with java.util.logging
 * 
 * 
 * @author m.gond
 */
public class FileHandlerLogAccessApplications extends FileHandler {
  /**
   * Construct a default <tt>FileHandlerLogAccessApplications</tt>. This will be configured entirely from
   * <tt>LogManager</tt> properties (or their default values).
   * <p>
   * 
   * @exception IOException
   *              if there are IO problems opening the files.
   * @exception SecurityException
   *              if a security manager exists and if the caller does not have <tt>LoggingPermission("control"))</tt>.
   */
  public FileHandlerLogAccessApplications() throws IOException, SecurityException {
    super();
  }

}