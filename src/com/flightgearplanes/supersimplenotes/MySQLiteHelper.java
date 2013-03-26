package com.flightgearplanes.supersimplenotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

  public static final String TABLE_COMMENTS = "comments";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_ID_TYPE = "integer primary key autoincrement";
  public static final String COLUMN_TITLE = "title";
  public static final String COLUMN_TITLE_TYPE = "text not null";
  public static final String COLUMN_CONTENTS = "contents";
  public static final String COLUMN_CONTENTS_TYPE = "text not null";
  public static final String COLUMN_DATE_MODIFIED = "dateModified";
  public static final String COLUMN_DATE_MODIFIED_TYPE = "integer not null";

  private static final String DATABASE_NAME = "commments.db";
  private static final int DATABASE_VERSION = 2;

  // Database creation sql statement
  private static final String DATABASE_CREATE = "create table "
      + TABLE_COMMENTS + "(" + COLUMN_ID + " " + COLUMN_ID_TYPE
      + ", " + COLUMN_TITLE + " " + COLUMN_TITLE_TYPE + ", "
      + COLUMN_CONTENTS + " " + COLUMN_CONTENTS_TYPE + ", "
      + COLUMN_DATE_MODIFIED + " " + COLUMN_DATE_MODIFIED_TYPE + ");";

  public MySQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	  Log.w(MySQLiteHelper.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion);
	  switch (oldVersion) {
	  case 1:
		  switch (newVersion) {
		  case 2:
			  db.execSQL("alter table " + TABLE_COMMENTS + " rename to old" + TABLE_COMMENTS);
			  db.execSQL(DATABASE_CREATE);
			  db.execSQL("insert into " + TABLE_COMMENTS + " select *, '0' from old" + TABLE_COMMENTS);
			  db.execSQL("drop table old" + TABLE_COMMENTS);
			  break;

		  default:
			  destroyAndRecreate(db);
			  break;
		  }
		  break;

	  default:
		  destroyAndRecreate(db);
		  break;
	  }
	  
	  

  }
  
  private void destroyAndRecreate(SQLiteDatabase db) {
	  db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
	  onCreate(db);
  }

} 