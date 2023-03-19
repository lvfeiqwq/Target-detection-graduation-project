package com.lvfei.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 *
 * </p>
 *
 * @author Lvfei
 * @since 2023-03-04
 */
public class Incident implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer category;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate incidentDate;

    private String incidentPlace;

    private String detail;

    private String imgUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }
    public LocalDate getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(LocalDate incidentDate) {
        this.incidentDate = incidentDate;
    }
    public String getIncidentPlace() {
        return incidentPlace;
    }

    public void setIncidentPlace(String incidentPlace) {
        this.incidentPlace = incidentPlace;
    }
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "Incident{" +
                "id=" + id +
                ", category=" + category +
                ", incidentDate=" + incidentDate +
                ", incidentPlace=" + incidentPlace +
                ", detail=" + detail +
                ", imgUrl=" + imgUrl +
                "}";
    }
}
