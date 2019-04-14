package org.erachain.entities;


public class ActiveJob {

    private int jobId;
    private int records;
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

    public ActiveJob(int jobId, int records) {
        this.jobId = jobId;
        this.records = records;
    }

}
