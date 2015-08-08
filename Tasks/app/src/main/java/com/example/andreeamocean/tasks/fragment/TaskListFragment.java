package com.example.andreeamocean.tasks.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;

import com.example.andreeamocean.tasks.R;
import com.example.andreeamocean.tasks.adapter.TaskArrayAdapter;
import com.example.andreeamocean.tasks.loader.TasksLoader;
import com.example.andreeamocean.tasks.model.Tasks;
import android.support.v7.widget.SearchView.OnQueryTextListener;

import java.util.List;

/**
 * Created by andreea.mocean on 7/4/2015.
 */
public class TaskListFragment extends ListFragment implements OnQueryTextListener, LoaderManager.LoaderCallbacks<List<Tasks>> {
    private static final String SELECTED_TASK_POSITION = "com.example.andreeamocean.tasks.SELECTED_TASK_POSITION";
    /**
     * The id of {@link Loader} responsible for loading the tasks in the list.
     */
    private static int TASKS_LOADER_ID = 1;
    /**
     * Flag for dual pane layout.
     */
    boolean mDualPane;
    /**
     * The curent selected position of a task.
     */
    int mSelectedPosition = 0;
    /**
     * The current filter the user has provided.
     */
    String mCurFilter;
    /**
     * Callback interface to share events with the activity.
     */
    private OnTaskSelectedListener mTaskListener;
    /**
     * Adapter of the fragment.
     */
    private TaskArrayAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new TaskArrayAdapter(getActivity(), R.layout.task_list);
        setListAdapter(mAdapter);
        setListShown(false);
        getLoaderManager().initLoader(TASKS_LOADER_ID, null, this);
        setHasOptionsMenu(true);

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = getActivity().findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mSelectedPosition = savedInstanceState.getInt(SELECTED_TASK_POSITION, 0);
        }

        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            getListView().setItemChecked(mSelectedPosition, true);
//            mTaskListener.onTaskSelected(mAdapter.getItem(mSelectedPosition));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mTaskListener = (OnTaskSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTaskSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mTaskListener.onTaskSelected(mAdapter.getItem(position));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_TASK_POSITION, mSelectedPosition);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
        getLoaderManager().restartLoader(TASKS_LOADER_ID, null, this);
        return true;
    }

    @Override
    public Loader<List<Tasks>> onCreateLoader(int i, Bundle bundle) {
        if (i == TASKS_LOADER_ID) {
            return new TasksLoader(getActivity(), mCurFilter);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Tasks>> loader, List<Tasks> tasks) {
        mAdapter.setTasks(tasks);
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Tasks>> loader) {
        mAdapter.setTasks(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_task, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(this);

    }

    // Container Activity must implement this interface
    public interface OnTaskSelectedListener {
        void onTaskSelected(Tasks task);
    }

}

