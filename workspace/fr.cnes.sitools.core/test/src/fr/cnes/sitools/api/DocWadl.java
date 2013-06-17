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
package fr.cnes.sitools.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Class to generate WADL documentation
 * 
 * 
 * @author m.marseille (AKKA Technologies)
 */
public class DocWadl {

  /** Application name */
  private String applicationName = null;

  /** XML File output */
  private File docWadl;
  
  /** HTML File output */
  private File docHtml;

  /**
   * Constructor
   * @param appName the name of the web application
   */
  public DocWadl(String appName) {
    super();
    this.applicationName = appName;
    try {
      String docUrl = "./documentation/wadl/";
      String baseUrl = docUrl + this.applicationName;
      File wadlDirectory = new File(baseUrl);
      if (!wadlDirectory.exists()) {
        wadlDirectory.mkdirs();
      }
      if (appName != null && !appName.equals("")) {
        this.docWadl = new File(baseUrl + "/" + this.applicationName + ".xml");
        this.docWadl.createNewFile();
        this.docHtml = new File(baseUrl + "/" + this.applicationName + ".html");
        this.docHtml.createNewFile();
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Get the print stream for Wadl
   * @return a stream where wadl can be written.
   * @throws FileNotFoundException if file is not found
   */
  public PrintStream getWadlPrintStream() throws FileNotFoundException {
    PrintStream ps = new PrintStream(docWadl);
    return ps;
  }
  
  /**
   * Get the print stream for Html
   * @return a stream where HTML can be written.
   * @throws FileNotFoundException if file is not found
   */
  public PrintStream getHtmlPrintStream() throws FileNotFoundException {
    PrintStream ps = new PrintStream(docHtml);
    return ps;
  }

}
