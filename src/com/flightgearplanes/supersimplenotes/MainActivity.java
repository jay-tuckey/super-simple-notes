package com.flightgearplanes.supersimplenotes;

import java.util.List;
import com.flightgearplanes.supersimplenotes.R;
import com.flightgearplanes.supersimplenotes.DialogDeleteConfirmation.OnClickListener;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View.OnKeyListener;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnKeyListener, OnClickListener,
	LoaderCallbacks<Cursor>
{
	// Create variables for transferring data in intents
	public final static String CONTENTS = "com.flightgearplanes.supersimplenotes.first.CONTENTS";
	public final static String SELECTED_NOTE = "com.flightgearplanes.supersimplenotes.first.SELECTED_NOTE";
	public final static String SELECTED_NOTE_TITLE = "com.flightgearplanes.supersimplenotes.first.SELECTED_NOTE_TITLE";
	
	// Whole app debug tag
	public final static String TAG = "SuperSimpleNotes";
	
	private EditText inputNote;
	private int selectedNote = -1;
	private EditText inputContents;
  	private Toast toast;
  	private int idToDelete;
  	private Menu menu;
  	private ListView list;
  	private MenuItem saveButton;
  	private boolean doEnableMenu = false;
  	private ActionBar actionbar;
  	private boolean sizeLargeOrBigger = false;
  	private boolean v11OrGreater = false;
  	private SimpleCursorAdapter adapter;
  	
  	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
  	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
    
	    int openNote = getIntent().getIntExtra(NoteWidget.EXTRA_WORD, -1);

	    // Some stuff for the new method of loading notes:
	    getSupportLoaderManager().initLoader(NotesDataSourceV2.ALL_NOTES, null, this);

	    // Find the list in the view
	    list = (ListView) findViewById(R.id.list);
	    // Use the SimpleCursorAdapter to show the
	    // elements in a ListView
	    // Fields from the database (projection)
	    // Must include the _id column for the adapter to work
	    String[] from = { MySQLiteHelper.COLUMN_TITLE };
	    // Fields on the UI to which we map
	    int[] to = new int[] { R.id.label };
	    
	    adapter = new SimpleCursorAdapter(this, R.layout.main_list_row, null, from, to, 0);
	    list.setAdapter(adapter);
	    
	    
	    // Open a note if called from the widget.
	    if(openNote != -1) {
	    	selectNoteById(openNote);
	    }
	    
	    
	    // Set up the android version
	    if(android.os.Build.VERSION.SDK_INT >= 11)
	    	v11OrGreater = true;
	    
	    // Set up action bar, if the android version is 11 or greater
	    if(v11OrGreater) {
	    	actionbar = getActionBar();
	    	actionbar.setSubtitle(R.string.click_note_to_open);
	    }
	    
	    
	    
	    // Set up a listener, so that when the done key is pressed on the keyboard, the current 
	    // note name is completed and added
	    inputNote = (EditText) findViewById(R.id.note);
	    inputNote.setImeOptions(EditorInfo.IME_ACTION_DONE);
	    inputNote.setOnEditorActionListener(new OnEditorActionListener() {
		
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					onClick(findViewById(R.id.add));
				}
				return false;
			} });
	    
	    // Set up a boolean variable to indicate whether the size of the device
	    // is large or bigger
	    if (((getResources().getConfiguration().screenLayout & 
  				Configuration.SCREENLAYOUT_SIZE_MASK) >= 
  				Configuration.SCREENLAYOUT_SIZE_LARGE)) {
	    	sizeLargeOrBigger = true;
	    	inputContents = (EditText) findViewById(R.id.noteContents);
	    }
	    
	    
	    // Set up an item clicked listener for the list
	    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	@Override
	    	public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
	    		onListItemClick(index);
	    	}
		});
	    // Set up a long click listener
	    list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
		
	    	@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v, int index, long arg3) {
	    		onDeleteLongClick(index, arg3);	
	    		return true;
	    	}
	    });
    
  	}
  
  	public boolean onCreateOptionsMenu(Menu menu) {
  		this.menu = menu;
  		if (sizeLargeOrBigger) {
  			// on a large screen device ...
  			MenuInflater menuInflater = getMenuInflater();
  			menuInflater.inflate(R.menu.main, menu);
  			saveButton = menu.getItem(0);
  		
  			// Check if the menu should be left enabled. If this isn't set, the menu will be
  			// disabled after using the widget
  			if(!doEnableMenu)
  				saveButton.setEnabled(false);
  		}
  		return true;
	}

  	// Will be called via the onClick attribute
  	// of the buttons in main.xml
  	public void onClick(View view) {

	    switch (view.getId()) {
	    case R.id.add:
	    	// Check that there is a name in the input box
	    	if(inputNote.getText().toString().length() != 0){
	    		Uri url = Uri.parse(NotesDataSourceV2.CONTENT_URI + "/" + NotesDataSourceV2.INSERT_NOTE);
	    		ContentValues values = new ContentValues();
	    		values.put(MySQLiteHelper.COLUMN_TITLE, inputNote.getText().toString());
	    		Uri newId = getContentResolver().insert(url, values);
	    		selectNote(newId);
	    		inputNote.setText("");
  				selectedNote = 0;
	    		if(v11OrGreater)
	    			
	    			updateWidget();
	    		// Refresh the list with the new item
	    		getSupportLoaderManager().restartLoader(NotesDataSourceV2.ALL_NOTES, null, this);
	    	} else {
	    		toast = Toast.makeText(this, R.string.nothingEntered, Toast.LENGTH_SHORT);
	    		toast.show();
	    	}
	    	
	    	break;
	    }
	    adapter.notifyDataSetChanged();
  	}
  
  	// Handle clicks on the menu, in particular the save button
  	public boolean onOptionsItemSelected(MenuItem menuItem) {
		// Handle item selection
	    switch (menuItem.getItemId()) {
	        case R.id.menu_save:
	        	saveNote();
	        	Toast.makeText(this, R.string.noteSaved, Toast.LENGTH_SHORT).show();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(menuItem);
	    }
	}
  
  	public void deleteNote(int id) {
  		if (list.getAdapter().getCount() > 0) {
  			Cursor cursor = (Cursor) list.getAdapter().getItem(id);
  			Uri uri = Uri.parse(NotesDataSourceV2.CONTENT_URI.toString());
  			getContentResolver().delete(uri,
  					MySQLiteHelper.COLUMN_ID + "=" + cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)),
  					null);
  			
  			// Remove the note from the list
  			getSupportLoaderManager().restartLoader(NotesDataSourceV2.ALL_NOTES, null, this);
  			
  			if(v11OrGreater)
  				updateWidget();
  		}
  	}
  
  	public void onDeleteLongClick(int position, long id) {
  		idToDelete = position;
  		DialogDeleteConfirmation deleteConfirmation = new DialogDeleteConfirmation();
  		deleteConfirmation.show(getSupportFragmentManager(), "test");
  	}
  
  	// Will be called when an item in the list is clicked on.
  	public void onListItemClick(int id) {
  		selectedNote = id;
  		Cursor cursor = (Cursor) list.getAdapter().getItem(id);
  		selectNoteById(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
  	}
  
  	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void selectNoteById(int id) {
  		
  		// Fetch the note's data
  		Uri uri = Uri.parse(NotesDataSourceV2.CONTENT_URI.toString());
  		Cursor cursor = getContentResolver().query(uri, NotesDataSourceV2.ALL_COLUMNS, MySQLiteHelper.COLUMN_ID + "='" + String.valueOf(id) + "'", null, null);
  		cursor.moveToFirst();

  		if (sizeLargeOrBigger) {
  			// on a large screen device ...
  			setViewerText(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CONTENTS)));
  			actionbar = getActionBar();
  			actionbar.setTitle(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_TITLE)));
  			actionbar.setSubtitle(R.string.press_save_to_close);
  			// Check if the menu is defined. If this isn't done, the clicking on the widget
  			// will crash the program
  			if(menu != null) {
  				saveButton.setEnabled(true);
  			} else {
  				doEnableMenu = true;
  			}
  		} else {
  		
  			Intent intent = new Intent(this, ActivityItemViewer.class);
  			intent.putExtra(CONTENTS, cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CONTENTS)));
  			intent.putExtra(SELECTED_NOTE, cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
  			intent.putExtra(SELECTED_NOTE_TITLE, cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_TITLE)));
  			startActivity(intent);
 		
  		}
  		cursor.close();
  	}
  	
  	private void selectNote(Uri id) {
  		List<String> segments = id.getPathSegments();
  		try {
  			selectNoteById(Integer.parseInt(segments.get(segments.size() - 1)) );
  		}
  		catch (NumberFormatException nfe) {
  			Log.wtf(TAG, "Number passed to SelectNoteById failed to parse!! WTF?");
  		}
  	}
  	
  	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void closeNote() {
  		saveButton.setEnabled(false);
		EditText viewer = (EditText) findViewById(R.id.noteContents);
		viewer.setEnabled(false);
		viewer.setText("");
		actionbar = getActionBar();
		actionbar.setTitle(R.string.app_name);
		actionbar.setSubtitle(R.string.click_note_to_open);
  	}
  	

  
  	private void setViewerText(String text) {
  		EditText viewer = (EditText) findViewById(R.id.noteContents);
  		viewer.setEnabled(true);
  		viewer.setText(text);
  	}

  	@Override
  	protected void onResume() {
  		getSupportLoaderManager().restartLoader(NotesDataSourceV2.ALL_NOTES, null, this);
  		super.onResume();
  	}

  
  
  	@Override
  	protected void onPause() {
  		super.onPause();
  	}

  	@Override
  	public boolean onKey(View view, int keyCode, KeyEvent event) {
	  
  		if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction()==0) {
  			onClick(findViewById(R.id.add));
  		}
  		return false;
  	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onClick(DialogInterface dialog, int which) {
		deleteNote(idToDelete);
		if (sizeLargeOrBigger) {
			// on a large screen device ...
			closeNote();
		}
	}
	
	private void saveNote() {

		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_CONTENTS, inputContents.getText().toString());
		Cursor cursor = (Cursor) list.getAdapter().getItem(selectedNote);
		int test = getContentResolver().update(NotesDataSourceV2.CONTENT_URI, values,
				MySQLiteHelper.COLUMN_ID +"="+ cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)),
				null);
		Log.w(TAG, "Updated rows: " + String.valueOf(test));
		Log.w(TAG, "EditText: " + inputContents.getText().toString());
		Log.w(TAG, "Selected Note: " + String.valueOf(selectedNote));
   		closeNote();
   		getSupportLoaderManager().restartLoader(NotesDataSourceV2.ALL_NOTES, null, this);
	}
	
	// A function to update the widgets' lists
	@SuppressLint("NewApi")
	private void updateWidget() {
		// Get the list of widgets
		int widgetIDs[] = AppWidgetManager.getInstance(
				getApplication()).getAppWidgetIds(new ComponentName(getApplication(), NoteWidget.class));
		// Trigger the data change function
		AppWidgetManager.getInstance(getApplication()).notifyAppWidgetViewDataChanged(widgetIDs, R.id.widgetList);
	}

	
	
	
	// This function will load the notes from the database without locking up the
	// UI
	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle argumentBundle) {
		// Pick the action to take
		
		switch(loaderID) {
		case NotesDataSourceV2.ALL_NOTES:
			return new CursorLoader(this, NotesDataSourceV2.CONTENT_URI,
					NotesDataSourceV2.ALL_COLUMNS,
					null,
					null,
					MySQLiteHelper.COLUMN_DATE_MODIFIED + " DESC");
					
		}						
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		
	    
	    adapter.swapCursor(data);
	     
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
		
	}

} 