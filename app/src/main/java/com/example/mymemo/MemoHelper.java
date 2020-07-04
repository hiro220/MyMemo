package com.example.mymemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

public class MemoHelper extends SQLiteOpenHelper {

    private static String TAG = "MemoHelper";
    static final private String DBName = "Memo_DB";         // データベースの名前
    static final private int VERSION = 5;                   // データベースのバージョン

    static final private String Memo_Table = "MEMO_TABLE";  // メモのテーブルの名前
    static final private String Date_Table = "DATE_TABLE";  // 更新時間のテーブルの名前

    public MemoHelper(Context context){
        super(context, DBName, null, VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        // CREATE TABLE
        // MEMO_TABLE
        db.execSQL("CREATE TABLE " + Memo_Table + "(" +
                "uuid TEXT PRIMARY KEY, " +
                "title TEXT, " +
                "body TEXT)");
        // DATE_TABLE
        db.execSQL("CREATE TABLE " + Date_Table + "(" +
                "memo_uuid TEXT PRIMARY KEY," +
                "date1 TEXT, " +
                "date2 TEXT, " +
                "date3 TEXT)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // DROP TABLE
        db.execSQL("DROP TABLE IF EXISTS " + Memo_Table);
        db.execSQL("DROP TABLE IF EXISTS " + Date_Table);
        onCreate(db);
    }

    /* 以下、他クラスから呼び出すメソッド */

    public void createData(String id) {
        // 新しくメモをデータベースに保存
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            // uuid, タイトル, 本文の保存
            cv.put("uuid", id);
            cv.put("title", "名前のないメモ");
            cv.put("body", "");
            db.insert(Memo_Table, null, cv);

            cv.clear();
            // 日時の保存
            cv.put("memo_uuid", id);
            cv.put("date1", "");
            cv.put("date2", "");
            cv.put("date3", "");
            db.insert(Date_Table, null, cv);

            Log.i(TAG, "新しいデータの登録: "+id);
        }
    }

    public void saveData(String id, String title, String text, String date) {
        // データを更新
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            // メモの更新
            cv.put("title", title);
            cv.put("body", text);
            String[] args = {id};
            db.update(Memo_Table, cv, "uuid=?", args);

            // cvにセットした値をクリア
            cv.clear();

            // 更新日の更新
            Cursor c = db.rawQuery("SELECT date1, date2" +
                                        " FROM " + Date_Table +
                                        " WHERE memo_uuid=?", args);
            c.moveToFirst();
            cv.put("date1", date);
            cv.put("date2", c.getString(0));
            cv.put("date3", c.getString(1));
            db.update(Date_Table, cv, "memo_uuid=?", args);
            c.close();

            Log.i(TAG, "データの更新: "+id);

        }
    }

    public void deleteMemo(String uuid){
        // データを削除
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String[] args = {uuid};
            db.delete(Memo_Table, "uuid=?", args);
            db.delete(Date_Table, "memo_uuid=?", args);

            Log.i(TAG, "データの削除: "+uuid);
        }
    }

    public ListItem getOneItem(String uuid){
        // uuidのデータを取得し、ListItemに変換する
        Cursor c;
        String title;
        String body;
        String[] date = new String[3];
        String[] args = {uuid};

        try (SQLiteDatabase db = getWritableDatabase()) {
            // タイトルを取得
            c = db.rawQuery("SELECT title, body " +
                                 "FROM " + Memo_Table +
                                 " WHERE uuid=?", args);
            c.moveToFirst();
            title = c.getString(0);
            body = c.getString(1);

            // 更新日時を取得
            c = db.rawQuery("SELECT date1, date2, date3 " +
                            "FROM " + Date_Table +
                            " WHERE memo_uuid=?", args);
            boolean eol = c.moveToFirst();
            for (int i=0; i < 3 && eol; i++){
                date[i] = c.getString(i);
                Log.i(TAG, "date: " + date[i]);
            }
        }
        c.close();
        // ListItemを作成
        ListItem item = new ListItem();
        item.setUuid(uuid);
        item.setTitle(title);
        item.setBody(body);
        item.setDate(date);

        return item;
    }

    public ArrayList<ListItem> getAllItem(){
        ArrayList<ListItem> items = new ArrayList<>();
        Cursor c;
        try (SQLiteDatabase db = getWritableDatabase()) {
            c = db.rawQuery("SELECT uuid" +
                                " FROM " + Memo_Table, null);
            // 全てのデータをArrayListに格納
            boolean eol = c.moveToFirst();
            while (eol) {
                ListItem item = getOneItem(c.getString(0));
                Log.i(TAG, "uuid: "+c.getString(0));
                items.add(item);
                eol = c.moveToNext();
            }
        }
        c.close();
        return items;
    }
}
