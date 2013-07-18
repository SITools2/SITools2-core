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
package fr.cnes.sitools.form.dataset.dto;

import java.util.ArrayList;
import java.util.List;

import fr.cnes.sitools.form.dataset.model.Form;
import fr.cnes.sitools.form.dataset.model.SimpleParameter;
import fr.cnes.sitools.form.dataset.model.Zone;
import fr.cnes.sitools.form.model.AbstractParameter;
import fr.cnes.sitools.form.model.Value;

/**
 * FormDTO class to store Form definition
 * 
 * @author d.arpin (AKKA Technologies)
 */
public final class FormDTO {

  /**
   * Form identifier
   */
  private String id;

  /**
   * Resource parent (DataSet)
   */
  private String parent;

  /**
   * Form name
   */
  private String name;

  /**
   * Associated CSS
   */
  private String css;

  /**
   * Form description
   */
  private String description;

  /**
   * Comment for <code>parametersDTO</code>
   */
  private List<ParameterDTO> parameters;
  
  /**
   * Comment for <code>zoneDTO</code>
   */
  private List<ZoneDTO> zones;


  /**
   * width
   */
  private int width;

  /**
   * height
   */
  private int height;

  /**
   * If the Form is authorized
   */
  private String authorized;

  /**
   * Parent url attachment DataSet
   */
  private String parentUrl;

  /**
   * Constructor
   */
  public FormDTO() {
    super();
  }

  /**
   * Gets the id value
   * 
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the value of id
   * 
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
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
   * Sets the value of parent
   * 
   * @param parent
   *          the parent to set
   */
  public void setParent(String parent) {
    this.parent = parent;
  }

  /**
   * Gets the name value
   * 
   * @return the name
   */
  public String getName() {
    return name;
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
   * Gets the description value
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
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
   * Gets the parametersDTO value
   * 
   * @return the parametersDTO
   */
  public List<ParameterDTO> getParameters() {
    return parameters;
  }

  /**
   * Get the width of the form
   * 
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * Set the width
   * 
   * @param width
   *          the width to set
   */
  public void setWidth(int width) {
    this.width = width;
  }

  /**
   * Returns the height of the form
   * 
   * @return the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * Set the height of the form
   * 
   * @param height
   *          the height to set
   */
  public void setHeight(int height) {
    this.height = height;
  }

  /**
   * Sets the value of parametersDTO
   * 
   * @param parameters
   *          the parametersDTO to set
   */
  public void setParameters(List<ParameterDTO> parameters) {
    this.parameters = parameters;
  }

  /**
   * Get the CSS of a form
   * 
   * @return the CSS
   */
  public String getCss() {
    return css;
  }

  /**
   * Set the CSS of a form
   * 
   * @param css
   *          the CSS to set
   */
  public void setCss(final String css) {
    this.css = css;
  }

  /**
   * Transform a form to a DTO
   * 
   * @param form
   *          the form to transform
   * @return the DTO corresponding to the form
   */
  public static FormDTO formToDTO(Form form) {
    FormDTO dto = new FormDTO();
    dto.setId(form.getId());
    dto.setParent(form.getParent());
    dto.setName(form.getName());
    dto.setDescription(form.getDescription());
    dto.setParameters(ParametersDTO.parametersToDTO(form.getParameters()));
    dto.setZones(zonesToDTO(form.getZones()));
    dto.setWidth(form.getWidth());
    dto.setHeight(form.getHeight());
    dto.setCss(form.getCss());
    dto.setParentUrl(form.getParentUrl());
    return dto;
  }

  /**
   * Transform a DTO to a form
   * 
   * @param dto
   *          the DTO to transform
   * @return a form corresponding to the DTO
   */
  public static Form dtoToForm(FormDTO dto) {
    Form form = new Form();
    form.setId(dto.getId());
    form.setParent(dto.getParent());
    form.setName(dto.getName());
    form.setDescription(dto.getDescription());
    form.setParameters(ParametersDTO.dtoToParameters(dto.getParameters()));
    form.setZones(dtoToZones(dto.getZones()));
    form.setWidth(dto.getWidth());
    form.setHeight(dto.getHeight());
    form.setCss(dto.getCss());
    return form;
  }

  /**
   * Sets the value of authorized
   * 
   * @param authorized
   *          the authorized to set
   */
  public void setAuthorized(String authorized) {
    this.authorized = authorized;
  }

  /**
   * Gets the authorized value
   * 
   * @return the authorized
   */
  public String getAuthorized() {
    return authorized;
  }

  /**
   * Sets the value of parentUrl
   * 
   * @param parentUrl
   *          the parentUrl to set
   */
  public void setParentUrl(String parentUrl) {
    this.parentUrl = parentUrl;
  }

  /**
   * Gets the parentUrl value
   * 
   * @return the parentUrl
   */
  public String getParentUrl() {
    return parentUrl;
  }

  /**
   * Gets the zones value
   * @return the zones
   */
  public List<ZoneDTO> getZones() {
    return zones;
  }

  /**
   * Sets the value of zones
   * @param zones the zones to set
   */
  public void setZones(List<ZoneDTO> zones) {
    this.zones = zones;
  }

  /**
   * Convert a list of Zone to a ZoneDTO
   * 
   * @param zones a list of zones
   * @return {@link ZoneDTO}
   */
  public static List<ZoneDTO> zonesToDTO(List<Zone> zones) {

    List<ZoneDTO> zonesDTO = new ArrayList<ZoneDTO>();

    if (zones != null) {
      for (Zone myzone : zones) {
        zonesDTO.add(zoneToDTO(myzone));
      }
    }
  
    return zonesDTO;

  }
  
  /**
   * Convert A Zone To a ZoneDTO
   * 
   * @param zone A zone
   * @return {@link ZoneDTO}
   */
  private static ZoneDTO zoneToDTO(Zone zone) {

    ZoneDTO zoneDTO = new ZoneDTO();

    zoneDTO.setCss(zone.getCss());
    zoneDTO.setHeight(zone.getHeight());
    zoneDTO.setId(zone.getId());
    zoneDTO.setTitle(zone.getTitle());
    zoneDTO.setPosition(zone.getPosition());
    zoneDTO.setWidth(zone.getWidth());
    zoneDTO.setCollapsible(zone.isCollapsible());
    zoneDTO.setCollapsed(zone.isCollapsed());
    if (!zone.getParams().isEmpty()){
      zoneDTO.setParams(ParametersDTO.parametersToDTO(zone.getParams()));
    }
    return zoneDTO;
    
  }
  
  
  /**
   * dtoToZones
   * @param dtos
   * @return
   */
  public static List<Zone> dtoToZones(List<ZoneDTO> dtos) {

    List<Zone> zones = new ArrayList<Zone>();
    if ((dtos != null) && (dtos.size() > 0)) {
      for (ZoneDTO zoneDTO : dtos) {
        zones.add(dtoToZone(zoneDTO));
      }
    }
    return zones;
  }
  
  /**
   * dtoToZone
   * @param zoneDTO
   * @return
   */
  private static Zone dtoToZone(ZoneDTO zoneDTO) {

    Zone zone = new Zone();

    zone.setId(zoneDTO.getId());
    zone.setCss(zoneDTO.getCss());
    zone.setHeight(zoneDTO.getHeight());
    zone.setPosition(zoneDTO.getPosition());
    zone.setWidth(zoneDTO.getWidth());
    zone.setTitle(zoneDTO.getTitle());
    zone.setCollapsible(zoneDTO.isCollapsible());
    zone.setCollapsed(zoneDTO.isCollapsed());
    if (!zoneDTO.getParams().isEmpty()) {
      zone.setParams(ParametersDTO.dtoToParameters(zoneDTO.getParams()));
    }
    return zone;
  }

  
  

}
