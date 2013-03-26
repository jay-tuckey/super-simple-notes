package com.flightgearplanes.supersimplenotes;

import java.util.Date;
import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class NotesDataSourceV2 extends ContentProvider {
	
	// Database
	private MySQLiteHelper database;
	
	// Final variables
	public static final int ALL_NOTES = 1;
	public static final int NOTE_ID = 2;
	public static final int SELECT_NOTE_ID = 3;
	
	public static final String NOTE_TABLE = "comments";
	public static final String[] ALL_COLUMNS = {MySQLiteHelper.COLUMN_ID,
		MySQLiteHelper.COLUMN_TITLE, MySQLiteHelper.COLUMN_CONTENTS};
	
	// Values for inserting notes
	public static final String INSERT_NOTE = "insertnote";
	public static final String ADD_NOTE_TITLE = "ADD_NOTE_TITLE";
	
	// Values for selection notes
	public static final String SELECT_NOTE = "selectnote";
	
	public static final String AUTHORITY = "com.flightgearplanes.supersimplenotes";

	private static final String BASE_PATH = "notes";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, ALL_NOTES);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/" + INSERT_NOTE, NOTE_ID);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/" + SELECT_NOTE + "/#", SELECT_NOTE_ID);
	}
	
	
	 
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case ALL_NOTES:
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		SQLiteDatabase db = database.getWritableDatabase();
		int numDeleted = db.delete(MySQLiteHelper.TABLE_COMMENTS, selection, null);
		Log.w(MainActivity.TAG, "Deleted items: " + String.valueOf(numDeleted));
		return numDeleted;
	}
	
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		// Check what we are inserting
		int uriType = sURIMatcher.match(uri);
		switch(uriType) {
		case NOTE_ID:
			values.put(MySQLiteHelper.COLUMN_CONTENTS, "");
			values.put(MySQLiteHelper.COLUMN_DATE_MODIFIED, new Date().getTime());
			SQLiteDatabase db = database.getWritableDatabase();
			long id = db.insert(MySQLiteHelper.TABLE_COMMENTS, null, values);
			Uri returnUri = Uri.parse(uri + "/" + String.valueOf(id));
			
			return returnUri;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}
	
	@Override
	public boolean onCreate() {
		database = new MySQLiteHelper(getContext());
		return false;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		// Using SQLiteQueryBuilder to create a query
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		// Check if the caller has requested a column which does not exist
		checkColumns(projection);
		
		// Set the table
		queryBuilder.setTables(NOTE_TABLE);
		
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case ALL_NOTES:
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		Date date = new Date();
		values.put(MySQLiteHelper.COLUMN_DATE_MODIFIED, date.getTime());
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case ALL_NOTES:
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		SQLiteDatabase db = database.getWritableDatabase();
		
		int numRows = db.update(MySQLiteHelper.TABLE_COMMENTS, values, selection, null);
		return numRows;
	}
	
	// A function to make sure that a non-existent column isn't
	// requested
	
	private void checkColumns(String[] projection) {
		if (projection != null) {
		      HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
		      HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(ALL_COLUMNS));
		      // Check if all columns which are requested are available
		      if (!availableColumns.containsAll(requestedColumns)) {
		        throw new IllegalArgumentException("Unknown columns in projection");
		      }
		    }
	}

	
}
