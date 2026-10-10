package io.tam.decimalclock;
import android.app.*;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
public class MainActivity extends Activity {
 private final Handler handler=new Handler(Looper.getMainLooper());
 private TextView decimal,standard,status;
 private final Runnable tick=new Runnable(){public void run(){LocalTime now=LocalTime.now();decimal.setText(DecimalTime.format(now,true));standard.setText(now.format(DateTimeFormatter.ofPattern("h:mm:ss a",java.util.Locale.US)));status.setText(ClockService.running?"WIDGET LIVE · pauses with screen off":"WIDGET PAUSED");handler.postDelayed(this,43);}};
 private int dp(int v){return (int)(v*getResources().getDisplayMetrics().density);}
 private TextView text(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setGravity(Gravity.CENTER);t.setPadding(0,dp(8),0,dp(8));return t;}
 private void startLive(){try{startForegroundService(new Intent(this,ClockService.class));}catch(RuntimeException e){Toast.makeText(this,"Unable to start live widget: "+e.getMessage(),Toast.LENGTH_LONG).show();}}
 @Override public void onCreate(Bundle b){
  super.onCreate(b);
  getWindow().setStatusBarColor(Color.rgb(12,23,17));getWindow().setNavigationBarColor(Color.rgb(12,23,17));
  ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(Color.rgb(12,23,17));
  LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setGravity(Gravity.CENTER);root.setPadding(dp(24),dp(60),dp(24),dp(40));root.setBackgroundColor(Color.rgb(12,23,17));
  root.setOnApplyWindowInsetsListener((v,insets)->{v.setPadding(dp(24),dp(32)+insets.getSystemWindowInsetTop(),dp(24),dp(24)+insets.getSystemWindowInsetBottom());return insets;});
  root.addView(text("DECIMAL CLOCK",18,0xff8caf97));
  decimal=text("0.00000",60,0xff9effbc);decimal.setTypeface(Typeface.MONOSPACE);decimal.setAutoSizeTextTypeUniformWithConfiguration(24,60,1,android.util.TypedValue.COMPLEX_UNIT_SP);decimal.setMaxLines(1);root.addView(decimal,new LinearLayout.LayoutParams(-1,dp(110)));
  standard=text("",24,0xffd1e0d5);standard.setVisibility(getSharedPreferences("clock",0).getBoolean("comparison",false)?View.VISIBLE:View.GONE);root.addView(standard);
  root.addView(text("10 hours · 100 minutes · 100 seconds\n10 ticks per second\n5.00000 = noon   •   8.75000 = 9 PM",15,0xff8caf97));
  status=text("",12,0xff9effbc);root.addView(status);
  Button start=new Button(this);start.setText("Enable live widget");start.setOnClickListener(v->{if(Build.VERSION.SDK_INT>=33&&checkSelfPermission("android.permission.POST_NOTIFICATIONS")!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},7);else startLive();});root.addView(start);
  Button add=new Button(this);add.setText("Add home-screen widget");add.setOnClickListener(v->{AppWidgetManager m=AppWidgetManager.getInstance(this);if(m.isRequestPinAppWidgetSupported())m.requestPinAppWidget(new ComponentName(this,ClockWidget.class),null,null);else Toast.makeText(this,"Long-press your home screen → Widgets → Decimal Clock",Toast.LENGTH_LONG).show();});root.addView(add);
  Switch comparison=new Switch(this);comparison.setText("Show normal time (app + widget)");comparison.setTextColor(0xffd1e0d5);comparison.setPadding(0,dp(16),0,dp(16));comparison.setChecked(getSharedPreferences("clock",0).getBoolean("comparison",false));comparison.setOnCheckedChangeListener((v,on)->{getSharedPreferences("clock",0).edit().putBoolean("comparison",on).apply();standard.setVisibility(on?View.VISIBLE:View.GONE);ClockWidget.render(this,ClockService.running);});root.addView(comparison);
  Switch fast=new Switch(this);fast.setText("Fast widget ticks (more battery)");fast.setTextColor(0xffd1e0d5);fast.setPadding(0,dp(16),0,dp(16));fast.setChecked(getSharedPreferences("clock",0).getBoolean("fast",false));fast.setOnCheckedChangeListener((v,on)->{getSharedPreferences("clock",0).edit().putBoolean("fast",on).apply();if(ClockService.running)startLive();});root.addView(fast);
  Switch awake=new Switch(this);awake.setText("Keep screen on while app is open");awake.setTextColor(0xffd1e0d5);awake.setOnCheckedChangeListener((v,on)->{if(on)getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);});root.addView(awake);
  Button stop=new Button(this);stop.setText("Pause live widget");stop.setOnClickListener(v->stopService(new Intent(this,ClockService.class)));root.addView(stop);
  root.addView(text("Fast mode samples real ticks ~4.6 times/sec.\nNormal widget mode fixes the final digit at 0.\nAfter reboot or force-stop, open app and enable live.\nOffline. No ads. No account.",12,0xff8caf97));
  scroll.addView(root);setContentView(scroll);
 }
 @Override public void onRequestPermissionsResult(int r,String[] p,int[] g){super.onRequestPermissionsResult(r,p,g);if(r==7)startLive();}
 @Override public void onResume(){super.onResume();handler.post(tick);}
 @Override public void onPause(){handler.removeCallbacks(tick);super.onPause();}
}
