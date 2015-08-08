package com.example.andreeamocean.tasks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.andreeamocean.tasks.R;
import com.example.andreeamocean.tasks.model.Tasks;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by andreea.mocean on 7/4/2015.
 */
public class TaskArrayAdapter extends ArrayAdapter<Tasks> {

    private SimpleDateFormat mDateFormat;

    public TaskArrayAdapter(Context context, int resource) {
        super(context, resource);
        mDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_list, parent, false);
            holder = new ViewHolder();
            holder.mTitle = (TextView) convertView.findViewById(R.id.title);
            holder.mDescription = (TextView) convertView.findViewById(R.id.description);
            holder.mDueDate = (TextView) convertView.findViewById(R.id.due_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Tasks task = getItem(position);
        holder.mTitle.setText(task.getTitle());
        holder.mDescription.setText(task.getDescription());
        holder.mDueDate.setText(mDateFormat.format(task.getDueDate()));
        return convertView;
    }

    /**
     * Sets the adapters list of tasks.
     * @param tasks the new list of tasks
     */
    public void setTasks(List<Tasks> tasks) {
        clear();
        if (tasks != null) {
            for (int i = 0; i < tasks.size(); i++) {
                add(tasks.get(i));
            }
        }
    }

    private static final class ViewHolder {
        private TextView mTitle;
        private TextView mDueDate;
        private TextView mDescription;
    }
}
