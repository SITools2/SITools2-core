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
package fr.cnes.sitools.applications.tests;

import java.util.HashSet;
import java.util.Set;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.representation.StringRepresentation;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.plugins.applications.business.AbstractApplicationPlugin;
import fr.cnes.sitools.plugins.applications.model.ApplicationPluginModel;
import fr.cnes.sitools.plugins.applications.model.ApplicationPluginParameter;

/**
 * Application used for tests
 * 
 * 
 * @author m.gond
 */
public class ApplicationTest extends AbstractApplicationPlugin {
  /**
   * Default constructor
   * 
   * @param context
   *          context
   */
  public ApplicationTest(Context context) {
    super(context);
    constructor();
  }

  /** Default constructor */
  public ApplicationTest() {
    super();
    constructor();

  }

  /**
   * Constructor with context and model of the application configuration.
   * 
   * @param arg0
   *          Restlet context
   * @param model
   *          model
   */
  public ApplicationTest(Context arg0, ApplicationPluginModel model) {
    super(arg0, model);
  }

  /** the common part of constructor */
  public void constructor() {

    ApplicationPluginParameter xsEnum = new ApplicationPluginParameter("xsEnum", "An example of xsEnum");
    xsEnum.setValue("value1"); // default value
    String valueXsEnum = "xs:enum[value1, value2, value3, value4]";
    xsEnum.setValueType(valueXsEnum);
    this.addParameter(xsEnum);

    ApplicationPluginParameter param1 = new ApplicationPluginParameter("1", "1");
    ApplicationPluginParameter param2 = new ApplicationPluginParameter("2", "2");
    this.addParameter(param1);
    this.addParameter(param2);

    this.getModel().setClassAuthor("AKKA Technologies");
    this.getModel().setClassVersion("0.1");
    this.getModel().setClassOwner("CNES");

  }

  @Override
  public void sitoolsDescribe() {
    this.setName("APPLICATION TEST");
    this.setDescription("APPLICATION FOR TEST PURPOSE, DON'T DO ANYTHING");
    this.setAuthor("AKKA Technologies");
    this.setOwner("CNES");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.Application#createInboundRoot()
   */
  @Override
  public Restlet createInboundRoot() {
    Router router = new Router(getContext());
    router.attachDefault(new Restlet() {

      /*
       * (non-Javadoc)
       * 
       * @see org.restlet.Restlet#handle(org.restlet.Request, org.restlet.Response)
       */
      @Override
      public void handle(Request arg0, Response arg1) {

        arg1.setEntity(new StringRepresentation("TEST"));

        super.handle(arg0, arg1);
      }

    });

    return router;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.applications.plugins.business.AbstractApplicationPlugin #getValidator()
   */
  @Override
  public Validator<AbstractApplicationPlugin> getValidator() {
    // TODO Auto-generated method stub
    return new Validator<AbstractApplicationPlugin>() {
      /**
       * //only for tests validation, test that parameter 1 value is 1 and parameter 2 value is 2
       * 
       * @param item
       *          the AbstractFilter to validate
       * @return a set of constraintViolation
       */
      @Override
      public Set<ConstraintViolation> validate(AbstractApplicationPlugin item) {
        HashSet<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();

        ApplicationPluginParameter param = item.getParameter("1");
        if (!param.getValue().equals("param1_value")) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("Param 1 value must be param1_value");
          constraint.setInvalidValue(param.getValue());
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }
        param = item.getParameter("2");
        if (param.getName().equals("2")) {
          if (!param.getValue().equals("param2_value")) {
            ConstraintViolation constraint = new ConstraintViolation();
            constraint.setMessage("Param 2 value must be param2_value");
            constraint.setInvalidValue(param.getValue());
            constraint.setLevel(ConstraintViolationLevel.CRITICAL);
            constraint.setValueName(param.getName());
            constraints.add(constraint);
          }
        }
        return constraints;
      }
    };
  }

}
