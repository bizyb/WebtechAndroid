package db;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import bizu.work.placessearch.SortBy;


public class Database  extends SQLiteOpenHelper{


    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "abc.db";

    private static final String TABLE_NEARBY_PLACES = "nearby_places";
    private static final String TABLE_REVIEWS = "reviews";

    private static final String COLUMN_PRIMARY_KEY = "id";
    private static final String COLUMN_PLACE_ID = "place_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_VICINITY = "vicinity";
    private static final String COLUMN_CATEGORY_ICON = "category_icon";
    private static final String COLUMN_FAVORITED = "favorited";
    private static final String COLUMN_PAGE_NUM = "page_num";
    private static final String COLUMN_INSERTION_ORDER = "insertion_order";

    //Applicable to Details
    private static final String COLUMN_PHONE_NUMBER = "phone_number";
    private static final String COLUMN_PRICE_LEVEL = "price_level";
    private static final String COLUMN_RATING = "rating";
    private static final String COLUMN_GOOGLE_PAGE = "google_page";
    private static final String COLUMN_WEBSITE = "website";


    // Applicable to Reviews
    private static final String COLUMN_FOREIGN_KEY = TABLE_NEARBY_PLACES + "_id";
    private static final String COLUMN_AUTHOR_NAME = "author_name";
    private static final String COLUMN_AUTHOR_URL = "author_url";
    private static final String COLUMN_LANGUAGE = "language";
    private static final String COLUMN_AVATAR = "profile_photo_url";
    //private static final String COLUMN_RATING = "rating";
    private static final String COLUMN_RELATIVE_TIME = "relative_time";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_EPOCH_TIME = "EPOCH_time";
    private static final String COLUMN_FORMATTED_TIME = "formatted_time";
    private static final String COLUMN_REVIEW_SOURCE = "review_source";
    private static final String COLUMN_DEFAULT_INDEX = "default_index";
    private static final String COLUMN_DETAILS_PLACE = "details_place";


    private String CREATE_TABLE = "CREATE TABLE " + TABLE_NEARBY_PLACES + "("
                            + COLUMN_PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + COLUMN_PLACE_ID + " TEXT,"
                            + COLUMN_NAME + " TEXT,"
                            + COLUMN_CATEGORY_ICON + " TEXT,"
                            + COLUMN_FAVORITED + " INTEGER,"
                            + COLUMN_PAGE_NUM + " INTEGER,"
                            + COLUMN_PHONE_NUMBER + " TEXT,"
                            + COLUMN_PRICE_LEVEL + " INTEGER,"
                            + COLUMN_INSERTION_ORDER + " INTEGER,"
                            + COLUMN_RATING + " REAL,"
                            + COLUMN_GOOGLE_PAGE + " TEXT,"
                            + COLUMN_WEBSITE + " TEXT,"
                            + COLUMN_VICINITY + " TEXT" + ")";

    private String CREATE_REVIEWS_TABLE = "CREATE TABLE " + TABLE_REVIEWS + "("
            + COLUMN_PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_FOREIGN_KEY + " INTEGER,"
            + COLUMN_AUTHOR_NAME + " TEXT,"
            + COLUMN_AUTHOR_URL + " TEXT,"
            + COLUMN_LANGUAGE + " TEXT,"
            + COLUMN_AVATAR + " TEXT,"
            + COLUMN_RATING + " INTEGER,"
            + COLUMN_RELATIVE_TIME + " TEXT,"
            + COLUMN_TEXT + " TEXT,"
            + COLUMN_EPOCH_TIME + " INTEGER,"
            + COLUMN_FORMATTED_TIME + " TEXT,"
            + COLUMN_REVIEW_SOURCE + " TEXT,"
            + COLUMN_DEFAULT_INDEX + " INTEGER,"
            +  " FOREIGN KEY(" + COLUMN_FOREIGN_KEY + ") REFERENCES "+ TABLE_NEARBY_PLACES + "(_id))";



    private String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NEARBY_PLACES;
    private String DROP_TABLE_REVIEWS = "DROP TABLE IF EXISTS " + TABLE_REVIEWS;

    private Context context;

    public Database(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_REVIEWS_TABLE);

        Log.d("db", CREATE_TABLE);

    }

    @Override
    public  void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(DROP_TABLE);
        db.execSQL(DROP_TABLE_REVIEWS);
        onCreate(db);

    }

    public void dropRows() {

        // remove all existing entries from the db except for the ones that have been
        // favorited
        // TODO: does this do cascade deletion?

        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_FAVORITED + "=?";
        db.delete(TABLE_NEARBY_PLACES, whereClause, new String[]{"0"});
//        db.close();

    }

    /* Return a row cursor
    *
    * */
    private CursorContainer getCursor(String placeID, String cursorFor) {

        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_PLACE_ID + "=?";
        String[] selectionArgs = {placeID};

        String col = COLUMN_PRIMARY_KEY;

        if (cursorFor.equals("favorites")) {
            col = COLUMN_FAVORITED;
        }
        else if (cursorFor.equals(("insertionOrder"))) {

            col = COLUMN_INSERTION_ORDER;
        }
        String[] columns = {
                col
        };

        Cursor cursor = db.query(TABLE_NEARBY_PLACES,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);

        CursorContainer container = new CursorContainer(db, cursor);

        return container;

    }

    /*
    *  If placeID is already favorited, set it to false and return false.
    *  Otherwise, the place is being favorited for the first time so set the
    *  field to true and return true. Display a toast to indicate the addition or removal of the
    *  place from the list.
    *
    * */

    public boolean addToFav(String placeID) {

        boolean isFavorited = false;
        int state;

        CursorContainer container = getCursor(placeID, "favorites");
        SQLiteDatabase db = container.db();
        Cursor cursor = container.cursor();

        ContentValues values = new ContentValues();
        try {

            while(cursor.moveToNext()) {

                int index = cursor.getColumnIndex(COLUMN_FAVORITED);
                state = cursor.getInt(index);

                if (state == 0) {

                    values.put(COLUMN_FAVORITED, 1);
                    isFavorited = true;
                }
                else {
                    values.put(COLUMN_FAVORITED, 0);
                }
                db.update(TABLE_NEARBY_PLACES, values, COLUMN_PLACE_ID + "= ?",
                        new String[] {placeID});
            }

        }
        finally {
            cursor.close();
            db.close();
        }

        return isFavorited;

    }

    public boolean checkState(String placeID, String checkFor) {

        boolean state = false;
        CursorContainer container = getCursor(placeID, checkFor);
        SQLiteDatabase db = container.db();
        Cursor cursor = container.cursor();

        try {
            if (checkFor.equals("rowExistence")) {

                int cursorCount = cursor.getCount();
                if (cursorCount > 0) {
                    state = true;
                }
            } else if (checkFor.equals("favorites")) {


                while (cursor.moveToNext()) {

                    state = cursor.getInt(cursor.getColumnIndex(COLUMN_FAVORITED)) == 1;

                }
            }
        }
        finally {
            cursor.close();
            db.close();
        }

        return state;

    }

    public void addEntry(String place_id, String name, String vicinity,
                         int favorited, String category_icon, int...optional) {

        int insertionOrder = getCount();
        int pageNum = getPageNum();
        boolean rowExists = checkState(place_id, "rowExistence");
        if (optional.length > 0) {
            insertionOrder -= optional[0]; // offset
        }
        Log.i("in addEntry", " ----insertiong order and name---------------------  " + name  + ": " + insertionOrder + "");

        SQLiteDatabase db = this.getWritableDatabase();
        if (!rowExists) {



            ContentValues values = new ContentValues();
            values.put(COLUMN_PLACE_ID, place_id);
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_FAVORITED, favorited); // initially they're all set to false
            values.put(COLUMN_PAGE_NUM, pageNum);
            values.put(COLUMN_CATEGORY_ICON, category_icon);
            values.put(COLUMN_VICINITY, vicinity);
            values.put(COLUMN_INSERTION_ORDER, insertionOrder);
//
            db.insert(TABLE_NEARBY_PLACES, null, values);
//


        }
        else {
            // the row exists so update its insertion order
//            CursorContainer container = getCursor(place_id, "insertionOrder");
//            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

//            try {

                values.put(COLUMN_INSERTION_ORDER, insertionOrder);
                db.update(TABLE_NEARBY_PLACES, values, COLUMN_PLACE_ID + "= ?",
                        new String[] {place_id});
//            }
//            finally {
////                db.close();
//            }
        }
        db.close();
    }

    public JSONArray getDBPage(int pageNum) {

        JSONArray results = new JSONArray();

        String countQuery = "SELECT  * FROM " + TABLE_NEARBY_PLACES + " ORDER BY " + COLUMN_INSERTION_ORDER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        final int PAGE_SIZE = 20;
        int i = 0;
        int start = (pageNum - 1) * PAGE_SIZE;
        int end = pageNum * PAGE_SIZE;




        try {
            Log.i("in getDbPage", " about to enter while loop------------------------------- cursor count:   " + cursor.getCount() + "");
            while (cursor.moveToNext()) {
                Log.i("in getDbPage", "inside loop: i = ------------------------------    " + i + "");
                Log.i("in getDbPage", "inside loop: start = ------------------------------    " + start + "");
                Log.i("in getDbPage", "inside loop: end = ------------------------------    " + end + "");
               if (i >= start) {

                    //TODO: are these sorted by the primary key or are we just assuming?

                    JSONObject row = new JSONObject();


                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                    String vicinity = cursor.getString(cursor.getColumnIndex(COLUMN_VICINITY));
                    String iconURL = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_ICON));
                    String place_id = cursor.getString(cursor.getColumnIndex(COLUMN_PLACE_ID));

                    try {
                        row.put("name", name);
                        row.put("vicinity", vicinity);
                        row.put("icon", iconURL);
                        row.put("place_id", place_id);
                        results.put(row);
                    }
                    catch(Exception e){
                        // TODO: output no results/failed to get results error here
                        Log.e("error", e.toString());
                    }
               }
               if (i >= end) {break;}
                i++;
            }
        }
        finally {
            cursor.close();
            db.close();
        }



        return results;
    }

    public JSONArray getSortedReviews(String placeID, String source, SortBy sortBy) {


        JSONArray reviews = new JSONArray();

        //TODO: save to db if for the first time; otherwise check fromDB flag
        // and act accordingly

        try {

            JSONObject row = new JSONObject();

            String author = "Kai Colucci";
            String authorURL = "https://www.google.com/maps/contrib/111028753854868402896/reviews";
            String avatar = "https://lh3.googleusercontent.com/-LDBFaAenwDc/AAAAAAAAAAI/AAAAAAAAAWA/OFIsFer2zZA/s128-c0x00000000-cc-rp-mo-ba4/photo.jpg";
            String text = "Absolutely not worth the hour + wait but if it’s 30 minutes or so I’d say probably worth it. Just go to Lokal across the street for some drinks. \n\nPizza is incredible. My party discussed for quite some time what makes the crust so good and we came to the, not verified, conclusion that it must come from the bagel shop that is literally next door. \n\nAnd then it’s also a brewery! Only tried the ‘quarters only’ but it was 10/10. A bit expensive but worth it for sure.";
            String date = "2018-03-17 1:46:13";
            Integer rating = 3;
            row.put("author", author);
            row.put("authorURL", authorURL);
            row.put("avatar", avatar);
            row.put("text", text);
            row.put("date", date);
            row.put("rating", rating);

            for (int i = 0; i < 10; i++) {
                reviews.put(row);
            }

        }
        catch(Exception e){
            // TODO: output no results/failed to get results error here
            Log.d("error", e.toString());
        }

        return reviews;

    }

    public JSONObject getDetailsInfo(String place_id) {

        JSONObject row = new JSONObject();

        //TODO: save to db if for the first time; otherwise check fromDB flag
        // and act accordingly

        try {

            String name = "University of Southern California";
            String vicinity = "Los Angeles, CA 90007, USA";
            String phoneNumber = "(213) 740-2311";
            String priceLevel = "$$";
            String rating = "****";
            String googlePage = "https://stackoverflow.com/questions/6763111/how-to-change-color-of-textview-hyperlink";
            String website = "http://usc.edu";

            row.put("name", name);
            row.put("formatted_address", vicinity);
            row.put("formatted_phone_number", phoneNumber);
            row.put("price_level", priceLevel);
            row.put("rating", rating);
            row.put("url", googlePage);
            row.put("website", website);

        }
        catch(Exception e){
            // TODO: output no results/failed to get results error here
            Log.d("error", e.toString());
        }

        return row;
    }

    public ArrayList<String> getDetailsPhotos(String response, boolean fromDB) {

        ArrayList<String> arr = new ArrayList<>();
        if (fromDB) {

            try {

                arr.add("https://www.hdwallpapers.in/walls/new_year_high_quality-wide.jpg");
                arr.add("https://www.hdwallpapers.in/walls/fantasy_world_of_flowers-wide.jpg");
                arr.add("https://www.hdwallpapers.in/walls/metro_city_eve-wide.jpg");
                arr.add("https://www.hdwallpapers.in/walls/metro_city_eve-wide.jpg");

            }

            catch(Exception e){
                // TODO: output no results/failed to get results error here
                Log.d("error", e.toString());
            }

        }
        else {
            //parse the response, save to db and return the jsonobj
        }

        return arr;
    }

    public String getPlaceName(String place_id) {

        String name = "University of Southern California";
        return name;
    }


//
    private int getPageNum() {

        // Determine the page number of a new entry based on the number of entries
        // already in the database
        // maximum pageNum is 3 for Google NearbyPlaces search

        final int PAGE_SIZE = 20;
        int pageNum;

        String countQuery = "SELECT  * FROM " + TABLE_NEARBY_PLACES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();


        if (count <= PAGE_SIZE) { pageNum = 1; }
        else if (count <= PAGE_SIZE * 2) { pageNum = 2; }
        else {pageNum = 3; }

//        Log.i("pageNum", pageNum + "");
//        Log.i("count", count + "");

        cursor.close();
        db.close();
        return pageNum;

    }

    public int getCount() {

        int count = 0;

        String countQuery = "SELECT  * FROM " + TABLE_NEARBY_PLACES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();

        cursor.close();
        db.close();
        Log.i("count", count + "");
        return count;

    }

    /* A wrapper class to hold a cursor and db file descriptors so that the client can close
     *  both connections.
     */
    class CursorContainer {

        private SQLiteDatabase db;
        private Cursor cursor;

        public CursorContainer(SQLiteDatabase db,  Cursor cursor) {

            this.db = db;
            this.cursor = cursor;

        }

        public SQLiteDatabase db() { return db; }

        public Cursor cursor() { return cursor;}

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

