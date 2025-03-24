package Sort;

// _1_~ 방법들은 Comparsion Sort, 이를 모두 정렬하는 가짓수는 n!
// 정렬을 통해 생기는 트리의 말단 노드가 n! 이상의 노드를 갖기 위해선 2^h >= n!을 만족하는 h를 가져야 하고
// 이 식은 h > O(n log n) -> h는 트리의 높이 (Comparison Sort의 시간 복잡도) -> 이 O(n log n)을 줄이기 위해선 Comparison을 하지 않는 것

// 계수 정렬: Counting 필요 (숫자가 몇 번 등장 했는지), 정렬하려는 숫자가 특정한 범위 내에 있을 때 사용 ,등장 횟수를 누적합해 삽입 -> 안정 정렬
// 시간 복잡도: O(n + k): k는 배열에서 등장하는 최대 값, 공간 복잡도: O(k) -> k만큼의 배열을 만들어야 함
// 장점: O(n)의 시간 복잡도,
// 단점: 배열 사이즈를 n 마큼 돌 때 증가시켜주는 Counting 배열의 크기가 큼 (메모리 낭비가 심함), k가 너무 클 경우 비효율적 (ex: 입력 범위가 1조 -> O(k) 가 되어버림)

import java.util.Arrays;

import Sort.ArrGenerator.ArrGenerator;

public class _2_1_Counting {
	public static void main(String[] args) {
		ArrGenerator a = new ArrGenerator();

		int[] arr = a.init();

		int n = arr.length;
		System.out.println("Before Arr: " + Arrays.toString(arr));

		int[] sortedArr = new int[n];

		int[] counting = new int[n * 6];

		for (int i = 0; i < n; i++) {
			counting[arr[i]]++;
		}

		System.out.println("First Counting Arr: " + Arrays.toString(counting));

		for (int i = 1; i < counting.length; i++) {
			counting[i] += counting[i - 1];
		}

		System.out.println("Second Counting Arr: " + Arrays.toString(counting));

		for (int i = n - 1; i >= 0; i--) {
		System.out.println("==========================================================");
			System.out.println("Sorting counting: " + Arrays.toString(counting));

			counting[arr[i]]--;
			sortedArr[counting[arr[i]]] = arr[i];
			System.out.println("Sorted Arr: " + Arrays.toString(sortedArr));
		}
		System.out.println("After arr = " + Arrays.toString(sortedArr));

	}
}
