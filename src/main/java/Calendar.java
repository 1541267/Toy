import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.StringTokenizer;

public class Calendar {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("표시 할 년도와 달 입력: ");
		StringTokenizer st = new StringTokenizer(br.readLine());

		int year = Integer.parseInt(st.nextToken());
		System.out.println("월 입력: ");
		int month = Integer.parseInt(st.nextToken());

		LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
		LocalDate nextMonth = firstDayOfMonth.plusMonths(1);

		// 월요일 = 1 ~ 일요일 = 7
		int offsetWeekDays = firstDayOfMonth.getDayOfWeek().getValue() % 7;

		System.out.println("Su Mo Tu We Th Fr Sa  ");

		for (int i = 0; i < offsetWeekDays; i++) {
			System.out.print("   ");
		}

		LocalDate dayIterator = firstDayOfMonth;
		while (dayIterator.isBefore(nextMonth)) {
			System.out.printf("%2d ", dayIterator.getDayOfMonth());
			if (dayIterator.getDayOfWeek() == DayOfWeek.SATURDAY) {
				System.out.println();
			}
			dayIterator = dayIterator.plusDays(1);
		}
	}
}
