package db;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class Database  extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "PlacesSearch.db";

    private static final String TABLE_NAME = "webtech";

    private static final String COLUMN_PRIMARY_KEY = "id";
    private static final String COLUMN_PLACE_ID = "place_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_VICINITY = "vicinity";
    private static final String COLUMN_CATEGORY_ICON = "category_icon";
    private static final String COLUMN_FAVORITED = "favorited";
    private static final String COLUMN_PAGE_NUM = "page_num";

    private String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                            + COLUMN_PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + COLUMN_PLACE_ID + " TEXT,"
                            + COLUMN_NAME + " TEXT,"
                            + COLUMN_CATEGORY_ICON + " TEXT,"
                            + COLUMN_FAVORITED + " INTEGER,"
                            + COLUMN_PAGE_NUM + " INTEGER,"
                            + COLUMN_VICINITY + " TEXT" + ")";

    private String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public Database(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE);
        Log.d("db", CREATE_TABLE);

    }

    @Override
    public  void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void dropRows() {

        // remove all existing entries from the db except for the ones that have been
        // favorited

        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_FAVORITED + "=?";
        db.delete(TABLE_NAME, whereClause, new String[]{"0"});



    }

    private boolean rowExists(String place_id) {

        // a row may already exist because it's been favorited

//        Log.i("exists", "inside rowExists");
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_PLACE_ID + "=?";
        String[] selectionArgs = { place_id };

        String[] columns = {
                COLUMN_PRIMARY_KEY
        };

        Cursor cursor = db.query(TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0){
            return true;
        }
        return false;

    }

    public void addEntry(String place_id, String name, String vicinity,
                         int favorited, String category_icon) {

        rowExists(place_id);

        if (!rowExists(place_id)) {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_PLACE_ID, place_id);
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_FAVORITED, favorited); // initially they're all false
            values.put(COLUMN_PAGE_NUM, getPageNum());
            values.put(COLUMN_CATEGORY_ICON, category_icon);
            values.put(COLUMN_VICINITY, vicinity);

            db.insert(TABLE_NAME, null, values);
            db.close();
        }
    }


//
    public int getPageNum() {

        // Determine the page number of a new entry based on the number of entries
        // already in the database
        // maximum pageNum is 3 for Google NearbyPlaces search

        final int PAGE_SIZE = 20;
        int pageNum;

        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        if (count <= PAGE_SIZE) { pageNum = 1; }
        else if (count <= PAGE_SIZE * 2) { pageNum = 2; }
        else {pageNum = 3; }

        Log.i("pageNum", pageNum + "");
        Log.i("count", count + "");
        return pageNum;

    }
}


//    public void addUser(User user){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_USER_NAME, user.getName());
//        values.put(COLUMN_USER_EMAIL, user.getEmail());
//        values.put(COLUMN_USER_PASSWORD, user.getPassword());
//
//        db.insert(TABLE_USER, null, values);
//        db.close();
//    }

//    public boolean checkUser(String email){
//        String[] columns = {
//                COLUMN_USER_ID
//        };
//        SQLiteDatabase db = this.getWritableDatabase();
//        String selection = COLUMN_USER_EMAIL + " = ?";
//        String[] selectionArgs = { email };
//
//        Cursor cursor = db.query(TABLE_USER,
//                columns,
//                selection,
//                selectionArgs,
//                null,
//                null,
//                null);
//        int cursorCount = cursor.getCount();
//        cursor.close();
//        db.close();
//
//        if (cursorCount > 0){
//            return true;
//        }
//        return false;
//    }
//
//    public boolean checkUser(String email, String password){
//        String[] columns = {
//                COLUMN_USER_ID
//        };
//        SQLiteDatabase db = this.getWritableDatabase();
//        String selection = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " =?";
//        String[] selectionArgs = { email, password };
//
//        Cursor cursor = db.query(TABLE_USER,
//                columns,
//                selection,
//                selectionArgs,
//                null,
//                null,
//                null);
//        int cursorCount = cursor.getCount();
//        cursor.close();
//        db.close();
//
//        if (cursorCount > 0){
//            return true;
//        }
//        return false;
//    }

