package org.erachain.entities.request;

import org.apache.commons.lang3.time.DateUtils;
import org.erachain.repositories.DbUtils;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Request {

    private int id;
    private int accountId;
    private String period;  // hour day minute second
    private Timestamp lastRun;

    public int getLastReceived() {
        return lastReceived;
    }

    public void setLastReceived() {
        lastReceived = 0;
    }

    public void addlastReceived() {
        lastReceived ++;
    }

    private int lastReceived = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Timestamp getLastRun() {
        return lastRun;
    }

    public void setLastRun(Timestamp lastRun) {
        this.lastRun = lastRun;
    }

    public boolean checkTime() {
        if (lastRun == null)
            return true;

        Date date = new Date(lastRun.getTime());
        switch (period) {
            case ("month") :
                date = DateUtils.addMonths(date, 1);
                break;
            case ("week") :
                date = DateUtils.addWeeks(date, 1);
                break;
            case ("day") :
                date = DateUtils.addDays(date, 1);
                break;
            case ("hour") :
                date = DateUtils.addHours(date, 1);
                 break;
            case ("minute") :
                date = DateUtils.addMinutes(date, 1);
                break;
        }
        if (date.before(new Date()))
            return true;

        return false;
    }
    public void setParams(Map<String, String> params, DbUtils dbUtils) {
        List<Params> pars = dbUtils.fetchData(Params.class, "Params" , " requestId = " + id);
        pars.forEach(param -> {
            String value = param.getDefValue();
            if (param.isCurrent() &&
                 "date".equalsIgnoreCase(param.getDateType())) {
                SimpleDateFormat format = new SimpleDateFormat(param.getFormat());

                Date date = new Date();
                value = format.format( date);
            }
            params.put(param.getParamName(), value);
        });
    }
    public int setResult(Map<String, String> params, DbUtils dbUtils) throws SQLException {
        ActRequest actRequest = new ActRequest();
        actRequest.setRequestId(id);
        actRequest.setPeriod(period);
        actRequest.setDateRun(new Timestamp(System.currentTimeMillis()));
        int actRequestId = dbUtils.setDbObj(actRequest, "ActRequest", true);
        actRequest.setId(actRequestId);
        params.keySet().stream().forEach(name -> {
            ActParams actParams = new ActParams();
            actParams.setActRequestId(actRequestId);
            actParams.setParamName(name);
            actParams.setParamValue(params.get(name));
            int actParamsId = 0;
            try {
                actParamsId = dbUtils.setDbObj(actParams, "ActParams", true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            actParams.setId(actParamsId);
        });
        return actRequestId;
    }

}
