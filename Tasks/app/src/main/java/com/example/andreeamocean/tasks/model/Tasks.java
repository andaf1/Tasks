package com.example.andreeamocean.tasks.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by andreea.mocean on 7/4/2015.
 */
public class Tasks implements Serializable{
    /**
     * Task id autoincrement.
     */
    private long id;
    /**
     * Name of the task.
     */
    private String title;
    /**
     * Description of the task.
     */
    private String description;
    /**
     *  The date on which the task falls due.
     */
    private Date dueDate;
    /**
     * The date to reminder the task.
     */
    private Date reminder;
    /**
     * Task's priority.
     */
    private String priority;

    public Tasks(String title, String description, Date dueDate, Date reminder, String priority) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.reminder = reminder;
        this.priority = priority;
    }

    public Tasks() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getReminder() {
        return reminder;
    }

    public void setReminder(Date reminder) {
        this.reminder = reminder;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
