package RecursionFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SumNumberEachPlace {

	static String n;
	static int totalNum = 0;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		n = br.readLine();

		sumNumber(n.length() - 1);

	}

	private static void sumNumber(int index) {

		totalNum += n.charAt(index) - '0';

		if (index == 0) {
			System.out.println("totalNum = " + totalNum);
			System.exit(0);
		}

		sumNumber(index - 1);
	}
}
