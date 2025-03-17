package sort;

// 선택 정렬: 원소를 넣을 위치(인덱스(선택되어있음))는 정해져 있는 것이 _1_Bubble Sort 와의 차이
// 시험에서 n회 회전 정렬 시 모양 구하기 문제?, 1회전시 최소값이 맨 앞, 최소값 순서 정렬
// 시간 복잡도: Best & Worst & Avg = O(n^2), 공간 복잡도: 주어진 배열 안에서 교환(Swap) O(1)
// 장점: 알고리즘 단순, Bubble에 비해 비교횟수가 적어 많은 교환이 일어나는 자료 상태에서 비교적 효율적, 제자리 정렬(배열 안에서 교환)이라 다른 메모리 공간 필요 X
// 단점: O(n^2) 비효율, 불안정 정렬(Unstable Sort), 배열의 길이가 길 수록 비효율적


import java.io.IOException;
import java.util.Arrays;

import sort.customArr.CustomArray;

public class _2_Selection {
	public static void main(String[] args) throws IOException {

		CustomArray a = new CustomArray();

		int[] arr = a.generateArr();

		System.out.println("Before Sort arr = " + Arrays.toString(arr));

		int index, temp;

		for (int i = 0; i < arr.length - 1; i++) {
			index = i;
			for (int j = i + 1; j < arr.length; j++) {
				if (arr[index] > arr[j]) {
					index = j;
				}
			}
			temp = arr[index];
			arr[index] = arr[i];
			arr[i] = temp;
		}
		System.out.println("======================================");
		System.out.println("After Sort arr = " + Arrays.toString(arr));
	}
}
