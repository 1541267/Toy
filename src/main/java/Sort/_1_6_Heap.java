package Sort;

// 힙 정렬: 완전 이진 트리(삽입 시 왼쪽부터 차례대로 추가) 를 기본으로 하는 Heap 구조를 기반으로한 정렬 방식
// 시간 복잡도: Best: O(n log n), Worst: O(n log n), Avg: O(n log n)
// 최대 힙 구성(삽입시 부모와 비교, 부모가 큰 값이 되도록 비교) -> 루트(힙의 시작 값)가 최댓값이 됨, 마지막 요소와 교환 후 힙의 사이즈 -1
// 사이즈가 1보다 크면 위 과정 반복

// 장점: 시간복잡도 모두 O(n log n), 제자리 정렬 O(1)의 공간 복잡도, 부분 정렬에 효율적(가장 큰 or 작은) k개의 요소만 필요할 때
// 우선순위 큐 구현, 대용량 데이터에 적합 (퀵 정렬처럼 대용량 데이터 처리에 효율적), 최악의 경우의 성능 보장 or 메모리 제약이 있는 환경에서 유용
// 단점: 불안정 정렬, 캐시 지역성 낮음(메모리 접근이 불규칙, 캐시 효율성이 낮음)
// 실제 구현에서 상수 요소가 높음 같은 O(n log n) 퀵 정렬, 합병 정렬에 비해 더 많은 연산 필요, 이미 정렬된 데이터에 최적화되지 않음(동일한 시간)
// 구현 복잡

import java.util.Arrays;

import Sort.ArrGenerator.ArrGenerator;

public class _1_6_Heap {
	public static void main(String[] args) {
		ArrGenerator a = new ArrGenerator();

		int[] arr = a.init();

		System.out.println("Before Arr: " + Arrays.toString(arr));

		heapSort(arr);
		System.out.println("==========================================================");
		System.out.println("After Arr: " + Arrays.toString(arr));

	}

	private static void heapSort(int[] arr) {
		int n = arr.length;

		// maxHeap 초기화
		for (int i = n / 2 - 1; i >= 0; i--) {        // 부모(root)노드 기준으로 왼쪽 자식노드 (2n + 1), 오른쪽 자식노드 (2n + 2)
			System.out.println("==========================================================");
			System.out.println("Before heapify: " + Arrays.toString(arr));
			heapify(arr, n, i);        // 배열을 힙으로 구성, 자식노드로  부터 부모노드 비교, 부모가 항상 크게 조정, O(n)
			System.out.println("After heapify: " + Arrays.toString(arr));
		}

		// maxHeap의 extract 연산
		for (int i = n - 1; i > 0; i--) {
			swap(arr, 0, i);					// 최댓값이 마지막 위치로 이동
			heapify(arr, i, 0);				// 요소가 하나 제거된 이후에 다시 최대 힙을 구성, 루트 기준으로 진행
		}
	}

	private static void heapify(int[] arr, int n, int i) {
		int p = i;
		int l = i * 2 + 1;
		int r = i * 2 + 2;

		// 왼쪽 자식 노드
		if (l < n && arr[p] < arr[l]) {
			p = l;
		}

		// 오른쪽 자식 노드
		if (r < n && arr[p] < arr[r]) {
			p = r;
		}

		// 부모노드 < 자식노드
		if (i != p) {
			swap(arr, p, i);
			heapify(arr, n, p);
		}
	}

	private static void swap(int[] arr, int a, int b) {
		System.out.println("==========================================================");

		System.out.println("Before Swap Arr: " + Arrays.toString(arr));
		int temp = arr[a];
		arr[a] = arr[b];
		arr[b] = temp;
		System.out.println("After Swap Arr: " + Arrays.toString(arr));
	}

}
