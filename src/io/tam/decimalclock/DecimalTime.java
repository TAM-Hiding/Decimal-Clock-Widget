package io.tam.decimalclock;
import java.time.LocalTime;
import java.util.Locale;
public final class DecimalTime {
 public static final long DAY_NANOS=86_400_000_000_000L;
 public static long ticks(LocalTime time){return time.toNanoOfDay()/86_400_000L;}
 public static String format(LocalTime time, boolean showTicks){
  long t=ticks(time); if(!showTicks)t=t/10*10;
  return String.format(Locale.US,"%d.%05d",t/100000,t%100000);
 }
 public static long delay(LocalTime time, boolean fast){
  long quantum=fast?216_000_000L:864_000_000L;
  return Math.max(1,(quantum-time.toNanoOfDay()%quantum+999999)/1000000);
 }
}
