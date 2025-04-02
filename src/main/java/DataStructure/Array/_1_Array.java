package DataStructure.Array;

// C++ 의 사이즈 구하기, sizeof(arr) / sizeof(arr[0]);

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import Algorithm.Sort.ArrGenerator.ArrGenerator;

public class _1_Array {
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) throws IOException {

		ArrGenerator a = new ArrGenerator();
		int[] arr = a.init();
		int select = 0;

		while (select != -1) {
			System.out.println("==========================================================");
			System.out.println("배열 알고리즘 선택 \n1:회전 | 2:저글링 | 3:역전 | 4: 최대 합 | 5: arr[i] = i 재배열 \n-1: 종료");

			select = Integer.parseInt(br.readLine());

			switch (select) {
				case 1:
					rotateArr(arr);
					break;
				case 2:
					juggling(arr);
					break;
				case 3:
					reverseArr(arr);
					break;
				case 4:
					searchMaximumSum(arr);
					break;
				case 5:
					int[] arr2 = {19, 7, 0, 3, 18, 15, 12, 6, 1, 8,
						11, 10, 9, 5, 13, 16, 2, 14, 17, 4};
					rearangement(arr2);
					// int[] arr3 = {-1, -1, 6, 1, 9, 3, 2, -1, 4, -1};
					int[] arr3 = {2, -1, 1, 4, 0, 3};
					rearangement(arr3);
					break;
				default:
					System.out.println("다시 입력");
			}
		}

	}

	private static void rotateArr(int[] arr) throws IOException {
		// 1 배열 회전
		System.out.println("==========================================================");
		System.out.println("회전 Before Arr: " + Arrays.toString(arr));
		System.out.println("배열 회전, 0 입력시 종료");

		do {
			int temp = arr[0];
			for (int i = 0; i < arr.length - 1; i++) {
				arr[i] = arr[i + 1];
			}
			arr[arr.length - 1] = temp;

		} while (!(Integer.parseInt(br.readLine()) == 0));
	}

	private static int gcd(int a, int b) {
		return (b == 0) ? a : gcd(b, a % b);
	}

	private static void juggling(int[] arr) throws IOException {
		System.out.println("==========================================================");
		System.out.print("저글링, 왼쪽으로 이동 할 칸 수 입력 ");
		int n = arr.length;
		int d = Integer.parseInt(br.readLine());

		d = d % n;
		if (d == 0) {
			System.out.println("회전할 필요가 없습니다. 종료");
			return;
		}

		// 저글링 알고리즘
		// arr의 크기 (n), 왼쪽으로 d칸 회전하는 경우 n과 d의 최대공약수(GCD) 구함
		System.out.println("저글링 Before Arr: " + Arrays.toString(arr));
		leftRotate(arr, d, n);
		System.out.println("저글링 After Arr: " + Arrays.toString(arr));

	}

	private static void leftRotate(int[] arr, int d, int n) {

		int gcdValue = gcd(d, n);
		System.out.println("GCD(" + d + ", " + n + ") = " + gcdValue);

		for (int i = 0; i < gcdValue; i++) {
			int temp = arr[i];
			int j = i;

			while (true) {
				int k = j + d;
				if (k >= n)
					k = k - n;
				if (k == i)
					break;

				arr[j] = arr[k];
				j = k;
			}
			arr[j] = temp;
		}
	}

	private static void reverseArr(int[] arr) throws IOException {
		// 회전 시키는 수에 대해 구간을 나누고 해당 구간들을 reverse, 두 구간을 합치고 다시 reverse
		System.out.println("==========================================================");
		System.out.println("Before ReverseArr: " + Arrays.toString(arr));
		System.out.println("회전 시킬 구간: ");

		int d = Integer.parseInt(br.readLine()), n = arr.length;
		rotateReverseArr(arr, 0, d - 1);
		rotateReverseArr(arr, d, n - 1);
		// 위에까지는 나누어진 두 구간의 역전, 아래는 합치고 합친것 까지 역전
		// rotateReverseArr(arr, 0, n - 1);
		System.out.println("After ReverseArr: " + Arrays.toString(arr));

	}

	private static void rotateReverseArr(int[] arr, int start, int end) {

		while (start < end) {
			int temp = arr[start];
			arr[start] = arr[end];
			arr[end] = temp;

			start++;
			end--;
		}
	}

	private static void searchMaximumSum(int[] arr) {
		// i * arr[i] 의 최댓값 구하기 , 초기 arrSum += arr[i], curSum = i*arr[i]
		// curSum = curSum + arrSum - n * arr[n - i];
		// 여기서 n회 회전 시킨 값을 구하려면 -> R0 = 0*a0 .. (n-1)an-1 & R1 = 0*a1, a2 .. an-1 -> a2 .. (n-2)an-1, (n-1)an0
		// Rj - (Rj-1) = arrSum - n* arr[n-i], == Rj = (Rj-1) + arrSum - n*arr[n-i]
		System.out.println("==========================================================");
		System.out.println("특정 최대 합 구하기 (i * arr[i]) ");
		System.out.println("Arr: " + Arrays.toString(arr));
		int arrSum = 0, curSum = 0;
		int n = arr.length;
		// 처음 sum 값
		for (int i = 0; i < n; i++) {
			arrSum += arr[i];
			curSum += (i * arr[i]);
		}

		int maxSum = curSum;
		int rotationCount = 0;
		// 한 칸씩 회전
		for (int i = 1; i < n; i++) {
			curSum = curSum + arrSum - n * arr[n - i];

			if (curSum > maxSum) {
				maxSum = curSum;
				rotationCount = i;
			}

		}
		System.out.println("최대 합: " + maxSum + ", 회전 횟수: " + rotationCount);
	}

	private static void rearangement(int[] arr) {

		// arr[i] = i 가 없으면 -1
		// arr[i]를 저장(x) arr[x]를 탐색후 저장(y), arr[x] != -1 & arr[x]가 x가 아닌 동안 탐색
		// arr[x] 를 x로 저장, 기존의 x를 y로 수정
		System.out.println("특정 배열을 arr[i] = i 로 재배열, 없으면 -1");
		System.out.println("Before Rearangeement: " + Arrays.toString(arr));

		for (int i = 0; i < arr.length; i++) {
			if (arr[i] != -1 && arr[i] != i) {
				System.out.println("i : " + i);
				int x = arr[i];

				while (arr[x] != -1 && arr[x] != x) {
					int y = arr[x];
					arr[x] = x;
					x = y;
				}
				arr[x] = x;
				if (arr[i] != i) {
					arr[i] = -1;
				}
				System.out.println("While Rearangeement: " + Arrays.toString(arr));
			}

		}
		System.out.println("After Rearangement : " + Arrays.toString(arr));
	}
}
