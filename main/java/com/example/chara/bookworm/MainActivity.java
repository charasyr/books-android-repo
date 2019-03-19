package com.example.chara.bookworm;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private SimpleCursorAdapter adapter;
    private BookWormDBHelper dbHelper;
    private SQLiteDatabase db;
    private String[] projection;
    private String titleToUse, authorToUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lv = (ListView) findViewById(R.id.booksListView);

        dbHelper = new BookWormDBHelper(this);
        db = dbHelper.getReadableDatabase();

        projection = new String[]{
                BookWormContract.BookEntry._ID,
                BookWormContract.BookEntry.COLUMN_NAME_TITLE,
                BookWormContract.BookEntry.COLUMN_NAME_AUTHOR,
                BookWormContract.BookEntry.COLUMN_NAME_RATING
        };

        Cursor cursor = db.query(BookWormContract.BookEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
        String[] fromColumns = {BookWormContract.BookEntry.COLUMN_NAME_TITLE,
                BookWormContract.BookEntry.COLUMN_NAME_AUTHOR, BookWormContract.BookEntry.COLUMN_NAME_RATING};
        int[] toViews = {R.id.titleTextView, R.id.authorTextView, R.id.ratingTextView};
        adapter = new SimpleCursorAdapter(this, R.layout.item_view, cursor, fromColumns, toViews, 0);
        lv.setAdapter(adapter);
        registerForContextMenu(lv);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addFabButton);
        fab.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, AddBookActivity.class);
                intent.putExtra("title", "~");
                intent.putExtra("author", "~");
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo info){
        super.onCreateContextMenu(menu, view, info);

        AdapterView.AdapterContextMenuInfo i = (AdapterView.AdapterContextMenuInfo) info;
        Cursor c = (Cursor) ((ListView) view).getItemAtPosition(i.position);
        titleToUse = c.getString(c.getColumnIndex(BookWormContract.BookEntry.COLUMN_NAME_TITLE));
        authorToUse = c.getString(c.getColumnIndex(BookWormContract.BookEntry.COLUMN_NAME_AUTHOR));

        menu.setHeaderTitle(R.string.options);
        menu.add(0, view.getId(), 0, R.string.edit);
        menu.add(0, view.getId(), 0 , R.string.delete);
    }

    public boolean onContextItemSelected(MenuItem item){
        if(item.getTitle().toString().equals(getResources().getString(R.string.edit))){
            Intent intent = new Intent(MainActivity.this, AddBookActivity.class);
            intent.putExtra("title", titleToUse);
            intent.putExtra("author", authorToUse);
            startActivityForResult(intent, 1);
            return true;
        }else if(item.getTitle().toString().equals(getResources().getString(R.string.delete))){
            ask();
            return true;
        }else{
            return super.onContextItemSelected(item);
        }
    }

    private void ask(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.deleteQuestion);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void delete(){
        dbHelper = new BookWormDBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(BookWormContract.BookEntry.TABLE_NAME, BookWormContract.BookEntry.COLUMN_NAME_TITLE +
                " = ? AND " + BookWormContract.BookEntry.COLUMN_NAME_AUTHOR + " = ?",
                new String[]{"" + titleToUse, authorToUse});
        showAllBooks();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                showAllBooks();
            }
        }
    }

    private void showAllBooks(){
        Cursor cursor = db.query(BookWormContract.BookEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
        adapter.changeCursor(cursor);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.all:
                showAllBooks();
                return true;
            case R.id.top3:
                show3Books(" DESC");
                return true;
            case R.id.bottom3:
                show3Books(" ASC");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void show3Books(String sort){
        Cursor cursor = db.query(BookWormContract.BookEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                BookWormContract.BookEntry.COLUMN_NAME_RATING + sort,
                "3");
        adapter.changeCursor(cursor);
    }
}
