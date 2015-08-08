package com.example.andreeamocean.tasks.persistence;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by andreeamocean on 7/19/15.
 */
public class TasksContract {
    public static final String AUTHORITY = "com.example.andreeamocean.Tasks";

    // This class cannot be instantiated
    private TasksContract() {
    }

    /**
     * Tasks table contract
     */
    public static final class Tasks implements BaseColumns {

        // This class cannot be instantiated
        private Tasks() {}

        /**
         * The table name offered by this provider
         */
        public static final String TABLE_NAME = "tasks";

        /*
         * URI definitions
         */

        /**
         * The scheme part for this provider's URI
         */
        private static final String SCHEME = "content://";

        /**
         * Path parts for the URIs
         */

        /**
         * Path part for the Tasks URI
         */
        private static final String PATH_TASKS= "/tasks";

        /**
         * Path part for the Task ID URI
         */
        private static final String PATH_TASKS_ID = "/tasks/";

        /**
         * 0-relative position of a task ID segment in the path part of a task ID URI
         */
        public static final int TASK_ID_PATH_POSITION = 1;

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_TASKS);

        /**
         * The content URI base for a single task. Callers must
         * append a numeric task id to this Uri to retrieve a task
         */
        public static final Uri CONTENT_ID_URI_BASE
                = Uri.parse(SCHEME + AUTHORITY + PATH_TASKS_ID);

        /**
         * The content URI match pattern for a single task, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN
                = Uri.parse(SCHEME + AUTHORITY + PATH_TASKS_ID + "/#");


        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "due_date DESC";

        /*
         * Column definitions
         */

        /**
         * Column name for the title of the task
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_TITLE = "title";

        /**
         * Column name of the task content
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_DESCRIPTION= "description";

        /**
         * Column name for the due date timestamp
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String COLUMN_NAME_DUE_DATE = "due_date";

        /**
         * Column name for the reminder timestamp
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String COLUMN_NAME_REMINDER_DATE = "reminder";

        /**
         * Column name for the priority of the task
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_PRIORITY= "priority";
    }
}
