package RecursionFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// GPT 재귀함수

public class FibonacciNumbers {

	static List<Long> list = new ArrayList<>();
	static int n;

	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		n = Integer.parseInt(br.readLine());
		list.add(0L);
		list.add(1L);

		fibonacchi(1, 1);

	}

	private static void fibonacchi(int number, int idx) {

		long first = list.get(idx - 1);
		long second = list.get(idx);

		list.add(first + second);

		idx++;

		if (idx == n) {
			System.out.println("list = " + list);
			System.out.println("list.get(idx) = " + list.get(idx));
			System.exit(0);
		}

		fibonacchi(number, idx);
	}
}
