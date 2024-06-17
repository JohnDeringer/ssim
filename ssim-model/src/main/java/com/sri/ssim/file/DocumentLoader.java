package com.sri.ssim.file;

import com.sri.ssim.persistence.ArtifactFile;
import com.sri.ssim.schema.ANNOTATIONDOCUMENT;

import org.apache.commons.lang.StringUtils;

import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 9/11/12
 *
 * Loads and unmarshals a file into an XML document
 */
public class DocumentLoader {

    private static String uploadDirectory = null;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public String getUploadDirectory() {
        return uploadDirectory;
    }
    public void setUploadDirectory(String directory) {
        logger.info("Setting uploadDirectory [" + uploadDirectory + "]");
        uploadDirectory = directory;
    }

    public File getEncounterDocuments(Set<String> fileNames, String tmpFileName) {
        FileOutputStream fout;
        ZipOutputStream zout = null;
        File file = null;
        if (!fileNames.isEmpty()) {
            file = new File(tmpFileName);
            try {
                fout = new FileOutputStream(file);
                zout = new ZipOutputStream(fout);
                byte[] buffer = new byte[1024];
                for (String filename : fileNames) {
                    ZipEntry ze = new ZipEntry(filename);

                    FileInputStream fin =
                            new FileInputStream(uploadDirectory + "/" + filename);
                    zout.putNextEntry(ze);

                    int length;

                    while((length = fin.read(buffer)) > 0) {
                        zout.write(buffer, 0, length);
                    }

                    zout.closeEntry();

                    fin.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (zout != null) {
                    try {
                        zout.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return file;
    }

    public DocumentWrapper loadDocument(@NotNull ArtifactFile artifactFile) {
        DocumentWrapper documentWrapper = new DocumentWrapper();
        String filename = artifactFile.getFileName();

        // TODO: Handle NULL documentWrapper case

        logger.info("Attempting to read file [" + filename +
                "] from directory [" + uploadDirectory + "]");

        if (StringUtils.isNotEmpty(filename)) {
            File file = new File(uploadDirectory + "/" + filename);
            if (filename.toLowerCase().endsWith(".eaf")) {
                documentWrapper =
                       new DocumentWrapper(
                               processElanFile(file),
                               filename,
                               ANNOTATIONDOCUMENT.class);
            } else {
                documentWrapper.setFilename(filename);
            }
        }

        return documentWrapper;
    }

    private ANNOTATIONDOCUMENT processElanFile(@NotNull File file) {

        ANNOTATIONDOCUMENT annotationDocument = null;

        try {
            JAXBContext jaxbContext
                        = JAXBContext.newInstance("com.sri.ssim.schema");
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            annotationDocument
                    = (ANNOTATIONDOCUMENT) unmarshaller.unmarshal(file);

            JAXBSource source = new JAXBSource(jaxbContext, annotationDocument);
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            URL schemaURL = this.getClass().getResource("/EAFv2.7.xsd");

            Schema schema = sf.newSchema(schemaURL);


            Validator validator = schema.newValidator();
            validator.setErrorHandler(new ElanErrorHandler());
            validator.validate(source);

            logger.info("ArtifactFile [" + file.getName() + "] is valid");

        } catch (Exception e) {
            logger.error("Error processing file", e);
        }

        return annotationDocument;
    }
}
