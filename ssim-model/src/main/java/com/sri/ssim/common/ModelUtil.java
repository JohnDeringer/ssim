package com.sri.ssim.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 1/15/13
 */
public class ModelUtil {

    private static final Logger logger = LoggerFactory.getLogger("ModelUtil");

    @Nullable
    public static Date getDate(@NotNull String dateString) {
        // Fri Apr 01 00:00:00 PDT 2011
        //DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("MM/dd/yy");
        Date date;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            date = null;
            logger.error("Exception parsing Date String [" + dateString + "]");
        }
        return date;
    }

    @Nullable
    private static Date getDateAndTime(@NotNull String dateString, @NotNull String timeString) {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("MM/dd/yy hh:mm:ss");
        Date date;
        try {
            date = dateFormat.parse(dateString + " " + timeString);
        } catch (ParseException e) {
            date = null;
            logger.error("Exception parsing DateTime String [" + dateString + " " + timeString + "]");
        }
        return date;
    }

}
