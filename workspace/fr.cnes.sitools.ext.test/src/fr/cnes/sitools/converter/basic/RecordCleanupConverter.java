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
package fr.cnes.sitools.converter.basic;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.converter.business.AbstractConverter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameterType;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import java.util.HashSet;
import java.util.Map;

/**
 * Exemple de convertisseur pour supprimer les caractères spéciaux de chaines de caracteres dans XML et remplacer une
 * chaine [SI non vide] par une autre dans la colonne spécifiée.
 * 
 * @author jp.boignard
 * 
 */
public class RecordCleanupConverter extends AbstractConverter {

  /** Class logger */
  private static final Logger LOGGER = Logger.getLogger(RecordCleanupConverter.class.getName());

  /**
   * Constructor.
   */
  public RecordCleanupConverter() {
    //
    this.setName("ConverterReplace");
    this.setDescription("A converter that replace a string by another one.");

    this.setClassAuthor("AKKA Technologies");
    this.setClassOwner("CNES");
    this.setClassVersion("0.2");
    //
    ConverterParameter column = new ConverterParameter("column", "Column to process",
        ConverterParameterType.CONVERTER_PARAMETER_INOUT);
    ConverterParameter search = new ConverterParameter("search", "string to search",
        ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    ConverterParameter replace = new ConverterParameter("replace", "string to replace",
        ConverterParameterType.CONVERTER_PARAMETER_INTERN);

    //
    column.setValue("");
    column.setValueType("string");
    search.setValue("");
    search.setValueType("string");
    replace.setValue("");
    replace.setValueType("string");
    //

    this.addParam(column);
    this.addParam(search);
    this.addParam(replace);

    LOGGER.log(Level.INFO, "Converter :{0} version {1}", new Object[] { this.getName(), this.getClassVersion() });
  }

  @Override
  public final Record getConversionOf(final Record rec) throws Exception {
    Record out = rec;

    try {
      AttributeValue column = this.getInOutParam("column", rec);
      ConverterParameter search = this.getInternParam("search");
      ConverterParameter replace = this.getInternParam("replace");

      if (!isNull(column)) {
        String value = String.valueOf(column.getValue());

        // TODO moyen plus propre de supprimer les &#x8; et &#x16;
        value = value.replace("", "");
        value = value.replace("", "");

        LOGGER.log(Level.FINEST, "Value={0} search={1} replace={2}",
            new Object[] { value, search.getValue(), replace.getValue() });

        value = value.replace(search.getValue(), replace.getValue());

        column.setValue(value);
      }
    }
    catch (Exception e) {
      LOGGER.log(Level.INFO, "RecordCleanupConverter error", e);
    }

    return out;
  }

  @Override
  public Validator<AbstractConverter> getValidator() {
    // TODO Auto-generated method stub
    return new Validator<AbstractConverter>() {

      @Override
      public Set<ConstraintViolation> validate(AbstractConverter item) {
        Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
        Map<String, ConverterParameter> params = item.getParametersMap();
        ConverterParameter param = params.get("column");
        String value = param.getAttachedColumn();
        if (value.equals("")) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("An attribute of the dataset must be choosen");
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }
        return constraints;
      }
    };
  }

}
