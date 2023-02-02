package Datetime_Utils;

import java.time.Clock;
import java.time.ZoneId;

/**
 * 时间日期工具类
 *
 * @author ryan
 */
public class Datetime_Utils {
	// 系统默认UTC时钟（当前瞬时时间 System.currentTimeMillis()） 
	protected static Clock utcClock = Clock.systemUTC();
	// //系统默认时区时钟（当前瞬时时间）
	Clock zoneClock = Clock.systemDefaultZone();
	
	// 返回当前时间
	/**
	 * 获取当前时间戳
	 * @return 1675374966417
	 */
	public static long getTimeStamp() {
		System.out.println(utcClock.millis());
		return utcClock.millis();
	}
	
	/**
	 * 返回某时区的时钟
	 * 如：巴黎时区："Europe/Paris"
	 * @param timezone 
	 * @return
	 */
	public static Clock getZoneClock(String timezone) {
		return Clock.system(ZoneId.of(timezone));
	}
	
	public static ZoneId getSystemTimezone() {
		return ZoneId.systemDefault();
	}

}