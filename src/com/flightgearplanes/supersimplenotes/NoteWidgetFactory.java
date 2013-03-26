package com.flightgearplanes.supersimplenotes;



import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class NoteWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
	private Cursor notes;
	private Context context = null;
	@SuppressWarnings("unused")
	private int widgetId;

	
	public NoteWidgetFactory(Context ctxt, Intent intent) {
		this.context = ctxt;
		widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

		
	
		notes = context.getContentResolver().query(NotesDataSourceV2.CONTENT_URI,
				NotesDataSourceV2.ALL_COLUMNS, null, null, MySQLiteHelper.COLUMN_DATE_MODIFIED + " DESC");
		
	}
	
	@Override
	public int getCount() {
		return(notes.getCount());
	}

	@Override
	public long getItemId(int position) {
		return(position);
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public RemoteViews getViewAt(int position) {
		RemoteViews row=new RemoteViews(context.getPackageName(),
				R.layout.widget_row);
		
		notes.moveToPosition(position);
		row.setTextViewText(android.R.id.text1, notes.getString(notes.getColumnIndex(MySQLiteHelper.COLUMN_TITLE)));

		Intent intent=new Intent();
		Bundle extras=new Bundle();
		
		extras.putInt(NoteWidget.EXTRA_WORD, notes.getInt(notes.getColumnIndex(MySQLiteHelper.COLUMN_ID))); //(int) notes.getInt(0)); // get(position).getId());
		intent.putExtras(extras);
		row.setOnClickFillInIntent(android.R.id.text1, intent);

		return(row);
	}
	
	public Note getNoteWithId(int id) {
		/*
		for(int i = 0; i < notes.size(); i++) {
			if(notes.get(i).getId() == id)
				return(notes.get(i));
		}*/
		return null;
	}
	
	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void onCreate() {
		// no-op
		
	}

	@Override
	public void onDataSetChanged() {
		// no-op

		notes.close();
		notes = context.getContentResolver().query(NotesDataSourceV2.CONTENT_URI,
				NotesDataSourceV2.ALL_COLUMNS, null, null, MySQLiteHelper.COLUMN_DATE_MODIFIED + " DESC");

	}

	@Override
	public void onDestroy() {
		// no-op
		
	}
	
}
