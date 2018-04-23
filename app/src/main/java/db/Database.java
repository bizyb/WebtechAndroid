package db;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TableRow;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import bizu.work.placessearch.SortBy;


public class Database  extends SQLiteOpenHelper{


    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_NAME = "PlacesSearch.db";

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
    private static final String COLUMN_FAV_INSERTION_ORDER = "fav_insertion_order";

    //Applicable to Details
    private static final String COLUMN_PHONE_NUMBER = "phone_number";
    private static final String COLUMN_PRICE_LEVEL = "price_level";
    private static final String COLUMN_RATING = "rating";
    private static final String COLUMN_GOOGLE_PAGE = "google_page";
    private static final String COLUMN_WEBSITE = "website";
    private static final String COLUMN_PHOTOS = "photos";



    // Applicable to Reviews and Details
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
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";

//    private static final String COLUMN_DETAILS_PLACE = "details_place";


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
                            + COLUMN_FAV_INSERTION_ORDER + " INTEGER,"
                            + COLUMN_RATING + " REAL,"
                            + COLUMN_PHOTOS + " TEXT,"
                            + COLUMN_LATITUDE + " REAL,"
                            + COLUMN_LONGITUDE + " REAL,"
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
            + COLUMN_PLACE_ID + " TEXT" + ")";



    private String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NEARBY_PLACES;
    private String DROP_TABLE_REVIEWS = "DROP TABLE IF EXISTS " + TABLE_REVIEWS;

    private Context context;
    private String delim;
    private String placeName;
    private String placeID;


    public Database(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.delim = "::::::::";
        this.placeName = "?";
        this.placeID = "?";
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
        else if (cursorFor.equals("name")) {

            col = COLUMN_NAME;
        }
        else if (cursorFor.equals("photos")) {

            col = COLUMN_PHOTOS;
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



    private void  saveReviewsToDB(JSONArray reviews, String placeID, String reviewsFrom) {

        // parse out the source
        // parse out the place id

//        String source = reviewsFrom;
//        String authorName;
//        String authorURL;
//        String language;
//        String avatr;
//        String relativeTime;
//        String text;
//        String formattedTime;
//        int defaultIndex;
//        int epoch;
//        int rating;



        SQLiteDatabase db = this.getWritableDatabase();
        try {

            for (int i = 0; i < reviews.length(); i++) {

                JSONArray row = reviews.getJSONArray(i);

                String authorName = row.getString(0);
                String authorURL = row.getString(1);
                String language = row.getString(2);
                String avatr = row.getString(3);
                int rating = row.getInt(4);
                String relativeTime = row.getString(5);
                String text = row.getString(6);
                int epoch = row.getInt(7);
                String formattedTime = row.getString(8);
                int defaultIndex = row.getInt(10);


                ContentValues values = new ContentValues();
                values.put(COLUMN_AUTHOR_NAME, authorName);
                values.put(COLUMN_AUTHOR_URL, authorURL);
                values.put(COLUMN_LANGUAGE, language);
                values.put(COLUMN_AVATAR, avatr);
                values.put(COLUMN_RATING, rating);
                values.put(COLUMN_RELATIVE_TIME, relativeTime);
                values.put(COLUMN_TEXT, text);
                values.put(COLUMN_EPOCH_TIME, epoch);
                values.put(COLUMN_FORMATTED_TIME, formattedTime);
                values.put(COLUMN_REVIEW_SOURCE, reviewsFrom);
                values.put(COLUMN_DEFAULT_INDEX, defaultIndex);
                values.put(COLUMN_PLACE_ID, placeID);

                db.insert(TABLE_REVIEWS, null, values);


            }

        }
        catch(Exception e){
            // TODO: output no results/failed to get results error here
            Log.e("error", e.toString());
            Log.i("in saveReviewsToDB", "-------saveReviewsToDB-----------ERROR-----------------------");
        }

        finally {
            db.close();
        }


    }

    /*
    * Save details info to the database. Note: this routine is executed during the transition from
    * ResultsActivity to DetailsActivity. The information for details is incomplete at this stage.
    * Later, we populate the missing yelp reviews asynchronously.
    *
    * PRE: place already exists in the database
    * */
    public void saveDetailsToDB(String response) {

        double latitude;
        double longitude;
        double rating;
        int price_level;
        String formatted_address;
        String formatted_phone_number;
        String name;
        String google_page;
        String website;
        JSONArray photosArray;
        JSONArray googleReviews;
        String photosStr;
        String place_id;

        JSONObject result = new JSONObject();
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            JSONObject responseJSON = new JSONObject(response);
            result = responseJSON.getJSONObject("result");



            latitude = responseJSON.getDouble("centerLat");
            longitude = responseJSON.getDouble("centerLon");
            rating = result.getDouble("rating");
            price_level = result.getInt("price_level");
            formatted_address = result.getString("formatted_address");
            formatted_phone_number = result.getString("formatted_phone_number");
            name = result.getString("name");
            google_page = result.getString("url");
            place_id = result.getString("place_id");
            website = result.getString("website");
            photosArray = responseJSON.getJSONArray("photosArray");
            photosStr = mergePhotoURLs(photosArray);

            String key = "google_reviews_" + place_id;
            googleReviews = responseJSON.getJSONArray(key);


//            Log.i("in saveDetailsToDB", "-------price_level---------------------------------: " + price_level + "");
//            Log.i("in saveDetailsToDB", "-------place_id---------------------------------: " + place_id);
//            Log.i("in saveDetailsToDB", "-------formatted_phone_number---------------------------------: " + formatted_phone_number);
//            Log.i("in saveDetailsToDB", "-------photosStr---------------------------------: " + photosStr);
//
//
            ContentValues values = new ContentValues();

            this.placeName = name;
            this.placeID = place_id;

            values.put(COLUMN_LATITUDE, latitude);
            values.put(COLUMN_LONGITUDE, longitude);
            values.put(COLUMN_RATING, rating);
            values.put(COLUMN_PRICE_LEVEL, price_level);
            values.put(COLUMN_VICINITY, formatted_address);
            values.put(COLUMN_PHONE_NUMBER, formatted_phone_number);
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_GOOGLE_PAGE, google_page);
            values.put(COLUMN_WEBSITE, website);
            values.put(COLUMN_PHOTOS, photosStr);
//            values.put(COLUMN_PLACE_ID, place_id);

            db.update(TABLE_NEARBY_PLACES, values, COLUMN_PLACE_ID + "= ?",
                    new String[] {place_id});

            saveReviewsToDB(googleReviews, place_id, "Google");
    }
     catch(Exception e){
        // TODO: output no results/failed to get results error here
        Log.e("error", e.toString());
        Log.i("in saveDetailsToDB", "-------saveDetailsToDB-----------ERROR-----------------------");
    }

    finally {
            db.close();
        }

    }

    /*
    * Merge all photo URLs into a single string with a common delimiter.
    * */
    private String mergePhotoURLs(JSONArray photosArray) {

        String photos = "";

        try {

            for (int i = 0; i < photosArray.length(); i++) {

                if (i == photosArray.length() - 1) {

                    photos += photosArray.getString(i);
                }
                else {
                    photos += photosArray.getString(i) + delim;
                }

            }

        }
        catch(Exception e){
            // TODO: output no results/failed to get results error here
            Log.e("error", e.toString());
            Log.i("in mergePhotoURLs", "mergePhotoURLs--------------------ERROR--------------");
        }

        return photos;
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
        int favInsertionOrder = getCount("favorites");


        CursorContainer container = getCursor(placeID, "favorites");
        SQLiteDatabase db = container.db();
        Cursor cursor = container.cursor();

        ContentValues values = new ContentValues();
        try {

            while(cursor.moveToNext()) {

                int index = cursor.getColumnIndex(COLUMN_FAVORITED);
                state = cursor.getInt(index);

                if (state == 0) {

                    favInsertionOrder++;
                    values.put(COLUMN_FAVORITED, 1);
                    values.put(COLUMN_FAV_INSERTION_ORDER, favInsertionOrder);
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
//        Log.i("in addEntry", " ----entry---------------------  " + name  + ": " + insertionOrder + "");

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

                values.put(COLUMN_INSERTION_ORDER, insertionOrder);
                db.update(TABLE_NEARBY_PLACES, values, COLUMN_PLACE_ID + "= ?",
                        new String[] {place_id});

//            Log.i("in addEntry", " ----exists new insertion order---------------------  " + name  + ": " + insertionOrder + "");

        }
        db.close();
    }



    public JSONArray getDBPage(int pageNum, String pageFor) {

        JSONArray results = new JSONArray();

        String query = "SELECT  * FROM " + TABLE_NEARBY_PLACES + " ORDER BY " + COLUMN_INSERTION_ORDER;
        if (pageFor.equals("favorites")) {

            query = "SELECT  * FROM " + TABLE_NEARBY_PLACES + " WHERE " + COLUMN_FAVORITED + " =1" + " ORDER BY " + COLUMN_FAV_INSERTION_ORDER;

        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        final int PAGE_SIZE = 20;
        int i = 0;
        int start = (pageNum - 1) * PAGE_SIZE;
        int end = pageNum * PAGE_SIZE;




        try {
//            Log.i("in getDbPage", " about to enter while loop------------------------------- cursor count:   " + cursor.getCount() + "");
            while (cursor.moveToNext()) {
//                Log.i("in getDbPage", "inside loop: i = ------------------------------    " + i + "");
//                Log.i("in getDbPage", "inside loop: start = ------------------------------    " + start + "");
//                Log.i("in getDbPage", "inside loop: end = ------------------------------    " + end + "");
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

    public JSONArray getSortedReviews(String placeID, String reviewsFrom, SortBy sortBy) {


        JSONArray reviews = new JSONArray();

        String query = "SELECT  * FROM " + TABLE_REVIEWS + " WHERE " + COLUMN_REVIEW_SOURCE + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[] {reviewsFrom});

        try {

            JSONObject row = new JSONObject();

//            String author = "Kai Colucci";
//            String authorURL = "https://www.google.com/maps/contrib/111028753854868402896/reviews";
//            String avatar = "https://lh3.googleusercontent.com/-LDBFaAenwDc/AAAAAAAAAAI/AAAAAAAAAWA/OFIsFer2zZA/s128-c0x00000000-cc-rp-mo-ba4/photo.jpg";
//            String text = "Absolutely not worth the hour + wait but if it’s 30 minutes or so I’d say probably worth it. Just go to Lokal across the street for some drinks. \n\nPizza is incredible. My party discussed for quite some time what makes the crust so good and we came to the, not verified, conclusion that it must come from the bagel shop that is literally next door. \n\nAnd then it’s also a brewery! Only tried the ‘quarters only’ but it was 10/10. A bit expensive but worth it for sure.";
//            String date = "2018-03-17 1:46:13";
//            Integer rating = 3;

            while (cursor.moveToNext()) {

                String author = cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR_NAME));
                String authorURL = cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR_URL));
                String avatar = cursor.getString(cursor.getColumnIndex(COLUMN_AVATAR));
                String text = cursor.getString(cursor.getColumnIndex(COLUMN_TEXT));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_FORMATTED_TIME));
                Integer rating = cursor.getInt(cursor.getColumnIndex(COLUMN_RATING));

                row.put("author", author);
                row.put("authorURL", authorURL);
                row.put("avatar", avatar);
                row.put("text", text);
                row.put("date", date);
                row.put("rating", rating);

                reviews.put(row);

//                for (int i = 0; i < 10; i++) {
//                    reviews.put(row);
//                }


            }

//            row.put("author", author);
//            row.put("authorURL", authorURL);
//            row.put("avatar", avatar);
//            row.put("text", text);
//            row.put("date", date);
//            row.put("rating", rating);
//
//            for (int i = 0; i < 10; i++) {
//                reviews.put(row);
//            }

        }
        catch(Exception e){
            // TODO: output no results/failed to get results error here
            Log.d("error", e.toString());
            Log.i("in mergePhotoURLs", "mergePhotoURLs--------------------ERROR--------------");
        }
        finally {
            cursor.close();
            db.close();
        }

        return reviews;

    }

    public JSONObject getDetailsInfo(String place_id) {

        JSONObject row = new JSONObject();

        //TODO: save to db if for the first time; otherwise check fromDB flag
        // and act accordingly

//        ("SELECT EmployeeName FROM Employee WHERE EmpNo=?", new String[] {empNo + ""});

//        rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});

        String query = "SELECT  * FROM " + TABLE_NEARBY_PLACES + " WHERE " + COLUMN_PLACE_ID + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[] {place_id});

        try {

//            String name = "University of Southern California";
//            String vicinity = "Los Angeles, CA 90007, USA";
//            String phoneNumber = "(213) 740-2311";
//            String priceLevel = "$$";
//            String rating = "****";
//            String googlePage = "https://stackoverflow.com/questions/6763111/how-to-change-color-of-textview-hyperlink";
//            String website = "http://usc.edu";

            while (cursor.moveToNext()) {

                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String vicinity = cursor.getString(cursor.getColumnIndex(COLUMN_VICINITY));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER));
                String priceLevel = cursor.getInt(cursor.getColumnIndex(COLUMN_PRICE_LEVEL)) + "";
                String rating = cursor.getString(cursor.getColumnIndex(COLUMN_RATING)) + "";
                String googlePage = cursor.getString(cursor.getColumnIndex(COLUMN_GOOGLE_PAGE));
                String website = cursor.getString(cursor.getColumnIndex(COLUMN_WEBSITE));

//                String priceLevelStr = '$' * priceLevel + "";
                row.put("name", name);
                row.put("formatted_address", vicinity);
                row.put("formatted_phone_number", phoneNumber);
                row.put("price_level", priceLevel);
                row.put("rating", rating);
                row.put("url", googlePage);
                row.put("website", website);
            }

        }
        catch(Exception e){
            // TODO: output no results/failed to get results error here
            Log.d("error", e.toString());
        }
        finally {
            cursor.close();
            db.close();
        }

        return row;
    }

    public ArrayList<String> getDetailsPhotos(String placeID) {

        ArrayList<String> arr = new ArrayList<>();
        CursorContainer container = getCursor(placeID, "photos");
        SQLiteDatabase db = container.db();
        Cursor cursor = container.cursor();


//        if (fromDB) {

            try {

                while (cursor.moveToNext()) {

                    String URLs = cursor.getString(0);
                    String [] photosArray = URLs.split(delim);

                    arr = new ArrayList<>(Arrays.asList(photosArray));

//                    for (String url : arr) {
//
//                        Log.i("in getDetailsPhotos", "--------------------photo url--------------: " + url);
//                    }


//                    arr.add("https://www.hdwallpapers.in/walls/new_year_high_quality-wide.jpg");
//                    arr.add("https://www.hdwallpapers.in/walls/fantasy_world_of_flowers-wide.jpg");
//                    arr.add("https://www.hdwallpapers.in/walls/metro_city_eve-wide.jpg");
//                    arr.add("https://www.hdwallpapers.in/walls/metro_city_eve-wide.jpg");

                }
//                Log.i("in getDetailsPhotos", "--------------------cursor finished--------------");
            }

            catch(Exception e){
                // TODO: output no results/failed to get results error here
                Log.d("error", e.toString());
                Log.i("in getDetailsPhotos", "--------------------ERROR--------------");
            }
            finally {
                cursor.close();
                db.close();
            }

//        }
//        else {
//            //parse the response, save to db and return the jsonobj
//        }

        return arr;
    }

//    public String getPlaceName(String place_id) {
//
//        String name = "University of Southern California";
//        return name;
//    }
    public String getPlaceID() {

        return placeID;
    }

    public String getPlaceName(String placeID) {



        String name = "?";
        CursorContainer container = getCursor(placeID, "name");
        SQLiteDatabase db = container.db();
        Cursor cursor = container.cursor();

        while (cursor.moveToNext()) {

//            Log.i("in resultsFa", "resultsFavClickHandler--------------------columnName--------------: " + cursor.getColumnName(0));
            name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        }

        cursor.close();
        db.close();
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

    public int getCount(String...optional) {

        int count = 0;
        String query = "SELECT  * FROM " + TABLE_NEARBY_PLACES;

        if (optional.length > 0) {

            if (optional[0].equals("favorites")) {
                query = "SELECT  * FROM " + TABLE_NEARBY_PLACES + " WHERE " + COLUMN_FAVORITED + " =1";
            }
        }


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        count = cursor.getCount();

        cursor.close();
        db.close();
//        Log.i("count", count + "");
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
