package com.sri.ssim.persistence;

import com.sri.ssim.file.AnnotatedLine;

import org.apache.commons.lang.StringUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 9/11/12
 */
public class EntityUtil {

    @NotNull
    public static List<Line> toLineEntity(
            ArtifactFile artifactFile,
            List<AnnotatedLine> annotatedLines) {

        // Line collection for persisting
        List<Line> lineEntities = new ArrayList<Line>();

        if (annotatedLines != null && !annotatedLines.isEmpty()) {
            // Sort the lines by start time
            Comparator<AnnotatedLine> sortByStartTime =
                    new SortByStartTime();
            Collections.sort(annotatedLines, sortByStartTime);

            // Convert annotatedLine collection to array.
            //  This will allow us to compute the intervening time
            //   (current annotatedLine start time) - (prior annotatedLine end time)
            AnnotatedLine[] annotatedLineArray =
                    annotatedLines.toArray(new AnnotatedLine[annotatedLines.size()]);

            // Calculate numWords, turnTime, interveningTime and transform
            //  Line object to Line entity
            for (int i = 0; i < annotatedLineArray.length; i++) {
                long priorEndTime = annotatedLineArray[i].getStartTime();
                if (i > 0) {
                    priorEndTime = annotatedLineArray[i - 1].getEndTime();
                }
                lineEntities.add(
                        toLineEntity(artifactFile, annotatedLineArray[i], priorEndTime)
                );
            }
        }

        return lineEntities;
    }

    @NotNull
    public static Line toLineEntity(
            ArtifactFile artifactFile,
            @NotNull AnnotatedLine annotatedLine, long priorEndTime) {

        Line lineEntity = new Line();

        lineEntity.setArtifactFile(artifactFile);

        lineEntity.setAnnotationId(annotatedLine.getAnnotationId());
        lineEntity.setParticipant(annotatedLine.getParticipant());
        lineEntity.setUtterance(annotatedLine.getUtterance());
        lineEntity.setStartTime(annotatedLine.getStartTime());
        lineEntity.setEndTime(annotatedLine.getEndTime());

        lineEntity.setNumWords(
                calculateNumWords(annotatedLine.getUtterance())
        );

        lineEntity.setTurnTime(
                calculateTurnTime(
                        annotatedLine.getStartTime(), annotatedLine.getEndTime()
                )
        );

        lineEntity.setInterveningTime(
                calculateInterveningTime(
                        priorEndTime, annotatedLine.getStartTime()
                )
        );

        return lineEntity;
    }

    public static class SortByStartTime implements Comparator<AnnotatedLine> {
        public int compare(AnnotatedLine u1, AnnotatedLine u2) {
            return (int)(u1.getStartTime() - u2.getStartTime());
        }
    }

    // Calculate the number of the number of words in a line
    private static int calculateNumWords(String text) {
        if (StringUtils.isEmpty(text)) {
            return 0;
        }

        // Remove the {} text before calculating number of words
        text = removeTaggedText(text, "{", "}");

        // Remove "()" empty/meaningless characters from word-count
        text = removeEmptyTaggedText(text, "(", ")");

        text = removePeriod(text);

        // Remove extra white-spaces since we are using " " as our count delimiter
        text = removeExtraneousWhiteSpace(text);

        int numWords = 0;
        if (text != null && text.length() > 0) {
            numWords = text.split(" ").length;
        }

        return numWords;
    }

    private static String removePeriod(String text) {
        return text.replaceAll("\\.+", "");
    }

    private static String removeExtraneousWhiteSpace(String text) {
        return text.replaceAll("\\s+", " ").trim();
    }

    // Remove unwanted 'tagged' text from a string
    private static String removeTaggedText(String text, String begTag, String endTag) {
        int begIndex = text.indexOf(begTag);
        while (begIndex != -1) {
            StringBuilder sb = new StringBuilder();
            int endIndex = text.indexOf(endTag, begIndex);
            if (endIndex != -1) {
                sb.append(text.substring(0, begIndex));
                sb.append(text.substring(endIndex + 1, text.length()));

                text = sb.toString();
                begIndex = text.indexOf(begTag);
            } else {
                break;
            }
        }
        return text;
    }

    // Remove unwanted/meaningless text such as "(())"
    private static String removeEmptyTaggedText(String text, String begTag, String endTag) {
        int begIndex = text.lastIndexOf(begTag);
        while (begIndex != -1) {
            StringBuilder sb = new StringBuilder();
            int endIndex = text.indexOf(endTag, begIndex);
            if (endIndex == begIndex + 1) {
                sb.append(text.substring(0, begIndex));
                sb.append(text.substring(endIndex + 1, text.length()));

                text = sb.toString();
                begIndex = text.lastIndexOf(begTag);
            } else {
                break;
            }
        }
        return text;
    }

    // The amount of time someone spoke, in milliseconds
    private static long calculateTurnTime(long startTime, long endTime) {
        return endTime - startTime;
    }

    // The amount of time between the end of one line and the beginning of the next
    //  In milliseconds
    private static long calculateInterveningTime(long priorEndTime, long startTime) {
        return startTime - priorEndTime;
    }

}
