import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class YearProgressBefore {
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH시 mm분 ss초");

	public static void main(String[] args) {
		// 현재 날짜/시간 정보 한 번만 가져오기
		LocalDateTime currentDateTime = LocalDateTime.now();
		int currentYear = currentDateTime.getYear();
		LocalDate currentDate = currentDateTime.toLocalDate();

		// 연초와 연말 계산
		LocalDate firstDay = LocalDate.of(currentYear, 1, 1);
		LocalDateTime firstDateTime = firstDay.atStartOfDay();
		LocalDate lastDay = LocalDate.of(currentYear, 12, 31);

		// 다음 해 계산
		LocalDateTime nextYear = LocalDateTime.of(currentYear + 1, 1, 1, 0, 0);

		// 크리스마스 계산
		LocalDate christmasDay = LocalDate.of(
			currentDate.isAfter(LocalDate.of(currentYear, 12, 25)) ? currentYear + 1 : currentYear,
			12, 25);
		long daysUntilChristmas = ChronoUnit.DAYS.between(currentDate, christmasDay);

		// 올해 총 일수
		double totalDays = ChronoUnit.DAYS.between(firstDay, lastDay) + 1;
		double daysUntilNextYear = ChronoUnit.DAYS.between(currentDate, lastDay.plusDays(1));

		// 다음 해까지 남은 시간
		long hoursUntilNextYear = ChronoUnit.HOURS.between(currentDateTime, nextYear);
		long minutesUntilNextYear = ChronoUnit.MINUTES.between(currentDateTime, nextYear);
		long secondsUntilNextYear = ChronoUnit.SECONDS.between(currentDateTime, nextYear);

		// 경과 시간 계산
		long passingTimeDays = ChronoUnit.DAYS.between(firstDateTime, currentDateTime);
		long passingTimeHours = ChronoUnit.HOURS.between(firstDateTime, currentDateTime);
		long passingTimeMinutes = ChronoUnit.MINUTES.between(firstDateTime, currentDateTime);
		long passingTimeSeconds = ChronoUnit.SECONDS.between(firstDateTime, currentDateTime);

		// 진행률 계산 (초 단위 비교)
		double totalSeconds = ChronoUnit.SECONDS.between(firstDateTime, nextYear);
		double currentSeconds = ChronoUnit.SECONDS.between(firstDateTime, currentDateTime);
		double progressPercentageSeconds = Math.floor(currentSeconds / totalSeconds * 100_000) / 1000.0;

		// 출력
		System.out.printf(
			"""
							
				==============================================
				| %s %s, %s        \s
				|---------------------------------------------
				| 크리스마스 까지 %d일                  \s
				|---------------------------------------------
				| %d년 진행: %.3f%%                          \s
				|---------------------------------------------
				| 보낸 시간                                     \s
				| %d일 | %d시간 | %d분 | %d초         \s
				|---------------------------------------------
				| %d년 까지                              \s
				| %d일 | %d시간 | %d분 | %d초     \s
				==============================================
				""",
			currentDateTime.format(DATE_FORMAT),
			currentDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREA),
			currentDateTime.format(TIME_FORMAT),
			daysUntilChristmas,
			currentYear, progressPercentageSeconds,
			passingTimeDays, passingTimeHours, passingTimeMinutes, passingTimeSeconds,
			currentYear + 1,
			(int)daysUntilNextYear, hoursUntilNextYear, minutesUntilNextYear, secondsUntilNextYear
		);
	}
}