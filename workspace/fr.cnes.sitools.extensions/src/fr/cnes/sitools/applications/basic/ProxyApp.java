 /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 * This file is part of SITools2.
 *
 * SITools2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SITools2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package fr.cnes.sitools.applications.basic;

import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.plugins.applications.business.AbstractApplicationPlugin;
import fr.cnes.sitools.plugins.applications.model.ApplicationPluginModel;
import fr.cnes.sitools.plugins.applications.model.ApplicationPluginParameter;
import fr.cnes.sitools.proxy.RedirectorProxy;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.routing.Redirector;
import org.restlet.routing.Router;
import org.restlet.routing.Template;

import java.util.HashSet;
import java.util.Set;

/**
 * Plugin Application to redirect client request to target template url in Redirector mode.
 *
 * @author jp.boignard
 *
 */
public final class ProxyApp extends AbstractApplicationPlugin {
  /** PARAM_URLCLIENT */
  private static final String PARAM_URLCLIENT = "urlClient";
  /** PARAM_USEPROXY */
  private static final String PARAM_USEPROXY = "useProxy";
  /** PARAM_MODE */
  private static final String PARAM_MODE = "mode";
  /** PARAM_CATEGORY */
  private static final String PARAM_CATEGORY = "category";

  /**
   * Default constructor
   *
   * @param context
   *          context
   */
  public ProxyApp(Context context) {
    super(context);
    constructor();
  }

  /**
   * Default constructor Used when getting parameters for generic configuration of an application instance
   */
  public ProxyApp() {
    super();
    constructor();
  }

  /**
   * Constructor with context and model of the application configuration used when actually creating application
   * instance
   *
   * @param arg0
   *          Restlet context
   * @param model
   *          model contains configuration parameters of the application instance
   */
  public ProxyApp(Context arg0, ApplicationPluginModel model) {
    super(arg0, model);

    // Category parameter of ProxyApp
    try {
      Category category = (Category.valueOf(getParameter(PARAM_CATEGORY).getValue()));

      if (null == model.getCategory()) {
        model.setCategory(category);
      }
      setCategory(category);

    } catch (Exception e) {
      getLogger().severe(e.getMessage());
    }

    register();
  }

  /** the common part of constructor */
  public void constructor() {

    this.getModel().setClassAuthor("AKKA Technologies");
    this.getModel().setClassVersion("0.1");
    this.getModel().setClassOwner("CNES");

    ApplicationPluginParameter param1 = new ApplicationPluginParameter();
    param1.setName(PARAM_URLCLIENT);
    param1.setDescription("template for client URL");
    this.addParameter(param1);

    ApplicationPluginParameter param2 = new ApplicationPluginParameter();
    param2.setName(PARAM_USEPROXY);
    param2.setDescription("TRUE for proxy usage");
    this.addParameter(param2);

    ApplicationPluginParameter param3 = new ApplicationPluginParameter();
    param3.setName(PARAM_MODE);
    param3
            .setDescription("CLIENT_PERMANENT=1 CLIENT_FOUND=2 CLIENT_SEE_OTHER=3 CLIENT_TEMPORARY=4 SERVER_OUTBOUND=6 SERVER_INBOUND=7");
    this.addParameter(param3);

    ApplicationPluginParameter param4 = new ApplicationPluginParameter();
    param4.setName(PARAM_CATEGORY);
    param4.setDescription("ADMIN");
    this.addParameter(param4);
  }

  @Override
  public void sitoolsDescribe() {
    this.setName("ProxyApp");
    this.setAuthor("AKKA Technologies");
    this.setOwner("CNES");
    this.setDescription("Proxy Application plugin");
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restlet.Application#createInboundRoot()
   */
  @Override
  public Restlet createInboundRoot() {
    Restlet redirector;

    Router router = new Router(getContext());
    router.setDefaultMatchingMode(Template.MODE_STARTS_WITH);

    ApplicationPluginParameter urlClientParam = this.getParameter("urlClient");
    ApplicationPluginParameter useProxy = this.getParameter("useProxy");
    ApplicationPluginParameter modeParam = this.getParameter("mode");

    String urlClient = urlClientParam.getValue();


    int mode = Integer.parseInt(modeParam.getValue());

    if (Redirector.MODE_SERVER_OUTBOUND == mode) {
      urlClient += "{rr}";
    }

    if (Boolean.parseBoolean(useProxy.getValue())) {
      redirector = new RedirectorProxy(getContext(), urlClient, mode);
    } else {
      redirector = new Redirector(getContext(), urlClient, mode);
    }

    // return redirector;
    router.attachDefault(redirector);
    return router;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restlet.ext.wadl.WadlApplication#handle(org.restlet.Request, org.restlet.Response)
   */
  @Override
  public void handle(Request request, Response response) {
    super.handle(request, response);
    // request.setHostRef(request.getResourceRef().getBaseRef());
  }

  @Override
  public Validator<AbstractApplicationPlugin> getValidator() {
    return new Validator<AbstractApplicationPlugin>() {

      @Override
      public Set<ConstraintViolation> validate(AbstractApplicationPlugin item) {
        Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
        ApplicationPluginParameter param = item.getParameter(PARAM_URLCLIENT);
        String value = param.getValue();
        if (value.equals("")) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("This parameter must be set");
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setInvalidValue(value);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }

        param = item.getParameter(PARAM_USEPROXY);
        value = param.getValue();
        if (!value.equals("TRUE") && !value.equals("FALSE")) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("This parameter must be either TRUE or FALSE");
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setInvalidValue(value);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }

        param = item.getParameter(PARAM_MODE);
        value = param.getValue();
        try {
          int mode = Integer.valueOf(value);
          if (mode < 1 || mode > 7) {
            ConstraintViolation constraint = new ConstraintViolation();
            constraint.setMessage("This parameter must be from 1 to 7");
            constraint.setLevel(ConstraintViolationLevel.CRITICAL);
            constraint.setInvalidValue(value);
            constraint.setValueName(param.getName());
            constraints.add(constraint);
          }
        } catch (NumberFormatException ex) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("This parameter must be from 1 to 7");
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setInvalidValue(value);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }

        param = item.getParameter(PARAM_CATEGORY);
        value = param.getValue();
        if (value.equals("")) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("This parameter should be set");
          constraint.setLevel(ConstraintViolationLevel.WARNING);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }
        return constraints;
      }

    };
  }
}
