package org.erachain.entities.request;

import org.erachain.repositories.DbUtils;
import org.erachain.utils.DateUtl;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {

    // db values
    private int id;
    private int accountId;
    private String runPeriod;  // hour day minute second
    private Timestamp lastRun;
    private String submitPeriod;  // hour day minute second
    private String offUnit;   // unit offset - hour day minute second
    private int offValue;     // offset from the beginning of period
    // end of db values
    private String paramValue;

    private String paramName;

    private Date submitDate;

    public boolean isCurrentDate() {
        return isCurrentDate;
    }


    private boolean isCurrentDate;

    private Map<String, String> params;

    public String getParamName() {
        return paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public String getSubmitPeriod() {
        return submitPeriod;
    }

    public void setSubmitPeriod(String submitPeriod) {
        this.submitPeriod = submitPeriod;
    }

    public String getOffUnit() {
        return offUnit;
    }

    public void setOffUnit(String offUnit) {
        this.offUnit = offUnit;
    }

    public int getOffValue() {
        return offValue;
    }

    public void setOffValue(int offValue) {
        this.offValue = offValue;
    }

    public String getRunPeriod() {
        return runPeriod;
    }

    public void setRunPeriod(String runPeriod) {
        this.runPeriod = runPeriod;
    }

    public int getLastReceived() {
        return lastReceived;
    }

    public void setLastReceived() {
        lastReceived = 0;
    }

    public void addlastReceived() {
        lastReceived++;
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

    public Timestamp getLastRun() {
        return lastRun;
    }

    public void setLastRun(Timestamp lastRun) {
        this.lastRun = lastRun;
    }

    public boolean checkTime(DateUtl dateUtl) {

        if (lastRun == null)
            return true;

        if (!isCurrentDate)
            return false;

        Date date = new Date(lastRun.getTime());
        String[] period = runPeriod.split("_");
        int value = 1;
        String periodRun = runPeriod;
        if (period.length > 1) {
            value = Integer.parseInt(period[0]);
            periodRun = period[1];
        }
        date = dateUtl.addUnit(date, periodRun, value);
        if (date.before(new Date()))
            return true;

        return false;
    }
    public Map<String, String> getParams(DbUtils dbUtils, DateUtl dateUtl) {
        if (params != null)
            return params;
        List<Params> pars = dbUtils.fetchData(Params.class, "Params", " requestId = " + id);
        SimpleDateFormat format = null;
        params = new HashMap<>();
        for (Params param : pars) {
            String value = param.getDefValue();
            boolean isCurrent = param.getCurValue() > 0 ? true : false;
            boolean isDate = "date".equalsIgnoreCase(param.getDataType());
            if (isDate) {
                paramName = param.getParamName();
                paramValue = value;
                isCurrentDate = isCurrent;
            }
            if (isCurrent && isDate) {
                format = new SimpleDateFormat(param.getFormat());
                if (offUnit != null && offValue != 0 && format != null) {
                    Date date = new Date();
                    date = dateUtl.addUnit(date, offUnit, - offValue);
                    date = dateUtl.getAlign(date, submitPeriod);
                    value = format.format(date);
                    paramValue = value;
                    submitDate = dateUtl.getFirst(date, submitPeriod);
                    submitDate = dateUtl.addUnit(submitDate, submitPeriod,  1);
                    submitDate = dateUtl.addUnit(submitDate, offUnit,  offValue - 1);
                }

            }
            params.put(param.getParamName(), value);
        }
        return params;
    }
    public int getActRequestId(DbUtils dbUtils, DateUtl dateUtl) throws Exception {
        if (params == null) {
            params = this.getParams(dbUtils, dateUtl);
            if (params == null)
                throw new Exception(" missing params for a request");
        }

        int actRequestId = dbUtils.getActRequestId(paramName, paramValue);
        return actRequestId;
    }
    public int setActRequestId(DbUtils dbUtils, DateUtl dateUtl) throws Exception {
        int actRequestId = 0;
        ActRequest actRequest = new ActRequest();
        actRequest.setRequestId(getId());
        actRequest.setPeriod(getRunPeriod());
        actRequest.setDateRun(new Timestamp(System.currentTimeMillis()));
        if (submitDate != null)
            actRequest.setDateSubmit(new Timestamp(submitDate.getTime()));
        try {
            actRequestId = dbUtils.setDbObj(actRequest, "ActRequest", true);
        } catch (SQLException e) {
            throw new SQLException(" create  ActRequest " + e.getMessage());
        }
        actRequest.setId(actRequestId);

        for (String name : params.keySet()) {
            ActParams actParams = new ActParams();
            actParams.setActRequestId(actRequestId);
            actParams.setParamName(name);
            actParams.setParamValue(params.get(name));
            int actParamsId = 0;
            try {
                actParamsId = dbUtils.setDbObj(actParams, "ActParams", true);
            } catch (SQLException e) {
                throw  new SQLException(" create act params " + e.getMessage());
            }
            actParams.setId(actParamsId);
        }
        return actRequestId;
    }

//

}
