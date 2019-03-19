package com.example.chara.bookworm;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class AddBookActivity extends AppCompatActivity {
    private BookWormDBHelper dbHelper;
    private String titleToUse, authorToUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        Intent intent = getIntent();
        titleToUse = intent.getExtras().getString("title", "~");
        authorToUse = intent.getExtras().getString("author", "~");

        if(!titleToUse.equals("~") && !authorToUse.equals("~")){
            setTitle(R.string.edit);

            String[] projection = new String[]{
                    BookWormContract.BookEntry._ID,
                    BookWormContract.BookEntry.COLUMN_NAME_TITLE,
                    BookWormContract.BookEntry.COLUMN_NAME_AUTHOR,
                    BookWormContract.BookEntry.COLUMN_NAME_RATING
            };
            dbHelper = new BookWormDBHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(BookWormContract.BookEntry.TABLE_NAME, projection, BookWormContract.BookEntry.COLUMN_NAME_TITLE +
                    " = ? AND " + BookWormContract.BookEntry.COLUMN_NAME_AUTHOR + " = ?",
                    new String[]{"" + titleToUse, authorToUse}, null, null, null, null);
            cursor.moveToFirst();

            String title = cursor.getString(cursor.getColumnIndex(BookWormContract.BookEntry.COLUMN_NAME_TITLE));
            String author = cursor.getString(cursor.getColumnIndex(BookWormContract.BookEntry.COLUMN_NAME_AUTHOR));
            float rating = cursor.getFloat(cursor.getColumnIndex(BookWormContract.BookEntry.COLUMN_NAME_RATING));

            EditText titleText = (EditText) findViewById(R.id.title_editText);
            titleText.setText(title);

            EditText authorText = (EditText) findViewById(R.id.author_editText);
            authorText.setText(author);

            RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
            ratingBar.setRating(rating);
        }else{
            setTitle(R.string.newBook);
        }

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDetails();
            }
        });
    }

    private void getDetails(){
        String title = ((EditText) findViewById(R.id.title_editText)).getText().toString();
        String author = ((EditText) findViewById(R.id.author_editText)).getText().toString();
        float rating = ((RatingBar) findViewById(R.id.ratingBar)).getRating();

        if(title.equals("")){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.invalid));
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }else{
            save(title, author, rating);
        }
    }

    private void save(String title, String author, float rating){
        dbHelper = new BookWormDBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BookWormContract.BookEntry.COLUMN_NAME_TITLE, title);
        values.put(BookWormContract.BookEntry.COLUMN_NAME_AUTHOR, author);
        values.put(BookWormContract.BookEntry.COLUMN_NAME_RATING, rating);

        if(titleToUse.equals("~") && authorToUse.equals("~")){
            long newRowId = db.insert(BookWormContract.BookEntry.TABLE_NAME, null, values);
            if(newRowId != -1){
                String s = getResources().getString(R.string.the) + " \"" +
                        title + "\" " + getResources().getString(R.string.added);
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
                finishActivity();
            }else{
                final String s = getResources().getString(R.string.the) + " \""+
                        title + "\" " + getResources().getString(R.string.alreadyExists);

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(s);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        }else{
            try{
                db.update(BookWormContract.BookEntry.TABLE_NAME, values, BookWormContract.BookEntry.COLUMN_NAME_TITLE +
                        " = ? AND " + BookWormContract.BookEntry.COLUMN_NAME_AUTHOR + " = ?",
                        new String[]{"" + titleToUse, authorToUse});
                Toast.makeText(this, R.string.changesSaved, Toast.LENGTH_SHORT).show();
                finishActivity();
            }catch (Exception e){
                Toast.makeText(this, R.string.noChanges, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void finishActivity(){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);

        finish();
    }
}
