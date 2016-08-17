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
package fr.cnes.sitools.dataset.converter.model;

/**
 * 
 * Parameter types
 * 
 * @author AKKA
 * 
 * Parameter types associated to converters :
 * IN : DataSet column
 * OUT : output DataSet column
 * INOUT : the DataSet column as input is modified
 * INTERN : internal parameter used for conversion setting
 * 
 * 
 */
public enum ConverterParameterType {

  /**
   * A parameter IN is read only
   */
  CONVERTER_PARAMETER_IN,

  /**
   * A parameter OUT is write only
   */
  CONVERTER_PARAMETER_OUT,

  /**
   * A parameter INOUT is readable and writable
   */
  CONVERTER_PARAMETER_INOUT,

  /**
   * A parameter INTERNAL set a value used by conversion
   */
  CONVERTER_PARAMETER_INTERN

}
