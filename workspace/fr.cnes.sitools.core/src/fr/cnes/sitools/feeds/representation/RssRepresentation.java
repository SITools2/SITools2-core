package fr.cnes.sitools.feeds.representation;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.feeds.model.FeedEntryModel;
import fr.cnes.sitools.feeds.model.FeedModel;
import fr.cnes.sitools.util.DateUtils;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class RssRepresentation extends OutputRepresentation {


    private final FeedModel feedModel;
    private final String selfUrl;
    private final String publicHostDomain;

    public RssRepresentation(FeedModel feedModel, String selfUrl, String publicHostDomain) {
        super(MediaType.APPLICATION_RSS);
        this.feedModel = feedModel;
        this.selfUrl = selfUrl;
        this.publicHostDomain = publicHostDomain;
    }

    public void write(OutputStream outputStream) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        try {
            //Write header
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n");
            writer.write("<channel>\n");
            //write channel details
            writer.write("<title>" + feedModel.getTitle() + "</title>\n");
            writer.write("<description>" + feedModel.getDescription() + "</description>\n");
            writer.write("<link>" + feedModel.getLink() + "</link>\n");
            writer.write("<atom:link href=\"" + selfUrl + "\" rel=\"self\" type=\"application/rss+xml\" />\n");
            writer.flush();

            for (FeedEntryModel entry : feedModel.getEntries()) {
                writer.write("<item>\n");
                writer.write("<title>" + entry.getTitle() + "</title>\n");
                writer.write("<description>" + entry.getDescription() + "</description>\n");
                writer.write("<pubDate>" + DateUtils.format(entry.getPublishedDate(), DateUtils.FORMAT_RFC_822_FOUR_DIGIT_YEAR) + "</pubDate>\n");
                writer.write("<link>" + entry.getLink() + "</link>\n");
                writer.write("<guid>" + entry.getLink() + "</guid>\n");
                if (entry.getAuthor() != null) {
                    writer.write("<author>" + entry.getAuthor().getEmail() + " (" + entry.getAuthor().getName() + ")</author>\n");
                }

                if (entry.getImage() != null && entry.getImage().getUrl() != null) {
                    writer.write("<enclosure url=\"" + getEnclosureUrl(entry) + "\" type=\"" + entry.getImage().getType() + "\"/>\n");
                }

                writer.write("</item>\n");
                writer.flush();
            }

            writer.write("</channel>\n");
            writer.write("</rss>\n");
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
}
