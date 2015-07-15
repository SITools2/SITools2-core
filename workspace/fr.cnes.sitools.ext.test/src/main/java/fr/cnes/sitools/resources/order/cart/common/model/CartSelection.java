package fr.cnes.sitools.resources.order.cart.common.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.dataset.model.Column;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CartSelection implements IResource, Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 2829660753185960207L;

  private String selectionId;
  private String selectionName;
  private String datasetId;
  private String dataUrl;
  private String datasetName;
  private String nbRecords;
  private String orderDate;
  private String selections;
  private String ranges;
  private String[] dataToExport;
  private int startIndex;

  private List<Column> colModel;

  private List<Map<String, String>> records;

  public CartSelection() {
  }

  public String getSelectionName() {
    return selectionName;
  }

  public void setSelectionName(String selectionName) {
    this.selectionName = selectionName;
  }

  public String getDatasetId() {
    return datasetId;
  }

  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  public String getDataUrl() {
    return dataUrl;
  }

  public void setDataUrl(String dataUrl) {
    this.dataUrl = dataUrl;
  }

  public String getDatasetName() {
    return datasetName;
  }

  public void setDatasetName(String datasetName) {
    this.datasetName = datasetName;
  }

  public String getNbRecords() {
    return nbRecords;
  }

  public void setNbRecords(String nbRecords) {
    this.nbRecords = nbRecords;
  }

  public String getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(String orderDate) {
    this.orderDate = orderDate;
  }

  public String getSelectionId() {
    return selectionId;
  }

  public void setSelectionId(String selectionId) {
    this.selectionId = selectionId;
  }

  @Override
  public String getId() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setId(String id) {
    // TODO Auto-generated method stub

  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

  public List<Map<String, String>> getRecords() {
    return records;
  }

  public void setRecords(List<Map<String, String>> records) {
    this.records = records;
  }

  public String getSelections() {
    return selections;
  }

  public void setSelections(String selections) {
    this.selections = selections;
  }

  public List<Column> getColModel() {
    return colModel;
  }

  public void setColModel(List<Column> colModel) {
    this.colModel = colModel;
  }

  public String getRanges() {
    return ranges;
  }

  public void setRanges(String ranges) {
    this.ranges = ranges;
  }

  public String[] getDataToExport() {
    return dataToExport;
  }

  public void setDataToExport(String[] dataToExport) {
    this.dataToExport = dataToExport;
  }

  public int getStartIndex() {
    return startIndex;
  }

  public void setStartIndex(int startIndex) {
    this.startIndex = startIndex;
  }

}
