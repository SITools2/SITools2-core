/**
 * Copyright 2005-2010 Noelios Technologies.
 * 
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL 1.0 (the
 * "Licenses"). You can select the license that you prefer but you may not use
 * this file except in compliance with one of these Licenses.
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0.html
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1.php
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1.php
 * 
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0.php
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://www.noelios.com/products/restlet-engine
 * 
 * Restlet is a registered trademark of Noelios Technologies.
 */

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
