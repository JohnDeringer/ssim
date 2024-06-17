package com.sri.ssim.file;

import com.sri.ssim.schema.ANNOTATIONDOCUMENT;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import java.io.File;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 9/10/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/repository-model-beans.xml")
public class FileProcessorTest {

    @Autowired
    ElanDocumentParser elanFileProcessor;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before
    public void init() {

    }

    @Test
    public void processElanFile() {

        try {
            File file =
                    new File("/Users/johnderinger/devel/ssim.warehouse/warehouse-model/doc/TR2_Empty_Gas_Tank_UNH.eaf");

            JAXBContext jaxbContext
                            = JAXBContext.newInstance("com.sri.ssim.schema");
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            ANNOTATIONDOCUMENT annotationDocument
                    = (ANNOTATIONDOCUMENT) unmarshaller.unmarshal(file);

            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(
                        new File("/Users/johnderinger/devel/ssim.warehouse/warehouse-model/src/main/resources/EAFv2.7.xsd"));
            unmarshaller.setSchema(schema);

            elanFileProcessor.parseDocument(annotationDocument);

            Assert.isTrue(!elanFileProcessor.getAnnotatedLines().isEmpty());
            Assert.notNull(elanFileProcessor.getAnnotatedLines().get(0));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
