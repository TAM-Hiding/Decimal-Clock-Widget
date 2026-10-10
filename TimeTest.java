import io.tam.decimalclock.DecimalTime;
import java.time.LocalTime;
public class TimeTest {
 static void check(String expected,LocalTime t){String actual=DecimalTime.format(t,true);if(!expected.equals(actual))throw new AssertionError(expected+" != "+actual);}
 public static void main(String[] args){
  check("0.00000",LocalTime.MIDNIGHT);
  check("5.00000",LocalTime.NOON);
  check("8.75000",LocalTime.of(21,0));
  check("2.50000",LocalTime.of(6,0));
  check("9.99999",LocalTime.MAX);
  check("0.00001",LocalTime.ofNanoOfDay(86_400_000L));
  check("0.00010",LocalTime.ofNanoOfDay(864_000_000L));
  for(long n=0;n<DecimalTime.DAY_NANOS;n+=12345678901L){
   LocalTime t=LocalTime.ofNanoOfDay(n);
   if(!DecimalTime.format(t,false).endsWith("0"))throw new AssertionError("Fixed tick");
   long delay=DecimalTime.delay(t,false);
   if(delay<1||delay>864)throw new AssertionError("Delay "+delay);
  }
  System.out.println("PASS: midnight, noon, 6 AM, 9 PM, day rollover, tick duration, fixed tick, scheduling bounds.");
 }
}
