package fr.cnes.sitools.resources.atom.representation;

public class AtomEntryDTO {


    private String id;

    private String title;

    private String description;

    private String published;

    private String updated;

    private String geometry;

    private String download;
    private String downloadMimeType;

    private String quicklook;
    private String thumbnail;


    public AtomEntryDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getDownloadMimeType() {
        return downloadMimeType;
    }

    public void setDownloadMimeType(String downloadMimeType) {
        this.downloadMimeType = downloadMimeType;
    }

    public String getQuicklook() {
        return quicklook;
    }

    public void setQuicklook(String quicklook) {
        this.quicklook = quicklook;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
