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
package fr.cnes.sitools.mock.units.converters;

 import fr.cnes.sitools.units.dimension.model.SitoolsUnitConverter;

 import javax.measure.quantity.QuantityFactory;
 import javax.measure.quantity.Velocity;
 import javax.measure.unit.MetricSystem;
 import javax.measure.unit.UnitConverter;
 import java.math.MathContext;

 /**
  * Converter to go from frequency to wavelength
  *
  * @author m.marseille (AKKA Technologies)
  */
 public class FrequencyWavelengthConverter extends SitoolsUnitConverter {

   /** Serial number */
   private static final long serialVersionUID = 1L;

   /**
    * Speed of light
    */
   private static final Velocity C = QuantityFactory.getInstance(Velocity.class).create(2.99792e8, MetricSystem.METRES_PER_SECOND);

   /**
    * Constructor
    */
   public FrequencyWavelengthConverter() {
     super();
     this.setStartUnit(MetricSystem.HERTZ);
     this.setTargetUnit(MetricSystem.METRE);
   }

   @Override
   public UnitConverter getBaseToTargetConverter() {
     UnitConverter startConverter = this.getStartUnit().getConverterToMetric();
     UnitConverter targetConverter = this.getTargetUnit().getConverterToMetric().inverse();
     return targetConverter.concatenate(this).concatenate(startConverter);
   }

   @Override
   public UnitConverter inverse() {
     UnitConverter startConverter = this.getStartUnit().getConverterToMetric().inverse();
     UnitConverter targetConverter = this.getTargetUnit().getConverterToMetric();
     return targetConverter.concatenate(this).concatenate(startConverter);
   }

   @Override
   public double convert(double value) {
     return C.doubleValue(MetricSystem.METRES_PER_SECOND) / value;
   }

   @Override
   public Number convert(Number value, MathContext ctx) {
     return convert(value.doubleValue());
   }

   @Override
   public boolean equals(Object cvtr) {
     return (cvtr instanceof FrequencyWavelengthConverter);
   }

   @Override
   public int hashCode() {
     return 0;
   }

   @Override
   public boolean isLinear() {
     return true;
   }

 }
