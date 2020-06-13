package com.example.mymemo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.util.Log;

public class MemoHelper extends SQLiteOpenHelper {

    private static String TAG = "MemoHelper";
    static final private String DBName = "Memo_DB";         // データベースの名前
    static final private int VERSION = 4;                   // データベースのバージョン

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
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date TEXT, " +
                "memo_uuid TEXT)");
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
            cv.put("uuid", id);
            cv.put("title", "名前のないメモ");
            cv.put("body", "");
            db.insert(Memo_Table, null, cv);

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

            // 更新日の登録
            cv.put("date", date);
            cv.put("uuid", id);
            db.insert(Date_Table, null, cv);

            Log.i(TAG, "データの更新: "+id);
        }
    }

    public void deleteMemo(String uuid){
        // データを削除
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String[] args = {uuid};
            db.delete(Memo_Table, "uuid=?", args);
            db.delete(Date_Table, "uuid=?", args);

            Log.i(TAG, "データの削除: "+uuid);
        }
    }
}
