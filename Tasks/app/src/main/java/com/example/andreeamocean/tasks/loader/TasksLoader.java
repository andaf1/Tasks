package com.example.andreeamocean.tasks.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

import com.example.andreeamocean.tasks.model.Tasks;
import com.example.andreeamocean.tasks.persistence.TasksContentProvider;
import com.example.andreeamocean.tasks.persistence.TasksContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by andreeamocean on 8/1/15.
 */
public class TasksLoader extends AsyncTaskLoader<List<Tasks>> {

    private Context mContext;
    /**
     * List of cached tasks.
     */
    private List<Tasks> mTasks;
    /**
     * The current filter the user has provided.
     */
    private String mCurFilter;


    public TasksLoader(Context context, String mCurFilter) {
        super(context);
        this.mContext = context;
        this.mCurFilter = mCurFilter;
    }

    @Override

    public List<Tasks> loadInBackground() {
        String select = mCurFilter != null ? TasksContract.Tasks.COLUMN_NAME_TITLE + " like '%" + mCurFilter + "%'" : null;
        List<Tasks> tasks = new ArrayList<Tasks>();
        Cursor cursor = mContext.getContentResolver().query(TasksContract.Tasks.CONTENT_URI, TasksContentProvider.READ_TASKS_PROJECTION, select, null, null);
        Calendar calendar = Calendar.getInstance();
        Date dueDate, reminder;
        Tasks task;
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                task = new Tasks();
                task.setId(cursor.getLong(0));
                task.setTitle(cursor.getString(1));
                task.setDescription(cursor.getString(2));
                calendar.setTimeInMillis(cursor.getLong(3));
                dueDate = calendar.getTime();
                task.setDueDate(dueDate);
                if(cursor.getLong(4) > 0) {
                    calendar.setTimeInMillis(cursor.getLong(4));
                    reminder = calendar.getTime();
                    task.setReminder(reminder);
                }
                task.setPriority(cursor.getString(5));
                tasks.add(task);
            }
        }
        return tasks;
    }

    @Override
    public void deliverResult(List<Tasks> data) {
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mTasks != null) {
            deliverResult(mTasks);
        }
        if (takeContentChanged() || mTasks == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
        mTasks = null;
    }


}
