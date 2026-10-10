package io.tam.decimalclock;
import android.app.*;
import android.appwidget.*;
import android.content.*;
import android.widget.RemoteViews;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
public class ClockWidget extends AppWidgetProvider {
 public static int[] ids(Context c){return AppWidgetManager.getInstance(c).getAppWidgetIds(new ComponentName(c,ClockWidget.class));}
 public static void render(Context c,boolean live){
  LocalTime now=LocalTime.now();
  boolean fast=c.getSharedPreferences("clock",0).getBoolean("fast",false);
  RemoteViews v=new RemoteViews(c.getPackageName(),R.layout.widget);
  v.setTextViewText(R.id.decimal,live?DecimalTime.format(now,fast):"—.—————");
  v.setTextViewText(R.id.standard,live?now.format(DateTimeFormatter.ofPattern("h:mm:ss a",java.util.Locale.US))+" · "+(fast?"LIVE TICKS":"TICKS = 0"):"Tap to enable live updates");
  v.setOnClickPendingIntent(R.id.root,PendingIntent.getActivity(c,0,new Intent(c,MainActivity.class),PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT));
  AppWidgetManager.getInstance(c).updateAppWidget(ids(c),v);
 }
 @Override public void onUpdate(Context c,AppWidgetManager m,int[] ids){render(c,ClockService.running);}
 @Override public void onEnabled(Context c){render(c,ClockService.running);}
 @Override public void onDisabled(Context c){c.stopService(new Intent(c,ClockService.class));}
 @Override public void onAppWidgetOptionsChanged(Context c,AppWidgetManager m,int id,android.os.Bundle b){render(c,ClockService.running);}
}
