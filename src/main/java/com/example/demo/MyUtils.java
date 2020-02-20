package com.example.demo;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.regex.Pattern;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

public class MyUtils {
	public static Timestamp getTodayWithoutTime() {
		Timestamp res = null;
		try {
		    Date t1 = new Date();	
			t1.setHours(0);
			t1.setMinutes(0);
			t1.setSeconds(0);		    
			res = new Timestamp(t1.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public static Timestamp getTomorrowWithoutTime() {
		Timestamp res = null;
		try {
		    Date t1 = new Date();	
			t1.setHours(0);
			t1.setMinutes(0);
			t1.setSeconds(0);		    
			res = new Timestamp(t1.getTime());
			res = getNextDay(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public static MyJSONWrapper getJSONWraperFromExcep(ConstraintViolationException e) {
		MyJSONWrapper res = new MyJSONWrapper("Error",null);		
		Set<ConstraintViolation<?>> errors = e.getConstraintViolations();
		String err = "";
		for(ConstraintViolation cv : errors) {
			err +=  cv.getPropertyPath() + " :: " + cv.getMessage() + ";";
		}
		res.setMessage(err);
		return res;
	}


	public static Timestamp getTimestamp(String dateStr) {
		Timestamp res = null;
		try {
		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		    Date dateTemp = dateFormat.parse(dateStr);				
			res = new Timestamp(dateTemp.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	public static Timestamp getNextDay(Timestamp t1) {
		Timestamp res = null;
		try {
			res = new Timestamp(t1.getTime() + 24*60*60*1000);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	public static Timestamp getDateOnly(Timestamp t1) {
		try {
			t1.setHours(0);
			t1.setMinutes(0);
			t1.setSeconds(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t1;
	}

    public static String validatePass(String pass) {
        String res = null;
        try {
            if (Pattern.matches(".*[^\\w\\s].*", pass) == false) {
                res = "Special Symbol is required!";
            }
            if (pass.length() < 8) {
                res = "length is too short, min length 8!";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    
    public static String validateName(String name) {
        String res = null;
        try {
            if (Pattern.matches("^[a-z,A-Z, ]+$", name) == false) {
                res = "Invalid Name!";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }



}
