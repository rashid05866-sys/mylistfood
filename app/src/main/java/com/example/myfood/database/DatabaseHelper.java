package com.example.myfood.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myfood.entity.Product;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final static String dbName = "product-app.db";
    private final static Integer version = 1;
    public static final String TABLE_NAME ="ProductTable";
    public static final String COL1 = "id";
    public static final String COL2 = "name";
    public static final String COL3 = "active";
    private static final String CREATE_TABLE="create table if not exists "+ TABLE_NAME + "(" + COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT,"+COL2+" TEXT NOT NULL,"
            + COL3 + " INTEGER);";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS "+ TABLE_NAME;
    private Context context;

    public DatabaseHelper(Context context) {
        super(context,dbName,null,version);
        context = this.context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE);
        } catch (Exception e) {
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public boolean insertProduct(Product objEmp) {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL2,objEmp.getName());
        cv.put(COL3,objEmp.isActive());

        long result = db.insert(TABLE_NAME,null,cv);
        if(result == -1)

            return false;
        else
            return true;
    }
    // Метод для получения всех записей
    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    // Или возвращаем List объектов
    public List<Product> getAllUsers() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                //product.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                product.setName(cursor.getString(cursor.getColumnIndex("name")));
                //product.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                productList.add(0, product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productList;
    }
}
