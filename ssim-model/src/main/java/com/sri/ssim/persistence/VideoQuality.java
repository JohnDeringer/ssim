package com.sri.ssim.persistence;

//import com.sri.ssim.schema.VideoQualityEnum;

import javax.persistence.*;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/1/12
 */
@XmlRootElement
@Entity(name = "VideoQuality")
@Table(name = "VIDEO_QUALITY", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "VALUE"
    })
})
public class VideoQuality implements Serializable {

    protected String value;
    protected Long id;

    @Basic
    @Column(name = "VALUE", length = 100, nullable = false)
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Primary key
     * @return A auto-generated primary key of type Long
     */
    @Id
    @Column(name = "VIDEO_QUALITY_ID", scale = 0)
//    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

//    @Transient
//    public VideoQualityEnum getEnumValue() {
//        VideoQualityEnum enumValue = null;
//        String value = getValue();
//        if (value != null) {
//            enumValue = VideoQualityEnum.fromValue(value);
//        }
//        return enumValue;
//    }
//    public void setEnumValue(VideoQualityEnum enumValue) {
//        setValue(enumValue.value());
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VideoQuality that = (VideoQuality) o;

        if (value != null ? !value.equals(that.value) : that.value != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

}
