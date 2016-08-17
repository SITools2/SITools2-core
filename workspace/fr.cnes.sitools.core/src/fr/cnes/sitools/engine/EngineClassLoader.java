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
package fr.cnes.sitools.engine;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import org.restlet.engine.Engine;


/**
 * Flexible engine class loader. Uses the current class's class loader as its parent. Can also check with the user class
 * loader defined by {@link Engine#getUserClassLoader()} or with {@link Thread#getContextClassLoader()} or with
 * {@link Class#forName(String)}.
 * 
 * @author Jerome Louvel
 */
public final class EngineClassLoader extends ClassLoader {

  /** The parent RESTlet engine. */
  private final SitoolsEngine engine;

  /**
   * Constructor.
   * @param engine the parent RESTlet engine
   */
  public EngineClassLoader(SitoolsEngine engine) {
    super(EngineClassLoader.class.getClassLoader());
    this.engine = engine;
  }

  @Override
  public Class<?> findClass(String name) throws ClassNotFoundException {
    Class<?> result = null;
    
    // First try the user class loader
    ClassLoader cl = getEngine().getUserClassLoader();

    if (cl != null) {
      try {
        result = cl.loadClass(name);
      }
      catch (ClassNotFoundException cnfe) {
        cnfe.printStackTrace();
      }
    }

    // Then try the current thread's class loader
    if (result == null) {
      cl = Thread.currentThread().getContextClassLoader();

      if (cl != null) {
        try {
          result = cl.loadClass(name);
        }
        catch (ClassNotFoundException cnfe) {
          cnfe.printStackTrace();
        }
      }
    }

    // Finally try with this ultimate approach
    if (result == null) {
      try {
        result = Class.forName(name);
      }
      catch (ClassNotFoundException cnfe) {
        cnfe.printStackTrace();
      }
    }

    // Otherwise throw an exception
    if (result == null) {
      throw new ClassNotFoundException(name);
    }

    return result;
  }

  @Override
  public URL findResource(String name) {
    URL result = null;

    // First try the user class loader
    ClassLoader cl = getEngine().getUserClassLoader();

    if (cl != null) {
      result = cl.getResource(name);
    }

    // Then try the current thread's class loader
    if (result == null) {
      cl = Thread.currentThread().getContextClassLoader();

      if (cl != null) {
        result = cl.getResource(name);
      }
    }

    return result;
  }

  @Override
  public Enumeration<URL> findResources(String name) throws IOException {
    Enumeration<URL> result = null;

    // First try the user class loader
    ClassLoader cl = getEngine().getUserClassLoader();

    if (cl != null) {
      result = cl.getResources(name);
    }

    // Then try the current thread's class loader
    if (result == null) {
      cl = Thread.currentThread().getContextClassLoader();

      if (cl != null) {
        result = cl.getResources(name);
      }
    }

    return result;
  }

  /**
   * Returns the parent Restlet engine.
   * 
   * @return The parent Restlet engine.
   */
  public SitoolsEngine getEngine() {
    return engine;
  }

  @Override
  public Enumeration<URL> getResources(String name) throws IOException {
    Enumeration<URL> allUrls = super.getResources(name);
    ArrayList<URL> result = new ArrayList<URL>();

    if (allUrls != null) {
      try {
        URL url;
        while (allUrls.hasMoreElements()) {
          url = allUrls.nextElement();

          if (result.indexOf(url) == -1) {
            result.add(url);
          }
        }
      }
      catch (NullPointerException e) {
        e.printStackTrace();
      }
    }

    return Collections.enumeration(result);
  }

}
