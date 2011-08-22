package com.pxr.tutorials.widget.basic;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.lang.String;
import java.lang.Math;
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
	
	class OzcoData {
	    private Long shares;
	    private Long hashrate;
	    private Long roundduration;
	    
	    public Long getRewards() {return shares;}
	    public Long getHashrate() {return hashrate;}
	    public Long getRoundduration() {return roundduration;}
	    
	    public void setRewards(Long shares) {this.shares = shares;}
	    public void setHashrate(Long hashrate) {this.hashrate = hashrate;}
	    public void setRoundduration(Long roundduration) {this.roundduration = roundduration;}

	    public String toString() {
	        return String.format("shares: %d, hashrate: %d, round duration: %d", shares, hashrate, roundduration);
	    }
	}
	
	class MtGoxData {
	    private Ticker ticker;
	    
	    public class Ticker {
	    	private Float last;
	    	
	    	public Float getLast() {return last;}
	    	
	    }

	    public void setTicker(Ticker ticker) {this.ticker = ticker;}

	    public String toString() {
	        return String.format("last: %d", ticker.last);
	    }
	}
	
	class BitParkingData {
	    private Ticker ticker;
	    
	    public class Ticker {
	    	private Float last;
	    	
	    	public Float getLast() {return last;}
	    	
	    }

	    public void setTicker(Ticker ticker) {this.ticker = ticker;}

	    public String toString() {
	        return String.format("last: %d", ticker.last);
	    }
	}

	
	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {
        String json = "";
        
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
        OzcoPersonalData myozcopersonaldata = new Gson().fromJson(json, OzcoPersonalData.class);
        
        try {
			URLConnection ozcopersonal = new URL("https://ozco.in/api.php").openConnection();
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
        
        OzcoData myozcodata = new Gson().fromJson(json, OzcoData.class);
        Long ozcohours = myozcodata.getRoundduration() / 3600;
        Long ozcominutes = (myozcodata.getRoundduration() % 3600)/60;
        Long ozcopoolrate = myozcodata.getHashrate() / 1000;
        
        String btcdiff = "";
        try {
			URLConnection blockexdiff = new URL("http://blockexplorer.com/q/getdifficulty").openConnection();
			BufferedReader in = new BufferedReader( new InputStreamReader( blockexdiff.getInputStream()));					
			btcdiff = in.readLine();
			in.close();	
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			json = "MalformedUrlException";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			json = "IOException";
		}
        
        Float btcdifffloat = Float.parseFloat(btcdiff);
        Long btcdifflong = btcdifffloat.longValue();
        DecimalFormat twoDForm = new DecimalFormat("###.###");
        Double mybtcperday = myozcopersonaldata.getHashrate() / Math.pow(2,32) * 50 / btcdifflong * 86400 * 1000000;
        
        
        try {
			URLConnection mtgoxurl = new URL("https://mtgox.com/api/0/data/ticker.php").openConnection();
			BufferedReader in = new BufferedReader( new InputStreamReader( mtgoxurl.getInputStream()));					
			json = in.readLine();
			in.close();	
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			json = "MalformedUrlException";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			json = "IOException";
		}
        
        MtGoxData mymtgoxdata = new Gson().fromJson(json, MtGoxData.class);
        
        String dotbitstuff = new String("");
        String [] dotbitarray = new String[10];
        String nmcdiff = new String("");
        String nmcnextdiff = new String("");
        String nmcnextdiffdate = new String("");
        String btcnextdiff = new String("");
        String btcnextdiffdate = new String("");
        
        try {
			URLConnection dotbiturl = new URL("http://dot-bit.org/tools/nextDifficulty.php").openConnection();
			BufferedReader in = new BufferedReader( new InputStreamReader( dotbiturl.getInputStream()));					
			dotbitstuff = in.readLine();
			dotbitstuff = dotbitstuff.trim();
			while (!dotbitstuff.equalsIgnoreCase("<td><abbr title=\"Last difficulty change\">Last</abbr></td>")) {
				dotbitstuff = in.readLine();
				dotbitstuff = dotbitstuff.trim();
			}
			dotbitstuff = in.readLine();
			dotbitstuff = in.readLine();
			dotbitstuff = in.readLine();
			dotbitstuff = dotbitstuff.trim();
			dotbitarray = dotbitstuff.split(">");
			nmcdiff = dotbitarray[2];
			
			dotbitstuff = in.readLine();
			dotbitstuff = dotbitstuff.trim();
			while (!dotbitstuff.equalsIgnoreCase("<td><abbr title=\"Based on last 120 blocks speed + elapsed blocks speed since current difficulty change\">Next</abbr></td>")) {
				dotbitstuff = in.readLine();
				dotbitstuff = dotbitstuff.trim();
			}
			dotbitstuff = in.readLine();
			dotbitstuff = in.readLine();
			dotbitstuff = dotbitstuff.trim();
			dotbitarray = dotbitstuff.split(">");
			nmcnextdiffdate = dotbitarray[1];
			
			dotbitstuff = in.readLine();
			dotbitstuff = dotbitstuff.trim();
			dotbitarray = dotbitstuff.split(">");
			nmcnextdiff = dotbitarray[1];
			
			dotbitstuff = in.readLine();
			dotbitstuff = dotbitstuff.trim();
			while (!dotbitstuff.equalsIgnoreCase("<td><abbr title=\"Based on last 120 blocks speed + elapsed blocks speed since current difficulty change\">Next</abbr></td>")) {
				dotbitstuff = in.readLine();
				dotbitstuff = dotbitstuff.trim();
			}
			dotbitstuff = in.readLine();
			dotbitstuff = in.readLine();
			dotbitstuff = dotbitstuff.trim();
			dotbitarray = dotbitstuff.split(">");
			btcnextdiffdate = dotbitarray[1];
			
			dotbitstuff = in.readLine();
			dotbitstuff = dotbitstuff.trim();
			dotbitarray = dotbitstuff.split(">");
			btcnextdiff = dotbitarray[1];
			
			
			in.close();	
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			json = "MalformedUrlException";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			json = "IOException";
		}
        
        nmcdiff = nmcdiff.substring(0, (nmcdiff.length()-6));
        nmcdiff = nmcdiff.replaceAll("'", "");
        Float nmcdifffloat = Float.parseFloat(nmcdiff);
        Long nmcdifflong = nmcdifffloat.longValue();
        
        nmcnextdiffdate = nmcnextdiffdate.substring(0, (nmcnextdiffdate.length()-10));
        
        nmcnextdiff = nmcnextdiff.substring(0, (nmcnextdiff.length()-6));
        nmcnextdiff = nmcnextdiff.replaceAll("'", "");
        Float nmcnextdifffloat = Float.parseFloat(nmcnextdiff);
        Long nmcnextdifflong = nmcnextdifffloat.longValue();
        
        btcnextdiffdate = btcnextdiffdate.substring(0, (btcnextdiffdate.length()-10));
        
        btcnextdiff = btcnextdiff.substring(0, (btcnextdiff.length()-6));
        btcnextdiff = btcnextdiff.replaceAll("'", "");
        Float btcnextdifffloat = Float.parseFloat(btcnextdiff);
        Long btcnextdifflong = btcnextdifffloat.longValue();
        
        Double mynmcperday = myozcopersonaldata.getHashrate() / Math.pow(2,32) * 50 / nmcdifflong * 86400 * 1000000;
        Double mybtcperdaynextdiff = myozcopersonaldata.getHashrate() / Math.pow(2,32) * 50 / btcnextdifflong * 86400 * 1000000;
        Double mynmcperdaynextdiff = myozcopersonaldata.getHashrate() / Math.pow(2,32) * 50 / nmcnextdifflong * 86400 * 1000000;
        
        try {
			URLConnection bitparkingurl = new URL("http://exchange.bitparking.com:8080/api/ticker").openConnection();
			BufferedReader in = new BufferedReader( new InputStreamReader( bitparkingurl.getInputStream()));					
			json = in.readLine();
			in.close();	
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			json = "MalformedUrlException";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			json = "IOException";
		}
        
        BitParkingData mybitparkingdata = new Gson().fromJson(json, BitParkingData.class);
        
        
        
        DateFormat format = SimpleDateFormat.getTimeInstance( SimpleDateFormat.MEDIUM, Locale.getDefault() );
        CharSequence text = "Last Updated: " + format.format( new Date()) + "\n" 
        		+ "My Hashrate: " + myozcopersonaldata.getHashrate() + " Mh/s - Pool: " + ozcopoolrate + " Gh/s\n" 
        		+ "Last Block: " + ozcohours + " hrs, " + ozcominutes + " mins.\n\n"
        		+ "Current BTC Difficulty: " + btcdifflong + "\n"
        		+ "BTC/Day: " + twoDForm.format(mybtcperday) + " - $/Day: " + twoDForm.format(mymtgoxdata.ticker.getLast()*mybtcperday) + "\n\n"
        		+ "Current NMC Difficulty: " + nmcdifflong + "\n" 
        		+ "NMC/Day: " + twoDForm.format(mynmcperday) + " - $/Day: " + twoDForm.format(mymtgoxdata.ticker.getLast()*mynmcperday*mybitparkingdata.ticker.getLast()) + "\n\n"
        		+ "Next BTC Difficulty: " + btcnextdifflong + "\n"
        		+ "Date of Change: " + btcnextdiffdate + "\n"
        		+ "BTC/Day: " + twoDForm.format(mybtcperdaynextdiff) + " - $/Day: " + twoDForm.format(mymtgoxdata.ticker.getLast()*mybtcperdaynextdiff) + "\n\n"
        		+ "Next NMC Difficulty: " + nmcnextdifflong + "\n"
        		+ "Date of Change: " + nmcnextdiffdate + "\n"
        		+ "NMC/Day: " + twoDForm.format(mynmcperdaynextdiff) + " - $/Day: " + twoDForm.format(mymtgoxdata.ticker.getLast()*mynmcperdaynextdiff*mybitparkingdata.ticker.getLast()); 
               
        
        
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
