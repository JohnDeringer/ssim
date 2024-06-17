package com.sri.ssim.persistence;

import com.sri.ssim.schema.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 8/29/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/repository-model-beans.xml")
public class EncounterDaoTest {

    @Autowired
    EncounterDao encounterDao;

    @Before
    public void init() {

    }

    @Test
    public void createFindCollector() {
        String value = getRandomValue("Collector");

        Collector createEntity = new Collector();
        createEntity.setValue(value);

        createEntity = encounterDao.createCollector(createEntity);

        Assert.notNull(createEntity, "Entity creation failed");
        Assert.notNull(createEntity.getId(), "Entity creation failed");
        Assert.isTrue(createEntity.getValue().equals(value),
                "Expected value [" + value +
                        "] but received value [" + createEntity.getValue() + "]");

        Collector findEntity = encounterDao.findCollectorByValue(value);

        Assert.notNull(findEntity, "Unable to find entity by value [" + value + "]");
        Assert.notNull(findEntity.getId(), "Entity is missing primary key [" + findEntity.getId() + "]");
        Assert.isTrue(findEntity.getValue().equals(value),
                "Expected value [" + value + "] but received value [" + findEntity.getValue() + "]");
    }

    @Test
    public void createFindCollectors() {
        String root1 = "C";
        String root2 = "ollector";

        // Create collector-1
        String value = getRandomValue(root1 + root2);
        Collector createEntity = new Collector();
        createEntity.setValue(value);
        createEntity = encounterDao.createCollector(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Create collector-2
        value = getRandomValue(root1 + root2);
        createEntity = new Collector();
        createEntity.setValue(value);
        createEntity = encounterDao.createCollector(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Find Collectors by root-1
        List<Collector> entities = encounterDao.findCollectors(root1);
        Assert.notNull(entities, "Unable to find entity by value [" + root1 + "]");
        Assert.isTrue(entities.size() == 2,
                "Expected value [2] but received value [" + entities.size() + "]");

        // Find Collectors by root-1 + root-2
        entities = encounterDao.findCollectors(root1 + root2);
        Assert.notNull(entities, "Unable to find entity by value [" + root1 + root2 + "]");
        Assert.isTrue(entities.size() == 2,
                "Expected value [2] but received value [" + entities.size() + "]");
    }

    @Test
    public void createFindCorpus() {
        String value = getRandomValue("Corpus");

        Corpus createEntity = new Corpus();
        createEntity.setValue(value);

        createEntity = encounterDao.createCorpus(createEntity);

        Assert.notNull(createEntity, "Entity creation failed");
        Assert.notNull(createEntity.getId(), "Entity creation failed");
        Assert.isTrue(createEntity.getValue().equals(value),
                "Expected value [" + value +
                        "] but received value [" + createEntity.getValue() + "]");

        Corpus findEntity = encounterDao.findCorpusByValue(value);

        Assert.notNull(findEntity, "Unable to find entity by value [" + value + "]");
        Assert.notNull(findEntity.getId(), "Entity is missing primary key [" + findEntity.getId() + "]");
        Assert.isTrue(findEntity.getValue().equals(value),
                "Expected value [" + value + "] but received value [" + findEntity.getValue() + "]");
    }

    @Test
    public void createFindEncounterQuality() {
        String value = getRandomValue("EncounterQuality");

        EncounterQuality createEntity = new EncounterQuality();
        createEntity.setValue(value);

        createEntity = encounterDao.createEncounterQuality(createEntity);

        Assert.notNull(createEntity, "Entity creation failed");
        Assert.notNull(createEntity.getId(), "Entity creation failed");
        Assert.isTrue(createEntity.getValue().equals(value),
                "Expected value [" + value +
                        "] but received value [" + createEntity.getValue() + "]");

        EncounterQuality findEntity = encounterDao.findEncounterQualityByValue(value);

        Assert.notNull(findEntity, "Unable to find entity by value [" + value + "]");
        Assert.notNull(findEntity.getId(), "Entity is missing primary key [" + findEntity.getId() + "]");
        Assert.isTrue(findEntity.getValue().equals(value),
                "Expected value [" + value + "] but received value [" + findEntity.getValue() + "]");
    }

    @Test
    public void createFindEncounterQualities() {
        String root1 = "Encounter";
        String root2 = "Quality";

        // Create EncounterQuality-1
        String value = getRandomValue(root1 + root2);
        EncounterQuality createEntity = new EncounterQuality();
        createEntity.setValue(value);
        createEntity = encounterDao.createEncounterQuality(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Create EncounterQuality-2
        value = getRandomValue(root1 + root2);
        createEntity = new EncounterQuality();
        createEntity.setValue(value);
        createEntity = encounterDao.createEncounterQuality(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Find EncounterQuality with root-1
        List<EncounterQuality> entities =
                encounterDao.findEncounterQualities(root1);
        Assert.notNull(entities, "Unable to find entity by value [" + root1 + "]");
        Assert.isTrue(entities.size() == 2,
                "Expected value [2] but received value [" + entities.size() + "]");

        // Find EncounterQuality with root-1 + root-2
        entities =
                encounterDao.findEncounterQualities(root1 + root2);
        Assert.notNull(entities, "Unable to find entity by value [" + root1 + root2 + "]");
        Assert.isTrue(entities.size() == 2,
                "Expected value [2] but received value [" + entities.size() + "]");
    }

    @Test
    public void createFindEncounterReason() {
        String value = getRandomValue("EncounterReason");

        EncounterReason createEntity = new EncounterReason();
        createEntity.setValue(value);

        createEntity = encounterDao.createEncounterReason(createEntity);

        Assert.notNull(createEntity, "Entity creation failed");
        Assert.notNull(createEntity.getId(), "Entity creation failed");
        Assert.isTrue(createEntity.getValue().equals(value),
                "Expected value [" + value +
                        "] but received value [" + createEntity.getValue() + "]");

        EncounterReason findEntity = encounterDao.findEncounterReasonByValue(value);

        Assert.notNull(findEntity, "Unable to find entity by value [" + value + "]");
        Assert.notNull(findEntity.getId(), "Entity is missing primary key [" + findEntity.getId() + "]");
        Assert.isTrue(findEntity.getValue().equals(value),
                "Expected value [" + value + "] but received value [" + findEntity.getValue() + "]");
    }

    @Test
    public void createFindEncounterReasons() {
        String root1 = "Encounter";
        String root2 = "Reason";

        // Create EncounterReason-1
        String value = getRandomValue(root1 + root2);
        EncounterReason createEntity = new EncounterReason();
        createEntity.setValue(value);
        createEntity = encounterDao.createEncounterReason(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Create EncounterReason-2
        value = getRandomValue(root1 + root2);
        createEntity = new EncounterReason();
        createEntity.setValue(value);
        createEntity = encounterDao.createEncounterReason(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Find EncounterReason with root-1
        List<EncounterReason> entities =
                encounterDao.findEncounterReasons(root1);
        Assert.notNull(entities, "Unable to find entity by value [" + root1 + "]");
        Assert.isTrue(entities.size() == 2,
                "Expected value [2] but received value [" + entities.size() + "]");

        // Find EncounterReason with root-1 + root-2
        entities =
                encounterDao.findEncounterReasons(root1 + root2);
        Assert.notNull(entities, "Unable to find entity by value [" + root1 + root2 + "]");
        Assert.isTrue(entities.size() == 2,
                "Expected value [2] but received value [" + entities.size() + "]");
    }

    @Test
    public void createFindEthnicity() {
        String value = getRandomValue("Ethnicity");

        Ethnicity createEntity = new Ethnicity();
        createEntity.setValue(value);

        createEntity = encounterDao.createEthnicity(createEntity);

        Assert.notNull(createEntity, "Entity creation failed");
        Assert.notNull(createEntity.getId(), "Entity creation failed");
        Assert.isTrue(createEntity.getValue().equals(value),
                "Expected value [" + value +
                        "] but received value [" + createEntity.getValue() + "]");

        Ethnicity findEntity = encounterDao.findEthnicityByValue(value);

        Assert.notNull(findEntity, "Unable to find entity by value [" + value + "]");
        Assert.notNull(findEntity.getId(), "Entity is missing primary key [" + findEntity.getId() + "]");
        Assert.isTrue(findEntity.getValue().equals(value),
                "Expected value [" + value + "] but received value [" + findEntity.getValue() + "]");
    }

    @Test
    public void createFindEthnicities() {
        String[] ethnicities =
                {"f", "fr", "fre", "fren", "frenc",
                        "French", "rench"};

        // Create Ethnicity-0 "f"
        String value = ethnicities[0];
        Ethnicity createEntity = new Ethnicity();
        createEntity.setValue(value);
        createEntity = encounterDao.createEthnicity(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Create Ethnicity-1 "fr"
        value = ethnicities[1];
        createEntity = new Ethnicity();
        createEntity.setValue(value);
        createEntity = encounterDao.createEthnicity(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Create Ethnicity-2 "fre"
        value = ethnicities[2];
        createEntity = new Ethnicity();
        createEntity.setValue(value);
        createEntity = encounterDao.createEthnicity(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Create Ethnicity-3 "fren"
        value = ethnicities[3];
        createEntity = new Ethnicity();
        createEntity.setValue(value);
        createEntity = encounterDao.createEthnicity(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Find Ethnicity - 0 "f"
        List<Ethnicity> entities = encounterDao.findEthnicities(ethnicities[0]);
        Assert.notNull(entities, "Unable to find entity by value [" + ethnicities[0] + "]");
        Assert.isTrue(entities.size() == 4,
                "Expected value [4] but received value [" + entities.size() + "]");


        // Create Ethnicity-4 "frenc"
        value = ethnicities[4];
        createEntity = new Ethnicity();
        createEntity.setValue(value);
        createEntity = encounterDao.createEthnicity(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Create Ethnicity-5 "French"
        value = ethnicities[5];
        createEntity = new Ethnicity();
        createEntity.setValue(value);
        createEntity = encounterDao.createEthnicity(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Create Ethnicity-6 "rench"
        value = ethnicities[6];
        createEntity = new Ethnicity();
        createEntity.setValue(value);
        createEntity = encounterDao.createEthnicity(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Find Ethnicity - 2 "fre"
        entities = encounterDao.findEthnicities(ethnicities[2]);
        Assert.notNull(entities, "Unable to find entity by value [" + ethnicities[2] + "]");
        Assert.isTrue(entities.size() == 4,
                "Expected value [4] but received value [" + entities.size() + "]");
    }

    @Test
    public void createFindFileType() {
        String value = getRandomValue("FileType");

        FileType createEntity = new FileType();
        createEntity.setValue(value);

        createEntity = encounterDao.createFileType(createEntity);

        Assert.notNull(createEntity, "Entity creation failed");
        Assert.notNull(createEntity.getId(), "Entity creation failed");
        Assert.isTrue(createEntity.getValue().equals(value),
                "Expected value [" + value +
                        "] but received value [" + createEntity.getValue() + "]");

        FileType findEntity = encounterDao.findFileTypeByValue(value);

        Assert.notNull(findEntity, "Unable to find entity by value [" + value + "]");
        Assert.notNull(findEntity.getId(), "Entity is missing primary key [" + findEntity.getId() + "]");
        Assert.isTrue(findEntity.getValue().equals(value),
                "Expected value [" + value + "] but received value [" + findEntity.getValue() + "]");
    }

    @Test
    public void createFindGeneralTime() {
        String value = getRandomValue("GeneralTime");

        GeneralTime createEntity = new GeneralTime();
        createEntity.setValue(value);

        createEntity = encounterDao.createGeneralTime(createEntity);

        Assert.notNull(createEntity, "Entity creation failed");
        Assert.notNull(createEntity.getId(), "Entity creation failed");
        Assert.isTrue(createEntity.getValue().equals(value),
                "Expected value [" + value +
                        "] but received value [" + createEntity.getValue() + "]");

        GeneralTime findEntity = encounterDao.findGeneralTimeByValue(value);

        Assert.notNull(findEntity, "Unable to find entity by value [" + value + "]");
        Assert.notNull(findEntity.getId(), "Entity is missing primary key [" + findEntity.getId() + "]");
        Assert.isTrue(findEntity.getValue().equals(value),
                "Expected value [" + value + "] but received value [" + findEntity.getValue() + "]");
    }

    @Test
    public void createFindGeneralTimes() {
        String root1 = "General";
        String root2 = "Time";

        // Create GeneralTime-1
        String value = getRandomValue(root1 + root2);
        GeneralTime createEntity = new GeneralTime();
        createEntity.setValue(value);
        createEntity = encounterDao.createGeneralTime(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Create GeneralTime-2
        value = getRandomValue(root1 + root2);
        createEntity = new GeneralTime();
        createEntity.setValue(value);
        createEntity = encounterDao.createGeneralTime(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Find GeneralTime by root-1
        List<GeneralTime> entities = encounterDao.findGeneralTimes(root1);
        Assert.notNull(entities, "Unable to find entity by value [" + root1 + "]");
        Assert.isTrue(entities.size() == 2,
                "Expected value [2] but received value [" + entities.size() + "]");

        // Find GeneralTime by root-1 + root-2
        entities = encounterDao.findGeneralTimes(root1 + root2);
        Assert.notNull(entities, "Unable to find entity by value [" + root1 + root2 + "]");
        Assert.isTrue(entities.size() == 2,
                "Expected value [2] but received value [" + entities.size() + "]");
    }

    @Test
    public void createFindGeographicOrigin() {
        String value = getRandomValue("GeographicLocation");

        GeographicLocation createEntity = new GeographicLocation();
        createEntity.setValue(value);

        createEntity = encounterDao.createGeographicLocation(createEntity);

        Assert.notNull(createEntity, "Entity creation failed");
        Assert.notNull(createEntity.getId(), "Entity creation failed");
        Assert.isTrue(createEntity.getValue().equals(value),
                "Expected value [" + value +
                        "] but received value [" + createEntity.getValue() + "]");

        GeographicLocation findEntity = encounterDao.findGeographicLocationByValue(value);

        Assert.notNull(findEntity, "Unable to find entity by value [" + value + "]");
        Assert.notNull(findEntity.getId(), "Entity is missing primary key [" + findEntity.getId() + "]");
        Assert.isTrue(findEntity.getValue().equals(value),
                "Expected value [" + value + "] but received value [" + findEntity.getValue() + "]");
    }


    @Test
    public void createFindGeographicOrigins() {

        String root1 = "Geographic";
        String root2 = "Origin";

        // Create GeographicLocation 1
        String value = getRandomValue(root1 + root2);
        GeographicLocation createEntity = new GeographicLocation();
        createEntity.setValue(value);
        createEntity = encounterDao.createGeographicLocation(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Create GeographicLocation 2
        value = getRandomValue(root1 + root2);
        createEntity = new GeographicLocation();
        createEntity.setValue(value);
        createEntity = encounterDao.createGeographicLocation(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Search using root1
        List<GeographicLocation> root1Entities =
                encounterDao.findGeographicLocations(root1);
        Assert.notNull(root1Entities, "Unable to find entity by value [" + root1 + "]");
        Assert.isTrue(root1Entities.size() == 2,
                "Expected '2' but received [" + root1Entities.size() + "]");

        // Search using root1 + root2
        List<GeographicLocation> root12Entities =
                encounterDao.findGeographicLocations(root1 + root2);
        Assert.notNull(root12Entities, "Unable to find entity by value [" + root1 + root2 + "]");
        Assert.isTrue(root12Entities.size() == 2,
                "Expected '2' but received [" + root12Entities.size() + "]");

    }

    @Test
    public void createFindLanguage() {
        String value = getRandomValue("Language");

        Language createEntity = new Language();
        createEntity.setValue(value);

        createEntity = encounterDao.createLanguage(createEntity);

        Assert.notNull(createEntity, "Entity creation failed");
        Assert.notNull(createEntity.getId(), "Entity creation failed");
        Assert.isTrue(createEntity.getValue().equals(value),
                "Expected value [" + value +
                        "] but received value [" + createEntity.getValue() + "]");

        Language findEntity = encounterDao.findLanguageByValue(value);

        Assert.notNull(findEntity, "Unable to find entity by value [" + value + "]");
        Assert.notNull(findEntity.getId(), "Entity is missing primary key [" + findEntity.getId() + "]");
        Assert.isTrue(findEntity.getValue().equals(value),
                "Expected value [" + value + "] but received value [" + findEntity.getValue() + "]");
    }

    @Test
    public void createFindLanguages() {
        String root1 = "Lang";
        String root2 = "uage";

        // Create Language-1
        String value = getRandomValue(root1 + root2);
        Language createEntity = new Language();
        createEntity.setValue(value);
        createEntity = encounterDao.createLanguage(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Create Language-2
        value = getRandomValue(root1 + root2);
        createEntity = new Language();
        createEntity.setValue(value);
        createEntity = encounterDao.createLanguage(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Find Languages - root-1
        List<Language> entities = encounterDao.findLanguages(root1);
        Assert.notNull(entities, "Unable to find entity by value [" + root1 + "]");
        Assert.isTrue(entities.size() == 2,
                "Expected value [2] but received value [" + entities.size() + "]");

        // Find Languages - root1 + root2
        entities = encounterDao.findLanguages(root1 + root2);
        Assert.notNull(entities, "Unable to find entity by value [" + root1 + root2 + "]");
        Assert.isTrue(entities.size() == 2,
                "Expected value [2] but received value [" + entities.size() + "]");
    }

    @Test
    public void createFindRank() {
        String value = getRandomValue("Rank");

        Rank createEntity = new Rank();
        createEntity.setValue(value);

        createEntity = encounterDao.createRank(createEntity);

        Assert.notNull(createEntity, "Entity creation failed");
        Assert.notNull(createEntity.getId(), "Entity creation failed");
        Assert.isTrue(createEntity.getValue().equals(value),
                "Expected value [" + value +
                        "] but received value [" + createEntity.getValue() + "]");

        Rank findEntity = encounterDao.findRankByValue(value);

        Assert.notNull(findEntity, "Unable to find entity by value [" + value + "]");
        Assert.notNull(findEntity.getId(), "Entity is missing primary key [" + findEntity.getId() + "]");
        Assert.isTrue(findEntity.getValue().equals(value),
                "Expected value [" + value + "] but received value [" + findEntity.getValue() + "]");
    }

    @Test
    public void createFindRanks() {
        String root1 = "R";
        String root2 = "ank";

        // Create Rank-1
        String value = getRandomValue(root1 + root2);
        Rank createEntity = new Rank();
        createEntity.setValue(value);
        createEntity = encounterDao.createRank(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Create Rank-2
        value = getRandomValue(root1 + root2);
        createEntity = new Rank();
        createEntity.setValue(value);
        createEntity = encounterDao.createRank(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Find Rank with root1
        List<Rank> entities = encounterDao.findRanks(root1);
        Assert.notNull(entities, "Unable to find entity by value [" + root1 + "]");
        Assert.isTrue(entities.size() == 2,
                "Expected value [2] but received value [" + entities.size() + "]");

        // Find Rank with root1 + root2
        entities = encounterDao.findRanks(root1 + root2);
        Assert.notNull(entities, "Unable to find entity by value [" + root1 + root2 + "]");
        Assert.isTrue(entities.size() == 2,
                "Expected value [2] but received value [" + entities.size() + "]");
    }

    @Test
    public void createFindRole() {
        String value = getRandomValue("Role");

        Role createEntity = new Role();
        createEntity.setValue(value);

        createEntity = encounterDao.createRole(createEntity);

        Assert.notNull(createEntity, "Entity creation failed");
        Assert.notNull(createEntity.getId(), "Entity creation failed");
        Assert.isTrue(createEntity.getValue().equals(value),
                "Expected value [" + value +
                        "] but received value [" + createEntity.getValue() + "]");

        Role findEntity = encounterDao.findRoleByValue(value);

        Assert.notNull(findEntity, "Unable to find entity by value [" + value + "]");
        Assert.notNull(findEntity.getId(), "Entity is missing primary key [" + findEntity.getId() + "]");
        Assert.isTrue(findEntity.getValue().equals(value),
                "Expected value [" + value + "] but received value [" + findEntity.getValue() + "]");
    }

    @Test
    public void createFindRoles() {
        String root1 = "R";
        String root2 = "ole";

        // Create Role-1
        String value = getRandomValue(root1 + root2);
        Role createEntity = new Role();
        createEntity.setValue(value);
        createEntity = encounterDao.createRole(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Create Role-2
        value = getRandomValue(root1 + root2);
        createEntity = new Role();
        createEntity.setValue(value);
        createEntity = encounterDao.createRole(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Find Role - root1
        List<Role> entities = encounterDao.findRoles(root1);
        Assert.notNull(entities, "Unable to find entity by value [" + root1 + "]");
        Assert.isTrue(entities.size() == 2,
                "Expected value [2] but received value [" + entities.size() + "]");

         // Find Role - root1 + root2
        entities = encounterDao.findRoles(root1 + root2);
        Assert.notNull(entities, "Unable to find entity by value [" + root1 + root2 + "]");
        Assert.isTrue(entities.size() == 2,
                "Expected value [2] but received value [" + entities.size() + "]");
    }

    @Test
    public void createFindSite() {
        String value = getRandomValue("Site");

        Site createEntity = new Site();
        createEntity.setValue(value);

        createEntity = encounterDao.createSite(createEntity);

        Assert.notNull(createEntity, "Entity creation failed");
        Assert.notNull(createEntity.getId(), "Entity creation failed");
        Assert.isTrue(createEntity.getValue().equals(value),
                "Expected value [" + value +
                        "] but received value [" + createEntity.getValue() + "]");

        Site findEntity = encounterDao.findSiteByValue(value);

        Assert.notNull(findEntity, "Unable to find entity by value [" + value + "]");
        Assert.notNull(findEntity.getId(), "Entity is missing primary key [" + findEntity.getId() + "]");
        Assert.isTrue(findEntity.getValue().equals(value),
                "Expected value [" + value + "] but received value [" + findEntity.getValue() + "]");
    }

    @Test
    public void createFindSites() {
        String root1 = "S";
        String root2 = "ite";

        // Create site-1
        String value = getRandomValue(root1 + root2);
        Site createEntity = new Site();
        createEntity.setValue(value);
        createEntity = encounterDao.createSite(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Create site-2
        value = getRandomValue(root1 + root2);
        createEntity = new Site();
        createEntity.setValue(value);
        createEntity = encounterDao.createSite(createEntity);
        Assert.notNull(createEntity, "Entity creation failed");

        // Search with root1
        List<Site> sites = encounterDao.findSites(root1);
        Assert.notNull(sites, "Unable to find entity by value [" + value + "]");
        Assert.isTrue(sites.size() == 2,
                "Expected value [2] but received value [" + sites.size() + "]");

        // Search with root1 + root2
        sites = encounterDao.findSites(root1 + root2);
        Assert.notNull(sites, "Unable to find entity by value [" + value + "]");
        Assert.isTrue(sites.size() == 2,
                "Expected value [2] but received value [" + sites.size() + "]");
    }

//    @Test
//    public void findDomain() {
//        Domain entity =
//                encounterDao.findDomainByValue(
//                        DomainEnum.LAW_ENFORCEMENT
//                );
//        Assert.notNull(entity, "Unable to find entity by value [" +
//                DomainEnum.LAW_ENFORCEMENT.value() + "]");
//        Assert.notNull(entity.getId(),
//                "Entity is missing primary key [" + entity.getId() + "]");
//    }

//    @Test
//    public void findVideoQuality() {
//        VideoQuality entity =
//                encounterDao.findVideoQualityByValue(
//                        VideoQualityEnum.BAD
//                );
//        Assert.notNull(entity, "Unable to find entity by value [" +
//                VideoQualityEnum.BAD.value() + "]");
//        Assert.notNull(entity.getId(),
//                "Entity is missing primary key [" + entity.getId() + "]");
//    }

//    @Test
//    public void findAudioQuality() {
//        AudioQuality entity =
//                encounterDao.findAudioQualityByValue(
//                        AudioQualityEnum.GOOD
//                );
//        Assert.notNull(entity, "Unable to find entity by value [" +
//                AudioQualityEnum.GOOD.value() + "]");
//        Assert.notNull(entity.getId(),
//                "Entity is missing primary key [" + entity.getId() + "]");
//        Assert.isTrue(entity.getValue().equals(AudioQualityEnum.GOOD.value()),
//                "Expected value [" + AudioQualityEnum.GOOD.value() +
//                        "] but received value [" + entity.getValue() + "]");
//    }

//    @Test
//    public void findParticipantType() {
//        ParticipantType entity =
//                encounterDao.findParticipantTypeByValue(
//                        ParticipantTypeEnum.INSTITUTION
//                );
//        Assert.notNull(entity, "Unable to find entity by value [" +
//                ParticipantTypeEnum.INSTITUTION.value() + "]");
//        Assert.notNull(entity.getId(),
//                "Entity is missing primary key [" + entity.getId() + "]");
//    }

//    @Test
//    public void findGender() {
//        Gender entity =
//                encounterDao.findGenderByValue(
//                        GenderEnum.FEMALE
//                );
//        Assert.notNull(entity, "Unable to find entity by value [" +
//                GenderEnum.FEMALE.value() + "]");
//        Assert.notNull(entity.getId(),
//                "Entity is missing primary key [" + entity.getId() + "]");
//        Assert.isTrue(entity.getValue().equals(GenderEnum.FEMALE.value()),
//                "Expected value [" + GenderEnum.FEMALE.value() +
//                        "] but received value [" + entity.getValue() + "]");
//    }

//    @Test
//    public void findInterviewType() {
//        InterviewType entity =
//                encounterDao.findInterviewTypeByValue(
//                        InterviewTypeEnum.COGNITIVE_TASK_ANALYSIS
//                );
//        Assert.notNull(entity, "Unable to find entity by value [" +
//                InterviewTypeEnum.COGNITIVE_TASK_ANALYSIS.value() + "]");
//        Assert.notNull(entity.getId(),
//                "Entity is missing primary key [" + entity.getId() + "]");
//        Assert.isTrue(entity.getValue().equals(InterviewTypeEnum.COGNITIVE_TASK_ANALYSIS.value()),
//                "Expected value [" + InterviewTypeEnum.COGNITIVE_TASK_ANALYSIS.value() +
//                        "] but received value [" + entity.getValue() + "]");
//    }

    @Test
    public void createFindEncounterMinimal() {
        String name = getRandomValue("encounterName");
        Encounter createEntity = createEncounterEntityMinimal(name);

        createEntity = encounterDao.createEncounter(createEntity);

        Assert.notNull(createEntity, "Error creating Entity [" + createEntity + "]");
        Assert.notNull(createEntity.getId(),
                "Error creating Encounter - id [" + createEntity.getId() + "]");
        Assert.isTrue(createEntity.getName().equals(name),
                "Expected value [" + name +
                        "] but received value [" + createEntity.getName() + "]");

        Encounter findEntityById = encounterDao.findEncounterById(createEntity.getId());

        Assert.notNull(findEntityById, "Error creating Entity [" + findEntityById + "]");
        Assert.notNull(findEntityById.getId(),
                "Error finding Entity - id [" + findEntityById.getId() + "]");
        Assert.isTrue(findEntityById.getName().equals(name),
                "Expected value [" + name +
                        "] but received value [" + findEntityById.getName() + "]");

        Encounter findEntityByName = encounterDao.findEncounterByName(name);

        Assert.notNull(findEntityByName, "Error creating Entity [" + findEntityByName + "]");
        Assert.notNull(findEntityByName.getId(),
                "Error finding Entity - id [" + findEntityByName.getId() + "]");
        Assert.isTrue(findEntityByName.getName().equals(name),
                "Expected value [" + name +
                        "] but received value [" + findEntityByName.getName() + "]");
    }

    @Test
    public void createFindEncounter() {
        String name = getRandomValue("encounterName");
        Encounter createEntity = createEncounterEntity(name);

        createEntity = encounterDao.createEncounter(createEntity);

        Assert.notNull(createEntity, "Error creating Entity [" + createEntity + "]");
        Assert.notNull(createEntity.getId(),
                "Error creating Encounter - id [" + createEntity.getId() + "]");
        Assert.isTrue(createEntity.getName().equals(name),
                "Expected value [" + name +
                        "] but received value [" + createEntity.getName() + "]");

        Encounter findEntityById = encounterDao.findEncounterById(createEntity.getId());

        Assert.notNull(findEntityById, "Error finding Entity [" + findEntityById + "]");
        Assert.notNull(findEntityById.getId(),
                "Error finding Entity - id [" + findEntityById.getId() + "]");
        Assert.isTrue(findEntityById.getName().equals(name),
                "Expected value [" + name +
                        "] but received value [" + findEntityById.getName() + "]");

        Encounter findEntityByName = encounterDao.findEncounterByName(name);

        Assert.notNull(findEntityByName, "Error creating Entity [" + findEntityByName + "]");
        Assert.notNull(findEntityByName.getId(),
                "Error finding Entity - id [" + findEntityByName.getId() + "]");
        Assert.isTrue(findEntityByName.getName().equals(name),
                "Expected value [" + name +
                        "] but received value [" + findEntityByName.getName() + "]");
    }

    @Test
    public void updateFindEncounter() {
        String name = getRandomValue("encounterName");
        Encounter createEntity = createEncounterEntity(name);

        createEntity = encounterDao.createEncounter(createEntity);

        Assert.notNull(createEntity, "Error creating Entity [" + createEntity + "]");
        Assert.notNull(createEntity.getId(),
                "Error creating Encounter - id [" + createEntity.getId() + "]");
        Assert.isTrue(createEntity.getName().equals(name),
                "Expected value [" + name +
                        "] but received value [" + createEntity.getName() + "]");

        EncounterQuality encounterQuality = new EncounterQuality();
        String encQuality = "Better";
        encounterQuality.setValue(encQuality);
        createEntity.setEncounterQuality(encounterQuality);

        encounterDao.updateEncounter(createEntity);

        Encounter findEncounter = encounterDao.findEncounterById(createEntity.getId());
        Assert.notNull(findEncounter, "Error finding Entity [" + findEncounter + "]");
        Assert.isTrue(findEncounter.getEncounterQuality().getValue().equals(encQuality),
                "Expected [" + encQuality + "] but received [" +
                        findEncounter.getEncounterQuality().getValue() + "]");

    }

    @Test
    public void createFindParticipant() {
        String name = getRandomValue("encounterName");
        Encounter encounterEntity = createEncounterEntityMinimal(name);

        encounterEntity = encounterDao.createEncounter(encounterEntity);

        String codeName = getRandomValue("codeName");
        Participant participantEntity = createParticipantEntity(codeName);
        // Add encounter relationship
        participantEntity.setEncounter(encounterEntity);
        // Add a Language
        Language languageEntity = new Language();
        String lang = "English";
        languageEntity.setValue(lang);
        participantEntity.getLanguages().add(languageEntity);

        participantEntity = encounterDao.createParticipant(participantEntity);

        Assert.notNull(participantEntity,
                "Error creating Entity [" + participantEntity + "]");
        Assert.notNull(participantEntity.getId(),
                "Error creating Entity - id [" + participantEntity.getId() + "]");
        Assert.isTrue(participantEntity.getEncounter().getId().equals(encounterEntity.getId()),
                "Expected EncounterId [" + encounterEntity.getId() +
                        "] but received [" + participantEntity.getEncounter().getId() + "]");

        Assert.isTrue(!participantEntity.getLanguages().isEmpty(), "No Languages");
        for (Language langEntity : participantEntity.getLanguages()) {
            Assert.isTrue(langEntity.getValue().equals(lang),
                    "Expected language [" + lang + "] but received [" +
                            langEntity.getValue() + "]");
        }

        // FindParticipantById
        Participant findParticipantEntity =
                encounterDao.findParticipantByEncounterIdAndCode(
                        encounterEntity.getId(), codeName);
        Assert.notNull(participantEntity,
                "Error finding Entity [" + findParticipantEntity + "]");
        Assert.isTrue(findParticipantEntity.getCodeName().equals(codeName),
                "Expected name [" + codeName +
                        "] but received [" + findParticipantEntity.getCodeName() + "]");
    }

    @Test
    public void createFindInterview() {

        String interviewName = getRandomValue("interviewName");
        Interview interviewEntity = createInterviewEntity(interviewName);

        interviewEntity = encounterDao.createInterview(interviewEntity);

        Assert.notNull(interviewEntity,
                "Error creating Entity [" + interviewEntity + "]");
        Assert.notNull(interviewEntity.getId(),
                "Error creating Entity - id [" + interviewEntity.getId() + "]");

        String encounterName = getRandomValue("encounterName");
        Encounter encounterEntity = createEncounterEntityMinimal(encounterName);

        encounterEntity.getInterviews().add(interviewEntity);

        encounterEntity = encounterDao.createEncounter(encounterEntity);

        Assert.notNull(interviewEntity,
                "Error creating Entity [" + encounterEntity + "]");
        Assert.notNull(encounterEntity.getId(),
                "Error creating Entity - id [" + encounterEntity.getId() + "]");
        Assert.isTrue(!encounterEntity.getInterviews().isEmpty(),
                "No Interviews");
        for (Interview interview : encounterEntity.getInterviews()) {
            Assert.isTrue(interview.getName().equals(interviewName),
                "Expected interview name [" + interviewName +
                        "] but received [" + interview.getName() + "]");
        }
    }

    @Test
    public void findFileNames() {

        Artifact artifactEntity = createArtifactEntity(elanFile[3]);
        artifactEntity = encounterDao.createArtifact(artifactEntity);
        Assert.notNull(artifactEntity,
                "Error creating Entity [" + artifactEntity + "]");

        artifactEntity = createArtifactEntity(elanFile[4]);
        artifactEntity = encounterDao.createArtifact(artifactEntity);
        Assert.notNull(artifactEntity,
                "Error creating Entity [" + artifactEntity + "]");

        artifactEntity = createArtifactEntity(elanFile[5]);
        artifactEntity = encounterDao.createArtifact(artifactEntity);
        Assert.notNull(artifactEntity,
                "Error creating Entity [" + artifactEntity + "]");

        List<ArtifactFile> files = encounterDao.findArtifactFilesLike("FPD_", 10);
        Assert.isTrue(files.size() == 3,
                "Expected [3] but received [" + files.size() + "]");
    }

    @Test
    public void createFindRelatedFiles() {

        String filename = this.getElanFilename();
        Artifact artifactEntity = createArtifactEntity(filename);

        // Create ArtifactFile
        ArtifactFile artifactFile1 = new ArtifactFile();
        artifactFile1.setFileName(filename);
        FileType fileType = new FileType();
        fileType.setValue("ELAN");
        artifactFile1.setFileType(fileType);
        artifactFile1.setProcessed(false);
        // Add file to ArtifactFiles collection
        artifactEntity.getArtifactFiles().add(artifactFile1);

        artifactEntity = encounterDao.createArtifact(artifactEntity);

        Assert.notNull(artifactEntity,
                "Error creating Entity [" + artifactEntity + "]");
        Assert.notNull(artifactEntity.getId(),
                "Error creating Entity - id [" + artifactEntity.getId() + "]");

        Artifact findArtifactEntity =
                encounterDao.findArtifactById(artifactEntity.getId());

        Assert.notNull(findArtifactEntity,
                "Error finding Entity [" + findArtifactEntity + "]");
        Assert.notNull(findArtifactEntity.getId(),
                "Error finding Entity - id [" + findArtifactEntity.getId() + "]");

        Assert.isTrue(!findArtifactEntity.getArtifactFiles().isEmpty(),
                "No Artifact files");

        // Create encounter
        String name = getRandomValue("encounterName");
        Encounter encounterEntity = createEncounterEntityMinimal(name);

        // Create encounter-artifact relationship
        encounterEntity.getArtifacts().add(artifactEntity);

        encounterEntity = encounterDao.createEncounter(encounterEntity);

        // Find encounter -> artifact
        Encounter findEncounter = encounterDao.findEncounterById(encounterEntity.getId());
        Assert.notNull(findEncounter, "Unable to find encounter by id [" + encounterEntity.getId() + "]");
        Assert.isTrue(!findEncounter.getArtifacts().isEmpty(), "No Artifacts");
        for (Artifact artifact : findEncounter.getArtifacts()) {
            Assert.isTrue(artifact.getArtifactName().equals(filename),
                    "Expected filename [" + filename + "] but received [" +
                        artifact.getArtifactName() + "]");
        }

        // Create another Encounter/Artifact and relate the previous
        filename = this.getElanFilename();
        artifactEntity = createArtifactEntity(filename);

        // Relate previous created file to this artifact
        artifactEntity.getRelatedFiles().add(artifactFile1);

        // Create ArtifactFile
        ArtifactFile artifactFile2 = new ArtifactFile();
        artifactFile2.setFileName(filename);
        fileType = new FileType();
        fileType.setValue("ELAN");
        artifactFile2.setFileType(fileType);
        artifactFile2.setProcessed(false);
        // Add file to ArtifactFiles collection
        artifactEntity.getArtifactFiles().add(artifactFile2);

        artifactEntity = encounterDao.createArtifact(artifactEntity);

        Assert.notNull(artifactEntity,
                "Error creating Entity [" + artifactEntity + "]");
        Assert.notNull(artifactEntity.getId(),
                "Error creating Entity - id [" + artifactEntity.getId() + "]");

        findArtifactEntity =
                encounterDao.findArtifactById(artifactEntity.getId());

        Assert.notNull(findArtifactEntity,
                "Error finding Entity [" + findArtifactEntity + "]");
        Assert.notNull(findArtifactEntity.getId(),
                "Error finding Entity - id [" + findArtifactEntity.getId() + "]");

        Assert.isTrue(!findArtifactEntity.getArtifactFiles().isEmpty(),
                "No Artifact files");
        Assert.isTrue(!findArtifactEntity.getRelatedFiles().isEmpty(),
                "No Related files");

        // Create encounter
        name = getRandomValue("encounterName");
        encounterEntity = createEncounterEntityMinimal(name);

        // Create encounter-artifact relationship
        encounterEntity.getArtifacts().add(artifactEntity);

        encounterEntity = encounterDao.createEncounter(encounterEntity);

        // Find encounter -> artifact
        findEncounter = encounterDao.findEncounterById(encounterEntity.getId());
        Assert.notNull(findEncounter, "Unable to find encounter by id [" + encounterEntity.getId() + "]");
        Assert.isTrue(!findEncounter.getArtifacts().isEmpty(), "No Artifacts");
        for (Artifact artifact : findEncounter.getArtifacts()) {
            Assert.isTrue(artifact.getArtifactName().equals(filename),
                    "Expected filename [" + filename + "] but received [" +
                        artifact.getArtifactName() + "]");
        }


    }

    @Test
    public void createFindArtifact() {

        String filename = this.getElanFilename();
        Artifact artifactEntity = createArtifactEntity(filename);

        artifactEntity = encounterDao.createArtifact(artifactEntity);

        Assert.notNull(artifactEntity,
                "Error creating Entity [" + artifactEntity + "]");
        Assert.notNull(artifactEntity.getId(),
                "Error creating Entity - id [" + artifactEntity.getId() + "]");

        Artifact findArtifactEntity =
                encounterDao.findArtifactById(artifactEntity.getId());

        Assert.notNull(findArtifactEntity,
                "Error finding Entity [" + findArtifactEntity + "]");
        Assert.notNull(findArtifactEntity.getId(),
                "Error finding Entity - id [" + findArtifactEntity.getId() + "]");

        // Create encounter
        String name = getRandomValue("encounterName");
        Encounter encounterEntity = createEncounterEntityMinimal(name);

        // Create encounter-artifact relationship
        encounterEntity.getArtifacts().add(artifactEntity);

        encounterEntity = encounterDao.createEncounter(encounterEntity);

        // Find encounter -> artifact
        Encounter findEncounter = encounterDao.findEncounterById(encounterEntity.getId());
        Assert.notNull(findEncounter, "Unable to find encounter by id [" + encounterEntity.getId() + "]");
        Assert.isTrue(!findEncounter.getArtifacts().isEmpty(), "No Artifacts");
        for (Artifact artifact : findEncounter.getArtifacts()) {
            Assert.isTrue(artifact.getArtifactName().equals(filename),
                    "Expected filename [" + filename + "] but received [" +
                        artifact.getArtifactName() + "]");
        }
    }

    @Test
    public void createUtterance() {
        // Create Artifact object
        Artifact artifactEntity = createArtifactEntity();

        // Create Line object
        String annotationId = getRandomValue("AN");
        //Line utteranceEntity = createUtteranceEntity(annotationId);
        // Set required Artifact FK
        //utteranceEntity.setArtifact(artifactEntity);
        // Add Line to Artifact
        //artifactEntity.getLines().add(utteranceEntity);

        artifactEntity = encounterDao.createArtifact(artifactEntity);
  /*
        Assert.notNull(artifactEntity,
                "Error creating Entity [" + artifactEntity + "]");
        Assert.isTrue(!artifactEntity.getLines().isEmpty(),
                "No Utterances [" + !artifactEntity.getLines().isEmpty() + "]");

        Assert.isTrue(
                artifactEntity.getLines().get(0).getAnnotationId().equals(annotationId),
                "Expected value [" + annotationId + "] but received value [" +
                        artifactEntity.getLines().get(0).getAnnotationId() + "]");
    */
    }

    @Test
    public void findEncounters() {
        // Create encounter
        Encounter encounter = createEncounterEntity(getRandomValue("ENCOUNTER"));

        // Retrieve artifact(s) and add utterance(s)
//        for (Artifact artifactEntity : encounter.getArtifacts()) {
//            Line utteranceEntity = createUtteranceEntity(getRandomValue("AN"));
//            utteranceEntity.setArtifact(artifactEntity);
//            artifactEntity.getLines().add(utteranceEntity);
//        }

        encounterDao.createEncounter(encounter);

        List<QueryItem> queryItems = new ArrayList<QueryItem>();
        // name
        QueryItem queryItem = new QueryItem();
        queryItem.setEntity(Encounter.class);
        queryItem.setAttribute(Encounter_.name.getName());
        queryItem.setComparison(QueryItem.Comparison.EQUAL);
        queryItem.setValue(encounter.getName());
        queryItems.add(queryItem);
        // duration
        queryItem = new QueryItem();
        queryItem.setOperator(QueryItem.Operator.and);
        queryItem.setEntity(Encounter.class);
        queryItem.setAttribute(Encounter_.duration.getName());
        queryItem.setComparison(QueryItem.Comparison.LE);
        queryItem.setValue(encounter.getDuration());
        queryItems.add(queryItem);
        // date
        queryItem = new QueryItem();
        queryItem.setOperator(QueryItem.Operator.and);
        queryItem.setEntity(Encounter.class);
        queryItem.setAttribute(Encounter_.collectionDate.getName());
        queryItem.setComparison(QueryItem.Comparison.BETWEEN);
        queryItem.setValue(encounter.getCollectionDate());
        queryItem.setValue2(encounter.getCollectionDate());
        queryItems.add(queryItem);
        // comment
        queryItem = new QueryItem();
        queryItem.setOperator(QueryItem.Operator.and);
        queryItem.setEntity(Encounter.class);
        queryItem.setAttribute(Encounter_.comments.getName());
        queryItem.setComparison(QueryItem.Comparison.LIKE);
        queryItem.setValue(encounter.getComments());
        queryItems.add(queryItem);
        // Phrase
        String phrase = "search";
//        for (Artifact artifactEntity : encounter.getArtifacts()) {
//            for (Line utteranceEntity : artifactEntity.getLines()) {
//                phrase = utteranceEntity.getPhrase();
//                break;
//            }
//        }
        //if (phrase != null) {
            queryItem = new QueryItem();
            queryItem.setOperator(QueryItem.Operator.or);
            queryItem.setEntity(Line.class);
            queryItem.setAttribute(Line_.utterance.getName());
            queryItem.setComparison(QueryItem.Comparison.LIKE);
            queryItem.setValue(phrase);
            queryItems.add(queryItem);
//        } else {
//            System.out.println("NO PHRASE DATA");
//        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        List<Encounter> encounters =
//                encounterDao.findEncounters(queryItems);
//
//        Assert.notNull(encounters, "Error creating Encounter");
//        Assert.isTrue(encounters.size() > 0, "Incorrect size [" + encounters.size() + "]");
//        Assert.isTrue(encounters.get(0).getName().equals(encounter.getName()),
//                "Incorrect name. Expected [" + encounter.getName() +
//                        "] but received [" + encounters.get(0).getName() + "]");
    }

    private Encounter createEncounterEntityMinimal(String name) {
        Encounter encounterEntity = new Encounter();
        // Name
        encounterEntity.setName(name);
        // Domain
        Domain domainEntity = new Domain();
        domainEntity.setValue("LAW_ENFORCEMENT");
        encounterEntity.setDomain(domainEntity);
        // EncounterQuality
        EncounterQuality encounterQuality = new EncounterQuality();
        encounterQuality.setValue("Good encounter");
        encounterEntity.setEncounterQuality(encounterQuality);
        // EncounterReason
        EncounterReason encounterReason = new EncounterReason();
        encounterReason.setValue("suspicious activity");
        encounterEntity.setEncounterReason(encounterReason);
        // Corpus
        Corpus corpus = new Corpus();
        corpus.setValue("body of the crime");
        encounterEntity.setCorpus(corpus);
        // GeographicLocation
        GeographicLocation geographicLocation = new GeographicLocation();
        geographicLocation.setValue("California");
        encounterEntity.setGeographicLocation(geographicLocation);
        // Site
        Site site = new Site();
        site.setValue("Right here");
        encounterEntity.setSite(site);
        // GeneralTime
        GeneralTime generalTime = new GeneralTime();
        generalTime.setValue("Morning");
        encounterEntity.setGeneralTime(generalTime);
        // Collector
        Collector collector = new Collector();
        collector.setValue("The Bone Collector");
        encounterEntity.setCollector(collector);
        // numberOfParticipants
        encounterEntity.setNumberOfParticipants(3);
        // bystanders
        encounterEntity.setBystanders(true);
        // duration
        encounterEntity.setDuration(23);
        // participantFirstEncounter
        encounterEntity.setParticipantFirstEncounter(true);
        // participantFamiliarity
        encounterEntity.setParticipantFamiliarity(false);
        // collectionDate
        encounterEntity.setCollectionDate(new Date());
        // comments
        encounterEntity.setComments("This is the comment field");

        return encounterEntity;
    }

    private Encounter createEncounterEntity(String name) {
        Encounter encounterEntity = new Encounter();
        // Name
        encounterEntity.setName(name);
        // Domain
        Domain domainEntity = new Domain();
        domainEntity.setValue("LAW_ENFORCEMENT");
        encounterEntity.setDomain(domainEntity);
        // EncounterQuality
        EncounterQuality encounterQuality = new EncounterQuality();
        encounterQuality.setValue("Good encounter");
        encounterEntity.setEncounterQuality(encounterQuality);
        // EncounterReason
        EncounterReason encounterReason = new EncounterReason();
        encounterReason.setValue("suspicious activity");
        encounterEntity.setEncounterReason(encounterReason);
        // Corpus
        Corpus corpus = new Corpus();
        corpus.setValue("body of the crime");
        encounterEntity.setCorpus(corpus);
        // GeographicLocation
        GeographicLocation geographicLocation = new GeographicLocation();
        geographicLocation.setValue("California");
        encounterEntity.setGeographicLocation(geographicLocation);
        // Site
        Site site = new Site();
        site.setValue("Right here");
        encounterEntity.setSite(site);
        // GeneralTime
        GeneralTime generalTime = new GeneralTime();
        generalTime.setValue("Morning");
        encounterEntity.setGeneralTime(generalTime);
        // Collector
        Collector collector = new Collector();
        collector.setValue("The Bone Collector");
        encounterEntity.setCollector(collector);
        // numberOfParticipants
        encounterEntity.setNumberOfParticipants(3);
        // bystanders
        encounterEntity.setBystanders(true);
        // duration
        encounterEntity.setDuration(23);
        // participantFirstEncounter
        encounterEntity.setParticipantFirstEncounter(true);
        // participantFamiliarity
        encounterEntity.setParticipantFamiliarity(false);
        // collectionDate
        encounterEntity.setCollectionDate(new Date());
        // comments
        encounterEntity.setComments("This is the comment field");
         // videoQuality
        VideoQuality videoQuality = new VideoQuality();
        videoQuality.setValue("GOOD");
        encounterEntity.setVideoQuality(videoQuality);
        // audioQuality
        AudioQuality audioQuality = new AudioQuality();
        audioQuality.setValue("OKAY");
        encounterEntity.setAudioQuality(audioQuality);
        // Participant
        String codeName = getRandomValue("codeName");
        Participant participantEntity = createParticipantEntity(codeName);
         // Add a Language to the participant
        Language languageEntity = new Language();
        String lang = "English";
        languageEntity.setValue(lang);
        participantEntity.getLanguages().add(languageEntity);
        // Add encounter relationship
        encounterEntity.getParticipants().add(participantEntity);

        // Interview
        String interviewName = getRandomValue("interviewName");
        Interview interviewEntity = createInterviewEntity(interviewName);
        // Add an artifact to the interview
        Artifact interviewArtifactEntity =
                createArtifactEntity("interview.doc");
        // Associate artifact with encounter
        interviewEntity.getArtifacts().add(interviewArtifactEntity);
        // Create relationship between encounter and artifact
        encounterEntity.getInterviews().add(interviewEntity);

        // Artifact
        String filename = this.getElanFilename();
        Artifact artifactEntity =
                createArtifactEntity(filename);

        // Create File
        ArtifactFile artifactFile = createArtifactFileEntity(filename);

        // Create another File
        String anotherFilename = this.getElanFilename();
        ArtifactFile anotherArtifactFile = createArtifactFileEntity(anotherFilename);

        //artifactFile.

        // Add artifactFile to artifactEntity
        //artifactEntity.getArtifactFiles().add(artifactFile);

        // Create encounter-artifact relationship
        encounterEntity.getArtifacts().add(artifactEntity);

        return encounterEntity;
    }

    private Interview createInterviewEntity(String name) {
        Interview interview = new Interview();

        interview.setName(name);
        // interviewType
        InterviewType interviewType = new InterviewType();
        interviewType.setValue("ETHNOGRAPHIC");
        interview.setInterviewType(interviewType);
        // collectionDate
        interview.setCollectionDate(new Date());
        // geographicLocation
        GeographicLocation geographicLocation = new GeographicLocation();
        geographicLocation.setValue(getRandomValue("GeoOrigin"));
        interview.setGeographicLocation(geographicLocation);
        // site
        Site site = new Site();
        site.setValue(getRandomValue("Site"));
        interview.setSite(site);

        return interview;
    }

    private Artifact createArtifactEntity() {
        return createArtifactEntity(getElanFilename());
    }

    private ArtifactFile createArtifactFileEntity(String filename) {
        ArtifactFile artifactFile = new ArtifactFile();
        // filename
        artifactFile.setFileName(filename);
        // fileType
        FileType fileType = new FileType();
        fileType.setValue("ELAN");
        artifactFile.setFileType(fileType);

        return artifactFile;
    }

    private Artifact createArtifactEntity(String artifactName) {
        Artifact artifact = new Artifact();
        // artifactName
        artifact.setArtifactName(artifactName);
//        // videoQuality
//        VideoQuality videoQuality = new VideoQuality();
//        videoQuality.setValue("GOOD");
//        artifact.setVideoQuality(videoQuality);
//        // audioQuality
//        AudioQuality audioQuality = new AudioQuality();
//        audioQuality.setValue("OKAY");
//        artifact.setAudioQuality(audioQuality);
        // description
        artifact.setDescriptor("Artifact description");
        // collectionDate
        // artifact.setCollectionDate(new Date()); --> moved to encounter
        // generalTime
//        GeneralTime generalTime = new GeneralTime();
//        generalTime.setValue("AfterNoon");
//        artifact.setGeneralTime(generalTime);
//        // collector
//        Collector collector = new Collector();
//        collector.setValue("The Bone Collector");
//        artifact.setCollector(collector);
//        // comments
//        artifact.setComments("Artifact comments");

        return artifact;
    }
 /*
    private Line createUtteranceEntity(String annotationId) {
        Line utterance = new Line();
        // annotationId;
        utterance.setAnnotationId(annotationId);
        // phrase;
        utterance.setPhrase("Hey how are you");
        // annotation;
        utterance.setAnnotation("Looking at ground");
        // startTime;
        utterance.setStartTime(100);
        // endTime;
        utterance.setEndTime(120);
        // turnTime;
        utterance.setTurnTime(20);
        // interveningTime;
        utterance.setInterveningTime(11);
        // numWords;
        utterance.setNumWords(4);
        // participant;
        utterance.setParticipant("IM1");

        return utterance;
    }
*/
    private Participant createParticipantEntity(String codeName) {
        Participant participant = new Participant();
        // ParticipantType
        ParticipantType participantType = new ParticipantType();
        participantType.setValue("CIVILIAN");
        participant.setParticipantType(participantType);
        // Gender
        Gender gender = new Gender();
        gender.setValue("FEMALE");
        participant.setGender(gender);
        // Rank
        Rank rank = new Rank();
        rank.setValue("SGT");
        participant.setRank(rank);
        // Role
        Role role = new Role();
        role.setValue("The Heavy");
        participant.setRole(role);
        // Ethnicity
        Ethnicity ethnicity = new Ethnicity();
        ethnicity.setValue("mulatto");
        participant.setEthnicity(ethnicity);
        // GeoOrigin
        GeographicLocation geographicLocation = new GeographicLocation();
        geographicLocation.setValue("Earth");
        participant.setGeographicLocation(geographicLocation);
        // Age
        Age pAge = new Age();
        pAge.setValue("20-30");
        participant.setAge(pAge);
        // CodeName
        participant.setCodeName(codeName);
        // TimeInCountry
        //participant.setTimeInCountry(101);
        // SociometricBadge
        participant.setSociometricBadge("salksdlfksdlkf.bdg");
        // Comments
        participant.setComments("Some participant comments");

        return participant;
    }

    private String getRandomValue(String seed) {
        Random r = new Random();
        int nextI = r.nextInt();
        return (nextI > 0 ? seed + ":" + nextI : seed + ":" + nextI * -1);
    }

    private String getElanFilename() {
        Random r = new Random();
        int nextI = r.nextInt(elanFile.length);

        String filename = elanFile[nextI];
        System.out.println("getElanFilename returning filename [" + filename + "]");
        return filename;
    }

    private String[] elanFile = {
        "24_April_2012_Group_1_ANON.eaf",
        "AWALP_April_2012_Transcript_2nd_Group.eaf",
        "OPA_10-175_6709@20100417012736.eaf",
        "SFPD_AnythingElse_UK_05212012.eaf",
        "SFPD_DrinkinginthePark_MI_05142012_3and4of7.eaf",
        "SFPD_IDroppedAlltheCharges_MI_05142012_5of7.eaf",
        "SFPD_ManfromLineUp_BA_05252012.eaf",
        "SFPD_MoveAlongDrinkingComplianceandResistance_UK_05142012.eaf",
        "SFPD_MoveAlong_Homeless_UK_050212.eaf",
        "SFPD_ShesGotaBeer_BA_05252012.eaf",
        "SPD_011666_6963@20090106213654.eaf",
        "SPD_012238_6978@20090201220925.eaf",
        "SPD_012256_6353@20090202005048.eaf",
        "SPD_022289_7455@20090331021633.eaf",
        "SPD_022289_7456@20090331021724.eaf",
        "SPD_022551_6805@20090412023835.eaf",
        "SPD_023462_5313@20090523211034.eaf",
        "SPD_023486_7420@20090524014031.eaf",
        "SPD_023530_6684@20090524041649.eaf",
        "SPD_023649_6709@20090529023720.eaf",
        "SPD_023649_6834@20090529023739.eaf",
        "SPD_023850_6751@20090604210054.eaf",
        "SPD_024099_7508@20090617225210.eaf",
        "SPD_024684_5452@20090709010643.eaf",
        "SPD_024852_5452@20090716202049.eaf",
        "SPD_024860_5192@20090716221303.eaf",
        "SPD_024924_6091@20090721162314.eaf",
        "SPD_025009_7523@20090727204108.eaf",
        "SPD_025021_7509@20090728075518.eaf",
        "SPD_028049_7467@20100105203650.eaf",
        "SPD_031644_6942@20100618072121.eaf",
        "SPD_033889_5452@20101004213335.eaf",
        "TR2_Night_Traffic_Stop.eaf",
        "TR2_Empty_Gas_Tank_UNH.eaf",
        "TR3_Junk_Yard_Car.eaf",
        "TR3_Speeding_Citation_UNH.eaf",
        "TR4_Car_Broken_Down_UNH.eaf",
        "TR4_Highway_Pedestrian_UNH.eaf",
        "TR4_Officer_Fills_Pedestrians_Gas_Tank.eaf",
        "TR5_Possible_DUI_UNH.eaf",
        "TR7_Lost.eaf",
        "TR7_Night_Swerving_UNH.eaf",
        "TR7_Speed_Warning.eaf",
        "TR7_Speeding_With_Baby.eaf",
        "TR7_Tailgater_UNH.eaf",
        "TR7_Two_Car_Pullover.eaf",
        "TR8_Friendly_Encounter.eaf",
        "TR8_Front_License_Plate_Warning.eaf",
        "TR8_License_Plate_Problems.eaf",
        "headcam_1_transcript.eaf"
    };


}
