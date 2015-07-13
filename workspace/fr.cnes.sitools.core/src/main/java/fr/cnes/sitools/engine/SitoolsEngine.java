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
package fr.cnes.sitools.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.engine.io.IoUtils;

import fr.cnes.sitools.plugins.applications.business.AbstractApplicationPlugin;
import fr.cnes.sitools.plugins.filters.model.FilterModel;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.units.dimension.helper.DimensionHelper;

/**
 * Like restlet engine to register Helpers (Sitools Converters ...)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class SitoolsEngine {

  /**
   * Descriptor folder path
   */
  public static final String DESCRIPTOR = "META-INF/services";

  /**
   * Descriptor file name
   */
  public static final String DESCRIPTOR_DATASET_CONVERTER = "fr.cnes.sitools.converter.ConverterHelper";

  /**
   * Descriptor file name
   */
  public static final String DESCRIPTOR_DATASET_FILTER = "fr.cnes.sitools.filter.FilterHelper";

  /**
   * Descriptor file name
   */
  public static final String DESCRIPTOR_DATASET_SVA = "fr.cnes.sitools.sva.SvaHelper";

  /**
   * Descriptor file name
   */
  public static final String DESCRIPTOR_APPLICATION_PLUGIN = "fr.cnes.sitools.plugins.applications.ApplicationHelper";

  /**
   * Descriptor file name
   */
  public static final String DESCRIPTOR_FILTER_PLUGIN = "fr.cnes.sitools.plugins.filters.FilterHelper";

  /**
   * Descriptor file name
   */
  public static final String DESCRIPTOR_RESOURCE_PLUGIN = "fr.cnes.sitools.plugins.resources.ResourceHelper";

  /**
   * Descriptor file name
   */
  public static final String DESCRIPTOR_UNITS = "fr.cnes.sitools.units.UnitsHelper";

  /**
   * Descriptor Path
   */
  public static final String DESCRIPTOR_DATASET_CONVERTER_PATH = DESCRIPTOR + "/" + DESCRIPTOR_DATASET_CONVERTER;

  /**
   * Descriptor Path
   */
  public static final String DESCRIPTOR_DATASET_FILTER_PATH = DESCRIPTOR + "/" + DESCRIPTOR_DATASET_FILTER;

  /**
   * Descriptor Path
   */
  public static final String DESCRIPTOR_DATASET_SVA_PATH = DESCRIPTOR + "/" + DESCRIPTOR_DATASET_SVA;

  /**
   * Descriptor Path
   */
  public static final String DESCRIPTOR_APPLICATION_PLUGIN_PATH = DESCRIPTOR + "/" + DESCRIPTOR_APPLICATION_PLUGIN;

  /**
   * Descriptor Path
   */
  public static final String DESCRIPTOR_FILTER_PLUGIN_PATH = DESCRIPTOR + "/" + DESCRIPTOR_FILTER_PLUGIN;

  /**
   * Descriptor Path
   */
  public static final String DESCRIPTOR_RESOURCE_PLUGIN_PATH = DESCRIPTOR + "/" + DESCRIPTOR_RESOURCE_PLUGIN;

  /**
   * Descriptor Path
   */
  public static final String DESCRIPTOR_UNITS_PLUGIN_PATH = DESCRIPTOR + "/" + DESCRIPTOR_UNITS;

  /**
   * The registered engine.
   */
  private static volatile SitoolsEngine instance = null;

  /**
   * Registered AbstractConverter list
   */
  private final List<fr.cnes.sitools.dataset.converter.business.AbstractConverter> datasetConverters;

  /**
   * Registered AbstractFilter list
   */
  private final List<fr.cnes.sitools.dataset.filter.business.AbstractFilter> datasetFilters;

  /**
   * Registered AbstractApplicationPlugin list
   */
  private List<AbstractApplicationPlugin> registeredApplicationPlugins;

  /**
   * Registered AbstractApplicationPlugin list
   */
  private List<FilterModel> filterPlugins;

  /**
   * Registered resources plugins
   */
  private List<ResourceModel> registeredParameterizedResources;

  /**
   * Registered resources plugins
   */
  private List<DimensionHelper> registeredDimensionHelpers;

  /** Class loader to use for dynamic class loading. */
  private volatile ClassLoader classLoader;

  /** User class loader to use for dynamic class loading. */
  private volatile ClassLoader userClassLoader;

  /**
   * Constructor that will automatically attempt to discover connectors.
   */
  public SitoolsEngine() {
    this(true);
  }

  /**
   * Constructor.
   * 
   * @param discoverHelpers
   *          True if helpers should be automatically discovered.
   */
  public SitoolsEngine(boolean discoverHelpers) {
    // Prevent engine initialization code from recreating other engines
    setInstance(this);

    this.classLoader = createClassLoader();
    this.userClassLoader = null;

    datasetConverters = new CopyOnWriteArrayList<fr.cnes.sitools.dataset.converter.business.AbstractConverter>();
    datasetFilters = new CopyOnWriteArrayList<fr.cnes.sitools.dataset.filter.business.AbstractFilter>();
    registeredApplicationPlugins = new CopyOnWriteArrayList<fr.cnes.sitools.plugins.applications.business.AbstractApplicationPlugin>();

    filterPlugins = new CopyOnWriteArrayList<FilterModel>();

    registeredParameterizedResources = new CopyOnWriteArrayList<ResourceModel>();
    registeredDimensionHelpers = new CopyOnWriteArrayList<DimensionHelper>();

    if (discoverHelpers) {
      try {
        discoverDatasetConverters();

        discoverDatasetFilters();

        discoverApplicationPlugins();

        discoverFilterPlugins();

        discoverParameterizedResources();

        discoverDimensionHelpers();
      }
      catch (IOException e) {
        Context.getCurrentLogger().log(Level.WARNING, "An error occured while discovering the sitools helpers.", e);
      }
    }
  }

  /**
   * Clears the current Restlet Engine altogether.
   */
  public static synchronized void clear() {
    setInstance(null);
  }

  /**
   * Returns the registered SitoolsEngine engine.
   * 
   * @return The registered SitoolsEngine engine.
   */
  public static synchronized SitoolsEngine getInstance() {
    SitoolsEngine result = instance;

    if (result == null) {
      result = register();
    }

    return result;
  }

  /**
   * Returns the classloader resource for a given name/path.
   * 
   * @param name
   *          The name/path to lookup.
   * @return The resource URL.
   */
  public static java.net.URL getResource(String name) {
    return getInstance().getClassLoader().getResource(name);
  }

  /**
   * Returns the class object for the given name using the engine classloader.
   * 
   * @param className
   *          The class name to lookup.
   * @return The class object or null if the class was not found.
   * @see #getClassLoader()
   * @throws ClassNotFoundException
   *           if class is not found
   */
  public static Class<?> loadClass(String className) throws ClassNotFoundException {
    return getInstance().getClassLoader().loadClass(className);
  }

  /**
   * Registers a new Restlet Engine.
   * 
   * @return The registered engine.
   */
  public static synchronized SitoolsEngine register() {
    return register(true);
  }

  /**
   * Registers a new Restlet Engine.
   * 
   * @param discoverPlugins
   *          True if plug-ins should be automatically discovered.
   * @return The registered engine.
   */
  public static synchronized SitoolsEngine register(boolean discoverPlugins) {
    SitoolsEngine result = new SitoolsEngine(discoverPlugins);
    SitoolsEngine.setInstance(result);
    return result;
  }

  /**
   * Sets the registered Restlet engine.
   * 
   * @param engine
   *          The registered Restlet engine.
   * @deprecated Use the {@link #register()} and {@link #register(boolean)} methods instead.
   */
  @Deprecated
  public static synchronized void setInstance(SitoolsEngine engine) {
    instance = engine;
  }

  /**
   * Creates a new class loader. By default, it returns an instance of {@link org.restlet.engine.util.EngineClassLoader}
   * .
   * 
   * @return A new class loader.
   */
  protected ClassLoader createClassLoader() {
    return new EngineClassLoader(this);
  }

  /**
   * Discovers of the converters available to the user
   * 
   * @throws IOException
   *           when converter folder not found
   */
  private void discoverDatasetConverters() throws IOException {
    registerHelpers(DESCRIPTOR_DATASET_CONVERTER_PATH, getDatasetConverters(), null);
  }

  /**
   * Discovers of the filters available to the user
   * 
   * @throws IOException
   *           when filter folder not found
   */
  private void discoverDatasetFilters() throws IOException {
    registerHelpers(DESCRIPTOR_DATASET_FILTER_PATH, getDatasetFilters(), null);
  }

  /**
   * Discovers of the applicationPlugin available to the user
   * 
   * @throws IOException
   *           when filter folder not found
   */
  private void discoverApplicationPlugins() throws IOException {
    registerHelpers(DESCRIPTOR_APPLICATION_PLUGIN_PATH, getRegisteredApplicationPlugins(), null);
  }

  /**
   * Discovers of the filterPlugin available to the user
   * 
   * @throws IOException
   *           when filter folder not found
   */
  private void discoverFilterPlugins() throws IOException {
    registerHelpers(DESCRIPTOR_FILTER_PLUGIN_PATH, getFilterPlugins(), null);
  }

  /**
   * Discovers the parameterized resources available
   * 
   * @throws IOException
   *           when not found
   */
  private void discoverParameterizedResources() throws IOException {
    registerHelpers(DESCRIPTOR_RESOURCE_PLUGIN_PATH, getRegisteredParameterizedResources(), null);
  }

  /**
   * Discovers the dimension helpers available
   * 
   * @throws IOException
   *           when not found
   */
  private void discoverDimensionHelpers() throws IOException {
    registerHelpers(DESCRIPTOR_UNITS_PLUGIN_PATH, getRegisteredDimensionHelpers(), null);
  }

  /**
   * Returns the class loader. It uses the delegation model with the Engine class's class loader as a parent. If this
   * parent doesn't find a class or resource, it then tries the user class loader (via {@link #getUserClassLoader()} and
   * finally the {@link Thread#getContextClassLoader()}.
   * 
   * @return The engine class loader.
   * @see org.restlet.engine.util.EngineClassLoader
   */
  public ClassLoader getClassLoader() {
    return classLoader;
  }

  /**
   * Parses a line to extract the provider class name.
   * 
   * @param line
   *          The line to parse.
   * @return The provider's class name or an empty string.
   */
  private String getProviderClassName(String line) {
    final int index = line.indexOf('#');
    if (index != -1) {
      line = line.substring(0, index);
    }
    return line.trim();
  }

  /**
   * Returns the list of available converters.
   * 
   * @return The list of available converters.
   */
  public List<fr.cnes.sitools.dataset.converter.business.AbstractConverter> getDatasetConverters() {
    return datasetConverters;
  }

  /**
   * Gets the registeredFilters value
   * 
   * @return the registeredFilters
   */
  public List<fr.cnes.sitools.dataset.filter.business.AbstractFilter> getDatasetFilters() {
    return datasetFilters;
  }

  /**
   * Returns the list of available ApplicationPlugins.
   * 
   * @return The list of available applicationPlugins.
   */
  public List<AbstractApplicationPlugin> getRegisteredApplicationPlugins() {
    return registeredApplicationPlugins;
  }

  /**
   * Returns the list of available FilterPlugins.
   * 
   * @return The list of available filterPlugins.
   */
  public List<FilterModel> getFilterPlugins() {
    return filterPlugins;
  }

  /**
   * Returns the list of available Dimension Helpers.
   * 
   * @return The list of available Dimension Helpers.
   */
  public List<DimensionHelper> getRegisteredDimensionHelpers() {
    return registeredDimensionHelpers;
  }

  /**
   * Returns the class loader specified by the user and that should be used in priority.
   * 
   * @return The user class loader
   */
  public ClassLoader getUserClassLoader() {
    return userClassLoader;
  }

  /**
   * Registers a helper.
   * 
   * @param classLoader
   *          The classloader to use.
   * @param provider
   *          Bynary name of the helper's class.
   * @param helpers
   *          The list of helpers to update.
   * @param constructorClass
   *          The constructor parameter class to look for.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void registerHelper(ClassLoader classLoader, String provider, List helpers, Class constructorClass) {
    if ((provider != null) && (!provider.equals(""))) {
      // Instantiate the factory
      Class providerClass;
      try {
        providerClass = classLoader.loadClass(provider);
        if (constructorClass == null) {
          helpers.add(providerClass.newInstance());
        }
        else {
          helpers.add(providerClass.getConstructor(constructorClass).newInstance(constructorClass.cast(null)));
        }
      }
      catch (ClassNotFoundException e) {
        Context.getCurrentLogger().log(Level.WARNING, "Class " + provider + "cannot be found ", e);
      }
      catch (InstantiationException e) {
        Context.getCurrentLogger().log(Level.WARNING, "Class " + provider + "cannot be instanciated ", e);
      }
      catch (IllegalAccessException e) {
        Context.getCurrentLogger().log(Level.WARNING, "Class " + provider + "cannot be accessed ", e);
      }
      catch (IllegalArgumentException e) {
        Context.getCurrentLogger().log(Level.SEVERE, "Illegal argument ", e);
      }
      catch (SecurityException e) {
        Context.getCurrentLogger().log(Level.SEVERE, "Security exception ", e);
      }
      catch (InvocationTargetException e) {
        Context.getCurrentLogger().log(Level.SEVERE, "Invokation failed ", e);
      }
      catch (NoSuchMethodException e) {
        Context.getCurrentLogger().log(Level.SEVERE, "Method not found in " + provider, e);
      }
    }
  }

  /**
   * Registers a helper.
   * 
   * @param classLoader
   *          The classloader to use.
   * @param configUrl
   *          Configuration URL to parse
   * @param helpers
   *          The list of helpers to update.
   * @param constructorClass
   *          The constructor parameter class to look for.
   */
  public void registerHelpers(ClassLoader classLoader, java.net.URL configUrl, List<?> helpers,
      Class<?> constructorClass) {
    try {
      BufferedReader reader = null;
      try {
        reader = new BufferedReader(new InputStreamReader(configUrl.openStream(), "utf-8"), IoUtils.BUFFER_SIZE);
        String line = reader.readLine();

        while (line != null) {
          registerHelper(classLoader, getProviderClassName(line), helpers, constructorClass);
          line = reader.readLine();
        }
      }
      catch (IOException e) {
        Context.getCurrentLogger().log(Level.SEVERE, "Unable to read the provider descriptor: " + configUrl.toString());
      }
      finally {
        if (reader != null) {
          reader.close();
        }
      }
    }
    catch (IOException ioe) {
      Context.getCurrentLogger().log(Level.SEVERE, "Exception while detecting the helpers.", ioe);
    }
  }

  /**
   * Registers a list of helpers.
   * 
   * @param descriptorPath
   *          Classpath to the descriptor file.
   * @param helpers
   *          The list of helpers to update.
   * @param constructorClass
   *          The constructor parameter class to look for.
   * @throws IOException
   *           if occurs.
   */
  public void registerHelpers(String descriptorPath, List<?> helpers, Class<?> constructorClass) throws IOException {
    final ClassLoader activeClassLoader = getClassLoader();
    Enumeration<java.net.URL> configUrls = activeClassLoader.getResources(descriptorPath);

    if (configUrls != null) {
      for (final Enumeration<java.net.URL> configEnum = configUrls; configEnum.hasMoreElements();) {
        registerHelpers(activeClassLoader, configEnum.nextElement(), helpers, constructorClass);
      }
    }
  }

  /**
   * Registers a factory that is used by the URL class to create the {@link java.net.URLConnection} instances when the
   * {@link java.net.URL#openConnection()} or {@link java.net.URL#openStream()} methods are invoked.
   * <p>
   * The implementation is based on the client dispatcher of the current context, as provided by
   * {@link Context#getCurrent()} method.
   */
  public void registerUrlFactory() {
    // Set up an java.net.URLStreamHandlerFactory for
    // proper creation of java.net.URL instances
    java.net.URL.setURLStreamHandlerFactory(new java.net.URLStreamHandlerFactory() {
      public java.net.URLStreamHandler createURLStreamHandler(String protocol) {
        final java.net.URLStreamHandler result = new java.net.URLStreamHandler() {

          @Override
          protected java.net.URLConnection openConnection(java.net.URL url) throws IOException {
            return new java.net.URLConnection(url) {

              @Override
              public void connect() throws IOException {
              }

              @Override
              public InputStream getInputStream() throws IOException {
                InputStream result = null;

                // Retrieve the current context
                final Context context = Context.getCurrent();

                if (context != null) {
                  final Response response = context.getClientDispatcher().handle(
                      new Request(Method.GET, this.url.toString()));

                  if (response.getStatus().isSuccess()) {
                    result = response.getEntity().getStream();
                  }
                }

                return result;
              }
            };
          }

        };

        return result;
      }

    });
  }

  /**
   * Sets the engine class loader.
   * 
   * @param newClassLoader
   *          The new user class loader to use.
   */
  public void setClassLoader(ClassLoader newClassLoader) {
    this.classLoader = newClassLoader;
  }

  /**
   * Sets the list of available converter helpers.
   * 
   * @param registeredConverters
   *          The list of available converter helpers.
   */
  public void setDatasetConverters(
      List<fr.cnes.sitools.dataset.converter.business.AbstractConverter> registeredConverters) {
    synchronized (this.datasetConverters) {
      if (registeredConverters != this.datasetConverters) {
        this.datasetConverters.clear();

        if (registeredConverters != null) {
          this.datasetConverters.addAll(registeredConverters);
        }
      }
    }
  }

  /**
   * Sets the list of available filter helpers.
   * 
   * @param registeredFilters
   *          The list of available filter helpers.
   */
  public void setDatasetFilters(List<fr.cnes.sitools.dataset.filter.business.AbstractFilter> registeredFilters) {
    synchronized (this.datasetFilters) {
      if (registeredFilters != this.datasetFilters) {
        this.datasetFilters.clear();

        if (registeredFilters != null) {
          this.datasetFilters.addAll(registeredFilters);
        }
      }
    }
  }

  /**
   * Sets the user class loader that should used in priority.
   * 
   * @param newClassLoader
   *          The new user class loader to use.
   */
  public void setUserClassLoader(ClassLoader newClassLoader) {
    this.userClassLoader = newClassLoader;
  }

  /**
   * Sets the value of registeredParameterizedResources
   * 
   * @param registeredParameterizedResources
   *          the registeredParameterizedResources to set
   */
  public void setRegisteredParameterizedResources(List<ResourceModel> registeredParameterizedResources) {
    this.registeredParameterizedResources = registeredParameterizedResources;
  }

  /**
   * Gets the registeredParameterizedResources value
   * 
   * @return the registeredParameterizedResources
   */
  public List<ResourceModel> getRegisteredParameterizedResources() {
    return registeredParameterizedResources;
  }

}
