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
package fr.cnes.sitools.utils;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;

/**
 * XStream Converter for AttributeValue
 * 
 * @author m.gond (AKKA Technologies)
 */

public class AttributeValueConverter implements Converter {

  @Override
  public boolean canConvert(Class clazz) {
    // TODO Auto-generated method stub
    return clazz.equals(AttributeValue.class);

  }

  @Override
  public void marshal(Object arg0, HierarchicalStreamWriter arg1, MarshallingContext arg2) {
    // TODO Auto-generated method stub

  }

  @Override
  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    // create a new attribute value
    AttributeValue attr = new AttributeValue();
    // moveDown to get the first value
    reader.moveDown();
    attr.setName(reader.getValue());
    // moveUp to go back to the root
    reader.moveUp();
    if (reader.hasMoreChildren()) {
      // moveDown to get the second value
      reader.moveDown();
      attr.setValue(reader.getValue());
      // moveUp to go back to the root
      reader.moveUp();
    }
    else {
      attr.setValue(null);
    }

    return attr;

  }

}
