package com.example.applicationcongess.PlayLoad.request;


import lombok.Data;

import java.time.DayOfWeek;

@Data
public class DataValidation {
    private DayOfWeek day;
    private int validations;

    public DataValidation(DayOfWeek day, int validations) {
        this.day = day;
        this.validations = validations;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public int getValidations() {
        return validations;
    }

    public void setValidations(int validations) {
        this.validations = validations;
    }
}
