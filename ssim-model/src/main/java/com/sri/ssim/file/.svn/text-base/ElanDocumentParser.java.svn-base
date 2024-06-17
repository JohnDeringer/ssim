package com.sri.ssim.file;

import com.sri.ssim.schema.*;

import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.XMLGregorianCalendar;

import java.util.*;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 9/11/12
 */
public class ElanDocumentParser implements DocumentParser {

    private List<AnnotatedLine> annotatedLines = new ArrayList<AnnotatedLine>();
    private Map<String, Long> timeSlotMap = new HashMap<String, Long>();

    private Date documentDate;
    private String mediaUrl;
    private String mediaType;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ElanDocumentParser() {
    }

    public ElanDocumentParser(ANNOTATIONDOCUMENT annotationDocument) {
        parseDocument(annotationDocument);
    }

    public void parseDocument(ANNOTATIONDOCUMENT annotationDocument) {

        if (annotationDocument != null) {
            setDocumentDate(annotationDocument.getDATE());

            processHeader(annotationDocument.getHEADER());

            processTimeOrder(annotationDocument.getTIMEORDER());

            processTiers(annotationDocument.getTIER());
        } else {
            logger.error("Unable to parse document [" + annotationDocument + "]");
        }
    }

    public Date getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar != null) {
            this.documentDate =
                    xmlGregorianCalendar.toGregorianCalendar().getTime();
        }
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public List<AnnotatedLine> getAnnotatedLines() {
        return annotatedLines;
    }

    private void processHeader(HeadType header) {
        for (HeadType.MEDIADESCRIPTOR mediaDescriptor : header.getMEDIADESCRIPTOR()) {
            setMediaUrl(mediaDescriptor.getRELATIVEMEDIAURL());
            setMediaType(mediaDescriptor.getMIMETYPE());
        }
    }

    private void processTimeOrder(TimeType timeType) {
        for (TimeType.TIMESLOT timeslot : timeType.getTIMESLOT()) {
            timeSlotMap.put(timeslot.getTIMESLOTID(), timeslot.getTIMEVALUE());
        }
    }

    private void processTiers(List<TierType> tiers) {
        for (TierType tier : tiers) {
            for (AnnotationType annotation : tier.getANNOTATION()) {
                AlignableType alignableType = annotation.getALIGNABLEANNOTATION();
                RefAnnoType refAnnoType = annotation.getREFANNOTATION();
                if (alignableType != null) {
                    // New Line
                    AnnotatedLine annotatedLine = new AnnotatedLine();
                    // Participant
                    String participant = tier.getPARTICIPANT();
                    if (participant == null) {
                        // Sometimes the participant is placed in the tierId
                        participant = tier.getTIERID();
                    }
                    annotatedLine.setParticipant(participant);
                    // AnnotationId
                    annotatedLine.setAnnotationId(alignableType.getANNOTATIONID());
                    // Start Time
                    Object timeSlotRef1 = alignableType.getTIMESLOTREF1();
                    if (timeSlotRef1 != null) {
                        annotatedLine.setStartTime(
                                getTimeSlot(timeSlotRef1)
                        );
                    }
                    // End Time
                    Object timeSlotRef2 = alignableType.getTIMESLOTREF2();
                    if (timeSlotRef2 != null) {
                        annotatedLine.setEndTime(
                                getTimeSlot(timeSlotRef2)
                        );
                    }
                    // Utterance
                    annotatedLine.setUtterance(alignableType.getANNOTATIONVALUE());
                    annotatedLines.add(annotatedLine);
                } else if (refAnnoType != null) {
                    // TODO Annotation ??? - The ANNOTATIONID is a reference to a parent
                    //   ANNOTATIONVALUE
/*
                    // New Line
                    Line utterance = new Line();
                    // Participant
                    utterance.setParticipant(tier.getPARTICIPANT());
                    // AnnotationId
                    utterance.setAnnotationId(refAnnoType.getANNOTATIONID());
                    // Start Time
                    Object timeSlotRef1 = refAnnoType.getTIMESLOTREF1();
                    if (timeSlotRef1 != null) {
                        utterance.setStartTime(
                                getTimeSlot(timeSlotRef1)
                        );
                    }
                    // End Time
                    Object timeSlotRef2 = refAnnoType.getTIMESLOTREF2();
                    if (timeSlotRef2 != null) {
                        utterance.setEndTime(
                                getTimeSlot(timeSlotRef2)
                        );
                    }
                    // Text
                    utterance.setText(refAnnoType.getANNOTATIONVALUE());
                    lines.add(utterance);
 */
                }
            }
        }
    }

    private Long getTimeSlot(@NotNull Object timeSlotRefObj) {
        TimeType.TIMESLOT timeslotRef = (TimeType.TIMESLOT) timeSlotRefObj;
        return timeSlotMap.get(timeslotRef.getTIMESLOTID());
    }

}
