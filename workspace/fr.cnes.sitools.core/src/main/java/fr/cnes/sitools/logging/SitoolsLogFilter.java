package fr.cnes.sitools.logging;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ClientInfo;
import org.restlet.engine.Engine;
import org.restlet.routing.Filter;
import org.restlet.security.Role;

import com.google.common.base.Joiner;

import fr.cnes.sitools.security.SecurityUtil;

/**
 * Log filter that logs all the traces from the request response
 * 
 * 
 * @author m.gond
 */
public class SitoolsLogFilter extends Filter {
  /**
   * The name of the logger to use
   */
  private String loggerName;

  /**
   * Instantiates a new sitools log filter.
   * 
   * @param loggerName
   *          the name of the logger to use
   */
  public SitoolsLogFilter(String loggerName) {
    super();
    this.loggerName = loggerName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.routing.Filter#afterHandle(org.restlet.Request, org.restlet.Response)
   */
  @Override
  protected void afterHandle(Request request, Response response) {
    super.afterHandle(request, response);
    Object logRecordObj = response.getAttributes().get("LOG_RECORD");
    if (logRecordObj != null) {

      ClientInfo clientInfo = request.getClientInfo();
      String user = null;
      String profile = null;
      if (clientInfo != null && clientInfo.getUser() != null) {
        user = clientInfo.getUser().getIdentifier();
        profile = "";
        List<Role> roles = clientInfo.getRoles();
        Set<String> rolesStr = new HashSet<String>();
        for (Role role : roles) {
          if (!SecurityUtil.PUBLIC_ROLE.equals(role.getName())) {
            rolesStr.add(role.getName());
          }
        }
        profile += Joiner.on(",").join(rolesStr);
      }

      LogRecord logRecord = (LogRecord) logRecordObj;
      logRecord.setMessage("User: " + user + "\tProfile: " + profile + "\t" + logRecord.getMessage());
      Logger logger = Engine.getLogger(loggerName);
      logger.log(logRecord);

    }
  }
}
