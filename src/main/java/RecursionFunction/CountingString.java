package RecursionFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class CountingString {
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());

		System.out.println("counting(br.readLine()) = " + counting(st.nextToken(), st.nextToken().charAt(0), 0, 0));
	}

	private static int counting(String s, char c, int index, int count) {

		if (index == s.length()) {
			return count;
		}

		if (s.charAt(index) == c) {
			return counting(s, c, index + 1, count + 1);
		}

		return counting(s, c, index + 1, count);

	}
}
