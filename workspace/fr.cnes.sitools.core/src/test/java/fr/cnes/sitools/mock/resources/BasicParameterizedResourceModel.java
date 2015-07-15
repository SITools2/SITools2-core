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

 import fr.cnes.sitools.common.SitoolsSettings;
 import fr.cnes.sitools.common.application.ContextAttributes;
 import fr.cnes.sitools.dataset.model.Column;
 import fr.cnes.sitools.dataset.model.DataSet;
 import fr.cnes.sitools.plugins.resources.model.DataSetSelectionType;
 import fr.cnes.sitools.plugins.resources.model.ResourceModel;
 import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
 import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;
 import fr.cnes.sitools.server.Consts;
 import fr.cnes.sitools.util.RIAPUtils;
 import org.restlet.Context;

 import java.util.List;

 /**
  * Model linked to resource
  *
  * @author m.marseille (AKKA Technologies)
  *
  */
 public class BasicParameterizedResourceModel extends ResourceModel {

   /**
    * Constructor
    */
   public BasicParameterizedResourceModel() {

     super();

     setClassAuthor("AKKA Technologies");
     setClassVersion("0.1");
     setName("BasicParameterizedResourceModel");
     setDescription("Resource model");
     setClassOwner("CNES");
     setResourceClassName("fr.cnes.sitools.resources.basic.BasicParameterizedResource");
     setDataSetSelection(DataSetSelectionType.ALL);

     ResourceParameter textToSend = new ResourceParameter("text", "text to send", ResourceParameterType.PARAMETER_INTERN);
     ResourceParameter textSent = new ResourceParameter("yourtext", "text that will be written",
         ResourceParameterType.PARAMETER_USER_INPUT);

     textToSend.setValue("You have sent the text :"); // default value
     textSent.setValue("Hello world!"); // default value

     addParam(textToSend);
     addParam(textSent);

     ResourceParameter xsEnum = new ResourceParameter("xsEnum", "An example of xsEnum",
         ResourceParameterType.PARAMETER_USER_INPUT);
     xsEnum.setValue("value1"); // default value
     String valueXsEnum = "xs:enum[value1, value2, value3, value4]";
     xsEnum.setValueType(valueXsEnum);
     this.addParam(xsEnum);

     ResourceParameter xsEnumMultiple = new ResourceParameter("xsEnumMultiple", "An example of xsEnumMultiple",
         ResourceParameterType.PARAMETER_USER_INPUT);
     xsEnumMultiple.setValue("value1"); // default value
     String valueTypexsEnumMultiple = "xs:enum-multiple[value1, value2, value3, value4]";
     xsEnumMultiple.setValueType(valueTypexsEnumMultiple);
     this.addParam(xsEnumMultiple);

     ResourceParameter xsEnumEditable = new ResourceParameter("xsEnumEditable", "An example of xsEnumMultiple",
         ResourceParameterType.PARAMETER_USER_INPUT);
     xsEnumEditable.setValue("value1"); // default value
     String valueTypexsEnumEditable = "xs:enum-editable[value1, value2, value3, value4]";
     xsEnumEditable.setValueType(valueTypexsEnumEditable);
     this.addParam(xsEnumEditable);

     ResourceParameter xsEnumEditableMultiple = new ResourceParameter("xsEnumEditableMultiple",
         "An example of xsEnumMultiple", ResourceParameterType.PARAMETER_USER_INPUT);
     xsEnumEditableMultiple.setValue("value1"); // default value
     String valueTypexsEnumEditableMultiple = "xs:enum-editable-multiple[value1, value2, value3, value4]";
     xsEnumEditableMultiple.setValueType(valueTypexsEnumEditableMultiple);
     this.addParam(xsEnumEditableMultiple);

     // Do not forget this part if you have a user input parameter in the URL
     this.completeAttachUrlWith("basicresource/{yourtext}");
   }

   @Override
   public void initParametersForAdmin(Context context) {
     super.initParametersForAdmin(context);

     String parent = (String) context.getAttributes().get("parent");
     String appClassName = (String) context.getAttributes().get("appClassName");
     SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
     Context componentContext = settings.getComponent().getContext();

     DataSet dataset = RIAPUtils.getObject(parent, settings.getString(Consts.APP_DATASETS_URL), context);

     if (dataset != null) {
       List<Column> cm = dataset.getColumnModel();
       ResourceParameter param;
       for (Column column : cm) {
         param = new ResourceParameter(column.getColumnAlias(), column.getColumnAlias(),
             ResourceParameterType.PARAMETER_INTERN);
         addParam(param);
       }
     }
     else {
       context.getLogger().info("No dataset found " + parent);
     }

   }

 }
