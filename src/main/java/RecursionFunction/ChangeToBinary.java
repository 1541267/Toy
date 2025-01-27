package RecursionFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChangeToBinary {

	static StringBuilder sb = new StringBuilder();

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		int n = Integer.parseInt(br.readLine());

		System.out.println("Integer.toBinaryString(n) = " + Integer.toBinaryString(n));
		System.out.println("toBinary(n) = " + toBinary(n));

	}

	private static String toBinary(int a) {

		if (a == 0) {
			return sb.toString();
		}
		return toBinary(a / 2) + (a % 2);
	}
}
