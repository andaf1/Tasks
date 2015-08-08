package com.example.andreeamocean.tasks.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.example.andreeamocean.tasks.R;
import com.example.andreeamocean.tasks.activity.AddTaskActivity;
import com.example.andreeamocean.tasks.activity.MapActivity;
import com.example.andreeamocean.tasks.activity.TaskDescriptionActivity;
import com.example.andreeamocean.tasks.model.Tasks;
import com.example.andreeamocean.tasks.persistence.TasksContract;
import com.example.andreeamocean.tasks.utils.CalendarEventHelper;
import com.example.andreeamocean.tasks.utils.NotificationEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by andreeamocean on 7/25/15.
 */
public class AddTaskFragment extends Fragment implements View.OnClickListener {

    public static final String CURRENT_TASK = "com.example.andreeamocean.tasks.CURRENT_TASK";
    private final int CUSTOMISE_REMINDER = 2;
    private final int REMINDER_ON_DUE_DATE = 1;
    private SimpleDateFormat mDateFormat;
    /**
     * {@link EditText} for the task title.
     */
    private EditText title;
    /**
     * {@link EditText} for the task description.
     */
    private EditText description;
    /**
     * {@link EditText} for the task due date.
     */
    private EditText dueDate;
    /**
     * {@link EditText} for the task due time.
     */
    private EditText dueTime;
    /**
     * {@link Spinner} for the task reminder.
     */
    private Spinner reminder;
    /**
     * {@link EditText} for the task custom reminder date.
     */
    private EditText customReminderDate;
    /**
     * {@link EditText} for the task custom reminder time.
     */
    private EditText customReminderTime;
    /**
     * {@link Spinner} for the task priority.
     */
    private Spinner priority;


    public static AddTaskFragment newInstance(Tasks task) {
        AddTaskFragment f = new AddTaskFragment();
        Bundle args = new Bundle();
        args.putSerializable(CURRENT_TASK, task);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.add_task, container, false);
        mDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        initUI(view);
        setUIArgs();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_add_task, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save:
                onTaskSaved();
        }
        return super.onOptionsItemSelected(item);
    }

    public Tasks getShownTask() {
        return getArguments() != null ? (Tasks) getArguments().getSerializable(CURRENT_TASK) : null;
    }

    private void initUI(View view) {
        Calendar calendar = Calendar.getInstance();
        title = (EditText) view.findViewById(R.id.add_title);
        description = (EditText) view.findViewById(R.id.add_description);
        // due date
        dueDate = (EditText) view.findViewById(R.id.add_due_date);
        dueDate.setText(mDateFormat.format(calendar.getTime()));
        dueDate.setOnClickListener(this);
        dueTime = (EditText) view.findViewById(R.id.add_due_time);
        dueTime.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
        dueTime.setOnClickListener(this);
        // reminder date
        customReminderDate = (EditText) view.findViewById(R.id.add_reminder_date);
        customReminderDate.setText(mDateFormat.format(calendar.getTime()));
        customReminderDate.setOnClickListener(this);
        customReminderTime = (EditText) view.findViewById(R.id.add_reminder_time);
        customReminderTime.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
        customReminderTime.setOnClickListener(this);

        priority = (Spinner) view.findViewById(R.id.add_priority);
        reminder = (Spinner) view.findViewById(R.id.add_reminder);
        reminder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == CUSTOMISE_REMINDER) {
                    customReminderTime.setVisibility(View.VISIBLE);
                    customReminderDate.setVisibility(View.VISIBLE);
                } else {
                    customReminderTime.setVisibility(View.GONE);
                    customReminderDate.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ImageButton button = (ImageButton) view.findViewById(R.id.find_location);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), MapActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setUIArgs() {
        Tasks task = getShownTask();
        if (task != null) {
            title.setText(task.getTitle());
            description.setText(task.getDescription());
            String[] priorityOptions = getResources().getStringArray(R.array.priority);
            priority.setSelection(Arrays.asList(priorityOptions).indexOf(task.getPriority()));
            String dueDateTask = mDateFormat.format(task.getDueDate());
            dueDate.setText(dueDateTask);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String dueTimeTask = timeFormat.format(task.getDueDate());
            dueTime.setText(dueTimeTask);
            if (task.getReminder() == null) {
                reminder.setSelection(0);
            } else if (task.getReminder().equals(task.getDueDate())) {
                reminder.setSelection(1);
            } else {
                reminder.setSelection(2);
                String reminderDateTask = mDateFormat.format(task.getReminder());
                String reminderTimeTask = timeFormat.format(task.getReminder());
                customReminderDate.setText(reminderDateTask);
                customReminderTime.setText(reminderTimeTask);
            }
        }
    }

    private void setDateField(final View editText) {
        Calendar newCalendar = Calendar.getInstance();
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                ((EditText) editText).setText(mDateFormat.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setTimeField(final View editText) {
        Calendar newCalendar = Calendar.getInstance();
        new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                ((EditText) editText).setText(selectedHour + ":" + selectedMinute);
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true).show();
    }

    private void onTaskSaved() {
        Tasks task = getShownTask();
        boolean updateTask = true;
        if (task == null) {
            task = new Tasks();
            updateTask = false;
        }
        task.setTitle(title.getText().toString());
        task.setDescription(description.getText().toString());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date dueDateTask = format.parse(dueDate.getText().toString() + " " + dueTime.getText().toString());
            task.setDueDate(dueDateTask);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (reminder.getFirstVisiblePosition() == CUSTOMISE_REMINDER) {
            try {
                Date reminderDateTask = format.parse(customReminderDate.getText().toString() + " " + customReminderTime.getText().toString());
                task.setReminder(reminderDateTask);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (reminder.getFirstVisiblePosition() == REMINDER_ON_DUE_DATE) {
            task.setReminder(task.getDueDate());
        }
        task.setPriority(priority.getSelectedItem().toString());

        ContentValues cv = new ContentValues();
        cv.put(TasksContract.Tasks.COLUMN_NAME_TITLE, task.getTitle());
        cv.put(TasksContract.Tasks.COLUMN_NAME_DESCRIPTION, task.getDescription());
        cv.put(TasksContract.Tasks.COLUMN_NAME_DUE_DATE, task.getDueDate().getTime());
        if (task.getReminder() != null) {
            cv.put(TasksContract.Tasks.COLUMN_NAME_REMINDER_DATE, task.getReminder().getTime());
        } else {
            cv.putNull(TasksContract.Tasks.COLUMN_NAME_REMINDER_DATE);
        }
        cv.put(TasksContract.Tasks.COLUMN_NAME_PRIORITY, task.getPriority());

        if (updateTask) {
            //update selected task
            updateTask(cv);
        } else {
            //insert task
            insertTask(cv);
        }
        if (task.getReminder() != null) {
            setReminder(task);
        }
        if (getActivity().findViewById(R.id.details) != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStackImmediate();
        } else {
            getActivity().finish();
        }
    }

    private void insertTask(ContentValues contentValues) {
        getActivity().getContentResolver().insert(TasksContract.Tasks.CONTENT_URI, contentValues);
    }

    private void updateTask(ContentValues contentValues) {
        Uri baseUri = Uri.withAppendedPath(TasksContract.Tasks.CONTENT_ID_URI_PATTERN, Uri.encode(String.valueOf(getShownTask().getId())));
        getActivity().getContentResolver().update(baseUri, contentValues, null, null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_due_date:
                setDateField(dueDate);
                break;
            case R.id.add_due_time:
                setTimeField(dueTime);
                break;
            case R.id.add_reminder_date:
                setDateField(customReminderDate);
                break;
            case R.id.add_reminder_time:
                setTimeField(customReminderTime);
                break;
        }
    }


    private void setReminder(Tasks task) {
        new CalendarEventHelper(getActivity(), task.getTitle(), task.getDescription(), "",
               task.getDueDate(), task.getDueDate(), task.getReminder(), 1).addEvent();
    }

    public void showNotification(Tasks task) {

        Intent intent = new Intent(getActivity(), NotificationEvent.class);
        intent.putExtra("task_reminder", task);
        AlarmManager manager = (AlarmManager) getActivity().getSystemService(Activity.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 0, intent, 0);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, task.getReminder().getTime(), 24 * 60 * 60 * 1000, pendingIntent);

    }

}
