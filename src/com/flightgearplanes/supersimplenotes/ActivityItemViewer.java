package com.flightgearplanes.supersimplenotes;



import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityItemViewer extends FragmentActivity {


	private EditText inputContents;
	private int selectedNoteId;
	private String selectedNoteTitle;
	private ActionBar actionbar;
	  
	
	@SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        // Yadeydah, the Activity starts with the normal stuff.
		super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_itemviewer);
        

        
        // Receive the intent data
        Intent intent = getIntent();
        String contents = intent.getStringExtra(MainActivity.CONTENTS);
        selectedNoteId = intent.getIntExtra(MainActivity.SELECTED_NOTE, 0);
        selectedNoteTitle = intent.getStringExtra(MainActivity.SELECTED_NOTE_TITLE);
        
        // And set our note editor to be enabled, and contain the contents of the intent
        EditText itemViewer = (EditText) findViewById(R.id.noteContents);
        itemViewer.setEnabled(true);
        itemViewer.setText(contents);
        
        // Set up action bar, but only if we're on ICS or greater
        if(android.os.Build.VERSION.SDK_INT >= 14) {
        	actionbar = getActionBar();
        	actionbar.setTitle(getString(R.string.editing_in_progress) + " " + selectedNoteTitle);
        	//actionbar.setTitle(getString(R.string.editing_in_progress) + " " + note.getTitle());
  			actionbar.setSubtitle(R.string.press_save_to_close);
        } else {
        	setTitle(getString(R.string.editing_in_progress) + " " + selectedNoteTitle);
        }
	    
	}
	
	// To create the Action Bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();  // Always call the superclass
	}
    
	// Catch clicks

	public boolean onOptionsItemSelected(MenuItem menuItem) {
		// Handle item selection
	    switch (menuItem.getItemId()) {
	        case R.id.menu_save:
	        	// No call to saveNote(), since that will be called by onPause()
	        	Toast.makeText(this, R.string.noteSaved, Toast.LENGTH_SHORT).show();
	        	finish();
	    		
	        	return true;
	        default:
	            return super.onOptionsItemSelected(menuItem);
	    }
	}
	
	private void saveNote() {
		inputContents = (EditText) findViewById(R.id.noteContents);
		
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_CONTENTS, inputContents.getText().toString());
		getContentResolver().update(NotesDataSourceV2.CONTENT_URI,
				values, MySQLiteHelper.COLUMN_ID + "=" + String.valueOf(selectedNoteId), null);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	  
	  
	@Override
	protected void onPause() {
		saveNote();
		Toast.makeText(this, R.string.noteSaved, Toast.LENGTH_SHORT).show();
		super.onPause();
	}

	
	
}
