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
package fr.cnes.sitools.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.logging.Logger;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Freemarker template utilities
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class TemplateUtils {

  /** Logger used */
  private static Logger logger = Logger.getLogger(TemplateUtils.class.getName());

  /**
   * Private constructor for utility classes
   */
  private TemplateUtils() {
    super();
  }

  /**
   * Builds a String using FreeMarker template whose name is given in parameter. Template file is loaded from object
   * class package
   * 
   * @param templatePath
   *          the path to use
   * @param object
   *          object to use
   * @return String
   */
  public static String toStringWithObjectClasspathTemplate(String templatePath, Object object) {
    try {
      Representation objectFtl = new ClientResource(LocalReference.createClapReference(object.getClass().getPackage())
          + "/" + templatePath + ".ftl").get();

      TemplateRepresentation tr = new TemplateRepresentation(objectFtl, object, MediaType.TEXT_PLAIN);
      String result = tr.getText();

      return result;
    }
    catch (ResourceException e) {
      e.printStackTrace();
      logger.warning("Error getting template " + templatePath + " for object " + object.toString());
    }
    catch (IOException e) {
      e.printStackTrace();
      logger.warning("Error getting template " + templatePath + " for object " + object.toString());
    }

    return "";
  }

  /**
   * To merge a template with a Map<String, Object> model.
   * 
   * @param templatePath
   *          complete template ftl file name.
   * @param map
   *          Map<String, Object> model
   * @return String result of mapping.
   */
  public static String mapToString(String templatePath, Map<String, Object> map) {
    return toString(templatePath, map);
  }

  /**
   * To merge a template with an Object model. Map<String, Object> form is recommended.
   * 
   * @param templatePath
   *          complete template ftl file name.
   * @param object
   *          Object model
   * @return String result of mapping.
   * 
   *         TODO missing MediaType information (detection ?) of result. We could use the file extention .html or .xml
   *         ..., instead of .ftl. It needs to know the complete template file name in among of this function.
   */
  public static String toString(String templatePath, Object object) {
    File templateFile = new File(templatePath);

    try {
      Configuration cfg = new Configuration();
      cfg.setDirectoryForTemplateLoading(templateFile.getParentFile());
      cfg.setObjectWrapper(new DefaultObjectWrapper());
      Template template = cfg.getTemplate(templateFile.getName());
      Writer out = new StringWriter();
      template.process(object, out);
      return out.toString();
    }
    catch (TemplateException e) {
      e.printStackTrace();
      logger.warning("Error getting template " + templatePath);
    }
    catch (IOException e) {
      e.printStackTrace();
      logger.warning("Error getting template " + templatePath);
    }

    return "";
  }

  /**
   * describeObjectClassesForTemplate
   * 
   * @param templatePath
   *          the template path
   * @param objects
   *          the Map of Objects
   */
  public static void describeObjectClassesForTemplate(String templatePath, Map<String, Object> objects) {

    String fieldsFilename = templatePath;
    int ftlindex = templatePath.indexOf(".ftl");
    if (ftlindex > 0) {
      fieldsFilename = templatePath.substring(0, ftlindex);
    }
    fieldsFilename = fieldsFilename + ".fields.txt";

    File describeFile = new File(templatePath.substring(0, templatePath.indexOf(".ftl")) + ".fields.txt");
    if (!describeFile.exists()) {

      StringBuffer buffer = new StringBuffer();
      for (String key : objects.keySet()) {
        Object object = objects.get(key);
        if (object != null) {
          describeClassForTemplate(key, object.getClass(), buffer);
        }
      }

      try {
        BufferedWriter out = new BufferedWriter(new FileWriter(describeFile));
        out.write(buffer.toString());
        out.flush();
        out.close();
      }
      catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  // /**
  // *
  // * @param templatePath
  // * @param objects
  // */
  // public static void describeObjectClassesForTemplate(String templatePath, Object... objects) {
  // File describeFile = new File(templatePath + ".fields.txt");
  // if (!describeFile.exists()) {
  //
  // StringBuffer buffer = new StringBuffer();
  // for (Object object : objects) {
  // describeClassForTemplate(object.getClass(), buffer);
  // }
  // try {
  // BufferedWriter out = new BufferedWriter(new FileWriter(describeFile));
  // out.write(buffer.toString());
  // out.flush();
  // out.close();
  // }
  // catch (IOException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // }
  // }

  /**
   * To produce a description of fields that can be used in template
   * 
   * @param entry
   *          the String entry
   * @param classe
   *          the class
   * @param buffer
   *          the StringBuffer
   */
  public static void describeClassForTemplate(String entry, Class classe, StringBuffer buffer) {
    buffer.append("# ---------------\n# " + classe.getName() + " " + entry + "\n");
    Field[] fields = classe.getDeclaredFields();
    for (Field field : fields) {
      buffer.append("${" + entry + '.' + field.getName() + "}\n");
    }
  }

}
