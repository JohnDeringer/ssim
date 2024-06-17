package com.sri.ssim.rest;

import javax.xml.bind.annotation.*;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 12/14/12
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public enum FormatType {

    @XmlEnumValue("original-video")
    ORIGINAL_VIDEO("original-video"),
    @XmlEnumValue("converted-video")
    CONVERTED_VIDEO("converted-video"),
    @XmlEnumValue("video-audio")
    VIDEO_AUDIO("video-audio"),
    @XmlEnumValue("alternate-audio")
    ALTERNATIVE_AUDIO("alternate-audio"),
    @XmlEnumValue("stimulated-recall-transcript")
    STIMULATED_RECALL_TRANSCRIPT("stimulated-recall-transcript"),
    @XmlEnumValue("sociometric-badge")
    SOCIOMETRIC_BADGE("sociometric-badge"),
    @XmlEnumValue("elan-transcript-orthographic")
    ELAN_TRANSCRIPT_ORTHOGRAPHIC("elan-transcript-orthographic"),
    @XmlEnumValue("traditional-transcript-orthographic")
    TRADITIONAL_TRANSCRIPT_ORTHOGRAPHIC("traditional-transcript-orthographic"),
    @XmlEnumValue("tab-delimited-transcript-orthographic")
    TAB_DELIMITED_TRANSCRIPT_ORTHOGRAPHIC("tab-delimited-transcript-orthographic"),
    @XmlEnumValue("elan-transcript-ca")
    ELAN_TRANSCRIPT_CA("elan-transcript-ca"),
    @XmlEnumValue("traditional-transcript-ca")
    TRADITIONAL_TRANSCRIPT_CA("traditional-transcript-ca"),
    @XmlEnumValue("tab-delimited-transcript-ca")
    TAB_DELIMITED_TRANSCRIPT_CA("tab-delimited-transcript-ca"),
    @XmlEnumValue("ethnographic")
    ETHNOGRAPHIC("ethnographic"),
    @XmlEnumValue("cta")
    COGNITIVE_TASK_ANALYSIS("cta"),
    @XmlEnumValue("other-transcript-ca")
    OTHER_TRANSCRIPT_CA("other-transcript-ca");

    private final String value;

    FormatType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FormatType fromValue(String v) {
        for (FormatType c: FormatType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
