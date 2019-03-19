package com.example.chara.bookworm;

import android.provider.BaseColumns;

public final class BookWormContract {
    public BookWormContract() {}

    public static abstract class BookEntry implements BaseColumns {
        public static final String TABLE_NAME = "books";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_RATING = "rating";
    }

    private static final String TEXT_TYPE = " TEXT";
    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + BookEntry.TABLE_NAME +
            " (" + BookEntry._ID + " INTEGER," + BookEntry.COLUMN_NAME_TITLE +
            TEXT_TYPE + "," + BookEntry.COLUMN_NAME_AUTHOR + TEXT_TYPE + "," +
            BookEntry.COLUMN_NAME_RATING + " NUM, PRIMARY KEY (" + BookEntry.COLUMN_NAME_TITLE + ", " +
            BookEntry.COLUMN_NAME_AUTHOR +  ") )";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + BookEntry.TABLE_NAME;
}
