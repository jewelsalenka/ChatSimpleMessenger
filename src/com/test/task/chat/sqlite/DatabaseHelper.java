package com.test.task.chat.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Olenka on 18.11.2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 11;
    private static final String DATABASE_CHAT = "chat";
    private static final String TABLE_MESSAGES = "messages";

    // Common column names
    private static final String KEY = "id";
    private static final String TEXT = "message";
    private static final String TYPE = "type";
    private static final String DATE = "date";

    private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES
            + "(" + KEY + " INTEGER PRIMARY KEY," + TYPE + " TEXT," + TEXT + " TEXT," + DATE + " DATETIME" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_CHAT, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("test", "DATA BASE CREAT");
        db.execSQL(CREATE_TABLE_MESSAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }

    public long insertMessage(Message m){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TEXT, m.getMessage());
        values.put(TYPE, m.getType().toString());
        values.put(DATE, m.getStringDate());
        long tag_id = db.insert(TABLE_MESSAGES, null, values);
        return tag_id;
    }

    public void insertMessages(List<Message> messageListist){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        for (Message m : messageListist) {
            ContentValues values = new ContentValues();
            values.put(TEXT, m.getMessage());
            values.put(TYPE, m.getType().toString());
            values.put(DATE, m.getStringDate());
            db.insert(TABLE_MESSAGES, null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        Log.i("test", "insertMessage");
    }

    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<Message>();
        String query = "SELECT * FROM " + TABLE_MESSAGES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String message = cursor.getString((cursor.getColumnIndex(TEXT)));
                Message.Type type = Message.Type.valueOf(cursor.getString(cursor.getColumnIndex(TYPE)));
                String timestamp = cursor.getString((cursor.getColumnIndex(DATE)));
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date date = null;
                try {
                    date = format.parse(timestamp);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e(DatabaseHelper.class.getSimpleName(), "cann't get date from db");
                }
                Message m = new Message(message, type, date);
                messages.add(m);
            } while (cursor.moveToNext());
        }
        return messages;
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
