package com.example.place_its;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class StatusBar extends Activity implements OnClickListener {
	NotificationManager nm;
	static final int uniqueID = 12323;
	protected void onCreate( Bundle b ) {
		super.onCreate( b );
		setContentView( R.layout.statusbar );
		Button stat = (Button)findViewById( R.id.b );
		stat.setOnClickListener( this );
		nm = (NotificationManager) getSystemService( NOTIFICATION_SERVICE );
		nm.cancel( uniqueID );
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent( this, StatusBar.class);
		PendingIntent pi = PendingIntent.getActivity( this, 0, intent, 0);
		
		String body = "You have a reminder for this location";
		String title = "Place-its";
		
		@SuppressWarnings("deprecation")
		Notification n = new Notification( R.drawable.ic_launcher, body, System.currentTimeMillis() );
		
		n.setLatestEventInfo( this, title, body, pi );
		n.defaults = Notification.DEFAULT_ALL;
		nm.notify(uniqueID, n );
		finish();
		
	}
	
	
}
