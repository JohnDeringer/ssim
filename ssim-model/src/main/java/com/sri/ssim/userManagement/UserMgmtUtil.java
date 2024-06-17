package com.sri.ssim.userManagement;

import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Random;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/26/12
 */
public class UserMgmtUtil {

    private static final String HASH_ALGORITHM_SHA_256 = "SHA-256";
    private static final String CHAR_SET_UTF_8 = "UTF-8";
    private static final int SALT_ARRAY_SIZE = 16;

    private static final Logger logger = LoggerFactory.getLogger(UserMgmtUtil.class);

    public static boolean isPasswordValid(
            @NotNull String password, @NotNull byte[] db_password) {

        byte[] salt = extractSalt(db_password);
        byte[] hashedPassword = hashPassword(salt, password);
        byte[] combinePassword = combinePasswordSaltAndHash(salt, hashedPassword);

        return Arrays.equals(combinePassword, db_password);
    }

    public static byte[] combinePasswordSaltAndHash(
            @NotNull byte[] salt, @NotNull byte[] password) {

        byte[] combined = new byte[salt.length + password.length];
        System.arraycopy(salt, 0, combined, 0, salt.length);
        System.arraycopy(password, 0, combined, salt.length, password.length);

        return combined;
    }

    public static byte[] hashPassword(
            @NotNull byte[] salt, @NotNull String password) {
        byte[] hash = new byte[0];
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM_SHA_256);
            digest.reset();
	        digest.update(salt);

            hash = digest.digest(password.getBytes(CHAR_SET_UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hash;
    }

    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_ARRAY_SIZE];
        Random r = new Random();
        r.nextBytes(salt);

        return salt;
    }

    private static byte[] extractSalt(@NotNull byte[] password) {
        byte[] salt = new byte[SALT_ARRAY_SIZE];
        if (password.length > salt.length) {
            System.arraycopy(password, 0, salt, 0, salt.length);
        }

        return salt;
    }

}
