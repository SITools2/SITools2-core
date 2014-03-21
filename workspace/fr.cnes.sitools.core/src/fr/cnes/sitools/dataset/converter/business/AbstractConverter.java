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
package fr.cnes.sitools.dataset.converter.business;

import java.util.Iterator;
import java.util.List;

import org.restlet.Context;

import fr.cnes.sitools.common.business.AbstractExtension;
import fr.cnes.sitools.common.validator.Validable;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameterType;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;

/**
 * Abstract class from which all converters must inherit.
 * 
 * @author m.marseille (AKKA Technologies)
 */
public abstract class AbstractConverter extends AbstractExtension<ConverterParameter> implements Validable {

  /**
   * Default constructor
   */
  public AbstractConverter() {
    super();
  }

  /**
   * Constructor with Context parameter
   * 
   * @param ctx
   *          The Context to instantiate the Converter with, contains the DataSet
   */
  public AbstractConverter(Context ctx) {
    super();
  }

  /**
   * Method to process conversion, Must be overridden
   * 
   * @param rec
   *          the record to be processed
   * @return the record processed
   * @throws Exception
   *           when conversion fails
   */
  public abstract Record getConversionOf(final Record rec) throws Exception;

  /**
   * Add a parameter to the Map of parameters The key to this parameter is its name
   * 
   * @param param
   *          the parameter to add
   */

  public final void addParam(final ConverterParameter param) {
    this.getParametersMap().put(param.getName(), param);
  }

  /**
   * Gets the <code>ConverterParameter</code> of type CONVERTER_PARAMETER_INTERN corresponding to the following
   * <code>name</code>
   * 
   * @param name
   *          The name of the <code>ConverterParameter</code>
   * @return a <code>ConverterParameter</code> of type CONVERTER_PARAMETER_INTERN corresponding to the following
   *         <code>name</code> or null if the <code>ConverterParameter</code> is not found
   */
  public final ConverterParameter getInternParam(final String name) {
    ConverterParameter param = this.getParametersMap().get(name);
    ConverterParameter paramOut = null;
    if (param.getParameterType() == ConverterParameterType.CONVERTER_PARAMETER_INTERN) {
      paramOut = param;
    }
    return paramOut;
  }

  /**
   * Gets the <code>AttributeValue</code> from the following <code>Record</code> corresponding to the following
   * <code>name</code>
   * 
   * @param name
   *          The name of the <code>ConverterParameter</code>
   * 
   * @param rec
   *          The record
   * @return a <code>AttributeValue</code> corresponding to the following <code>name</code> and <code>Record</code> or
   *         null if the <code>AttributeValue</code> is not found
   */
  public final AttributeValue getInParam(String name, Record rec) {
    ConverterParameter param = this.getParametersMap().get(name);
    List<AttributeValue> listRecord = rec.getAttributeValues();
    AttributeValue resultAttr = null;
    AttributeValue attr = null;
    for (Iterator<AttributeValue> it = listRecord.iterator(); it.hasNext() && resultAttr == null;) {
      attr = it.next();
      if (param.getParameterType() == ConverterParameterType.CONVERTER_PARAMETER_IN
          && attr.getName().equals(param.getAttachedColumn())) {
        resultAttr = attr;
      }
    }
    return resultAttr;
  }

  /**
   * Gets the <code>AttributeValue</code> from the following <code>Record</code> corresponding to the following
   * <code>name</code>
   * 
   * @param name
   *          The name of the <code>ConverterParameter</code>
   * 
   * @param rec
   *          The record
   * @return a <code>AttributeValue</code> corresponding to the following <code>name</code> and <code>Record</code> or
   *         null if the <code>AttributeValue</code> is not found
   */
  public final AttributeValue getOutParam(String name, Record rec) {
    ConverterParameter param = this.getParametersMap().get(name);
    List<AttributeValue> listRecord = rec.getAttributeValues();
    AttributeValue resultAttr = null;
    AttributeValue attr = null;
    for (Iterator<AttributeValue> it = listRecord.iterator(); it.hasNext() && resultAttr == null;) {
      attr = it.next();
      if (param.getParameterType() == ConverterParameterType.CONVERTER_PARAMETER_OUT
          && attr.getName().equals(param.getAttachedColumn())) {
        resultAttr = attr;
      }
    }
    if (resultAttr == null) {
      attr = new AttributeValue();
      attr.setName(param.getAttachedColumn());
      rec.getAttributeValues().add(attr);
    }
    return attr;
  }

  /**
   * Gets the <code>AttributeValue</code> from the following <code>Record</code> corresponding to the following
   * <code>name</code>
   * 
   * @param name
   *          The name of the <code>ConverterParameter</code>
   * 
   * @param rec
   *          The record
   * @return a <code>AttributeValue</code> corresponding to the following <code>name</code> and <code>Record</code> or
   *         null if the <code>AttributeValue</code> is not found
   */
  public final AttributeValue getInOutParam(String name, Record rec) {
    ConverterParameter param = this.getParametersMap().get(name);
    List<AttributeValue> listRecord = rec.getAttributeValues();
    AttributeValue attr = null;
    AttributeValue resultAttr = null;
    for (Iterator<AttributeValue> it = listRecord.iterator(); it.hasNext() && resultAttr == null;) {
      attr = it.next();
      if (param.getParameterType() == ConverterParameterType.CONVERTER_PARAMETER_INOUT
          && attr.getName().equals(param.getAttachedColumn())) {
        resultAttr = attr;
      }
    }
    return resultAttr;
  }

  /**
   * Return true if the attributeValue is considered as null, false otherwise
   * 
   * @param attr
   *          the AttributeValue to check
   * @return true if the attributeValue is considered as null, false otherwise
   */
  protected boolean isNull(AttributeValue attr) {
    if (attr == null) {
      return true;
    }
    Object value = attr.getValue();
    return (value == null || "".equals(value) || "null".equals(value));
  }

}
