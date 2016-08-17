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
package fr.cnes.sitools.api;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.logging.Logger;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.engine.Engine;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

/**
 * API documentation producer
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class DocAPI {
  
  /** Class logger */
  private static Logger logger = Engine.getLogger(DocAPI.class.getName());

  /** output stream */
  private PrintStream stream = System.out;

  /** documentation active */
  private boolean active = false;

  /** mediaType XML / JSON for tests */
  private MediaType mediaTest = null;

  /** Application name */
  private String applicationName = null;

  /**
   * The body
   */
  private String body = "";
  /**
   * The title
   */
  private String title = "";
  /**
   * The sumJmary
   */
  private String summary = "";

  /**
   * Constructor
   * 
   * @param testClass
   *          Test class
   * @param appName
   *          Application name - Title
   */
  public DocAPI(Class<?> testClass, String appName) {
    this.applicationName = appName;

    Package pack = testClass.getPackage();
    String name = pack.getName();
    File directory = new File("./documentation/api/" + name);
    directory.mkdirs();
    logger.finest("path: " + directory.getAbsolutePath());
    File outputFile = new File(directory, applicationName + ".html");

    try {
      FileOutputStream fos = new FileOutputStream(outputFile);
      stream = new PrintStream(fos);
      body = "";
      summary = "";
    }
    catch (FileNotFoundException e) {
      fail("DocAPI.constructor");
      e.printStackTrace();
    }
  }

  /**
   * Gets the mediaTest value
   * 
   * @return the mediaTest
   */
  public MediaType getMediaTest() {
    return mediaTest;
  }

  /**
   * Sets the value of mediaTest
   * 
   * @param mediaTest
   *          the mediaTest to set
   */
  public void setMediaTest(MediaType mediaTest) {
    this.mediaTest = mediaTest;
  }

  /**
   * Gets the active value
   * 
   * @return the active
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Sets the value of active
   * 
   * @param active
   *          the active to set
   */
  public void setActive(boolean active) {
    this.active = active;
  }

  /** Header HTML */
  public void head() {
    stream.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
    stream.println("<html>");
    stream.println("<head>");
    stream.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
    stream.println("<title>SITOOLS WEB CLIENT</title>");
    stream.println("<body>");
  }

  /** Footer HTML */
  public void close() {
    logger.finest("BODY: " + summary);
    head();
    stream.println(title);
    stream.println("<ul>" + summary + "</ul><hr>");
    stream.println(body);
    stream.println("</body>");
    stream.println("</html>");
    stream.close();
  }

  /**
   * Paragraph Level 1 in HTML
   * 
   * @param chapter
   *          String
   * @return true if docAPI is active
   */
  public boolean appendChapter(String chapter) {
    if (!isActive()) {
      return false;
    }
    title += "<h1><center>" + chapter + "</center></h1>";
    return true;
  }

  /**
   * Paragraph Level 2 in HTML
   * 
   * @param chapter chapter title
   * @param key key for the hypertext link
   * @return true if no errors
   */
  public boolean appendSubChapter(String chapter, String key) {
    if (!isActive()) {
      return false;
    }
    body += "<hr><h2><center><a name=\"" + key + "\">" + chapter + "</a></center></h2>";
    summary += "<li><a href=\"#" + key + "\">" + chapter + "</a></li>";
    return true;
  }

  /** Add a section in the documentation
   * @param section the section title
   * @return true if no errors 
   */
  public boolean appendSection(String section) {
    if (!isActive()) {
      return false;
    }
    body += "<h2>" + section + "</h2>";
    return true;
  }

  /**
   * Request text in HTML
   * 
   * @param method
   *          <code>Method</code>
   * @param cr
   *          <code>ClientResource</code>
   * @return true if docAPI is active
   */
  public boolean appendRequest(Method method, ClientResource cr) {
    if (!isActive()) {
      return false;
    }
    body += "<p><b>Method: </b>" + method.getName() + "</p>";
    body += "<p><b>URL: </b><kbd>" + cr.getReference().getPath();
    if (cr.getReference().getQuery() != null && cr.getReference().getQuery() != "") {
      body += "<kbd>?" + cr.getReference().getQuery() + "</kbd></p>";
    }
    else {
      body += "</kbd></p>";
    }
    return true;
  }

  /**
   * Request text in HTML with representation
   * 
   * @param method
   *          <code>Method</code>
   * @param cr
   *          <code>ClientResource</code>
   * @param rep
   *          <code>Representation</code>
   * @return true if docAPI is active
   */
  public boolean appendRequest(Method method, ClientResource cr, Representation rep) {

    if (!isActive()) {
      return false;
    }
    appendRequest(method, cr);
    try {
      if (mediaTest == MediaType.APPLICATION_XML) {
        body += "<p><b>Request XML body : </b><pre lang=\"xml\" style=\"color:green\">" + encodeHTML(rep.getText()) + "</pre></p>";
      }
      else {
        body += "<p><b>Request JSON body : </b><pre style=\"color:green\">" + rep.getText() + "</pre></p>";
      }
    }
    catch (IOException e) {
      fail("DocAPI.appendRequest");
      e.printStackTrace();
    }
    return true;
  }

  /**
   * Response text in HTML with representation
   * 
   * @param result
   *          <code>Representation</code>
   * @return true if docAPI is active
   */
  public boolean appendResponse(Representation result) {
    if (!isActive()) {
      return false;
    }
    try {
      if (mediaTest == MediaType.APPLICATION_XML) {
        body += "<p><b>Response XML body : </b><pre lang=\"xml\" style=\"color:red\">" + encodeHTML(result.getText()) + "</pre></p>";
      }
      else {
        body += "<p><b>Response JSON body : </b><pre style=\"color:red\">" + result.getText() + "</pre></p>";
      }
    }
    catch (IOException e) {
      fail("DocAPI.appendResponse");
      e.printStackTrace();
    }
    return true;
  }

  /**
   * Comment text in HTML
   * 
   * @param commentaire
   *          String
   * @return true if docAPI is active
   */
  public boolean appendComment(String commentaire) {
    if (!isActive()) {
      return false;
    }
    body += "<p>" + commentaire + "</p>";
    return true;
  }

  /** add parameters 
   * @param parameters the parameters and the 
   * @return true if no errors 
   */
  public boolean appendParameters(Map<String, String> parameters) {
    if (!isActive()) {
      return false;
    }
    body += "<p><strong>Parameters:</strong></p><ul>";
    for (Map.Entry<String, String> e : parameters.entrySet()) {
      body += "<li><strong>" + e.getKey() + ":</strong> " + e.getValue() + "</b></li>";
    }
    body += "</ul>";
    return true;
  }

  /**
   * XML text to HTML encoding
   * 
   * @param message
   *          String
   * @return String encoded
   */
  public String encodeHTML(String message) {
    String result = message.replace("<", "&lt;");
    result = result.replace(">", "&gt;");
    return result;
  }

}
