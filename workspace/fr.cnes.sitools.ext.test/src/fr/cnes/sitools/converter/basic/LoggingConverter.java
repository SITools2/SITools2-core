 /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.engine.Engine;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.converter.business.AbstractConverter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameterType;
import fr.cnes.sitools.datasource.jdbc.model.Record;

/**
 * Exemple pour supprimer les caractères spéciaux de chaines de caracteres dans XML et remplacer et une chaine [SI non
 * vide] par une autre dans la colonne spécifiée.
 * 
 * @author jp.boignard
 * 
 */
public class LoggingConverter extends AbstractConverter {

  /** Class logger */
  private static final Logger LOGGER = Engine.getLogger(LoggingConverter.class.getName());

  /**
   * Constructor.
   */
  public LoggingConverter() {
    //
    this.setName("LoggingConverter");
    this.setDescription("A converter logging some information on the record.");

    this.setClassAuthor("AKKA Technologies");
    this.setClassOwner("CNES");
    this.setClassVersion("0.2");
    //
    ConverterParameter column = new ConverterParameter("level", "level",
        ConverterParameterType.CONVERTER_PARAMETER_INTERN);

    column.setValue("SEVERE|WARNING|CONFIG|INFO|FINE|FINER|FINEST");
    column.setValueType("string");
    this.addParam(column);

    LOGGER.log(Level.INFO, "Converter :{0} version {1}", new Object[] {this.getName(), this.getClassVersion()});
  }

  @Override
  public final Record getConversionOf(final Record rec) throws Exception {
    Record out = rec;

    try {
      ConverterParameter level = this.getInternParam("level");
      Level loggingLevel = Level.parse((String) level.getValue());
      LOGGER.log(loggingLevel, "ID:{0} {1}", new Object[] {rec.getId(), rec.toString()});
    }
    catch (Exception e) {
      LOGGER.log(Level.WARNING, "RecordCleanupConverter error", e);
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
        ConverterParameter param = params.get("level");
        String value = param.getValue();
        if (!value.startsWith("SEVERE") && !value.startsWith("WARNING") && !value.startsWith("CONFIG")
            && !value.startsWith("INFO") && !value.startsWith("FINE") && !value.startsWith("FINER")
            && !value.startsWith("FINEST")) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint
              .setMessage("The parameter must one of the following values SEVERE|WARNING|CONFIG|INFO|FINE|FINER|FINEST");
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setInvalidValue(value);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }
        return constraints;
      }
    };
  }

}
