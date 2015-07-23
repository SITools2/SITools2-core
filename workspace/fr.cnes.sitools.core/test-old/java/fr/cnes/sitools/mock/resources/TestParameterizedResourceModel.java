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

 import fr.cnes.sitools.common.validator.ConstraintViolation;
 import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
 import fr.cnes.sitools.common.validator.Validator;
 import fr.cnes.sitools.plugins.resources.model.ResourceModel;
 import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
 import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;

 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;

 /**
  * ParameterizedResourceModel class for test purpose
  *
  * @author m.gond (AKKA Technologies)
  *
  */
 public class TestParameterizedResourceModel extends ResourceModel {

   /**
    * Constructor
    */
   public TestParameterizedResourceModel() {

     super();

     setClassAuthor("AKKA Technologies");
     setClassOwner("CNES");
     setClassVersion("0.1");
     setName("TestParameterizedResourceModel");
     setDescription("Resource model for Test purpose, don't do anything");
     setResourceClassName("fr.cnes.sitools.mock.resources.TestParameterizedResource");

     ResourceParameter urlAttach = new ResourceParameter("url", "attachment url",
         ResourceParameterType.PARAMETER_ATTACHMENT);

     ResourceParameter param1 = new ResourceParameter("1", "1", ResourceParameterType.PARAMETER_INTERN);
     ResourceParameter param2 = new ResourceParameter("2", "2", ResourceParameterType.PARAMETER_INTERN);

     urlAttach.setValue("/test"); // default value

     addParam(urlAttach);
     addParam(param1);
     addParam(param2);

     this.setApplicationClassName("fr.cnes.sitools.common.application.SitoolsParameterizedApplication");
   }

   /*
    * (non-Javadoc)
    *
    * @see fr.cnes.sitools.resources.plugins.model.ResourceModel#getValidator ()
    */
   @Override
   public Validator<ResourceModel> getValidator() {
     // Create a new Instance of Validator on an ResourceModel object
     return new Validator<ResourceModel>() {
       /**
        * //only for tests validation, test that parameter 1 value is 1 and parameter 2 value is 2
        *
        * @param item
        *          the AbstractSva to validate
        * @return a set of constraintViolation
        */
       @Override
       public Set<ConstraintViolation> validate(ResourceModel item) {
         Map<String, ResourceParameter> params = item.getParametersMap();
         HashSet<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
         ResourceParameter param = params.get("1");
         if (!param.getValue().equals("param1_value")) {
           ConstraintViolation constraint = new ConstraintViolation();
           constraint.setMessage("Param 1 value must be param1_value");
           constraint.setInvalidValue(param.getValue());
           constraint.setLevel(ConstraintViolationLevel.CRITICAL);
           constraint.setValueName(param.getName());
           constraints.add(constraint);
         }
         ResourceParameter param2 = params.get("2");
         if (!param2.getValue().equals("param2_value")) {
           ConstraintViolation constraint = new ConstraintViolation();
           constraint.setMessage("Param 2 value must be param2_value");
           constraint.setInvalidValue(param2.getValue());
           constraint.setLevel(ConstraintViolationLevel.CRITICAL);
           constraint.setValueName(param2.getName());
           constraints.add(constraint);
         }

         return constraints;
       }
     };
   }

 }
