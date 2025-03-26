package Algorithm.Sort;

// 버킷 정렬: 데이터를 여러 개의 버킷으로 분산 후 각 버킷 내부에서 정렬을 수행, 버킷을 순서대로 합침
// 분산 단계: n개의 균등한 크기의 버킷으로 나눔, 정렬 단계: 각 버킷 내부를 다른 정렬 알고리즘 으로 정렬, 수집 단계: 모든 버킷을 순샤대로 합쳐 배열 생성
// 주로 실수와 같이 연속적인 값이 일정 범위 내에 균등하게 분포되어 있는 데이터에 가장 잘 작동 특히 부동 소숫점 숫자, 정수에서도 사용은 가능
// 입력 데이터의 분포에 따라 성능이 크게 달라짐, 추가 메모리 필요
// 실수: ( 특히 0~1 범위의 값): O(n) 시간 복잡도에 가까운 성능 가능, 각 버킷에 들어가는 원소들이 비교적 적어지기 때문에 내부적으로 빠른 정렬 가능
// 정수: 정수의 범위가 너무 크면 버킷의 크기를 설정하는 데 문제 발생 가능 -> 입력이 너무 크면 메모리 낭비 -> ex 1~1_000_000 의 범위
// 시간 복잡도: Best O(n + k): 데이터가 균등하게 분포, Worst: O(n^2): 모든 원소가 하나의 버킷에 들어가는 경우, Avg: O(n+k): k는 버킷 수

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import Algorithm.Sort.ArrGenerator.ArrGenerator;

public class _2_3_Bucket {
	public static void main(String[] args) {

		ArrGenerator a = new ArrGenerator();

		int[] arr = a.init();
		System.out.println("Before Arr: " + Arrays.toString(arr));
		bucektSort(arr);
		System.out.println("After Arr: " + Arrays.toString(arr));

	}

	private static void bucektSort(int[] arr) {
		int n = arr.length;
		List<Integer>[] buckets = new ArrayList[n];

		for (int i = 0; i < n; i++) {
			buckets[i] = new ArrayList<>();
		}

		// 버킷에 원소 배치, 부동 소숫점
		// for (int i = 0; i < n; i++) {
		// 	int bucketIndex = (n * arr[i]);
		// 	buckets[bucketIndex].add(arr[i]);
		// }

		// 정수 배치
		int min = arr[0], max = arr[0];
		for (int i = 1; i < n; i++) {
			if (arr[i] < min)
				min = arr[i];
			if (arr[i] > max)
				max = arr[i];
		}

		// 각 원소를 적절한 버킷에 배치 (정수)
		int range = max - min + 1;
		for (int i = 0; i < n; i++) {
			// 버킷 인덱스 계산 공식: (값 - 최소값) * 버킷 수 -1) / (최대값 - 최소값)
			int bucketIndex = ((arr[i] - min) * (n - 1) / range);
			buckets[bucketIndex].add(arr[i]);
		}

		// 각 버킷 정렬
		for (int i = 0; i < n; i++) {
			Collections.sort(buckets[i]);
		}

		// 버킷 합치기
		int index = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < buckets[i].size(); j++) {
				arr[index++] = buckets[i].get(j);
			}
		}
	}
}
