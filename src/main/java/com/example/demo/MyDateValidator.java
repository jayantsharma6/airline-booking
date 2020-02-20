package com.example.demo;

import java.sql.Timestamp;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Future;

import org.springframework.stereotype.Component;

@Component
public class MyDateValidator implements ConstraintValidator<Future, Timestamp> {

    @Override
    public void initialize(Future future) {
    }

    @Override
    public boolean isValid(Timestamp localDate, ConstraintValidatorContext constraintValidatorContext) {
    	Timestamp today = new Timestamp(new Date().getTime());
        return localDate.after(today);
    }
}