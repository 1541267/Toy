package Algorithm.BinarySearch;

// 탐색 범위를 두 부분으로 분할하며 찾음 -> 완전 탐색 보다 훨씬 빠른 장점
// 시간 복잡도: O( log n)
// 배열을 우선 정렬, left(0), right(배열 길이), mid (둘의 중간)으로 시작
// 타깃이 mid 보다 높으면 left = left + 1, 낮으면 right = right - 1;, left > right 가 될 때까지 반복

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import Algorithm.Sort.ArrGenerator.ArrGenerator;

public class BinarySearch {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		ArrGenerator a = new ArrGenerator();

		int[] arr = a.init();

		long startTime = System.nanoTime();
		System.out.println("arr = " + Arrays.toString(arr));
		Arrays.sort(arr);
		long endTime = System.nanoTime();
		System.out.println("Arr Quick Sort Time: " + (endTime - startTime) / 1_000_000_000.0 + "s");

		System.out.print("찾으려는 값: ");

		int target = Integer.parseInt(br.readLine());

		startTime = System.nanoTime();
		int foundIndexFromLoop = 0;
		for (int i = 0; i < arr.length; i++) {
			if (target == arr[i]) {
				foundIndexFromLoop = i;
				break;
			}
		}
		endTime = System.nanoTime();

		System.out.println("반복문 완전 탐색 Time: " + (endTime - startTime) / 1_000_000.0 + "ms");

		startTime = System.nanoTime();
		int foundIndexFromBinarySearch = binarySearch(arr, target);
		endTime = System.nanoTime();
		System.out.println("binarySearch Time: " + (endTime - startTime) / 1_000_000.0 + "ms");

		System.out.println("foundIndexFromLoop: " + foundIndexFromLoop + ", 값: " + arr[foundIndexFromLoop] + "\n"
			+ "foundIndexFromBinarySearch: " + foundIndexFromBinarySearch + ", 값: " + arr[foundIndexFromBinarySearch]);
		System.out.println();
	}

	private static int binarySearch(int[] arr, int target) {
		int left = 0, right = arr.length - 1;

		while (left < right) {
			int mid = left + (right - left) / 2;

			if (arr[mid] > target) {
				right = mid - 1;
			} else if (arr[mid] < target) {
				left = mid + 1;
			}
			if (arr[mid] == target) {
				return mid;
			}
		}
		return left;
	}
}
