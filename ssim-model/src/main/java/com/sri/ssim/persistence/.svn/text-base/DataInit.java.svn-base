package com.sri.ssim.persistence;

import com.sri.ssim.schema.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 8/29/12
 */
public class DataInit {

    public DataInit(EncounterDao encounterDao, UserManagementDao userManagementDao) {

        Logger logger = LoggerFactory.getLogger(this.getClass());

        // Load VideoQuality values (good, ok, partly-usable, bad)
        // good, ok, partly-usable, bad
        encounterDao.addVideoQualityEntity(1l, "bad");
        encounterDao.addVideoQualityEntity(2l, "partly-usable");
        encounterDao.addVideoQualityEntity(3l, "ok");
        encounterDao.addVideoQualityEntity(4l, "good");

        // Load AudioQuality values (good, ok, partly-usable, bad)
        encounterDao.addAudioQualityEntity(1l, "bad");
        encounterDao.addAudioQualityEntity(2l, "partly-usable");
        encounterDao.addAudioQualityEntity(3l, "ok");
        encounterDao.addAudioQualityEntity(4l, "good");

/*
        // Load Domain values
        for (DomainEnum enumObject : DomainEnum.values()) {
            logger.info("Initializing enumerated value [" + enumObject + "]");
            encounterDao.addDomainEntity(enumObject);
        }

        // Load VideoQuality values
        for (VideoQualityEnum enumObject : VideoQualityEnum.values()) {
            logger.info("Initializing enumerated value [" + enumObject + "]");
            encounterDao.addVideoQualityEntity(enumObject);
        }

        // Load AudioQuality values
        for (AudioQualityEnum enumObject : AudioQualityEnum.values()) {
            logger.info("Initializing enumerated value [" + enumObject + "]");
            encounterDao.addAudioQualityEntity(enumObject);
        }

        // Load ParticipantType values
        for (ParticipantTypeEnum enumObject : ParticipantTypeEnum.values()) {
            logger.info("Initializing enumerated value [" + enumObject + "]");
            encounterDao.addParticipantTypeEntity(enumObject);
        }

        // Load Gender values
        for (GenderEnum enumObject : GenderEnum.values()) {
            logger.info("Initializing enumerated value [" + enumObject + "]");
            encounterDao.addGenderEntity(enumObject);
        }

        // Load InterviewType values
        for (InterviewTypeEnum enumObject : InterviewTypeEnum.values()) {
            logger.info("Initializing enumerated value [" + enumObject + "]");
            encounterDao.addInterviewTypeEntity(enumObject);
        }
*/
        // Load UserRole values
        for (UserRoleEnum enumObject : UserRoleEnum.values()) {
            logger.info("Initializing enumerated value [" + enumObject + "]");
            userManagementDao.addUserRoleEntity(enumObject);
        }

    }

}
