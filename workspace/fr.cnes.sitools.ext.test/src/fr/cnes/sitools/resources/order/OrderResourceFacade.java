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
package fr.cnes.sitools.resources.order;

import org.restlet.data.Form;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.database.common.DataSetExplorerUtil;
import fr.cnes.sitools.tasks.TaskUtils;

/**
 * Facade for OrderResource
 * 
 * 
 * @author m.gond
 */
public class OrderResourceFacade extends SitoolsParameterizedResource implements IOrderResource {
  /**
   * Description de la ressource
   */
  @Override
  public void sitoolsDescribe() {
    setName("OrderResourceFacade");
    setDescription("Resource to order data");
  }

  /**
   * Description WADL de la methode POST
   * 
   * @param info
   *          The method description to update.
   */
  @Override
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to order data from a dataset");
    info.setIdentifier("order");
    addStandardPostOrPutRequestInfo(info);
    DataSetExplorerUtil.addDatasetExplorerGetRequestInfo(info);
    DataSetApplication application = (DataSetApplication) getApplication();
    DataSetExplorerUtil.addDatasetExplorerGetFilterInfo(info, application.getFilterChained());
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
    this.addInfo(info);
  }

  /**
   * Create the order
   * 
   * @param represent
   *          the {@link Representation} entity
   * @param variant
   *          The {@link Variant} needed
   * @return a representation
   */
  public Representation orderPost(Representation represent, Variant variant) {
    processBody();
    return TaskUtils.execute(this, variant);
  }

  /**
   * Create the order
   * 
   * @param variant
   *          The {@link Variant} needed
   * @return a representation
   */
  public Representation orderGet(Variant variant) {
    return TaskUtils.execute(this, variant);
  }

  /**
   * process the body and save the request entity {@link Representation}
   */
  public void processBody() {
    Representation body = this.getRequest().getEntity();
    if (body != null && body.isAvailable() && body.getSize() > 0) {
      Form bodyForm = new Form(body);
      getContext().getAttributes().put(TaskUtils.BODY_CONTENT, bodyForm);
    }
    else {
      getContext().getAttributes().remove(TaskUtils.BODY_CONTENT);
    }
  }

}
