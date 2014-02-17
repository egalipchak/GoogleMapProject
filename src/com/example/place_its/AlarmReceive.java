package com.example.place_its;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceive extends BroadcastReceiver {
	private NotificationManager mNotificationManager;
	 private Notification notification;   

	  @SuppressWarnings("deprecation")
	@Override
	 public void onReceive(Context context, Intent intent) {
	  // TODO Auto-generated method stub       
	      mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	      CharSequence from = intent.getStringExtra("Name");
	      CharSequence message = intent.getStringExtra("Description");
	      PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
	      notification = new Notification(R.drawable.ic_launcher,"Notification", System.currentTimeMillis());
	      notification.setLatestEventInfo(context, from, message, contentIntent);
	      mNotificationManager.notify(Integer.parseInt(intent.getExtras().get("NotifyCount").toString()), notification);        
	      Toast.makeText(context, "New Notification Received", Toast.LENGTH_LONG).show(); 
	 }
}
