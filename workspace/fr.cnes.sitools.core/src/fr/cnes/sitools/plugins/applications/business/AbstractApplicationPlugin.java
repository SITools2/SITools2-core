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
package fr.cnes.sitools.plugins.applications.business;

import java.util.Date;

import org.restlet.Context;
import org.restlet.representation.Representation;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsParameterizedApplication;
import fr.cnes.sitools.common.validator.Validable;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.plugins.applications.ApplicationPluginStoreInterface;
import fr.cnes.sitools.plugins.applications.model.ApplicationPluginModel;
import fr.cnes.sitools.plugins.applications.model.ApplicationPluginParameter;

/**
 * Abstract Application to override
 * 
 * 
 * @author m.gond (AKKA Technologies)
 */
public abstract class AbstractApplicationPlugin extends SitoolsParameterizedApplication implements Validable {

  /** The model to get the parameters */
  private ApplicationPluginModel model;

  /**
   * Default constructor
   * 
   * 
   */
  public AbstractApplicationPlugin() {
    super();
    if (this.model == null) {
      model = new ApplicationPluginModel();
    }
  }

  /**
   * Constructor with context.
   * 
   * @param context
   *          Restlet host context
   */
  public AbstractApplicationPlugin(Context context) {
    super(context);

    if (this.model == null) {
      model = new ApplicationPluginModel();
    }
  }

  /**
   * Constructor with context and representation of the application configuration.
   * 
   * @param arg0
   *          Restlet context
   * @param arg1
   *          wadl representation
   */
  public AbstractApplicationPlugin(Context arg0, Representation arg1) {
    super(arg0, arg1);
    defaultDescribe();

    if (this.model == null) {
      model = new ApplicationPluginModel();
    }
  }

  /**
   * Constructor with context and representation of the application configuration.
   * 
   * @param arg0
   *          Restlet context
   * @param model
   *          ApplicationPLuginModel object
   */
  public AbstractApplicationPlugin(Context arg0, ApplicationPluginModel model) {
    super(arg0);
    this.model = model;
    this.setName(model.getLabel());
    this.setDescription(model.getDescription());
    this.setModel(model);
    this.setId(model.getId());
    this.setCategory(null);

    if (model.getCategory() != null) {
      this.setCategory(model.getCategory());
      register();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.common.application.SitoolsApplication#start()
   */
  @Override
  public synchronized void start() throws Exception {
    super.start();
    boolean isSynchro = getIsSynchro();
    if (isStarted()) {
      ApplicationPluginStoreInterface store = (ApplicationPluginStoreInterface) getContext().getAttributes().get(
          ContextAttributes.APP_STORE);
      ApplicationPluginModel appModel = store.retrieve(getId());
      if (appModel != null) {
        if (!isSynchro) {
          model.setStatus("ACTIVE");
          model.setLastStatusUpdate(new Date());
          store.update(model);
        }
      }
    }
    else {
      ApplicationPluginStoreInterface store = (ApplicationPluginStoreInterface) getContext().getAttributes().get(
          ContextAttributes.APP_STORE);
      ApplicationPluginModel appModel = store.retrieve(getId());
      getLogger().warning("ApplicationPlugin should be started.");
      if (appModel != null) {
        if (!isSynchro) {
          appModel.setStatus("INACTIVE");
          appModel.setLastStatusUpdate(new Date());
          store.update(appModel);
        }

      }
    }
  }

  @Override
  public synchronized void stop() throws Exception {
    super.stop();
    boolean isSynchro = getIsSynchro();
    if (isStopped()) {
      ApplicationPluginStoreInterface store = (ApplicationPluginStoreInterface) getContext().getAttributes().get(
          ContextAttributes.APP_STORE);
      ApplicationPluginModel appModel = store.retrieve(getId());
      if (appModel != null) {
        if (!isSynchro) {
          appModel.setStatus("INACTIVE");
          appModel.setLastStatusUpdate(new Date());
          store.update(appModel);
        }
      }
    }
    else {
      ApplicationPluginStoreInterface store = (ApplicationPluginStoreInterface) getContext().getAttributes().get(
          ContextAttributes.APP_STORE);
      ApplicationPluginModel appModel = store.retrieve(getId());
      getLogger().warning("ApplicationPlugin should be stopped.");
      if (appModel != null) {
        if (!isSynchro) {
          appModel.setStatus("ACTIVE");
          appModel.setLastStatusUpdate(new Date());
          store.update(appModel);
        }
      }
    }

  }

  /**
   * Gets the model value
   * 
   * @return the model
   */
  public final ApplicationPluginModel getModel() {
    return model;
  }

  /**
   * Sets the value of model
   * 
   * @param model
   *          the model to set
   */
  public final void setModel(ApplicationPluginModel model) {
    this.model = model;
  }

  /**
   * Add the given parameter to the Model.
   * 
   * @param param
   *          the parameter
   */
  public final void addParameter(ApplicationPluginParameter param) {
    if (this.model == null) {
      model = new ApplicationPluginModel();
    }
    model.getParametersMap().put(param.getName(), param);
  }

  /**
   * Retrieve a parameter from its given name
   * 
   * @param paramName
   *          the name of the parameter
   * @return the parameter
   */
  public final ApplicationPluginParameter getParameter(String paramName) {
    if (this.model == null) {
      return null;
    }
    return model.getParametersMap().get(paramName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.common.validator.Validable#getValidator()
   */
  @Override
  public Validator<AbstractApplicationPlugin> getValidator() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Return true if the application is in synchro mode, false otherwise
   * 
   * @return true if the application is in synchro mode, false otherwise
   */
  private boolean getIsSynchro() {
    Object dontUpdateStatusDate = getContext().getAttributes().get("IS_SYNCHRO");
    if (dontUpdateStatusDate == null) {
      return false;
    }
    return ((Boolean) dontUpdateStatusDate);
  }

}
