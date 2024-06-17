package com.sri.ssim.client;

import com.sri.ssim.persistence.ArtifactFile;
import com.sri.ssim.persistence.Line;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;

import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/11/12
 */
public class SolrFacade {

    private static SolrServer solrServer;
    private String solrUrlString = "http://localhost:8983/solr";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Long addDocument(@NotNull ArtifactFile artifactFile) {
        Long artifactFileId = artifactFile.getId();

        logger.info("Calling Solr with artifactFileId [" + artifactFileId +
                "] with [" + artifactFile.getLines().size() + "] lines");

        try {
            Collection<SolrInputDocument> documents = new ArrayList<SolrInputDocument>();

            for (Line line : artifactFile.getLines()) {
                logger.info("lineId [" + line.getId() + "]");
                String phrase = line.getUtterance();
                if (StringUtils.isNotEmpty(phrase)) {
                    SolrInputDocument solrDocument = new SolrInputDocument();
                    solrDocument.addField("artifactId", artifactFile.getArtifact().getId());
                    solrDocument.addField("fileId", artifactFile.getId());
                    solrDocument.addField("lineId", line.getId());
                    solrDocument.addField("utterance", line.getUtterance());
                    String annotation = line.getAnnotation();
                    if (StringUtils.isNotEmpty(annotation)) {
                        solrDocument.addField("annotation", annotation);
                    }
                    documents.add(solrDocument);
                }
            }

            if (!documents.isEmpty()) {
                getSolrServer().add(documents);

                logger.info("Sending document to Solr [" + documents.toString() + "]");

                getSolrServer().commit();
            } else {
                logger.warn("Unable to send empty document to Solr for fileId [" + artifactFileId + "]");
            }

        } catch (Exception e) {
            logger.error("Unexpected error sending data to solr [" + e + "]");
        }

        return artifactFileId;
    }

    public Long addDocumentArtifact(@NotNull ArtifactFile artifactFile) {
        Long artifactFileId = artifactFile.getId();

        logger.info("Calling Solr with artifactFileId [" + artifactFileId +
                "] with [" + artifactFile.getLines().size() + "] lines");

        try {
            Collection<SolrInputDocument> documents = new ArrayList<SolrInputDocument>();

            StringBuilder phrases = new StringBuilder();
            StringBuilder annotations = new StringBuilder();

            for (Line line : artifactFile.getLines()) {
                String phrase = line.getUtterance();
                String annotation = line.getAnnotation();

                if (phrase != null) {
                    phrases.append(addPadding(phrase));
                }

                if (annotation != null) {
                    annotations.append(addPadding(annotation));
                }
            }

            SolrInputDocument solrDocument = new SolrInputDocument();

            solrDocument.addField("artifactId", artifactFile.getArtifact().getId());
            solrDocument.addField("fileId", artifactFile.getId());
            solrDocument.addField("utterance", phrases.toString());

            if (annotations.length() > 0) {
                solrDocument.addField("annotation", annotations.toString());
            }
            documents.add(solrDocument);

            if (!documents.isEmpty()) {
                getSolrServer().add(documents);

                logger.info("Sending document to Solr [" + documents.toString() + "]");

                getSolrServer().commit();
            } else {
                logger.warn("Unable to send empty document to Solr for fileId [" + artifactFileId + "]");
            }

        } catch (Exception e) {
            logger.error("Unexpected error sending data to solr [" + e + "]");
        }

        return artifactFileId;
    }

    public QueryResponse query(SolrParams solrParams) throws SolrServerException {
        return getSolrServer().query(solrParams);
    }

    /**
     * Dependency Injection
     *
     * @param solrUrlString The URL for the Solr REST interface
     */
    public void setSolrUrlString(String solrUrlString) {
        this.solrUrlString = solrUrlString;
    }

    // Ensure line ends with a white-space
    private String addPadding(String text) {
        String rtnText = text;
        if (!text.endsWith(" ")) {
            rtnText = text + " ";
        }
        return rtnText;
    }

    private SolrServer getSolrServer() {
        if (solrServer == null) {
            solrServer = new ConcurrentUpdateSolrServer(solrUrlString, 10, 5);
        }
        return solrServer;
    }

}
