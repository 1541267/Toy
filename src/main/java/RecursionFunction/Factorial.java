package RecursionFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Factorial {
	// GPT 재귀함수 팩토리얼
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		int n = Integer.parseInt(br.readLine());

		System.out.println("factorial(n, 1) = " + factorial(n));
	}

	static int factorial(int number) {

		if (number == 0) {
			return 1;
		}

		return number * factorial(--number);
	}
}
