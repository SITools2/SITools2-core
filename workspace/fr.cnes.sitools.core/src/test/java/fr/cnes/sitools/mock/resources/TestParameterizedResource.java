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
package fr.cnes.sitools.mock.resources;

 import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
 import org.restlet.ext.wadl.MethodInfo;
 import org.restlet.representation.Representation;
 import org.restlet.resource.Get;

 /**
  * ParameterizedResource class for test purpose
  *
  * @author m.gond (AKKA Technologies)
  *
  */
 public class TestParameterizedResource extends SitoolsParameterizedResource {

   @Get
   @Override
   public Representation get() {
     // TODO Auto-generated method stub
     return null;
   }

   @Override
   public void sitoolsDescribe() {
     setName(this.getClass().getName());
     setDescription("Parameterized resource test, only for test purpose, don't do anything");
     setNegotiated(false);
   }

   @Override
   protected void describeGet(MethodInfo info) {
     this.addInfo(info);
   }

 }
