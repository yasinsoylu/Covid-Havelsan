package com.havelsan.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "covid_news")
public class CovidNews {

    @Id
    private String id;

    @Indexed
    private String city;

    @Indexed
    private String date;

    private Integer caseCount;
    private Integer deathCount;
    private Integer dischargesCount;
    private String theNews;

    public CovidNews() {
        // Necessary for MongoDB
    }

    public CovidNews(String date, String city, Integer caseCount, Integer deathCount, Integer dischargesCount, String theNews) {
        this.date = date;
        this.city = city;
        this.caseCount = caseCount;
        this.deathCount = deathCount;
        this.dischargesCount = dischargesCount;
        this.theNews = theNews;
    }

    @Override
    public String toString() {
        return "CovidNews{" +
                "id='" + id + '\'' +
                ", city='" + city + '\'' +
                ", date='" + date + '\'' +
                ", caseCount=" + caseCount +
                ", deathCount=" + deathCount +
                ", dischargesCount=" + dischargesCount +
                ", theNews='" + theNews + '\'' +
                '}';
    }

    public boolean isValid() {
        if (this.date != null && this.city != null && this.caseCount != null && this.deathCount != null
                && this.dischargesCount != null && this.theNews != null) {
            return true;
        }
        return false;
    }

    // Getter Setter Methods.
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public Integer getCaseCount() {
        return caseCount;
    }
    public void setCaseCount(Integer caseCount) {
        this.caseCount = caseCount;
    }

    public Integer getDeathCount() {
        return deathCount;
    }
    public void setDeathCount(Integer deathCount) {
        this.deathCount = deathCount;
    }

    public Integer getDischargesCount() {
        return dischargesCount;
    }
    public void setDischargesCount(Integer dischargesCount) {
        this.dischargesCount = dischargesCount;
    }

    public String getTheNews() {
        return theNews;
    }
    public void setTheNews(String theNews) {
        this.theNews = theNews;
    }
}
