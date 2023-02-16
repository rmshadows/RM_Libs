package Datetime_Utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

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
	
	// 时间
	/**
	 * 获取当前时间戳 ，此方法不受时区影响
	 * @return 1675374966417
	 */
	public static long getTimeStampNow() {
//		System.out.println(utcClock.millis());
//		System.out.println(System.currentTimeMillis());
		return utcClock.millis();
	}

	/**
	 * 返回当前时间，仅时间
	 * @param zoneClock 时区时钟 null默认当前
	 * @return 04:42:06.249760
	 */
	public static LocalTime getTimeNow(Clock zoneClock){
		if (zoneClock == null){
			return LocalTime.now();
		}else {
			return LocalTime.now(zoneClock);
		}
	}

	// 日期
	/**
	 * 返回今天日期（无时间）
	 * @param zoneClock 时区时钟 null则默认当前时区
	 * @return 2023-02-17
	 */
	public static LocalDate getDateNow(Clock zoneClock){
		if (zoneClock == null){
			return LocalDate.now();
		}else {
			return LocalDate.now(zoneClock);
		}
	}

	// 日期时间
	/**
	 * 返回当前日期时间
	 * @param zoneClock 时区时钟，null为默认
	 * @return 2023-02-17T05:36:53.899425
	 */
	public static LocalDateTime getDateTimeNow(Clock zoneClock){
		if (zoneClock == null){
			return LocalDateTime.now();
		}else {
			return LocalDateTime.now(zoneClock);
		}
	}

	/**
	 * 获取某天的零点和最大时刻
	 * @param localDate 某一天
	 * @return 零点或最大 2023-02-17T00:00 and 2023-02-17T23:59:59.999999999
	 */
	public static LinkedList<LocalDateTime> getDayMaxAndMin(LocalDate localDate){
		LinkedList<LocalDateTime> ll = new LinkedList<>();
		if (localDate == null){
			localDate = getDateNow(null);
		}
		ll.add(LocalDateTime.of(localDate, LocalTime.MIN));
		ll.add(LocalDateTime.of(localDate, LocalTime.MAX));
		return ll;
	}

	/**
	 * 获取某个月的第一天和最后一天
	 * @param localDate 某一天(本方法只在乎月份)
	 * @return 第一天和最后一天
	 */
	public static LinkedList<LocalDate> getFirstAndLastDayofmonth(LocalDate localDate){
		LinkedList<LocalDate> ll = new LinkedList<>();
		if (localDate == null){
			localDate = getDateNow(null);
		}


		return ll;
	}

	// 格式
	// ISO 8601规定的日期和时间分隔符是T。标准格式如下：
	/**
	 * 获取格式
	 * @param format 如"yyyy-MM-dd"
	 * @return DateTimeFormatter
	 */
	public static DateTimeFormatter getFormatter(String format){
		return DateTimeFormatter.ofPattern(format);
	}


	// 时区
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