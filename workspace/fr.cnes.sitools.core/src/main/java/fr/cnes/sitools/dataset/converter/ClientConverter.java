     /*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.dataset.converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.engine.Engine;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsMediaType;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.converter.business.AbstractConverter;
import fr.cnes.sitools.dataset.converter.business.ConverterChained;
import fr.cnes.sitools.dataset.converter.model.ConverterChainedModel;
import fr.cnes.sitools.dataset.converter.model.ConverterModel;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Low coupling between DateSetApplication and Query Converters
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class ClientConverter {

  /**
   * Private constructor for utility class
   */
  private ClientConverter() {
    super();
  }

  /**
   * Gets the ConverterChained object instance for that DataSet
   * 
   * @param context
   *          the RESTlet context
   * @param datasetId
   *          the id of the DataSet
   * @return a converter chained model
   */
  public static ConverterChainedModel getConverterChainedModel(Context context, String datasetId) {
    DataSetApplication datasetApp = (DataSetApplication) context.getAttributes().get("DataSetApplication");
    SitoolsSettings settings = datasetApp.getSettings();

    Request reqGET = new Request(Method.GET, RIAPUtils.getRiapBase() + settings.getString(Consts.APP_DATASETS_URL)
        + "/" + datasetId + settings.getString(Consts.APP_DATASETS_CONVERTERS_URL));

    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(SitoolsMediaType.APPLICATION_JAVA_OBJECT_SITOOLS_MODEL));
    reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);

    org.restlet.Response response = context.getClientDispatcher().handle(reqGET);

    if (response == null || Status.isError(response.getStatus().getCode())) {
      // return null;
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
    }

    @SuppressWarnings("unchecked")
    ObjectRepresentation<Response> or = (ObjectRepresentation<Response>) response.getEntity();
    try {
      Response resp = or.getObject();
      ConverterChainedModel converterChainedModel = (ConverterChainedModel) resp.getItem();
      if (converterChainedModel != null && converterChainedModel.getConverters() != null) {
        List<ConverterModel> converters = converterChainedModel.getConverters();
        for (Iterator<ConverterModel> iterator = converters.iterator(); iterator.hasNext();) {
          ConverterModel model = iterator.next();
          if (!"ACTIVE".equals(model.getStatus())) {
            iterator.remove();
          }
        }
      }
      return converterChainedModel;
    }
    catch (IOException e) { // marshalling error
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
    }
  }

  /**
   * Returns an instance of the converterChained class
   * 
   * @param converterChainedModel
   *          the model
   * @param context
   *          the RESTlet context
   * @return a converterChained object with the parameterized converters
   */
  public static ConverterChained getConverterChained(Context context, ConverterChainedModel converterChainedModel) {


    ConverterChained conv = new ConverterChained();
    conv.setName(converterChainedModel.getName());
    conv.setDescription(converterChainedModel.getDescription());
    conv.setContext(context);

    if (converterChainedModel.getConverters() != null) {
      ConverterModel convModel;
      AbstractConverter convImpl;
      try {
        for (Iterator<ConverterModel> it = converterChainedModel.getConverters().iterator(); it.hasNext();) {
          convModel = it.next();

          @SuppressWarnings("unchecked")
          Class<AbstractConverter> classImpl = (Class<AbstractConverter>) Class.forName(convModel.getClassName());
          convImpl = classImpl.newInstance();
          convImpl.setParametersMap(convModel.getParametersMap());

          convImpl.setContext(context);
          conv.addConverter(convImpl);
        }

      }
      catch (ClassNotFoundException e) {
        Engine.getLogger(ClientConverter.class.getName()).severe(e.getMessage());
      }
      catch (InstantiationException e) {
        Engine.getLogger(ClientConverter.class.getName()).severe(e.getMessage());
      }
      catch (IllegalAccessException e) {
        Engine.getLogger(ClientConverter.class.getName()).severe(e.getMessage());
      }
    }

    return conv;
  }

}
