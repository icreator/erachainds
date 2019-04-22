package org.erachain.entities;


public class ActiveJob {

    private int jobId;

    private int accountId;
    private int requestId;

    public int getAccountId() {
        return accountId;
    }


    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    private JobState state = JobState.READY;

    public void setState(JobState state) {
        this.state = state;
    }

    public void setState(int level) {
        for (JobState state : JobState.values()) {
            if (state.getLevelJob() == level) {
                this.setState(state);
                break;
            }
        }
    }

    public JobState getState() {
        return state;
    }

    public ActiveJob(int jobId, int accountId) {
        this.jobId = jobId;
        this.accountId = accountId;
    }

}
