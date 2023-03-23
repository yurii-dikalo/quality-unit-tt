package org.example.model.datatype;

import java.time.LocalDate;
import org.example.model.Data;

public class Query extends Data {
    private LocalDate fromDate;
    private LocalDate toDate;

    public Query() {
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }
}
