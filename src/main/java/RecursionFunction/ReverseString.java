package RecursionFunction;

// 문자 뒤집기

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReverseString {

	static StringBuilder sb = new StringBuilder();
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	static String line;
	public static void main(String[] args) throws IOException {

		line = br.readLine();

		reverseing(line.length() -1);
	}

	private static void reverseing(int index) {

		sb.append(line.charAt(index));

		if(index == 0) {
			System.out.println(sb.toString());
			System.exit(0);
		}

		reverseing(index - 1);
	}

}
