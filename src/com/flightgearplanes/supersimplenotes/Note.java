package com.flightgearplanes.supersimplenotes;

public class Note {
	private long id;
	private String title;
	private String contents;
	
	public Note() {};
	public Note(int id, String title, String contents) {
		this.id = id;
		this.title = title;
		this.contents = contents;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	  
	public String getContents() {
		return contents;
	}
	  
	public void setContents(String contents) {
		this.contents = contents;
	}
	  
	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return title;
	}
}
