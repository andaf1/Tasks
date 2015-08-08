package com.example.andreeamocean.tasks.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by andreeamocean on 8/2/15.
 */
public class CalendarEventHelper {

    /**
     * The context of current state.
     */
    private Context context;
    /**
     * The title of the event.
     */
    private String eventTitle;
    /**
     * The description of the event.
     */
    private String eventDescription;
    /**
     * Where the event takes place.
     */
    private String eventLocation;
    /**
     * The time the event starts.
     */
    private Date dateTimeStart;
    /**
     * The time the event end.
     */
    private Date dateTimeEnd;
    /**
     * The reminder date of the event.
     */
    private Date reminderDate;
    /**
     * The event triggers a reminder alarm - 0 false, 1 true.
     */
    private int hasAlarm;

    public CalendarEventHelper(Context context, String eventTitle, String eventDescription, String eventLocation, Date dateTimeStart, Date dateTimeEnd, Date reminderDate, int hasAlarm) {
        this.context = context;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventLocation = eventLocation;
        this.dateTimeStart = dateTimeStart;
        this.dateTimeEnd = dateTimeEnd;
        this.reminderDate = reminderDate;
        this.hasAlarm = hasAlarm;
    }

    /**
     * Inserts a new event.
     *
     * @return The row ID of the inserted row.
     */
    public long addEvent() {
        TimeZone timeZone = TimeZone.getDefault();

        ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.CALENDAR_ID, 1);
        event.put(CalendarContract.Events.TITLE, eventTitle);
        event.put(CalendarContract.Events.DESCRIPTION, eventDescription);
        event.put(CalendarContract.Events.EVENT_LOCATION, eventLocation);
        event.put(CalendarContract.Events.DTSTART, dateTimeStart.getTime());
        event.put(CalendarContract.Events.DTEND, dateTimeEnd.getTime());
        event.put(CalendarContract.Events.STATUS, 1);
        event.put(CalendarContract.Events.HAS_ALARM, hasAlarm);
        event.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

        Uri insertEventUri = context.getContentResolver().insert(CalendarContract.Events.CONTENT_URI, event);
        long eventID = Long.parseLong(insertEventUri.getLastPathSegment());
        if (hasAlarm == 1) {
            setReminder(eventID);
        }
        return eventID;
    }

    /**
     * Updates an event.
     *
     * @param eventID the id of the event.
     * @return the number of rows updated.
     */
    public int updateEvent(long eventID) {

        ContentValues event = new ContentValues();

        // The new values for the event
        event.put(CalendarContract.Events.CALENDAR_ID, 1);
        event.put(CalendarContract.Events.TITLE, eventTitle);
        event.put(CalendarContract.Events.DESCRIPTION, eventDescription);
        event.put(CalendarContract.Events.EVENT_LOCATION, eventLocation);
        event.put(CalendarContract.Events.DTSTART, dateTimeStart.getTime());
        event.put(CalendarContract.Events.DTEND, dateTimeEnd.getTime());
        event.put(CalendarContract.Events.STATUS, 1);
        event.put(CalendarContract.Events.HAS_ALARM, hasAlarm);

        Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        int rows = context.getContentResolver().update(updateUri, event, null, null);
        return rows;
    }

    /**
     * Deletes an event
     *
     * @param eventID the id of the event.
     * @return the number of rows deleleted.
     */
    public int deleteEvent(long eventID) {
        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        int rows = context.getContentResolver().delete(deleteUri, null, null);
        return rows;
    }

    /**
     * adds a reminder to an event.
     *
     * @param eventID the id of the event.
     */
    private void setReminder(long eventID) {

        long difference = dateTimeEnd.after(reminderDate) ? dateTimeEnd.getTime() - reminderDate.getTime() : 0;
        int days = (int) (difference / (1000 * 60 * 60 * 24));
        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);

        ContentValues reminders = new ContentValues();
        reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
        reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        reminders.put(CalendarContract.Reminders.MINUTES, min);

        context.getContentResolver().insert(CalendarContract.Reminders.CONTENT_URI, reminders);
    }

    /**
     * Deletes reminder.
     *
     * @param reminderID the id of the reminder.
     */
    public void deleteReminder(long reminderID) {
        Uri reminderUri = Uri.withAppendedPath(CalendarContract.Reminders.CONTENT_URI, String.valueOf(reminderID));
        context.getContentResolver().delete(reminderUri, null, null);
    }

    /**
     * Updates reminder.
     *
     * @param reminderID the id of the reminder.
     */
    public void updateReminder(long reminderID) {
        ContentValues reminders = new ContentValues();
        reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        reminders.put(CalendarContract.Reminders.MINUTES, 0);

        Uri updateReminderUri = Uri.withAppendedPath(CalendarContract.Reminders.CONTENT_URI, String.valueOf(reminderID));
        context.getContentResolver().update(updateReminderUri, reminders, null, null);
    }

}
