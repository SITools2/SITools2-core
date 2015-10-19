package fr.cnes.sitools.feeds.representation;

import fr.cnes.sitools.feeds.model.FeedEntryModel;
import fr.cnes.sitools.feeds.model.FeedModel;
import org.restlet.data.MediaType;
import org.restlet.engine.util.DateUtils;
import org.restlet.representation.OutputRepresentation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

public class AtomRepresentation extends OutputRepresentation {


    private final FeedModel feedModel;
    private final String selfUrl;
    private final String publicHostDomain;

    public AtomRepresentation(FeedModel feedModel, String selfUrl, String publicHostDomain) {
        super(MediaType.APPLICATION_RSS);
        this.feedModel = feedModel;
        this.selfUrl = selfUrl;
        this.publicHostDomain = publicHostDomain;
    }

    public void write(OutputStream outputStream) throws IOException {
        Date publishedDate = new Date();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        try {
            //Write header
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<feed xmlns=\"http://www.w3.org/2005/Atom\">\n");
            //write channel details
            writer.write("<title>" + feedModel.getTitle() + "</title>\n");
            writer.write("<subtitle>" + feedModel.getDescription() + "</subtitle>\n");
            writer.write("<link rel=\"alternate\" href=\"" + feedModel.getLink() + "\"/>\n");
            writer.write("<link rel=\"self\" href=\"" + selfUrl + "\"/>\n");
            writer.write("<id>" + feedModel.getLink() + "</id>\n");
            //TODO
            writer.write("<updated>" + DateUtils.format(publishedDate, DateUtils.FORMAT_RFC_3339.get(0)) + "</updated>\n");

            if (feedModel.getAuthor() != null) {
                writer.write("<author>\n");
                writer.write("<name>" + feedModel.getAuthor().getName() + "</name>\n");
                writer.write("<email>" + feedModel.getAuthor().getEmail() + "</email>\n");
                writer.write("</author>\n");
            }
            writer.flush();

            for (FeedEntryModel entry : feedModel.getEntries()) {
                writer.write("<entry>\n");
                writer.write("<title>" + entry.getTitle() + "</title>\n");
                writer.write("<summary>" + entry.getDescription() + "</summary>\n");
                writer.write("<updated>" + DateUtils.format(entry.getPublishedDate(), DateUtils.FORMAT_RFC_3339.get(0)) + "</updated>\n");
                writer.write("<link href=\"" + entry.getLink() + "\"/>\n");
                writer.write("<id>" + getFeedEntryId(entry) + "</id>\n");
                if (entry.getAuthor() != null) {
                    writer.write("<author>\n");
                    writer.write("<name>" + entry.getAuthor().getName() + "</name>\n");
                    writer.write("<email>" + entry.getAuthor().getEmail() + "</email>\n");
                    writer.write("</author>\n");
                }

                if (entry.getImage() != null && entry.getImage().getUrl() != null) {
                    writer.write("<link rel=\"enclosure\" type=\"" + entry.getImage().getType() + "\" href=\"" + getEnclosureUrl(entry) + "\" />\n");
                }

                writer.write("</entry>\n");
                writer.flush();

                if (publishedDate == null || publishedDate.getTime() < entry.getPublishedDate().getTime()) {
                    publishedDate = entry.getPublishedDate();
                }
            }


            writer.write("</feed>\n");
        } finally {
            writer.flush();
            writer.close();
            outputStream.close();
        }


    }

    private String getEnclosureUrl(FeedEntryModel feedEntryModel) {
        String url = feedEntryModel.getImage().getUrl();
        if (url.startsWith("/")) {
            return publicHostDomain + url;
        }
        return url;
    }

    private String getFeedEntryId(FeedEntryModel entry) {
        if (entry.getLink().endsWith("/")) {
            return entry.getLink();
        } else {
            return entry.getLink() + "/";
        }
    }
}
