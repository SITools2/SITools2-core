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
package fr.cnes.sitools.units.dimension.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.measure.unit.Unit;
import javax.measure.unit.UnitConverter;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.engine.SitoolsEngine;
import fr.cnes.sitools.units.dimension.helper.DimensionHelper;

/**
 * Class describing a dimension
 * 
 * @author m.marseille (AKKA technologies)
 */
@XStreamAlias("dimension")
public final class SitoolsDimension implements IResource {

  /** Identifier of the dimension */
  private String id;

  /** Name of the dimension */
  private String name;

  /** Description of the dimension */
  private String description;

  /** Parent identifier implementing the dimension */
  private String parent;

  /** Dimension helper name */
  private String dimensionHelperName;

  /** List of converters for special dimensions */
  private List<String> unitConverters;

//  /** List of unit names */
//  private List<String> unitNames;
//
//  /** List of unit labels */
//  private List<String> unitLabels;

  /** Indicates if the dimension is consistent */
  @XStreamOmitField
  private Boolean isConsistent = null;

  /** List of unit names */
  @XStreamAlias("units")
  private List<SitoolsUnit> sitoolsUnits = null;

  /**
   * Constructor
   */
  public SitoolsDimension() {
    super();
  }

  /**
   * Sets the value of isConsistent
   * 
   * @param isConsistent
   *          the isConsistent to set
   */
  public void setConsistent(Boolean isConsistent) {
    this.isConsistent = isConsistent;
  }

  /**
   * Gets the unitConverters value
   * 
   * @return the unitConverters
   */
  public List<String> getUnitConverters() {
    return unitConverters;
  }

  /**
   * Sets the value of unitConverters
   * 
   * @param unitConverters
   *          the unitConverters to set
   */
  public void setUnitConverters(List<String> unitConverters) {
    this.unitConverters = unitConverters;
  }

  @Override
  public String getId() {
    return id;
  }

  /**
   * Sets the value of name
   * 
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the value of description
   * 
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Sets the value of dimensionHelperName
   * 
   * @param dimensionHelperName
   *          the dimensionHelperName to set
   */
  public void setDimensionHelperName(String dimensionHelperName) {
    this.dimensionHelperName = dimensionHelperName;
  }

  /**
   * Get helper to have the converters
   * 
   * @param helperName
   *          the helper name
   * @return the helper
   */
  public DimensionHelper getDimensionHelper(String helperName) {
    try {
      @SuppressWarnings("unchecked")
      Class<DimensionHelper> helperClass = (Class<DimensionHelper>) Class.forName(helperName);
      Constructor<DimensionHelper> constructor = helperClass.getDeclaredConstructor();
      DimensionHelper helper = constructor.newInstance();
      return helper;
    }
    catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (SecurityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (NoSuchMethodException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IllegalArgumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (InvocationTargetException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  /**
   * Method to indicate if a dimension is consistent or not
   * 
   * @return true if dimension is consistent
   */
  @JsonIgnore
  public boolean isDimensionalyConsistent() {
    boolean consistent = true;
    for (int i = 0; i < getUnits().size() - 1; i++) {
      for (int j = i + 1; j < getUnits().size(); j++) {
        if (!getUnits().get(i).getUnit().isCompatible(getUnits().get(j).getUnit())) {
          consistent = false;
          break;
        }
      }
      if (!consistent) {
        break;
      }
    }
    return consistent;
  }

  /**
   * Indicates if the dimension is consistent or not
   * 
   * @return true if basic and registered converters are sufficient to be able to convert all units in the dimension
   */
  @JsonIgnore
  public Map<String, Unit<?>> getUnconsistency() {
    Map<String, Unit<?>> unconsistencies = new ConcurrentHashMap<String, Unit<?>>();
    for (int i = 0; i < getUnits().size() - 1; i++) {
      // Cartesian product here, now storing start and target units
      Unit<?> startUnit = getUnits().get(i).getUnit();
      for (int j = i + 1; j < getUnits().size(); j++) {
        Unit<?> targetUnit = getUnits().get(j).getUnit();
        // If dimensions are different, still a chance that a converter has been registered
        if (!startUnit.isCompatible(targetUnit)) {
          List<SitoolsUnitConverter> registeredConverters = this.getConverters(); // get the list of registered
                                                                                  // converters
          boolean converterFound = false;
          for (SitoolsUnitConverter conv : registeredConverters) {
            if (conv.getStartUnit().isCompatible(startUnit) && conv.getTargetUnit().isCompatible(targetUnit)) {
              converterFound = true;
              break;
            }
            if (conv.getStartUnit().isCompatible(targetUnit) && conv.getTargetUnit().isCompatible(startUnit)) {
              converterFound = true;
              break;
            }
          }
          if (!converterFound) {
            unconsistencies = new ConcurrentHashMap<String, Unit<?>>();
            unconsistencies.put("startUnit", startUnit);
            unconsistencies.put("targetUnit", targetUnit);
            return unconsistencies;
          }
        }
      }
    }
    return unconsistencies;
  }

  /**
   * List of converter beans
   * 
   * @return the list of instantiated converters
   */
  private List<SitoolsUnitConverter> getConverters() {
    List<SitoolsUnitConverter> list = new ArrayList<SitoolsUnitConverter>();
    List<SitoolsUnitConverter> registered;
    SitoolsEngine engine = SitoolsEngine.getInstance();
    for (DimensionHelper helper : engine.getRegisteredDimensionHelpers()) {
      if (helper.getClass().getCanonicalName().equals(dimensionHelperName)) {
        registered = helper.getRegisteredConverters();
        for (String converterName : unitConverters) {
          for (SitoolsUnitConverter conv : registered) {
            if (conv.getClass().getCanonicalName().equals(converterName)) {
              list.add(conv);
            }
          }
        }
      }
    }
    return list;
  }

  /**
   * Gets the dimensionHelperName value
   * 
   * @return the dimensionHelperName
   */
  public String getDimensionHelperName() {
    return dimensionHelperName;
  }

  /**
   * Set the identifier of the dimension
   * 
   * @param string
   *          the identifier to set
   */
  public void setId(String string) {
    this.id = string;
  }

//  /**
//   * Sets the value of unitNames
//   * 
//   * @param unitNames
//   *          the unitNames to set
//   */
//  public void setUnitNames(List<String> unitNames) {
//    this.unitNames = unitNames;
//  }
//
//  /**
//   * Gets the unitNames value
//   * 
//   * @return the unitNames
//   */
//  public List<String> getUnitNames() {
//    return unitNames;
//  }

  /**
   * Sets the value of parent
   * 
   * @param parent
   *          the parent to set
   */
  public void setParent(String parent) {
    this.parent = parent;
  }

  /**
   * Gets the parent value
   * 
   * @return the parent
   */
  public String getParent() {
    return parent;
  }

  /**
   * Gets the isConsistent value
   * 
   * @return the isConsistent
   */
  public boolean isConsistent() {
    if (isConsistent != null) {
      return isConsistent;
    }
    else {
      isConsistent = !this.getUnconsistency().containsKey("startUnit");
      return isConsistent;
    }
  }

  /**
   * Get the converter from two unit expressions
   * 
   * @param u
   *          the start unit
   * @param v
   *          the target unit
   * @return the associated converter
   */
  public UnitConverter getUnitConverter(String u, String v) {
    if (this.isConsistent()) {
      SitoolsUnit startUnit = new SitoolsUnit(u);
      SitoolsUnit targetUnit = new SitoolsUnit(v);
      if (startUnit.getUnit().isCompatible(targetUnit.getUnit())) {
        return startUnit.getUnit().getConverterToAny(targetUnit.getUnit());
      }
      else {
        List<SitoolsUnitConverter> registeredConverters = this.getConverters(); // get the list of registered converters
        for (SitoolsUnitConverter conv : registeredConverters) {
          if (conv.getStartUnit().isCompatible(startUnit.getUnit())
              && conv.getTargetUnit().isCompatible(targetUnit.getUnit())) {
            conv.setStartUnit(startUnit.getUnit());
            conv.setTargetUnit(targetUnit.getUnit());
            return conv.getBaseToTargetConverter();
          }
          if (conv.getStartUnit().isCompatible(targetUnit.getUnit())
              && conv.getTargetUnit().isCompatible(startUnit.getUnit())) {
            conv.setStartUnit(startUnit.getUnit());
            conv.setTargetUnit(targetUnit.getUnit());
            return conv.getTargetToBaseConverter();
          }
        }
      }
    }
    return null;
  }

  /**
   * Gets the sitoolsUnits value
   * 
   * @return the sitoolsUnits
   */
  public List<SitoolsUnit> getUnits() {
    return sitoolsUnits;
  }

  /**
   * Sets the value of sitoolsUnits
   * 
   * @param sitoolsUnits
   *          the sitoolsUnits to set
   */
  public void setUnits(List<SitoolsUnit> sitoolsUnits) {
    this.sitoolsUnits = sitoolsUnits;
  }

}
