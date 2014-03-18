package fr.cnes.sitools.util.logging;

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

public class SitoolsLogFilter extends Filter {

  private String loggerName;

  public SitoolsLogFilter(String securityLoggerName) {
    super();
    this.loggerName = securityLoggerName;
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
