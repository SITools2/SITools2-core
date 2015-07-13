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
package fr.cnes.sitools.dataset.database.mongodb;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;

/**
 * AttributeValue Converter to get a String of the value of an {@link AttributeValue} and not the object itself
 * especially used for MongoDB datasets
 * 
 * @author m.gond
 */
public final class SitoolsAttributeValueConverter implements Converter {
  /**
   * Can only convert AttributeValues
   * 
   * @param arg0
   *          The {@link Class} to check if it possible to convert
   * @return true if arg0 is an {@link AttributeValue}, false otherwise
   * 
   */
  public boolean canConvert(@SuppressWarnings("rawtypes") Class arg0) {
    return arg0.equals(AttributeValue.class);
  }

  /**
   * No unmarshaling
   * 
   * @param arg0
   *          {@link HierarchicalStreamReader}
   * @param arg1
   *          {@link UnmarshallingContext}
   * @return {@link Object}
   */
  public Object unmarshal(HierarchicalStreamReader arg0, UnmarshallingContext arg1) {
    return null;
  }

  /**
   * Create 2 nodes with name and value of the {@link AttributeValue}
   * 
   * @param arg0
   *          The Object to marshal
   * @param arg1
   *          {@link HierarchicalStreamWriter} the writer
   * @param arg2
   *          {@link MarshallingContext} the context
   */
  public void marshal(Object arg0, HierarchicalStreamWriter arg1, MarshallingContext arg2) {
    if (arg0 != null && arg0 instanceof AttributeValue) {
      AttributeValue value = (AttributeValue) arg0;
      arg1.startNode("name");
      arg1.setValue(value.getName());
      arg1.endNode();
      arg1.startNode("value");
      String objectValue = (value.getValue() != null) ? value.getValue().toString() : "";
      arg1.setValue(objectValue);
      arg1.endNode();
    }
  }
}
