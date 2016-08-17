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
package fr.cnes.sitools.dataset.converter.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.restlet.Context;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.datasource.jdbc.model.Record;

/**
 * A chain of converters
 * 
 * @author AKKA
 */
public class ConverterChained extends AbstractConverter {

  /**
   * The list of converters inside the model.
   */
  private List<AbstractConverter> converters;

  /**
   * Constructor.
   * 
   * @param n
   *          the name of the model
   * @param d
   *          the description of the model
   */
  public ConverterChained(final String n, final String d) {
    super();
    this.converters = new ArrayList<AbstractConverter>();
    setName(n);
    setDescription(d);
  }

  /**
   * Constructor
   */
  public ConverterChained() {
    // TODO Auto-generated constructor stub
    this.converters = new ArrayList<AbstractConverter>();
  }

  /**
   * Get the model name.
   * 
   * @return the model name
   */
  public final String getModelName() {
    return getName();
  }

  /**
   * Returns the list of converters.
   * 
   * @return the list of converters
   */
  public final List<AbstractConverter> getConverters() {
    return converters;
  }

  /**
   * Add a converter in the model.
   * 
   * @param ac
   *          the converter to add
   */
  public final void addConverter(final AbstractConverter ac) {
    converters.add(ac);
  }

//  /**
//   * Delete a converter from the model.
//   * 
//   * @param convname
//   *          the name of the converter to delete
//   * @throws Exception
//   *           when converter to remove is not found
//   */
//  public final void deleteConverter(final String convname) throws Exception {
//    boolean isfound = false;
//    for (int i = 0; i < converters.size(); i++) {
//      AbstractConverter c = converters.get(i);
//      if (c.getName().compareTo(convname) == 0) {
//        isfound = true;
//        converters.remove(i);
//        break;
//      }
//    }
//    if (!isfound) {
//      throw new Exception("Converter Model : converter to remove not found");
//    }
//  }

  /**
   * Apply the model to a record.
   * 
   * @param rec
   *          initial record
   * @return converted record
   */
  public final Record getConversionOf(Record rec) {
    Record out = null;
    if (converters.size() > 0) {
      for (int i = 0; i < converters.size(); i++) {
        try {
          out = converters.get(i).getConversionOf(rec);
          if (out != null) {
            rec = out;
          }
        }
        catch (Exception e) {
          Context.getCurrentLogger().log(Level.INFO, null, e);
        }
      }
    }
    if (out == null) {
      return rec;
    }
    return out;
  }

  @Override
  public Validator<AbstractConverter> getValidator() {
    // TODO Auto-generated method stub
    return new Validator<AbstractConverter>() {
      @Override
      public Set<ConstraintViolation> validate(AbstractConverter item) {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }
  
}
