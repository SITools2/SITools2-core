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
package fr.cnes.sitools.mock.units.helper;

 import fr.cnes.sitools.mock.units.converters.FrequencyWavelengthConverter;
 import fr.cnes.sitools.mock.units.astronomy.AstronomicSystem;
 import fr.cnes.sitools.units.dimension.helper.DimensionHelper;

 import javax.measure.unit.MetricSystem;
 import javax.measure.unit.USCustomarySystem;

 /**
  * Base Unit Converter Helper holding converters implemented by default
  * @author m.marseille (AKKA technologies)
  */
 public class SitoolsUnitConverterHelper extends DimensionHelper {

   /**
    * Constructor
    */
   public SitoolsUnitConverterHelper() {
     super();

     /**
      * Registering all converters and all systems here
      */
     this.registerUnitConverter(new FrequencyWavelengthConverter());
     this.registerSystem(AstronomicSystem.getInstance());
     this.registerSystem(MetricSystem.getInstance());
     this.registerSystem(USCustomarySystem.getInstance());
   }

 }
