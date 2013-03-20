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
package fr.cnes.sitools.dataset.converter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.restlet.Context;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.store.SitoolsStoreXML;
import fr.cnes.sitools.dataset.converter.model.ConverterChainedModel;
import fr.cnes.sitools.dataset.converter.model.ConverterModel;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;

/**
 * Implementation of converterStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public final class ConverterStoreXML extends SitoolsStoreXML<ConverterChainedModel> {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "converters";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Context
   */
  public ConverterStoreXML(File location, Context context) {
    super(ConverterChainedModel.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Context
   */
  public ConverterStoreXML(Context context) {
    super(ConverterChainedModel.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.converter.converterStore#create(fr.cnes.sitools.converter .model.converter)
   */
  @Override
  public ConverterChainedModel create(ConverterChainedModel converter) {
    ConverterChainedModel result = null;

    if (converter.getId() == null || "".equals(converter.getId())) {
      converter.setId(UUID.randomUUID().toString());
    }

    // Recherche sur l'id
    for (Iterator<ConverterChainedModel> it = getRawList().iterator(); it.hasNext();) {
      ConverterChainedModel current = it.next();
      if (current.getId().equals(converter.getId())) {
        getLog().info("ConverterChainedModel found");
        result = current;
        break;
      }
    }

    // ajout d'un id pour les objets converterModel
    // ajout des converterParameter dans le hashMap (parametersMap) et clean du
    // arrayList (parameters)
    if (converter.getConverters() != null) {
      for (Iterator<ConverterModel> it = converter.getConverters().iterator(); it.hasNext();) {
        ConverterModel convModel = it.next();
        if (convModel.getId() == null || "".equals(convModel.getId())) {
          convModel.setId(UUID.randomUUID().toString());
        }
      }

    }

    if (result == null) {
      getRawList().add(converter);
      result = converter;
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.project.ProjectStore#update(fr.cnes.sitools.project.model .Project)
   */
  @Override
  public ConverterChainedModel update(ConverterChainedModel converter) {
    ConverterChainedModel result = null;
    for (Iterator<ConverterChainedModel> it = getRawList().iterator(); it.hasNext();) {
      ConverterChainedModel current = it.next();
      if (current.getId().equals(converter.getId())) {
        getLog().info("Updating ConverterChainedModel");

        result = current;
        current.setDescription(converter.getDescription());
        current.setName(converter.getName());
        current.setParent(converter.getParent());

        // generate ids for converters if new converter have been added
        // ajout des converterParameter dans le hashMap (parametersMap) et clean
        // du
        // arrayList (parameters)
        if (converter.getConverters() != null) {
          ConverterModel converterModel;
          for (Iterator<ConverterModel> itConv = converter.getConverters().iterator(); itConv.hasNext();) {
            // generate Ids
            converterModel = itConv.next();
            if (converterModel.getId() == null || "".equals(converterModel.getId())) {
              converterModel.setId(UUID.randomUUID().toString());
            }
            // // fill hashMap
            // ConverterParameter conv;
            // for (Iterator<ConverterParameter> itConvParam = converterModel.getParameters().iterator(); itConvParam
            // .hasNext();) {
            // conv = itConvParam.next();
            // converterModel.getParametersMap().put(conv.getName(), conv);
            // }
            // converterModel.getParameters().clear();
          }
        }
        current.setConverters(converter.getConverters());

        it.remove();

        break;
      }
    }
    if (result != null) {
      getRawList().add(result);
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.project.ProjectStore#getList(fr.cnes.sitools.common.model .ResourceCollectionFilter)
   */
  @Override
  public List<ConverterChainedModel> getList(ResourceCollectionFilter filter) {
    List<ConverterChainedModel> result = new ArrayList<ConverterChainedModel>();
    if ((getRawList() == null) || (getRawList().size() <= 0) || (filter.getStart() > getRawList().size())) {
      return result;
    }

    // Filtre parent en premier
    if ((filter.getParent() != null) && !filter.getParent().equals("")) {
      for (ConverterChainedModel converter : getRawList()) {
        if (null == converter.getParent()) {
          continue;
        }
        if (converter.getParent().equals(filter.getParent())) {
          result.add(converter);
        }
      }
    }
    else {
      result.addAll(getRawList());
    }

    // Filtre Query
    if ((filter.getQuery() != null) && !filter.getQuery().equals("")) {
      for (ConverterChainedModel converter : result) {
        if (null == converter.getName()) {
          result.remove(converter);
        }
        if (converter.getName().toLowerCase().startsWith(filter.getQuery().toLowerCase())) {
          if ((filter.getParent() != null) && !filter.getParent().equals(converter.getParent())) {
            result.remove(converter);
          }
        }
      }
    }

    // Si index premier element > nombre d'elements filtres => resultat vide
    if (filter.getStart() > result.size()) {
      result.clear();
      return result;
    }

    // Tri
    sort(result, filter);

    return result;
  }

  /**
   * XStream FilePersistenceStrategy initialization
   * 
   * @param location
   *          Directory
   */
  public void init(File location) {
    Map<String, Class<?>> aliases = new HashMap<String, Class<?>>();
    aliases.put("converterChainedModel", ConverterChainedModel.class);
    aliases.put("converterModel", ConverterModel.class);
    aliases.put("converterParameter", ConverterParameter.class);
    this.init(location, aliases);
  }

  @Override
  public List<ConverterChainedModel> retrieveByParent(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}
