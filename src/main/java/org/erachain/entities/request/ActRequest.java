package org.erachain.entities.request;

import java.sql.Timestamp;

public class ActRequest {


    private int id;
    private int requestId;
    private String period;  // hour day minute second
    private Timestamp DateRun;

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
