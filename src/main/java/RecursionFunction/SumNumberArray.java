package RecursionFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

// 숫자 배열의 합

public class SumNumberArray {

	static int[] arr;

	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());

		int index = 0;

		arr = new int[st.countTokens()];

		while (st.hasMoreTokens()) {
			arr[index] = Integer.parseInt(st.nextToken());
			index++;
		}

		int total = 0;

		System.out.println("index = " + Arrays.toString(arr));
		sumArray(0, total);
	}

	private static void sumArray(int index, int total) {

		total += arr[index];

		if (index == arr.length - 1) {
			System.out.println(total);
			System.exit(0);
		}

		sumArray(index + 1, total);

	}
}
