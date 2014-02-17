package com.example.place_its;


public class Reminders {
	//private static final String LOGCAT = "REMINDER";
    private String msg;
    private long id;
    private int year, month, day;

    /**
     * Empty Constructor.
     */
    public Reminders() {}

    public Reminders( String message ) {
    	this.msg = message;
    }
    
    public Reminders( String message, int year, int month, int day ) {
    	this.msg = message;
    	this.year = year;
    	this.month = month;
    	this.day = day;
    }

    public void setId( long id ) {
    	this.id = id;
    }
    public long getId() {
    	return this.id;
    }
  
    /**
     * Set the reminder message.
     * @param msgDetail: Set a new message for a reminder.
     */
    public void setMessage( String msgDetail ) {
    	this.msg = msgDetail;
    }

    /**
     * Get reminder message.
     * @return the message of a reminder.
     */
    public String getMessage() {
    	//Log.d( LOGCAT, "get Reminder MESSAGE " + this.msgDetail );
        return this.msg;
    }
    
    public int getYear() {
    	return this.year;
    }
    public void setYear( int year ) {
    	this.year = year;
    }
    public int getMonth() {
    	return this.month;
    }
    public void setMonth( int month ) {
    	this.month = month;
    }
    public void setDay( int day ) {
    	this.day = day;
    }
    public int getDay() {
    	return this.day;
    }

    /**
     * Write a reminder to a string.
     * @return Information String.
     */
    public String toString() {
        return this.msg;
    }
}