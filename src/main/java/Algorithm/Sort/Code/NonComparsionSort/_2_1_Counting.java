package Algorithm.Sort.Code.NonComparsionSort;

// _1_~ 방법들은 Comparsion Sort, 이를 모두 정렬하는 가짓수는 n!
// 정렬을 통해 생기는 트리의 말단 노드가 n! 이상의 노드를 갖기 위해선 2^h >= n!을 만족하는 h를 가져야 하고
// 이 식은 h > O(n log n) -> h는 트리의 높이 (Comparison Sort의 시간 복잡도) -> 이 O(n log n)을 줄이기 위해선 Comparison을 하지 않는 것

// 계수 정렬: Counting 필요 (숫자가 몇 번 등장 했는지), 정렬하려는 숫자가 특정한 범위 내에 있을 때 사용, 등장 횟수를 누적합해 삽입 -> 안정 정렬
// 계수 정렬은 값 자체를 인덱스로 사용하기 떄문에 비교(Comparision)이 없어 비교 정렬의 O(n log n) 하한을 우회 가능
//
// 시간 복잡도: O(n + k): k는 배열에서 등장하는 값의 범위(max - min + 1), 공간 복잡도: O(k) -> k만큼의 배열을 만들어야 함
// 장점: O(n)의 시간 복잡도,
// 단점: 배열 사이즈를 n 만큼 돌 때 증가시켜주는 Counting 배열의 크기가 큼 (메모리 낭비가 심함), k가 너무 클 경우 비효율적 (ex: 입력 범위가 1조 -> O(k) 가 되어버림)
// -> ex) [100000, 100001, 100002] -> 최댓값은ㅁ 100002로 크지만 범위는 3이라 매우 효율적
// -> ex) [-500000, 500000] -> 값 자체는 크지 않아도 min, max의 차이(범위)가 크면 비효율적

import Algorithm.Sort.ArrGenerator;
import java.util.Arrays;

public class _2_1_Counting {

  static boolean isAllowPrint;

  public static void main(String[] args) {
    ArrGenerator a = new ArrGenerator();

    int[] arr = a.init();
    int[] arr2 = arr.clone();
    isAllowPrint = a.isAllowPrint();
    System.out.println("Before Arr: " + Arrays.toString(arr));

    long start = System.nanoTime();
    arr = countingSort(arr);
    String firstEnd = (System.nanoTime() - start) / 1_000_000.0 + "ms";

    start = System.nanoTime();
    arr2 = countingSort(arr2);
    String secondEnd = (System.nanoTime() - start) / 1_000_000.0 + "ms";

    System.out.println("after arr = " + Arrays.toString(arr));
    System.out.println("after arr2 = " + Arrays.toString(arr2));
    System.out.println("==========================================================");
    System.out.println("Counting Sort\nStable Sort: " + firstEnd + ", 간단 구현 Unstable Sort: " + secondEnd);
  }

  public static int[] countingSort(int[] arr) {
    if (arr == null || arr.length == 0) {return arr;}
    int min = arr[0], max = arr[0];

    for (int num : arr) {
      if (num < min) {min = num;}
      if (num > max) {max = num;}
    }

    int range = max - min + 1;

    int[] counting = new int[range];
    int[] output = new int[arr.length];

    for (int num : arr) {counting[num - min]++;}

    // 누적합
    for (int i = 1; i < range; i++) {counting[i] += counting[i - 1];}

    // 누적합 이후 counting[i]는 그 값이 배치될 마지막 인덱스(+1) = "값 (i+min) 이하인 원소의 개수"
    for (int i = arr.length - 1; i >= 0; i--) {
      int num = arr[i];
      output[--counting[num - min]] = num;
    }

    return output;
  }

  // 간단히 counting[i]번 등장 한 요소를 배열 순서를 참조하지 않고 삽입하기 때문에 UnStable Sort
  public static int[] unStableCountingSort(int[] arr) {
    int min = arr[0], max = arr[0];
    for (int num : arr) {
      if (num < min) {min = num;}
      if (num > max) {max = num;}
    }

    int range = max - min + 1;
    int[] counting = new int[range];

    for (int num : arr) {counting[num - min]++;}

    int[] output = new int[arr.length];
    int idx = 0;
    for (int i = 0; i < range; i++) {
      while (counting[i] > 0) {
        output[idx++] = i + min;
        counting[i]--;
      }
    }
    return output;
  }
}