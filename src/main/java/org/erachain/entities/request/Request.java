package org.erachain.entities.request;

import org.erachain.repositories.AccountProc;
import org.erachain.repositories.DataClient;
import org.erachain.repositories.DbUtils;
import org.erachain.utils.DateUtl;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

public class Request {

    // db values
    private int id;
    private int accountId;
    private String runPeriod;  // hour day minute second
    private Timestamp lastRun;
    private String submitPeriod;  // hour day minute second
    private String offUnit;   // unit offset - hour day minute second
    private int offValue;     // offset from the beginning of period
    private String timezone;
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

    public boolean checkTime(DateUtl dateUtl, AccountProc accountProc, Logger logger) throws SQLException {
        if (lastRun == null) {
            return true;
        }
        logger.debug("Enter start calcing...");
        Date date = calcPlannedDate(dateUtl);
        if (new Date().getTime() - date.getTime() > dateUtl.getmilliSecondsRunPeriod(runPeriod)) {
            logger.debug("start!");
            logger.debug("localDateTime now = " + LocalDateTime.now().toString());
            return true;
        }
        return false;
    }

    private Date calcPlannedDate(DateUtl dateUtl) {
        if (lastRun == null) {
            lastRun = new Timestamp(new Date().getTime());
        }
        Date lastRunPlannedTime = new Date(lastRun.getTime());
        String unitRunPeriod = getUnitRunPeriod();
        Date reduceToLowerBoundDate = dateUtl.reduceToLowerBound(lastRunPlannedTime,
                Objects.requireNonNull(unitRunPeriod));
        if (offUnit != null && offValue != 0) {
            reduceToLowerBoundDate = dateUtl.addUnit(reduceToLowerBoundDate,
                    offUnit, offValue);
        }
        LocalDateTime localDateTime = LocalDateTime.ofInstant(reduceToLowerBoundDate.toInstant(),
                ZoneId.systemDefault());
        if (unitRunPeriod.equals("day") ||
                unitRunPeriod.equals("week") ||
                unitRunPeriod.equals("month")) {
            String shiftTimezone = convertStringZoneToShift(timezone);
            ZoneOffset shift = ZoneOffset.of(shiftTimezone);
            localDateTime = localDateTime.atOffset(ZoneOffset.of("+00:00"))
                    .withOffsetSameInstant(shift).toLocalDateTime();
        }
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }


    private String getUnitRunPeriod() {
        if (runPeriod == null) {
            return null;
        }
        String periodRun = runPeriod;
        String[] period = runPeriod.split("_");
        if (period.length > 1) {
            periodRun = period[1];
        }
        return periodRun;
    }

    public void recalcSubmitDate(DateUtl dateUtl) {
        Date date = new Date();
        submitDate = getSubmitDate(dateUtl, date, submitPeriod);
        if (offUnit != null && offValue != 0) {
            submitDate = dateUtl.addUnit(submitDate, offUnit, offValue - 1);
        }
    }

    public void setupParameterDate(DateUtl dateUtl) {
        paramName = "date";
//        paramName = "forUniquePurposesDate";
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        if (offUnit != null && offValue != 0) {
            date = dateUtl.addUnit(date, offUnit, -offValue);
        }
        date = dateUtl.getAlign(date, submitPeriod);
        paramValue = format.format(date);
    }

    public void putArtificiallyParameterDate() {
        params.put(paramName, paramValue);
    }

    public Map<String, String> getParamsAndRecalcParams(DbUtils dbUtils, DateUtl dateUtl) {
        if (params != null) {
            return params;
        }
        List<Params> pars = dbUtils.fetchData(Params.class, "Params", " requestId = " + id);
        params = new HashMap<>();
        for (Params param : pars) {
            String value = param.getDefValue();
            if (param.getDataType().equals("date")) {
                Date date = calcPlannedDate(dateUtl);
                SimpleDateFormat format = new SimpleDateFormat(param.getFormat());
                if (offUnit != null && offValue != 0) {
                    date = dateUtl.addUnit(date, offUnit, -offValue);
                }
                date = dateUtl.getAlign(date, submitPeriod);
                LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(),
                        ZoneId.systemDefault());
                String timezoneShift = convertStringZoneToShift(timezone);
//                if (timezoneShift.charAt(0) == '+') {
//                    timezoneShift = "-" + timezoneShift.substring(1);
//                } else if (timezoneShift.charAt(0) == '-') {
//                    timezoneShift = "+" + timezoneShift.substring(1);
//                } else {
//                    throw new IllegalArgumentException("Invalid setup timezone");
//                }
                ZoneOffset shift = ZoneOffset.of(timezoneShift);
                LocalDateTime now = LocalDateTime.now();
                ZoneId zone = ZoneId.systemDefault();
                ZoneOffset zoneOffSet = zone.getRules().getOffset(now);
                localDateTime = localDateTime.atOffset(zoneOffSet)
                        .withOffsetSameInstant(shift).toLocalDateTime();
                ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
                date = Date.from(zonedDateTime.toInstant());
                value = format.format(date);
            }
            params.put(param.getParamName(), value);
        }
        return params;
    }

    private String convertStringZoneToShift(String timezone) {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime helper = localDateTime.atZone(ZoneId.of(timezone));
        ZoneOffset helperOffset = helper.getOffset();
        return helperOffset.getId().replaceAll("Z", "+00:00");
    }


    private Date getSubmitDate(DateUtl dateUtl, Date date, String submitPeriod) {
        String[] period = submitPeriod.split("_");
        int value = 1;
        String periodRun = submitPeriod;
        if (period.length > 1) {
            value = Integer.parseInt(period[0]);
            periodRun = period[1];
        }
        submitDate = dateUtl.addUnit(date, periodRun, value);
        return submitDate;
    }

    public int getActRequestId(DataClient dataClient, DbUtils dbUtils, DateUtl dateUtl) throws Exception {
        recalcSubmitDate(dateUtl);
        setupParameterDate(dateUtl);
        params = getParamsAndRecalcParams(dbUtils, dateUtl);
        putArtificiallyParameterDate();
        return dataClient.getActRequestId(id, paramName, paramValue);
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
            throw new SQLException("Create ActRequest " + e.getMessage());
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
                throw new SQLException("Create act params " + e.getMessage());
            }
            actParams.setId(actParamsId);
        }
        return actRequestId;
    }


}
