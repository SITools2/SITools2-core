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
package fr.cnes.sitools.dataset.converter.dto;

import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.dto.ExtensionModelDTO;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;

/**
 * CLass bean to keep a converter definition.
 * 
 * @author m.marseille (AKKA Technologies)
 */
@XStreamAlias("converterModel")
public class ConverterModelDTO extends ExtensionModelDTO<ConverterParameter> {

  /** serialVersionUID */
  private static final long serialVersionUID = -5661071686675543858L;
  /**
   * The status of the converter
   */
  private String status;

  /**
   * Constructor.
   */
  public ConverterModelDTO() {
    this.setName("NullConverter");
    this.setDescription("Converter with no action.");
    this.setParameters(new ArrayList<ConverterParameter>());
    this.setClassAuthor("");
    this.setClassVersion("");
  }

  /**
   * Sets the value of status
   * 
   * @param status
   *          the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Gets the status value
   * 
   * @return the status
   */
  public String getStatus() {
    return status;
  }

}
