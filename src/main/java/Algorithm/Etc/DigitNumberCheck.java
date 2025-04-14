package Algorithm.Etc;

import java.io.IOException;

public class DigitNumberCheck {
	public static void main(String[] args) throws IOException {

	}

	private static boolean check(int n) {

		while (n > 0) {
			if (n % 10 != 0 && n % 10 != 5) {
				return false;
			}
			n /= 10;
		}

		return true;
	}
}
