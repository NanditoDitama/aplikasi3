package com.example.laporan2;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class ApprovalRequest implements Serializable {
    private String id;
    private String userId;
    private String userName;
    private String type; // "add" atau "edit"
    private Map<String, Object> reportData;
    private Date timestamp;
    private String status; // "pending", "approved", "rejected"

    // Konstruktor kosong untuk Firestore
    public ApprovalRequest() {}

    // Konstruktor lengkap
    public ApprovalRequest(String id, String userId, String userName, String type,
                           Map<String, Object> reportData, Date timestamp, String status) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.type = type;
        this.reportData = reportData;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getter dan Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getReportData() {
        return reportData;
    }

    public void setReportData(Map<String, Object> reportData) {
        this.reportData = reportData;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
