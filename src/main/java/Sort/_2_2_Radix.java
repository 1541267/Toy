package Sort;

// 기수(Radix) 정렬: 데이터를 구성하는 기본 요소(Radix)를 이용해 정렬을 진행하는 방식 , 아래의 방법은 LSD
// 입력 데이터의 최대값에 따라 Counting Sort의 비효율성을 개선하기 위해 Radix Sort 사용 가능
// ex) 자릿수의 갑 별로 정렬 하므로 나올 수 있는 값의 최대 사이즈는 9
// 장점: 안정 정렬, 시간 복잡도: O(d *(n+b)) or O(nd): d는 정렬할 숫자의 자릿수, b는 10(k와 같으나 10으로 고정), Counting 경우 최댓값 k에 영향, 문자열|정수 가능
// 단점: 자릿수가 없는 것은 정렬 불가 (부동 소숫점), 중간 결과를 저장할 bucket 공간이 필요함

// 낮은 자리수 부터 정렬하는 이유: MSD(Most Significant Digit) 큰 자리수 부터, LSD(Least Siginificant Digit) 작은 자리수 부터 의 차이, 둘 다 가능
// LSD의 경우 1600000과 1을 비교할 때 Digit의 갯수만큼 따져야하는 단점이 있음, 그에 반해 MSD는 마지막 자리수 까지 확인할 필요 없음
// LSD는 중간 정렬 결과를 알 수 없음 (ex 10004와 70002의 비교), MSD는 중간에 중요한 숫자를 알 수 있음, 따라서 시간 줄이기 가능하나
// -> 정렬이 됐는지 확인하는 과정이 필요, 이 때문에 메모리를 더 사용
// LSD는 알고리즘이 일관됨(Branch Free Algorithm), MSD는 일관되지 못함 -> 때문에 Radix는 주로 LSD 언급
// -> MSD는 상위 자릿수부터 내려가기 때문에 하위 자릿수에서의 처리 방식이 상위 자릿수에서의 처리 방식에 영향을 받을 수 있음
// LSD는 자릿수가 정해진 경우 좀 더 빠를 수 있음

import java.util.Arrays;

import Sort.ArrGenerator.ArrGenerator;

public class _2_2_Radix {
	public static void main(String[] args) {
		ArrGenerator a = new ArrGenerator();

		int[] arr = a.init();

		int n = arr.length;
		System.out.println("Before Arr: " + Arrays.toString(arr));
		radixSort(arr, n);
		System.out.println("After Arr: " + Arrays.toString(arr));
	}

	private static void radixSort(int[] arr, int n) {
		int m = getMax(arr);
		// 최댓값을 나눴을 때 0이 나오면 모든 숫자가 i의 아래
		// exp: 자릿수
		for (int exp = 1; (m / exp) > 0; exp *= 10) {
			countSort(arr, n, exp);
		}

	}

	// 특정 자릿수를 기준으로 counting sort 수행
	private static void countSort(int[] arr, int n, int exp) {
		int[] buffer = new int[n];
		int[] count = new int[10];

		// exp의 자릿수에 해당하는 count 증가
		for (int i = 0; i < n; i++) {
			count[(arr[i] / exp) % 10]++;
		}

		// 누적 합
		for (int i = 1; i < 10; i++) {
			count[i] += count[i - 1];
		}

		// 일반적인 Counting sort 과정
		for (int i = n - 1; i >= 0; i--) {
			buffer[count[(arr[i] / exp) % 10] - 1] = arr[i];
			count[(arr[i] / exp) % 10]--;
		}

		// 원래 배열에 정렬된 결과 복사
		for (int i = 0; i < n; i++) {
			arr[i] = buffer[i];
		}
	}

	static int getMax(int[] arr) {

		int max = 0;
		for (int i = 0; i < arr.length; i++) {
			max = Math.max(arr[i], max);
		}
		return max;
	}
}
