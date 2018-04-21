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

    private static final String DATABASE_NAME = "PlacesSearch.db";

    private static final String TABLE_NEARBY_PLACES = "webtech";
    private static final String TABLE_REVIEWS = "reviews";

    private static final String COLUMN_PRIMARY_KEY = "id";
    private static final String COLUMN_PLACE_ID = "place_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_VICINITY = "vicinity";
    private static final String COLUMN_CATEGORY_ICON = "category_icon";
    private static final String COLUMN_FAVORITED = "favorited";
    private static final String COLUMN_PAGE_NUM = "page_num";

    //Applicable to Details
    private static final String COLUMN_PHONE_NUMBER = "phone_number";
    private static final String COLUMN_PRICE_LEVEL = "price_level";
    private static final String COLUMN_RATING = "rating";
    private static final String COLUMN_GOOGLE_PAGE = "google_page";
    private static final String COLUMN_WEBSITE = "website";


    // Applicable to Reviews
    //private static final String COLUMN_PLACE_ID = "place_id";
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
                            + COLUMN_RATING + " REAL,"
                            + COLUMN_GOOGLE_PAGE + " TEXT,"
                            + COLUMN_WEBSITE + " TEXT,"
                            + COLUMN_VICINITY + " TEXT" + ")";

    private String CREATE_REVIEWS_TABLE = "CREATE TABLE " + TABLE_REVIEWS + "("
            + COLUMN_PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PLACE_ID + " TEXT,"
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
            +  "FOREIGN KEY(" + COLUMN_DETAILS_PLACE + "_id) REFERENCES "+ TABLE_NEARBY_PLACES + "(id))";



    private String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NEARBY_PLACES;
    private String DROP_TABLE_REVIEWS = "DROP TABLE IF EXISTS " + TABLE_REVIEWS;

    public Database(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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

    }

    private boolean rowExists(String place_id) {

        // a row may already exist if it's been favorited

        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_PLACE_ID + "=?";
        String[] selectionArgs = { place_id };

        String[] columns = {
                COLUMN_PRIMARY_KEY
        };

        Cursor cursor = db.query(TABLE_NEARBY_PLACES,
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


        if (!rowExists(place_id)) {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_PLACE_ID, place_id);
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_FAVORITED, favorited); // initially they're all set to false
            values.put(COLUMN_PAGE_NUM, getPageNum());
            values.put(COLUMN_CATEGORY_ICON, category_icon);
            values.put(COLUMN_VICINITY, vicinity);

            db.insert(TABLE_NEARBY_PLACES, null, values);
            db.close();
        }
    }

    public JSONArray getDBPage(int pageNum) {

        JSONArray results = new JSONArray();

        String countQuery = "SELECT  * FROM " + TABLE_NEARBY_PLACES;
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

                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String vicinity = cursor.getString(cursor.getColumnIndex("vicinity"));
                    String iconURL = cursor.getString(cursor.getColumnIndex("category_icon"));

                    try {
                        row.put("name", name);
                        row.put("vicinity", vicinity);
                        row.put("icon", iconURL);
                        results.put(row);
                    }
                    catch(Exception e){
                        // TODO: output no results/failed to get results error here
                        Log.d("error", e.toString());
                    }
               }
               if (i >= end) {break;}
                i++;
            }
        }
        finally {
            cursor.close();
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
            Integer rating = 5;

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
    public int getPageNum() {

        // Determine the page number of a new entry based on the number of entries
        // already in the database
        // maximum pageNum is 3 for Google NearbyPlaces search

        final int PAGE_SIZE = 20;
        int pageNum;

        String countQuery = "SELECT  * FROM " + TABLE_NEARBY_PLACES;
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

