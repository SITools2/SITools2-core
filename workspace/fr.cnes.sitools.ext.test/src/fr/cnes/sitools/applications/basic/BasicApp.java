/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import fr.cnes.sitools.plugins.applications.business.AbstractApplicationPlugin;
import fr.cnes.sitools.plugins.applications.model.ApplicationPluginModel;
import fr.cnes.sitools.plugins.applications.model.ApplicationPluginParameter;

/**
 * BasicApp for testing
 * 
 * @author m.gond
 * 
 */
public class BasicApp extends AbstractApplicationPlugin {
  /**
   * Default constructor
   * 
   * @param context
   *          context
   */
  public BasicApp(Context context) {
    super(context);
    constructor();
  }

  /** Default constructor */
  public BasicApp() {
    super();
    constructor();

  }

  /**
   * Constructor with context and model of the application configuration.
   * 
   * @param arg0
   *          Restlet context
   * @param model
   *          model
   */
  public BasicApp(Context arg0, ApplicationPluginModel model) {
    super(arg0, model);
  }

  /** the common part of constructor */
  public void constructor() {

    // création du paramètre
    ApplicationPluginParameter param1 = new ApplicationPluginParameter();
    param1.setName("param1");
    param1.setDescription("Description de param1");
    // ajout du paramètre
    this.addParameter(param1);

    this.getModel().setClassAuthor("AKKA Technologies");
    this.getModel().setClassVersion("0.1");
    this.getModel().setClassOwner("CNES");

  }

  @Override
  public void sitoolsDescribe() {
    this.setName("APP PLUGINS");
    this.setDescription("BASIC APPLICATION PLUGINS");
    this.setAuthor("AKKA Technologies");
    this.setOwner("CNES");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.Application#createInboundRoot()
   */
  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    router.attachDefault(BasicResource.class);
    // récupération du paramètre
    ApplicationPluginParameter param = this.getParameter("param1");
    if (param != null) {
      router.attach("/" + param.getValue(), BasicResource2.class);
    }

    return router;

  }

}
