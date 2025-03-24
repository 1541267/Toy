package BruteForce;

import java.util.Arrays;

import Sort.ArrGenerator.ArrGenerator;

public class BruteForce {
	static int n;
	static int[] arr;
	static boolean[] used;
	static int[] newArr;

	public static void main(String[] args) {
		ArrGenerator a = new ArrGenerator();

		arr = a.init();
		n = arr.length;
		used = new boolean[n];
		newArr = new int[n];

		makingArr(0);

	}

	private static void makingArr(int depth) {
		if (n == depth) {
			System.out.println("newArr = " + Arrays.toString(newArr));
			for (int i = 0; i < n-1; i++) {
				System.out.println("a - b: " + (newArr[i] - newArr[i + 1]));
			}
		}

		for (int i = 0; i < n; i++) {
			if (!used[i]) {
				used[i] = true;
				newArr[depth] = arr[i];
				makingArr(depth + 1);
				used[i] = false;
			}
		}

	}
}
