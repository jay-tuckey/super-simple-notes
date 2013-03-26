package com.flightgearplanes.supersimplenotes;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;


@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class NoteWidget extends AppWidgetProvider {
	
	public static String EXTRA_WORD=
			"com.flightgearplanes.supersimplenotes.NOTE";

	@Override
	public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		for (int i=0; i<appWidgetIds.length; i++) {
			Intent svcIntent=new Intent(ctxt, ServiceNoteWidget.class);
		      
			svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
		      
			RemoteViews widget=new RemoteViews(ctxt.getPackageName(),
		                                          R.layout.widget_note);
		   
			widget.setRemoteAdapter(R.id.widgetList, svcIntent);

			Intent clickIntent=new Intent(ctxt, MainActivity.class);
			PendingIntent clickPI=PendingIntent
					.getActivity(ctxt, 0,
							clickIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);
		      
			widget.setPendingIntentTemplate(R.id.widgetList, clickPI);
			// Make the title of the widget clickable
			widget.setOnClickPendingIntent(R.id.widgetTitle, clickPI);
			widget.setOnClickFillInIntent(android.R.id.text1, clickIntent);
			
			appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
		}
		    
		super.onUpdate(ctxt, appWidgetManager, appWidgetIds);
	}
	
	

	
	
	
}
