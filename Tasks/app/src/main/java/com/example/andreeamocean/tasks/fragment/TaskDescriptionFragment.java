package com.example.andreeamocean.tasks.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.andreeamocean.tasks.R;
import com.example.andreeamocean.tasks.activity.AddTaskActivity;
import com.example.andreeamocean.tasks.model.Tasks;
import com.example.andreeamocean.tasks.persistence.TasksContract;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by andreea.mocean on 7/4/2015.
 */
public class TaskDescriptionFragment extends Fragment {

    public static final String SELECTED_TASK = "com.example.andreeamocean.tasks.SELECTED_TASK";
    private SimpleDateFormat mDateFormat;
    /**
     * {@link TextView} for the task title.
     */
    private TextView title;
    /**
     * {@link TextView} for the task description.
     */
    private TextView description;
    /**
     * {@link TextView} for the task due date.
     */
    private TextView dueDate;
    /**
     * {@link TextView} for the task reminder.
     */
    private TextView reminder;
    /**
     * {@link TextView} for the task priority.
     */
    private TextView priority;


    public static TaskDescriptionFragment newInstance(Tasks task) {
        TaskDescriptionFragment f = new TaskDescriptionFragment();
        Bundle args = new Bundle();
        args.putSerializable(SELECTED_TASK, task);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.task_description, container, false);
        initUI(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_description_task, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                onEdit();
                break;
            case R.id.action_delete:
                onDelete();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onDelete() {
        Uri baseUri = Uri.withAppendedPath(TasksContract.Tasks.CONTENT_ID_URI_PATTERN, Uri.encode(String.valueOf(getShownTask().getId())));
        getActivity().getContentResolver().delete(baseUri, null, null);
    }

    private void onEdit(){
        if (getActivity().findViewById(R.id.details) != null) {
            Fragment fragment = getFragmentManager().findFragmentById(R.id.details);
            AddTaskFragment addTaskFrag = null;
            if (fragment instanceof AddTaskFragment) {
                addTaskFrag = (AddTaskFragment)
                        getFragmentManager().findFragmentById(R.id.details);
            }
            if (addTaskFrag == null) {
                AddTaskFragment addTaskFragment = AddTaskFragment.newInstance(getShownTask());
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.details, addTaskFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        }else {
            Intent intent = new Intent();
            intent.setClass(getActivity(), AddTaskActivity.class);
            intent.putExtra(AddTaskFragment.CURRENT_TASK, getShownTask());
            startActivity(intent);
            finish();
        }
    }

    private void finish(){
        if (getActivity().findViewById(R.id.details) != null){
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStackImmediate();
        }
        else{
            getActivity().finish();
        }
    }

    public Tasks getShownTask() {
        return getArguments() != null ? (Tasks) getArguments().getSerializable(SELECTED_TASK) : null;
    }

    private void initUI(View view) {
        Tasks task = getShownTask();
        mDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        title = (TextView) view.findViewById(R.id.task_title);
        description = (TextView) view.findViewById(R.id.task_description);
        dueDate = (TextView) view.findViewById(R.id.task_due_date);
        reminder = (TextView) view.findViewById(R.id.task_reminder);
        priority = (TextView) view.findViewById(R.id.task_priority);
        if (task != null) {
            title.setText(task.getTitle());
            description.setText(task.getDescription());
            dueDate.setText(mDateFormat.format(task.getDueDate()));
            if (task.getReminder() == null) {
                reminder.setText(R.string.off);
            } else {
                reminder.setText(mDateFormat.format(task.getReminder()));
            }
            priority.setText(task.getPriority());
        }
    }
}
