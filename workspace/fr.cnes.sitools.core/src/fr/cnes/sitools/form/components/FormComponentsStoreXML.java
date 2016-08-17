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
package fr.cnes.sitools.form.components;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.ResourceComparator;
import fr.cnes.sitools.common.store.SitoolsStoreXML;
import fr.cnes.sitools.form.components.model.FormComponent;

/**
 * Implementation of FormComponentStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
@Deprecated
public final class FormComponentsStoreXML extends SitoolsStoreXML<FormComponent> {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "formComponents";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Restlet Context
   */
  public FormComponentsStoreXML(File location, Context context) {
    super(FormComponent.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Restlet Context
   */
  public FormComponentsStoreXML(Context context) {
    super(FormComponent.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public FormComponent update(FormComponent formComponent) {
    FormComponent result = null;
    for (Iterator<FormComponent> it = getRawList().iterator(); it.hasNext();) {
      FormComponent current = it.next();
      if (current.getId().equals(formComponent.getId())) {
        getLog().info("Updating formComponent");

        result = current;
        current.setId(formComponent.getId());
        current.setComponentDefaultHeight(formComponent.getComponentDefaultHeight());
        current.setComponentDefaultWidth(formComponent.getComponentDefaultWidth());
        current.setJsAdminObject(formComponent.getJsAdminObject());
        current.setJsUserObject(formComponent.getJsUserObject());
        current.setImageUrl(formComponent.getImageUrl());
        current.setType(formComponent.getType());
        current.setFileUrlAdmin(formComponent.getFileUrlAdmin());
        current.setFileUrlUser(formComponent.getFileUrlUser());
        current.setPriority(formComponent.getPriority());

        it.remove();

        break;
      }
    }
    if (result != null) {
      getRawList().add(result);
    }
    return result;
  }

  /**
   * Sort the list (by default on the name)
   * 
   * @param result
   *          list to be sorted
   * @param filter
   *          ResourceCollectionFilter with sort properties.
   */
  public void sort(List<FormComponent> result, ResourceCollectionFilter filter) {
    if ((filter != null) && (filter.getSort() != null) && !filter.getSort().equals("")) {
      Collections.sort(result, new ResourceComparator<FormComponent>(filter) {
        @Override
        public int compare(FormComponent arg0, FormComponent arg1) {
          if (arg0.getType() == null) {
            return 1;
          }
          if (arg1.getType() == null) {
            return -1;
          }
          String s1 = (String) arg0.getType();
          String s2 = (String) arg1.getType();

          return super.compare(s1, s2);
        }
      });
    }
  }

  /**
   * XStream FilePersistenceStrategy initialization
   * 
   * @param location
   *          Directory
   */
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("formComponent", FormComponent.class);
    this.init(location, aliases);
  }

  @Override
  public List<FormComponent> retrieveByParent(String id) {
    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}
