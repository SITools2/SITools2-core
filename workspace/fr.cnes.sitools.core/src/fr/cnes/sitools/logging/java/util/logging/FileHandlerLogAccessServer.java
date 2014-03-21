package fr.cnes.sitools.logging.java.util.logging;

import java.io.IOException;
import java.util.logging.FileHandler;

/**
 * Specific FileHandler for the all server access log. Only useful when using sitools with java.util.logging
 * 
 * 
 * @author m.gond
 */
public class FileHandlerLogAccessServer extends FileHandler {
  /**
   * Construct a default <tt>FileHandlerLogAccessServer</tt>. This will be configured entirely from <tt>LogManager</tt>
   * properties (or their default values).
   * <p>
   * 
   * @exception IOException
   *              if there are IO problems opening the files.
   * @exception SecurityException
   *              if a security manager exists and if the caller does not have <tt>LoggingPermission("control"))</tt>.
   */
  public FileHandlerLogAccessServer() throws IOException, SecurityException {
    super();
  }

}