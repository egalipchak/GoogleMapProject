package com.example.place_its;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



/* new imports */
import android.content.DialogInterface;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

public class ShowReminderList extends Activity implements View.OnClickListener {
	private static final String LOGCAT =  "SHOWLIST";
    private List<Reminders> myActiveList = new ArrayList<Reminders>();
    private List<Reminders> myWaitingList = new ArrayList<Reminders>();
    static boolean check = false;
    Button add_bt;
    TextView location;
    ListView waitingList, activeList;
    
    DBAdapter db = new DBAdapter(this); 
    Reminders aRem;
    private static int year;
    private static int month;
    private static int day;
    
    static final int DATE_DIALOG = 999;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_reminder_list);
        getInit();			// Initialize the data field.ton       
        db.open();      
        Log.d( LOGCAT, "TEST DATABASE OPEN ");
    }
    
    
    public static boolean isExist() {
    	return check;
    }

    public void onClick( View view ) {
    	switch( view.getId() ) {
            case R.id.Bt_Add:
            	//Log.d( LOGCAT, "CLICK on Bt_Add");
            	AlertDialog.Builder alert = new AlertDialog.Builder( ShowReminderList.this );
            	alert.setTitle( "Add New Reminder" );
            	final EditText input = new EditText( ShowReminderList.this );
            	input.setHint( "Message" );
            	//Log.d( LOGCAT, "CREATE AlertDialog" );
            	alert.setView( input );
            	//Log.d( LOGCAT, "Set alert view");
            	
            	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            		@Override
            		public void onClick( DialogInterface dialog, int whichButton ) {
            			String msg = input.getText().toString();
            			Toast.makeText( ShowReminderList.this, "Added", Toast.LENGTH_SHORT).show();
            			
            	        showDialog(DATE_DIALOG);
            	        long id = db.insertReminder(msg, year, month, day);    
            	        Log.d( LOGCAT, year + "/" + month + "/" + day );
    					aRem = new Reminders( msg );
    					aRem.setId( id );
    					//check = true;
            			myActiveList.add( aRem );

            	        selectedList( activeList, myActiveList );
            	        
            		}
            	});
            	alert.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
            		@Override
            		public void onClick( DialogInterface dialog, int whichButton ) {
            			Toast.makeText( ShowReminderList.this, "Cancel", Toast.LENGTH_SHORT).show();
            			Log.d( LOGCAT, "Do nothing" );	
            		}
            	}); 
            	alert.show();
            	break;
        }
    }
    
    
    protected Dialog onCreateDialog( int id ) {
		switch( id ) {
		case DATE_DIALOG:
			return new DatePickerDialog( this, datePickerListener, year, month, day);
		}
    	return null;
    	
    }
    
    private DatePickerDialog.OnDateSetListener datePickerListener
    		= new DatePickerDialog.OnDateSetListener() {	
				@Override
				public void onDateSet(DatePicker view, int newYear, int monthOfYear,
						int dayOfMonth) {
						year = newYear;
						month = monthOfYear;
						day = dayOfMonth;
				}
			};

    /**
     * A Helper function that use to populate the arrayList.
     * @param aList will be displayed on the screen.
     * @param arrayList will contain reminders info that will be displayed.
     */
    private void selectedList( ListView aList, List<Reminders> arrayList) {
        // Display the Item in the ArrayList.
        ArrayAdapter<Reminders> arrAdapter = new ArrayAdapter<Reminders>( this,
                android.R.layout.simple_list_item_1, arrayList  );

        aList.setAdapter( arrAdapter );
        // Register onClickListener to handle click events on each item in the ArrayList
        aList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText( getApplicationContext(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT ).show();
                
                // Get a specific reminder on the list.
                final Reminders item = (Reminders)adapterView.getItemAtPosition( i );
                final long id = item.getId();
                AlertDialog.Builder alert = new AlertDialog.Builder( ShowReminderList.this );
            	alert.setTitle( "Update the Reminder" );
            	final EditText input = new EditText( ShowReminderList.this );
            	input.setText( item.getMessage() );
            	alert.setView( input );
            	alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            		@Override
            		public void onClick( DialogInterface dialog, int whichButton ) {
            			String msg = input.getText().toString();
            			Toast.makeText( ShowReminderList.this, "Updated", Toast.LENGTH_SHORT).show();
            	 
            	        //db.updateReminder( id, msg, 0, 0, 0);
            	        showDialog(DATE_DIALOG);

            	        db.updateReminder( id, msg, year, month, day);    
            	        Log.d( LOGCAT, year + "/" + month + "/" + day );
            	        myActiveList.remove( item );
            	        aRem = new Reminders( msg );
            			myActiveList.add( aRem );
            	        selectedList( activeList, myActiveList );       			
            		}
            	});
            	alert.setNegativeButton( "Delete", new DialogInterface.OnClickListener() {
            		@Override
            		public void onClick( DialogInterface dialog, int whichButton ) {
            			Toast.makeText( ShowReminderList.this, "deleted", Toast.LENGTH_SHORT).show();
            			db.deleteReminder( id );
            			myWaitingList.add(item);
            			myActiveList.remove( item );
            			selectedList( activeList, myActiveList);
            			selectedList( waitingList, myWaitingList );
            		}
            	}); 
            	alert.show();
            }
        });
    }
    
    /* A Helper function that use to initialize the data field.
    */
    private void getInit() {
    	location  = ( TextView )findViewById( R.id.TV_Location );
    	add_bt    = ( Button )findViewById( R.id.Bt_Add );
    	add_bt.setOnClickListener( this );

    	activeList  = ( ListView )findViewById( R.id.LV_Active );
    	waitingList = ( ListView )findViewById( R.id.LV_Watiting );
    }
    
    @Override
    public void onPause() {
    	db.close();
    	checkActiveList();
    	super.onPause();
    }
    
    @Override
    public void onResume() {
    	db.open();
    	checkActiveList();
    	super.onResume();
    }
    
    @Override
    public void onStart() {
    	checkActiveList();
    	super.onStart();
    }   
    
    private void checkActiveList() {
    	if( myActiveList.size() == 0)
    		check = false;
    	else
    		check = true;
    }
}