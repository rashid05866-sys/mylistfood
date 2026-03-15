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
    private final static Integer version = 2;

    public static final String TABLE_NAME = "ProductTable";
    public static final String COL1 = "id";
    public static final String COL2 = "name";
    public static final String COL3 = "active";
    public static final String COL4 = "quantity";
    public static final String COL5 = "unit";

    public static final String DICT_TABLE_NAME = "ProductDictionary";
    public static final String D_COL1 = "id";
    public static final String D_COL2 = "name";
    public static final String D_COL3 = "category";
    public static final String D_COL4 = "unit";

    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COL2 + " TEXT NOT NULL," +
                    COL3 + " INTEGER," +
                    COL4 + " REAL," +
                    COL5 + " TEXT" +
                    ");";

    private static final String CREATE_DICT_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DICT_TABLE_NAME + " (" +
                    D_COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    D_COL2 + " TEXT NOT NULL UNIQUE," +
                    D_COL3 + " TEXT," +
                    D_COL4 + " TEXT" +
                    ");";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    private static final String DROP_DICT_TABLE = "DROP TABLE IF EXISTS " + DICT_TABLE_NAME;
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, dbName, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE);
            db.execSQL(CREATE_DICT_TABLE);
            seedDictionary(db);
        } catch (Exception e) {
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        db.execSQL(DROP_DICT_TABLE);
        onCreate(db);
    }

    public boolean insertProduct(Product objEmp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL2, objEmp.getName());
        cv.put(COL3, objEmp.isActive());
        cv.put(COL4, objEmp.getQuantity());
        cv.put(COL5, objEmp.getUnit());

        long result = db.insert(TABLE_NAME, null, cv);
        return result != -1;
    }

    // Метод для получения всех записей
    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    // Возвращаем List объектов
    public List<Product> getAllUsers() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COL1);
            int nameIndex = cursor.getColumnIndex(COL2);
            int activeIndex = cursor.getColumnIndex(COL3);
            int quantityIndex = cursor.getColumnIndex(COL4);
            int unitIndex = cursor.getColumnIndex(COL5);

            do {
                Product product = new Product();
                if (idIndex != -1) {
                    product.setId(cursor.getInt(idIndex));
                }
                if (nameIndex != -1) {
                    product.setName(cursor.getString(nameIndex));
                }
                if (activeIndex != -1) {
                    product.setActive(cursor.getInt(activeIndex) == 1);
                }
                if (quantityIndex != -1) {
                    product.setQuantity(cursor.getDouble(quantityIndex));
                } else {
                    product.setQuantity(1.0);
                }
                if (unitIndex != -1) {
                    product.setUnit(cursor.getString(unitIndex));
                } else {
                    product.setUnit("pcs");
                }
                productList.add(0, product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productList;
    }

    public Product getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COL1 + "=?", new String[]{String.valueOf(id)}, null, null, null);
        Product product = null;
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COL1);
            int nameIndex = cursor.getColumnIndex(COL2);
            int activeIndex = cursor.getColumnIndex(COL3);
            int quantityIndex = cursor.getColumnIndex(COL4);
            int unitIndex = cursor.getColumnIndex(COL5);

            product = new Product();
            if (idIndex != -1) {
                product.setId(cursor.getInt(idIndex));
            }
            if (nameIndex != -1) {
                product.setName(cursor.getString(nameIndex));
            }
            if (activeIndex != -1) {
                product.setActive(cursor.getInt(activeIndex) == 1);
            }
            if (quantityIndex != -1) {
                product.setQuantity(cursor.getDouble(quantityIndex));
            } else {
                product.setQuantity(1.0);
            }
            if (unitIndex != -1) {
                product.setUnit(cursor.getString(unitIndex));
            } else {
                product.setUnit("pcs");
            }
        }
        cursor.close();
        return product;
    }

    // Удаление продукта по id
    public void deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL1 + "=?", new String[]{String.valueOf(id)});
    }

    // Обновление статуса активности (куплено / не куплено)
    public void updateProductActive(int id, boolean active) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL3, active ? 1 : 0);
        db.update(TABLE_NAME, cv, COL1 + "=?", new String[]{String.valueOf(id)});
    }

    public void updateProductQuantityAndUnit(int id, double quantity, String unit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL4, quantity);
        cv.put(COL5, unit);
        db.update(TABLE_NAME, cv, COL1 + "=?", new String[]{String.valueOf(id)});
    }

    private void seedDictionary(SQLiteDatabase db) {
        // Овощи (вес, кг)
        insertDict(db, "Картофель", "vegetable", "kg");
        insertDict(db, "Морковь", "vegetable", "kg");
        insertDict(db, "Лук репчатый", "vegetable", "kg");
        insertDict(db, "Лук зелёный", "vegetable", "kg");
        insertDict(db, "Свёкла", "vegetable", "kg");
        insertDict(db, "Капуста белокочанная", "vegetable", "kg");
        insertDict(db, "Капуста цветная", "vegetable", "kg");
        insertDict(db, "Капуста брокколи", "vegetable", "kg");
        insertDict(db, "Огурцы", "vegetable", "kg");
        insertDict(db, "Помидоры", "vegetable", "kg");
        insertDict(db, "Перец сладкий", "vegetable", "kg");
        insertDict(db, "Кабачки", "vegetable", "kg");
        insertDict(db, "Баклажаны", "vegetable", "kg");
        insertDict(db, "Чеснок", "vegetable", "kg");

        // Фрукты и ягоды (вес, кг)
        insertDict(db, "Яблоки", "fruit", "kg");
        insertDict(db, "Бананы", "fruit", "kg");
        insertDict(db, "Апельсины", "fruit", "kg");
        insertDict(db, "Мандарины", "fruit", "kg");
        insertDict(db, "Груши", "fruit", "kg");
        insertDict(db, "Виноград", "fruit", "kg");
        insertDict(db, "Киви", "fruit", "kg");
        insertDict(db, "Лимоны", "fruit", "kg");
        insertDict(db, "Арбуз", "fruit", "kg");
        insertDict(db, "Дыня", "fruit", "kg");
        insertDict(db, "Клубника", "berry", "kg");
        insertDict(db, "Малина", "berry", "kg");
        insertDict(db, "Черника", "berry", "kg");
        insertDict(db, "Смородина", "berry", "kg");

        // Конфеты и сладости (вес, г)
        insertDict(db, "Конфеты ассорти", "candy", "g");
        insertDict(db, "Шоколадные конфеты", "candy", "g");
        insertDict(db, "Карамель", "candy", "g");
        insertDict(db, "Ирис", "candy", "g");
        insertDict(db, "Пастила", "candy", "g");
        insertDict(db, "Зефир", "candy", "g");
        insertDict(db, "Печенье весовое", "cookies", "g");
        insertDict(db, "Орехи жареные", "nuts", "g");
        insertDict(db, "Сухофрукты", "dried_fruit", "g");

        // Хлеб и выпечка (шт.)
        insertDict(db, "Хлеб белый", "bakery", "pcs");
        insertDict(db, "Хлеб чёрный", "bakery", "pcs");
        insertDict(db, "Батон", "bakery", "pcs");
        insertDict(db, "Булочка", "bakery", "pcs");
        insertDict(db, "Лаваш", "bakery", "pcs");
        insertDict(db, "Пита", "bakery", "pcs");

        // Молочные продукты (шт.)
        insertDict(db, "Молоко", "dairy", "pcs");
        insertDict(db, "Кефир", "dairy", "pcs");
        insertDict(db, "Ряженка", "dairy", "pcs");
        insertDict(db, "Йогурт", "dairy", "pcs");
        insertDict(db, "Сметана", "dairy", "pcs");
        insertDict(db, "Творог", "dairy", "pcs");
        insertDict(db, "Сыр твёрдый", "dairy", "pcs");
        insertDict(db, "Сыр плавленый", "dairy", "pcs");
        insertDict(db, "Творожный сыр", "dairy", "pcs");
        insertDict(db, "Яйца", "dairy", "pcs");

        // Крупы, макароны, бакалея (шт. упаковок)
        insertDict(db, "Сахар", "grocery", "pcs");
        insertDict(db, "Соль", "grocery", "pcs");
        insertDict(db, "Мука", "grocery", "pcs");
        insertDict(db, "Рис", "grocery", "pcs");
        insertDict(db, "Гречка", "grocery", "pcs");
        insertDict(db, "Овсянка", "grocery", "pcs");
        insertDict(db, "Пшено", "grocery", "pcs");
        insertDict(db, "Перловка", "grocery", "pcs");
        insertDict(db, "Макароны", "grocery", "pcs");
        insertDict(db, "Вермишель", "grocery", "pcs");
        insertDict(db, "Сухари панировочные", "grocery", "pcs");

        // Масла, соусы, консервы (шт.)
        insertDict(db, "Масло сливочное", "oil", "pcs");
        insertDict(db, "Масло растительное", "oil", "pcs");
        insertDict(db, "Оливковое масло", "oil", "pcs");
        insertDict(db, "Соевый соус", "sauce", "pcs");
        insertDict(db, "Кетчуп", "sauce", "pcs");
        insertDict(db, "Майонез", "sauce", "pcs");
        insertDict(db, "Томатная паста", "sauce", "pcs");
        insertDict(db, "Огурцы консервированные", "canned", "pcs");
        insertDict(db, "Тунец консервированный", "canned", "pcs");
        insertDict(db, "Кукуруза консервированная", "canned", "pcs");
        insertDict(db, "Горошек консервированный", "canned", "pcs");

        // Мясо, птица, рыба (вес, кг)
        insertDict(db, "Говядина", "meat", "kg");
        insertDict(db, "Свинина", "meat", "kg");
        insertDict(db, "Курица", "meat", "kg");
        insertDict(db, "Индейка", "meat", "kg");
        insertDict(db, "Фарш мясной", "meat", "kg");
        insertDict(db, "Рыба свежая", "fish", "kg");
        insertDict(db, "Рыба замороженная", "fish", "kg");

        // Колбасы и полуфабрикаты (шт.)
        insertDict(db, "Колбаса варёная", "sausage", "pcs");
        insertDict(db, "Колбаса полукопчёная", "sausage", "pcs");
        insertDict(db, "Сосиски", "sausage", "pcs");
        insertDict(db, "Сардельки", "sausage", "pcs");
        insertDict(db, "Пельмени", "semi_finished", "pcs");
        insertDict(db, "Вареники", "semi_finished", "pcs");

        // Напитки (шт.)
        insertDict(db, "Вода питьевая", "drinks", "pcs");
        insertDict(db, "Вода минеральная", "drinks", "pcs");
        insertDict(db, "Сок", "drinks", "pcs");
        insertDict(db, "Газировка", "drinks", "pcs");
        insertDict(db, "Чай чёрный", "drinks", "pcs");
        insertDict(db, "Чай зелёный", "drinks", "pcs");
        insertDict(db, "Кофе молотый", "drinks", "pcs");
        insertDict(db, "Кофе растворимый", "drinks", "pcs");

        // Хозяйственные товары (шт.)
        insertDict(db, "Туалетная бумага", "household", "pcs");
        insertDict(db, "Бумажные полотенца", "household", "pcs");
        insertDict(db, "Салфетки бумажные", "household", "pcs");
        insertDict(db, "Средство для мытья посуды", "household", "pcs");
        insertDict(db, "Стиральный порошок", "household", "pcs");
        insertDict(db, "Кондиционер для белья", "household", "pcs");
        insertDict(db, "Мешки для мусора", "household", "pcs");
        insertDict(db, "Губки для посуды", "household", "pcs");
    }

    private void insertDict(SQLiteDatabase db, String name, String category, String unit) {
        ContentValues cv = new ContentValues();
        cv.put(D_COL2, name);
        cv.put(D_COL3, category);
        cv.put(D_COL4, unit);
        db.insertWithOnConflict(DICT_TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void insertDictIfNotExists(String name, String category, String unit) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(DICT_TABLE_NAME, new String[]{D_COL1}, D_COL2 + "=?", new String[]{name}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        if (!exists) {
            ContentValues cv = new ContentValues();
            cv.put(D_COL2, name);
            cv.put(D_COL3, category);
            cv.put(D_COL4, unit);
            db.insert(DICT_TABLE_NAME, null, cv);
        }
    }

    public List<String> getAllDictionaryNames() {
        List<String> names = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DICT_TABLE_NAME, new String[]{D_COL2}, null, null, null, null, D_COL2 + " ASC");
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(D_COL2);
            do {
                if (nameIndex != -1) {
                    names.add(cursor.getString(nameIndex));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return names;
    }

    public String getUnitForName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DICT_TABLE_NAME, new String[]{D_COL4}, D_COL2 + "=?", new String[]{name}, null, null, null);
        String unit = null;
        if (cursor.moveToFirst()) {
            int unitIndex = cursor.getColumnIndex(D_COL4);
            if (unitIndex != -1) {
                unit = cursor.getString(unitIndex);
            }
        }
        cursor.close();
        return unit;
    }
}

