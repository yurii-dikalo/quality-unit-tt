package org.example.model.datatype;

import java.time.LocalDate;
import org.example.model.Data;

public class Record extends Data {
    private LocalDate date;
    private int waitingTime;

    public Record() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }
}
