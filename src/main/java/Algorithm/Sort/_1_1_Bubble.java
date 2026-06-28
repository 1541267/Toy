package Algorithm.Sort;

// 거품 정렬: 서로 인접한 원소를 대소비교, 1회전 시 가장 큰 값이 맨 뒤, 최대값 순서대로 정렬
// 시간복잡도: Best, Worts, Avg = O(n^2), 공간복잡도: 주어진 배열 안에서 교환(Swap) O(1)
// 장점: 구현 간단, 제자리 정렬(배열 안에서 교환)이라 다른 메모리 공간 필요 X, 안정 정렬(Stable Sort)
// 단점: O(n^2) 비효율, 정렬 되어있지 않은 원소를 정렬하기 위해 교환 연산(Swap)이 많이 발생, 배열의 길이가 길 수록 비효율적

import Algorithm.Sort.ArrGenerator.ArrGenerator;
import java.io.IOException;
import java.util.Arrays;

public class _1_1_Bubble {

  static boolean isAllowPrint;

  public static void main(String[] args) throws IOException {
    ArrGenerator a = new ArrGenerator();

    int[] arr = a.init();
    isAllowPrint = a.isAllowPrint();

    System.out.println("Before sort = " + Arrays.toString(arr));

    long start = System.nanoTime();

    for (int i = 0; i < arr.length - 1; i++) {
      for (int j = 1; j < arr.length - i; j++) {
        if (arr[j - 1] > arr[j]) {
          int temp = arr[j - 1];
          arr[j - 1] = arr[j];
          arr[j] = temp;
        }
      }
      if (isAllowPrint) {
        System.out.println("======================================");
        System.out.println(Arrays.toString(arr));
      }
    }
    System.out.println("After sort = " + Arrays.toString(arr));
    System.out.println("======================================");
    System.out.println("Buble Sort | " + (System.nanoTime() - start) / 1_000_000.0 + "ms");
  }
}
