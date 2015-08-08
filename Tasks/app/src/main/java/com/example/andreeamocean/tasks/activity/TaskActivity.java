
package com.example.andreeamocean.tasks.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.andreeamocean.tasks.R;
import com.example.andreeamocean.tasks.fragment.AddTaskFragment;
import com.example.andreeamocean.tasks.fragment.TaskDescriptionFragment;
import com.example.andreeamocean.tasks.fragment.TaskListFragment;
import com.example.andreeamocean.tasks.model.Tasks;

public class TaskActivity extends AppCompatActivity implements TaskListFragment.OnTaskSelectedListener {

    private boolean mDualPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        View detailsFrame = this.findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
        FloatingActionButton addNewTask = (FloatingActionButton) findViewById(R.id.add_task);
        addNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTaskAdded();
            }
        });
    }

    @Override
    public void onTaskSelected(Tasks task) {
        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        if (mDualPane) {
            Fragment fragment = getFragmentManager().findFragmentById(R.id.details);
            TaskDescriptionFragment taskFrag = null;
            if (fragment instanceof TaskDescriptionFragment) {
                taskFrag = (TaskDescriptionFragment)
                        getFragmentManager().findFragmentById(R.id.details);
            }
            if (taskFrag == null || taskFrag.getShownTask() != task) {
                taskFrag = TaskDescriptionFragment.newInstance(task);
                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.details, taskFrag);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        } else {
            // Otherwise we need to launch a new activity
            Intent intent = new Intent();
            intent.setClass(this, TaskDescriptionActivity.class);
            intent.putExtra(TaskDescriptionFragment.SELECTED_TASK, task);
            startActivity(intent);
        }
    }

    public void onTaskAdded() {
        if (mDualPane) {
            Fragment fragment = getFragmentManager().findFragmentById(R.id.details);
            AddTaskFragment addTaskFrag = null;
            if (fragment instanceof AddTaskFragment) {
                addTaskFrag = (AddTaskFragment)
                        getFragmentManager().findFragmentById(R.id.details);
            }
            if (addTaskFrag == null) {
                AddTaskFragment addTaskFragment = AddTaskFragment.newInstance(null);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.details, addTaskFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }

        } else {
            Intent intent = new Intent();
            intent.setClass(TaskActivity.this, AddTaskActivity.class);
            startActivity(intent);
        }
    }
}
