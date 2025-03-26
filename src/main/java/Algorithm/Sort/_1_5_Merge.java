package Algorithm.Sort;

// 병합 정렬: 분할 정복 방법을 통해 구현, 빠른 정렬로 분류되며 Quick과 함꼐 많이 언급, Quick과 반대로 안정 정렬
// 요소를 최소 길이로 쪼갠 후 정렬, 이 후 병합 시키면서 정렬(두 영역이 정렬이 되어있기 때문에 순차적으로 두 배열을 비교)해 나가는 방식, 쪼개는 방식은 Quick과 유사
// Quick과 차이점
// -> Quick: 피벗을 통해 정렬(Partition), 영역을 쪼갬(Quick Sort)
// -> Merge: 영억을 쪼갤 수 있을 만큼 쪼갬(Merge Sort) -> 정렬(Merge)
// 장점: 순차적인 비교로 정렬을 진행하므로 LinkedList의 정렬이 필요할 때 사용하면 효율적, 안정정렬, 최악의 경우에도 O(n log n) 보장
// -> LinkedList에서 Quick 사용시 Quick은 순차 접근이 아닌 임의 접근이기 때문에 성능이 좋지 않음(접근 연산에서의 비효율) 오버헤드 발생 가능
// -> 접근 시 배열: O(1), LinkedList: O(n)
// 단점: 추가적인 메모리 공간 필요(정렬을 위해 배열을 복사하는 과정 필요), 제자리 정렬이 아님(메모리 효율 떨어짐)
// 작은 배열에선 비효율적 (삽입 정렬 보다 오히려 느림),캐시 친화적이지 않음(메모리 접근이 많아 속도가 느려질 수 있음)
// 시간 복잡도: Best: O(n log n), Avg: O(n log n), Worst: O(n log n)

import java.io.IOException;
import java.util.Arrays;

import Algorithm.Sort.ArrGenerator.ArrGenerator;

public class _1_5_Merge {
	public static void main(String[] args) throws IOException {
		ArrGenerator a = new ArrGenerator();

		int[] arr = a.init();

		System.out.println("Befor: " + Arrays.toString(arr));

		mergeSort(arr, 0, arr.length - 1);
		System.out.println("Finish: " + Arrays.toString(arr));

	}

	private static void mergeSort(int[] arr, int left, int right) {
		System.out.println(
			"merge sort start arr : " + Arrays.toString(arr) + ", left : " + left + ", right : " + right);

		if (left < right) {

			int mid = (left + right) / 2;

			mergeSort(arr, left, mid);
			mergeSort(arr, mid + 1, right);
			System.out.println("==========================================================");
			System.out.println("Before Merge : " + Arrays.toString(arr));
			merge(arr, left, mid, right);
			System.out.println("==========================================================");
			System.out.println("After Merge : " + Arrays.toString(arr));
		}
	}

	private static void merge(int[] arr, int left, int mid, int right) {
		int[] L = Arrays.copyOfRange(arr, left, mid + 1);
		int[] R = Arrays.copyOfRange(arr, mid + 1, right + 1);

		System.out.println("Merge start Left : " + left + ", right: " + right + ", L arr : " + Arrays.toString(L) + ", R arr : " + Arrays.toString(R) );

		int i = 0, j = 0, k = left;
		int l1 = L.length, r1 = R.length;

		while (i < l1 && j < r1) {
			if (L[i] <= R[j]) {
				arr[k] = L[i++];
			} else {
				arr[k] = R[j++];
			}
			k++;
		}

		while (i < l1) {
			System.out.println("==========================================================");
			System.out.println("L Merge Before: " + Arrays.toString(arr));
			arr[k++] = L[i++];
			System.out.println("L Merge After: " + Arrays.toString(arr));
		}
		while (j < r1) {
			System.out.println("==========================================================");
			System.out.println("R Merge Before: " + Arrays.toString(arr));
			arr[k++] = R[j++];
			System.out.println("R Merge After: " + Arrays.toString(arr));
		}

	}
}
