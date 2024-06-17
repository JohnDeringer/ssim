package com.sri.ssim.rest;

import javax.xml.bind.annotation.*;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "annotationsType", propOrder = {
    "annotationMatch",
    "annotation",
    "andor"
})
public class AnnotationsType {

    @XmlElement(name = "annotation-match", required = true)
    protected String annotationMatch;
    @XmlElement(required = true)
    protected String annotation;
    protected String andor;


    public String getAnnotationMatch() {
        return annotationMatch;
    }
    public void setAnnotationMatch(String value) {
        this.annotationMatch = value;
    }

    public String getAnnotation() {
        return annotation;
    }
    public void setAnnotation(String value) {
        this.annotation = value;
    }

    public String getAndor() {
        return andor;
    }
    public void setAndor(String value) {
        this.andor = value;
    }

    @Override
    public String toString() {
        return "AnnotationsType{" +
                "annotationMatch='" + annotationMatch + '\'' +
                ", annotation='" + annotation + '\'' +
                ", andor='" + andor + '\'' +
                '}';
    }
}
