package fr.cnes.sitools.proxy;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.routing.Filter;

/**
 * DirectoryLogFilter Filter to manage logs on directory proxy resources (CSS/FTL/CGU)
 * 
 * @author tx.chevallier
 */
public class DirectoryLogFilter extends Filter {

  @Override
  protected void afterHandle(Request request, Response response) {
    // super.afterHandle(request, response);
    String description = this.getNext().getDescription();
    LogRecord record;

    if (response.getStatus().isError()) {
      record = new LogRecord(Level.INFO, "cannot access " + description.toUpperCase() + " editor  ");
    }
    else {
      if ((Boolean) response.getAttributes().get("IS_DIRECTORY_TARGET")) {
        record = new LogRecord(Level.FINE, "view of the " + description.toUpperCase() + " editor");
      }
      else {
        Reference ref = request.getResourceRef();
        if (request.getMethod().getName().equals(Method.GET.getName())) {
          record = new LogRecord(Level.FINE, "edit the " + description.toUpperCase() + " file " + ref.getLastSegment()
              + " in the editor");
        }
        else if (request.getMethod().getName().equals(Method.PUT.getName())) {
          record = new LogRecord(Level.INFO, "update the " + description.toUpperCase() + " file "
              + ref.getLastSegment() + " in the editor");
        }
        else {
          record = new LogRecord(Level.FINE, request.getMethod().getName() + " on " + description.toUpperCase()
              + " file" + ref.getLastSegment());
        }
      }

    }
    response.getAttributes().put("LOG_RECORD", record);

  }
}
