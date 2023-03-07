package Datetime_Utils;

import javax.swing.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;

public class Test {
	public static void main(String[] args) {
//		System.out.println(Datetime_Utils.getTimeNow(null));
		LocalDateTime ltd1 = LocalDateTime.of(2023,1,2,3,4);
		LocalDateTime ltd2 = LocalDateTime.of(2023,1,2,3,5);
		System.out.println(Datetime_Utils.durationLocalDate(ltd1, ltd2));
		System.out.println();

//		LocalDateTime lt = Datetime_Utils.getDateTimeNow(null);
//		System.out.println(lt);
//		Datetime_Utils.getFirstAndLastDayofmonth(null);
//		System.out.println(lt);
	}
}