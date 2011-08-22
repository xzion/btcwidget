package com.pxr.tutorials.widget.basic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.lang.String;
import java.net.*;
import java.io.*;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.google.gson.*;


public class WidgetActivity extends AppWidgetProvider {
	public static WidgetActivity Widget = null;
	public static Context context;
	public static AppWidgetManager appWidgetManager;
	public static int appWidgetIds[];	
	
	@Override
    public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds )    {		
		if (null == context) context = WidgetActivity.context;
	    if (null == appWidgetManager) appWidgetManager = WidgetActivity.appWidgetManager;
	    if (null == appWidgetIds) appWidgetIds = WidgetActivity.appWidgetIds;

	    WidgetActivity.Widget = this;
	    WidgetActivity.context = context;
	    WidgetActivity.appWidgetManager = appWidgetManager;
	    WidgetActivity.appWidgetIds = appWidgetIds;
	    
		Log.i("PXR", "onUpdate");
		
		final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];  
            
            updateAppWidget(context,appWidgetManager, appWidgetId);            
        }
        
    }
	
	class OzcoPersonalData {
	    private String confirmed_rewards;
	    private Long hashrate;
	    
	    public String getRewards() {return confirmed_rewards;}
	    public Long getHashrate() {return hashrate;}
	    
	    public void setRewards(String confirmed_rewards) {this.confirmed_rewards = confirmed_rewards;}
	    public void setHashrate(Long hashrate) {this.hashrate = hashrate;}

	    public String toString() {
	        return String.format("confirmed_rewards: %s, hashrate: %d", confirmed_rewards, hashrate);
	    }
	}

	
	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {
        String json = "";
        DateFormat format = SimpleDateFormat.getTimeInstance( SimpleDateFormat.MEDIUM, Locale.getDefault() );
        try {
			URLConnection ozcopersonal = new URL("https://ozco.in/api.php?api_key=e3aded53504da64b7b27193838b4e061c74665421792884f7fa920302acd0d6c").openConnection();
			BufferedReader in = new BufferedReader( new InputStreamReader( ozcopersonal.getInputStream()));					
			json = in.readLine();
			in.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			json = "MalformedUrlException";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			json = "IOException";
		}
        
        OzcoPersonalData mydata = new Gson().fromJson(json, OzcoPersonalData.class);
        CharSequence text = "Time: " + format.format( new Date()) + "\n" + mydata;
        
        
        Intent intent = new Intent(context, UpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        remoteViews.setOnClickPendingIntent(R.id.LinearLayout01, pendingIntent);
        
        remoteViews.setTextViewText(R.id.widget_textview, text);
 
        // Tell the widget manager
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }
	
	
	
	public static class UpdateService extends Service {
        @Override
        public void onStart(Intent intent, int startId) {
        	WidgetActivity.Widget.onUpdate(context, appWidgetManager, appWidgetIds);
        	Toast.makeText(context, "Updated Widget", Toast.LENGTH_SHORT).show();
        }

		@Override
		public IBinder onBind(Intent arg0) {
			return null;
		}
    }
	
}
