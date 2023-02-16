package Datetime_Utils;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;

public class Test {
	public static void main(String[] args) {
//		System.out.println(Datetime_Utils.getTimeNow(null));
		LocalDateTime lt = Datetime_Utils.getDateTimeNow(null);
		System.out.println(lt);
	}
}