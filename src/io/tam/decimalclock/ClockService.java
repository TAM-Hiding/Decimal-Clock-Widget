package io.tam.decimalclock;
import android.app.*;
import android.content.*;
import android.os.*;
import java.time.LocalTime;
public class ClockService extends Service {
 public static volatile boolean running=false;
 private final Handler handler=new Handler(Looper.getMainLooper());
 private final Runnable update=new Runnable(){public void run(){
  if(!running)return;
  if(((PowerManager)getSystemService(POWER_SERVICE)).isInteractive()){
   ClockWidget.render(ClockService.this,true);
   handler.postDelayed(this,DecimalTime.delay(LocalTime.now(),getSharedPreferences("clock",0).getBoolean("fast",false)));
  }
 }};
 private final BroadcastReceiver screen=new BroadcastReceiver(){public void onReceive(Context c,Intent i){handler.removeCallbacks(update);update.run();}};
 @Override public void onCreate(){
  super.onCreate();
  NotificationManager manager=getSystemService(NotificationManager.class);
  manager.createNotificationChannel(new NotificationChannel("live","Live clock",NotificationManager.IMPORTANCE_LOW));
  PendingIntent open=PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),PendingIntent.FLAG_IMMUTABLE);
  Intent stop=new Intent(this,ClockService.class).setAction("STOP");
  PendingIntent pause=PendingIntent.getService(this,1,stop,PendingIntent.FLAG_IMMUTABLE);
  Notification n=new Notification.Builder(this,"live").setSmallIcon(R.drawable.icon).setContentTitle("Decimal clock is live").setContentText("Updates pause with screen off. Tap to configure.").setContentIntent(open).setOngoing(true).addAction(new Notification.Action.Builder(null,"Stop",pause).build()).build();
  startForeground(1,n);
  running=true;
  IntentFilter f=new IntentFilter();f.addAction(Intent.ACTION_SCREEN_ON);f.addAction(Intent.ACTION_SCREEN_OFF);f.addAction(Intent.ACTION_TIME_CHANGED);f.addAction(Intent.ACTION_TIMEZONE_CHANGED);
  if(Build.VERSION.SDK_INT>=33)registerReceiver(screen,f,Context.RECEIVER_NOT_EXPORTED);else registerReceiver(screen,f);
 }
 @Override public int onStartCommand(Intent i,int flags,int id){
  if(i!=null&&"STOP".equals(i.getAction())){stopSelf();return START_NOT_STICKY;}
  handler.removeCallbacks(update);update.run();return START_STICKY;
 }
 @Override public void onDestroy(){running=false;handler.removeCallbacks(update);unregisterReceiver(screen);ClockWidget.render(this,false);super.onDestroy();}
 @Override public IBinder onBind(Intent i){return null;}
}
