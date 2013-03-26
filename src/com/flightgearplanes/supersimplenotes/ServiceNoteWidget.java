package com.flightgearplanes.supersimplenotes;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViewsService;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ServiceNoteWidget extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return(new NoteWidgetFactory(this.getApplicationContext(),
				intent));
	}
}
