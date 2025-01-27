package RecursionFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FindIncludeNumber {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		int[] arr = {1, 2, 3, 4, 5};

		int n = Integer.parseInt(br.readLine());

		System.out.println("findNumber(arr, n, 0) = " + findNumber(arr, n, 0));
	}

	private static boolean findNumber(int[] arr, int n, int index) {

		if (index >= arr.length) {
			return false;
		}

		if (arr[index] == n) {
			return true;
		}

		return findNumber(arr, n, index + 1);

	}
}
