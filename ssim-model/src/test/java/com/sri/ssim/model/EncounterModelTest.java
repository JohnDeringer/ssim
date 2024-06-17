package com.sri.ssim.model;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.apache.camel.spring.Main;


/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 8/29/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/repository-model-beans.xml")
public class EncounterModelTest {

    @Autowired
    EncounterModel encounterModel;

    @Test
    public void testEtlRoutes() throws Exception {
        // let's boot up the Spring application context for 2 seconds to check that it works OK
        Main.main("-duration", "2s", "-o", "target/site/cameldoc");
    }





}
