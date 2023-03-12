package Datetime_Utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class Test {
	public static void main(String[] args) {
		long a = Datetime_Utils.getTimeStampNow();
		System.out.println(a);
		Instant instant1 = Instant.ofEpochMilli(a);
	}
}