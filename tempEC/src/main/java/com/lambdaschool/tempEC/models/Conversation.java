package com.lambdaschool.tempEC.models;

import javax.persistence.*;

@Entity
@Table(name = "conversation")
public class Conversation extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long conversationid;

    private String survivornumber;
    private String ffname;
    private String ffnumber;

    private String school;

    public Conversation() {
    }

    public Conversation(String survivornumber, String ffname, String ffnumber) {
        this.school = "michigan";
        this.survivornumber = survivornumber;
        this.ffname = ffname;
        this.ffnumber = ffnumber;
    }

    public Conversation(String survivornumber, String ffname, String ffnumber, String school) {
        this.school = school;
        this.survivornumber = survivornumber;
        this.ffname = ffname;
        this.ffnumber = ffnumber;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public long getConversationid() {
        return conversationid;
    }

    public String getSurvivornumber() {
        return survivornumber;
    }

    public void setSurvivornumber(String survivornumber) {
        this.survivornumber = survivornumber;
    }

    public String getFfname() {
        return ffname;
    }

    public void setFfname(String ffname) {
        this.ffname = ffname;
    }

    public String getFfnumber() {
        return ffnumber;
    }

    public void setFfnumber(String ffnumber) {
        this.ffnumber = ffnumber;
    }

}
