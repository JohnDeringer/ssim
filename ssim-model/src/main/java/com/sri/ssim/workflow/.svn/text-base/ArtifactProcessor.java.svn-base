package com.sri.ssim.workflow;

import org.apache.camel.spring.SpringRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 9/6/12
 */
@Deprecated
public class ArtifactProcessor extends SpringRouteBuilder {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void configure() throws Exception {

        logger.info("ArtifactProcessor.configure");

        // Retrieve un-processed file names from the database
        from("jpa:com.sri.ssim.persistence.Artifact?consumer.namedQuery=Artifact.findUnprocessed&consumeDelete=false&consumer.useFixedDelay=true&delay=3000&consumeLockEntity=false")
        .to("bean:com.sri.ssim.file.ArtifactReader?method=processArtifacts");


    }

}
