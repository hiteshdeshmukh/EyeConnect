package org.tensorflow.lite.examples.detection.Note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "testdata";
  public static   ArrayList<String> arrayList=new ArrayList<>();
    public static   ArrayList<Double> arraysum=new ArrayList<>();
     // below int is our database version
    private static final int DB_VERSION = 1;
    static String date1;
    public static String getDate(){return date1;}
    static String error;
    public static String getError(){
        return error;
    }

    private String[] hello;


    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.

        //For storing QR details.

        String query2 = "create table note (" +
                "id integer primary key autoincrement,title text,message text,date text)";
        db.execSQL(query2);




    }

    // this method is use to add new course to our sqlite database.
    public void addValues(String title,String message,String date) {

       /** on below line we are creating a variable for
        *our sqlite database and calling writable method
        * as we are writing data in our database.*/

        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        values.put("title", title);

        values.put("message",message);
        values.put("date",date);

        // after adding all values we are passing
        // content values to our table.
        db.insertOrThrow("note", null, values);

    }

    public Cursor readallnotes() {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "select * from note order by id desc";
         return database.rawQuery(query, null);
    }

    // we have created a new method for reading all the courses.

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS note");
        onCreate(db);
    }

    public Boolean deleteData(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(" DELETE FROM note");
        db.close();
        return true;
    }



}
