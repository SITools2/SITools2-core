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

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.form.dataset.model.Parameters;
import fr.cnes.sitools.form.dataset.model.SimpleParameter;
import fr.cnes.sitools.form.model.AbstractParameter;
import fr.cnes.sitools.form.model.Value;

/**
 * Parameters for the DTO
 * 
 * @author AKKA (OpenWiz)
 */
public final class ParametersDTO {

  /**
   * Comment for <code>startParameterDTO</code>
   */
  private String startParameterDTO;

  /**
   * Comment for <code>parametersDTO</code>
   */
  @XStreamAlias("parameterDTO")
  private List<ParameterDTO> parametersDTO;

  /**
   * Description goes here.
   * 
   * @param parameters
   *          Parameters
   * @return ParametersDTO
   */
  public static List<ParameterDTO> parametersToDTO(List<AbstractParameter> parameters) {

    List<ParameterDTO> paramsDTO = new ArrayList<ParameterDTO>();

    for (AbstractParameter ap : parameters) {
      paramsDTO.add(abstractParameterToDTO(ap));
    }

    return paramsDTO;
  }

  /**
   * Description goes here.
   * 
   * @param parameters
   *          Parameters
   * @return ParametersDTO
   */
  public static ParametersDTO parametersToDTO(Parameters parameters) {

    ParametersDTO paramsDTO = new ParametersDTO();
    // paramsDTO.setStartParameterDTO(parameters.getStartParameter().getCode());

    paramsDTO.setParametersDTO(new ArrayList<ParameterDTO>());

    for (AbstractParameter ap : parameters.getParameters()) {
      paramsDTO.getParametersDTO().add(abstractParameterToDTO(ap));
    }

    return paramsDTO;
  }

  /**
   * Convert from abstract parameter to Parameter DTO.
   * 
   * @param abstractParameter
   *          An abstract parameter
   * @return ParametersDTO
   */
  private static ParameterDTO abstractParameterToDTO(AbstractParameter abstractParameter) {

    ParameterDTO paramDTO = new ParameterDTO();

    // Set type property
    String type = null;
    String sst = ((SimpleParameter) abstractParameter).getType();
    if (sst != null) {
      type = sst.toString();
    }
    // }
    // else if (abstractParameter instanceof NoSelection) {
    // NoSelectionType nst = ((NoSelection) abstractParameter).getType();
    // if (nst != null) {
    // type = nst.toString();
    // }
    // }

    paramDTO.setType(type);

    paramDTO.setWidth(abstractParameter.getWidth());
    paramDTO.setHeight(abstractParameter.getHeight());
    paramDTO.setXpos(abstractParameter.getXpos());
    paramDTO.setYpos(abstractParameter.getYpos());
    paramDTO.setId(abstractParameter.getId());
    paramDTO.setCss(abstractParameter.getCss());
    paramDTO.setJsAdminObject(abstractParameter.getJsAdminObject());
    paramDTO.setJsUserObject(abstractParameter.getJsUserObject());
    paramDTO.setValueSelection(abstractParameter.getValueSelection());
    paramDTO.setDefaultValues(abstractParameter.getDefaultValues());
    paramDTO.setAutoComplete(abstractParameter.isAutoComplete());
    if (abstractParameter.getParentParam() != null) {
      paramDTO.setParentParam(abstractParameter.getParentParam());
    }
    paramDTO.setDimensionId(abstractParameter.getDimensionId());
    paramDTO.setUnit(abstractParameter.getUnit());

    if (abstractParameter.getExtraParams() != null) {
      paramDTO.setExtraParams(abstractParameter.getExtraParams());
    }

    // Set code property
    paramDTO.setCode(abstractParameter.getCode());

    // Set label property
    paramDTO.setLabel(abstractParameter.getLabel());

    // Set Values
    List<Value> values = null;
    // if (abstractParameter instanceof MultipleSelection) {
    // values = ((MultipleSelection) abstractParameter).getValues();
    // }
    // else if (abstractParameter instanceof SimpleParameter) {
    values = ((SimpleParameter) abstractParameter).getValues();
    // }

    if (values != null) {
      paramDTO.setValues(new ArrayList<ValueDTO>());
      for (Value value : values) {
        paramDTO.getValues().add(valueToDTO(value));
      }
    }

    // Set geoConfig, defaultExtent, maxExtent for GeographicalAreaSelection
    // Set properties for DateBetweenSelection
    // if (abstractParameter instanceof DateBetweenSelection) {
    // paramDTO.setPeriodMinExtent(((DateBetweenSelection) abstractParameter).getPeriodMinExtent());
    // paramDTO.setPeriodMaxExtent(((DateBetweenSelection) abstractParameter).getPeriodMaxExtent());
    // paramDTO.setFrom(((DateBetweenSelection) abstractParameter).getFrom());
    // paramDTO.setTo(((DateBetweenSelection) abstractParameter).getTo());
    // paramDTO.setExcludedDates(((DateBetweenSelection) abstractParameter).getExcludedDates());
    // }

    return paramDTO;
  }

  /**
   * Transform DTO to parameters
   * 
   * @param dtos
   *          the list of DTOs to transform
   * @return the list of parameters
   */
  public static List<AbstractParameter> dtoToParameters(List<ParameterDTO> dtos) {

    List<AbstractParameter> params = new ArrayList<AbstractParameter>();
    if ((dtos != null) && (dtos.size() > 0)) {
      for (ParameterDTO parameterDTO : dtos) {
        params.add(dtoToAbstractParameter(parameterDTO));
      }
    }
    return params;
  }

  /**
   * Convert from abstract parameter to Parameter DTO.
   * 
   * @param paramDTO
   *          a parameter
   * @return ParametersDTO
   */
  private static AbstractParameter dtoToAbstractParameter(ParameterDTO paramDTO) {

    AbstractParameter param = null;

    SimpleParameter singleS = new SimpleParameter();
    if (paramDTO.getType() != null) {
      singleS.setType(paramDTO.getType());
    }
    if (paramDTO.getValues() != null) {
      List<Value> av = new ArrayList<Value>();
      for (ValueDTO value : paramDTO.getValues()) {
        av.add(dtoTovalue(value));
      }
      singleS.setValues(av);
    }
    param = singleS;

    if (param != null) {
      // // Set code property
      param.setCode(paramDTO.getCode());

      // // Set label property
      param.setLabel(paramDTO.getLabel());
      param.setWidth(paramDTO.getWidth());
      param.setHeight(paramDTO.getHeight());
      param.setXpos(paramDTO.getXpos());
      param.setYpos(paramDTO.getYpos());
      param.setId(paramDTO.getId());
      param.setCss(paramDTO.getCss());
      param.setJsAdminObject(paramDTO.getJsAdminObject());
      param.setJsUserObject(paramDTO.getJsUserObject());
      param.setDefaultValues(paramDTO.getDefaultValues());
      param.setValueSelection(paramDTO.getValueSelection());
      param.setAutoComplete(paramDTO.isAutoComplete());
      if (paramDTO.getParentParam() != null) {
        param.setParentParam(paramDTO.getParentParam());
      }
      param.setDimensionId(paramDTO.getDimensionId());
      param.setUnit(paramDTO.getUnit());

      if (paramDTO.getExtraParams() != null) {
        param.setExtraParams(paramDTO.getExtraParams());
      }

    }
    return param;
  }

  /**
   * Convert Value to ValueDTO
   * 
   * @param value
   *          A value
   * @return A ValueDTO
   */
  private static ValueDTO valueToDTO(Value value) {

    ValueDTO valueDTO = new ValueDTO();

    // Set code property
    valueDTO.setCode(value.getCode());

    // Set id property
    valueDTO.setSelected(value.isSelected());

    // Set value property
    valueDTO.setValue(value.getValue());

    valueDTO.setDefaultValue(value.getDefaultValue());

    // Set availableFor property
    if (value.getAvailableFor() != null) {
      List<String> valuesDTO = new ArrayList<String>();
      for (Value v : value.getAvailableFor()) {
        valuesDTO.add(v.getCode());
      }
      valueDTO.setAvailableFor(valuesDTO);
    }

    return valueDTO;
  }

  /**
   * Convert Value to ValueDTO
   * 
   * @param valueDTO
   *          A valueDTO
   * @return Value
   */
  private static Value dtoTovalue(ValueDTO valueDTO) {

    Value value = new Value();

    // Set code property
    value.setCode(valueDTO.getCode());

    // Set id property
    value.setSelected(valueDTO.isSelected());

    // Set value property
    value.setValue(valueDTO.getValue());
    value.setDefaultValue(valueDTO.getDefaultValue());

    // Set availableFor property
    if (valueDTO.getAvailableFor() != null) {
      List<Value> values = new ArrayList<Value>();
      for (String v : valueDTO.getAvailableFor()) {
        Value val = new Value();
        val.setCode(v);
        values.add(val);
        // FIXME the Value Object ou une nouvelle Value avec le code est
        // suffisante ?
      }
      value.setAvailableFor(values);
    }

    return value;
  }

  /**
   * Get the start parameter of the DTO
   * 
   * @return the startParameterDTO
   */
  public String getStartParameterDTO() {
    return startParameterDTO;
  }

  /**
   * Set the start parameter of the DTO
   * 
   * @param startParameterDTO
   *          the startParameterDTO to set
   */
  public void setStartParameterDTO(final String startParameterDTO) {
    this.startParameterDTO = startParameterDTO;
  }

  /**
   * Get the list of DTO parameters
   * 
   * @return the parametersDTO
   */
  public List<ParameterDTO> getParametersDTO() {
    return parametersDTO;
  }

  /**
   * Set the list of DTO parameters
   * 
   * @param parametersDTO
   *          the parametersDTO to set
   */
  public void setParametersDTO(List<ParameterDTO> parametersDTO) {
    this.parametersDTO = parametersDTO;
  }

}
