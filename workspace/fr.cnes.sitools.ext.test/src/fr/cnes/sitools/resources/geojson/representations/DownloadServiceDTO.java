package fr.cnes.sitools.resources.geojson.representations;

public class DownloadServiceDTO {


  private String mimetype;

  private String url;

  public DownloadServiceDTO() {
  }

  public DownloadServiceDTO(String mimetype, String url) {
    this.mimetype = mimetype;
    this.url = url;
  }

  public String getMimetype() {
    return mimetype;
  }

  public void setMimetype(String mimetype) {
    this.mimetype = mimetype;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
