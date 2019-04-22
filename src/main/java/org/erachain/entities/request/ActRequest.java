package org.erachain.entities.request;

import java.sql.Timestamp;

public class ActRequest {

    // db
    private int id;
    private int requestId;
    private String period;  // hour day minute second
    private Timestamp DateRun;
    private Timestamp DateSubmit;
    // db end

    public Timestamp getDateSubmit() {
        return DateSubmit;
    }

    public void setDateSubmit(Timestamp dateSubmit) {
        DateSubmit = dateSubmit;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Timestamp getDateRun() {
        return DateRun;
    }

    public void setDateRun(Timestamp dateRun) {
        DateRun = dateRun;
    }

}
