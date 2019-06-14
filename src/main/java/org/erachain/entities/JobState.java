package org.erachain.entities;

public enum JobState {
    READY(0),    // 0 - ready to start, ask Data  from client
    STARTED(1),  // 1 - started, Data received from client
    DATA_SUB(2), // 2 - Data submitted to Era, signature acquired
    DATA_ACC(3), // 3 - Data accepted by ERa, block and transaction  acquired
    INFO_SEND(4),// 4 - Information send to client
    INFO_ACC(5); // 5 - client acknowledged acceptation

    private final int levelJob;

    private JobState(int levelJob) {
        this.levelJob = levelJob;
    }
    public int getLevelJob() {
        return this.levelJob;
    }
}
