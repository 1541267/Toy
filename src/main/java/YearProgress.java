import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class YearProgress {
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH시 mm분 ss초");

	public static void main(String[] args) {

		LocalDate firstDay = LocalDate.of(LocalDate.now().getYear(), 1, 1);
		LocalDateTime firstDateTime = firstDay.atStartOfDay();
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDate chirstmasDay = LocalDate.of(currentDateTime.getYear(), 12, 25);

		LocalDate lastDay = LocalDate.of(currentDateTime.getYear(), 12, 31);

		LocalDateTime nextYear = LocalDateTime.of(currentDateTime.getYear() + 1, 1, 1, 0, 0);



		double totalDays = ChronoUnit.DAYS.between(firstDay, lastDay) + 1;

		double daysUntilNextYear = ChronoUnit.DAYS.between(currentDateTime.toLocalDate(), nextYear);

		if (ChronoUnit.DAYS.between(chirstmasDay, currentDateTime) > 0) {
			chirstmasDay = LocalDate.of(currentDateTime.getYear()+1, 12, 25);
		}
		long daysUntilChristmas = ChronoUnit.DAYS.between(currentDateTime.toLocalDate(), chirstmasDay);

		long hoursUntilNextYear = ChronoUnit.HOURS.between(currentDateTime, nextYear);
		long minutesUntilNextYear = ChronoUnit.MINUTES.between(currentDateTime, nextYear);
		long secondsUntilNextYear = ChronoUnit.SECONDS.between(currentDateTime, nextYear);

		long passingTimeDate = ChronoUnit.DAYS.between(firstDateTime, currentDateTime);
		long passingTimeHour = ChronoUnit.HOURS.between(firstDateTime, currentDateTime);
		long passingTimeMinute = ChronoUnit.MINUTES.between(firstDateTime, currentDateTime);
		long passingTimeSecond = ChronoUnit.SECONDS.between(firstDateTime, currentDateTime);

		// int progressPercentage = 100 - (int)(daysUntilNextYear / totalDays * 100);

		double totalSecond = ChronoUnit.SECONDS.between(firstDateTime, nextYear);
		double currentSecond = ChronoUnit.SECONDS.between(firstDateTime, currentDateTime);
		double progressPercentageSecond = (currentSecond / totalSecond * 100);
		// System.out.println(progressPercentage);

	System.out.printf(
		"""
			
			==============================================
			| %s %s, %s		 \s
			|---------------------------------------------
			| 크리스마스 까지 %d일					     \s
			|---------------------------------------------
			| %d년 진행: %.2f%%			                 \s
			|---------------------------------------------
			| 보낸 시간     	                             \s
			| %d일 | %d시간 | %d분 | %d초     	  \s
			|---------------------------------------------
			| %d년 까지					   	    	 \s
			| %d일 | %d시간 | %d분 | %d초     \s
			==============================================
			""",
			currentDateTime.format(DATE_FORMAT),
			currentDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREA),
			currentDateTime.format(TIME_FORMAT),
			daysUntilChristmas,
			currentDateTime.getYear(), progressPercentageSecond,
			passingTimeDate, passingTimeHour, passingTimeMinute, passingTimeSecond,
			// progressPercentage,
			currentDateTime.getYear() + 1,
			(int)daysUntilNextYear, hoursUntilNextYear, minutesUntilNextYear, secondsUntilNextYear
		);
	}
}
