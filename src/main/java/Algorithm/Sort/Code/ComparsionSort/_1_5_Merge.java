package Algorithm.Sort.Code.ComparsionSort;

// 병합 정렬: 분할 정복 방법을 통해 구현, 빠른 정렬로 분류되며 Quick과 함꼐 많이 언급, Quick과 반대로 안정 정렬
// 원소가 1개가 될 때까지 분할한 뒤 병합 시키면서 정렬(두 영역이 정렬이 되어있기 때문에 순차적으로 두 배열을 비교)해 나가는 방식, 쪼개는 방식은 Quick과 유사
// Quick과 차이점
// -> Quick: 피벗을 통해 Partition 과정에서 원소를 재배치 하며 분할
// -> Merge: 영역을 쪼갤 수 있을 만큼 쪼갠(Merge Sort, 단순히 반으로 나누며) 뒤 Merge 과정에서 정렬이 이뤄짐
// 장점: 순차적인 비교로 정렬을 진행하므로 LinkedList의 정렬이 필요할 때 사용하면 효율적, 안정정렬, 최악의 경우에도 O(n log n) 보장
// -> Merge Sort는 LinkedList 에서도 분할과 병합을 포인터 연결만 변경하여 수행할 수 있어 매우 효율적
// -> Quick Sort 사용시 Partition 과정에서 임의 접근과 반복적인 교환(swap)이 필요하므로 LinkedList에서는 비효율적 & (접근 연산에서의 비효율) 오버헤드 발생 가능
// -> 접근 시 배열: O(1), LinkedList: O(n)
// 단점: 추가적인 메모리 공간 필요(정렬을 위해 배열을 복사하는 과정 필요)
// 병합을 위한 추가 메모리 사용 & 메모리 접근량이 증가해 CPU Cache 효율이 Quickl Sort 보다 떨어짐
// 작은 배열에선 비효율적 (삽입 정렬 보다 오히려 느림),캐시 친화적이지 않음(메모리 접근이 많아 속도가 느려질 수 있음)
// 시간 복잡도: Best: O(n log n), Avg: O(n log n), Worst: O(n log n)
// 공간 복잡도: O(n)

// 데이터들의 크기가 주기억장치(RAM)에 다 들어가지 못하는 데이터 정렬에도 많이 사용
// 안정 정렬이 필요한 경우 사용 ex) 동접자는 이름순 유지 ...

import Algorithm.Sort.ArrGenerator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class _1_5_Merge {

  static boolean isAllowPrint;

  public static void main(String[] args) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    ArrGenerator a = new ArrGenerator();

    int[] arr = a.init();
    int[] temp = new int[arr.length];
    isAllowPrint = a.isAllowPrint();

    System.out.println("Before: " + Arrays.toString(arr));

    System.out.println("Merge Sort 방법 선택\n1: 공부용 정렬, 2: 배열 복사 부분이 개선된 정렬 ");
    int selectNum = Integer.parseInt(br.readLine());

    long start = System.nanoTime();

    mergeSort(arr, temp, selectNum, 0, arr.length - 1);
    System.out.println("==========================================================");
    System.out.println("result = " + Arrays.toString(arr));
    System.out.println("Merge Sort | " + (System.nanoTime() - start) / 1_000_000.0 + "ms");
  }

  private static void mergeSort(int[] arr, int[] temp, int selectNum, int left, int right) {
    if (isAllowPrint) {
      System.out.println("merge sort start arr : " + Arrays.toString(arr) + ", left : " + left + ", right : " + right);
    }

    if (left < right) {
      int mid = left + (right - left) / 2;

      mergeSort(arr, temp, selectNum, left, mid);
      mergeSort(arr, temp, selectNum, mid + 1, right);

      if (isAllowPrint) {
        System.out.println("==========================================================");
        System.out.println("Before Merge : " + Arrays.toString(Arrays.copyOfRange(arr, left, right + 1)));
      }

      switch (selectNum) {
        case 1:
          merge(arr, left, mid, right);
          break;
        case 2:
          optimizedMerge(arr, temp, left, mid, right);
          break;
        default:
          throw new IllegalArgumentException("잘못된 정렬 선택");
      }

      if (isAllowPrint) {
        System.out.println("==========================================================");
        System.out.println("After Merge : " + Arrays.toString(Arrays.copyOfRange(arr, left, right + 1)));
      }
    }
  }

  private static void merge(int[] arr, int left, int mid, int right) {
    // merge 구간을 임시 두 배열로 복사
    int[] L = Arrays.copyOfRange(arr, left, mid + 1);
    int[] R = Arrays.copyOfRange(arr, mid + 1, right + 1);

    if (isAllowPrint) {
      System.out.println(
          "Merge start Left : " + left + ", right: " + right + ", "
              + "L arr : " + Arrays.toString(L) + ", "
              + "R arr : " + Arrays.toString(R)
      );
    }

    int i = 0, j = 0, k = left;
    int leftSize = L.length, rightSize = R.length;

    while (i < leftSize && j < rightSize) {
      // if (L[i] < R[j]) 로 해버리면 오른쪽 값이 먼저 들어가므로 안정 정렬이 깨짐
      if (L[i] <= R[j]) {
        arr[k++] = L[i++];
      } else {
        arr[k++] = R[j++];
      }
    }

    while (i < leftSize) {
      if (isAllowPrint) {
        System.out.println("==========================================================");
        System.out.println("L Merge Before: " + Arrays.toString(Arrays.copyOfRange(arr, left, right + 1)));
      }
      arr[k++] = L[i++];

      if (isAllowPrint) {System.out.println("L Merge After: " + Arrays.toString(Arrays.copyOfRange(arr, left, right + 1)));}
    }

    while (j < rightSize) {
      if (isAllowPrint) {
        System.out.println("==========================================================");
        System.out.println("R Merge Before: " + Arrays.toString(Arrays.copyOfRange(arr, left, right + 1)));
      }

      arr[k++] = R[j++];

      if (isAllowPrint) {System.out.println("R Merge After: " + Arrays.toString(Arrays.copyOfRange(arr, left, right + 1)));}
    }
  }

  // 기존엔 매 merge 마다 Arrays.copyOfRange() 를 사용해 새로운 L, R 배열을 생성하여 데이터를 복사
  // 매 merge 마다 두 배열 생성(메모리 할당) & GC 대상 객체가 지속적으로 생성됨
  // 공부 확인용으론 좋으나 성능에서 매우 좋지 않음
  // -> temp 배열은 최초 한 번만 생성 & merge 시 필요한 구간만 temp로 복사한 후 temp에서 읽고 arr에 기록한다.
  private static void optimizedMerge(int[] arr, int[] temp, int left, int mid, int right) {
    // left ~ right 구간만 temp에 복사
    System.arraycopy(arr, left, temp, left, right - left + 1);

    if (isAllowPrint) {
      System.out.println(
          "Optimized Merge Start"
              + "\nleft : " + left
              + ", mid : " + mid
              + ", right : " + right
              + "\nTemp : " + Arrays.toString(Arrays.copyOfRange(temp, left, right + 1))
      );
    }

    int i = left;     // 왼쪽 시작 포인터
    int j = mid + 1;  // 오른쪽 시작 포인터
    int k = left;     // arr에 저장 할 위치

    while (i <= mid && j <= right) {
      // <= 로 비교해야 안정 정렬 보장
      if (temp[i] <= temp[j]) {
        arr[k++] = temp[i++];
      } else {
        arr[k++] = temp[j++];
      }
      if (isAllowPrint) {
        System.out.println("Merge : " + Arrays.toString(Arrays.copyOfRange(arr, left, right + 1)));
      }
    }

    // 개선된 방법은 기존 방법과 달리 배열을 좌 & 우 두 개로 나누지 않고 해당 범위를 하나의 배열로 만들어서 사용
    // 병합은 arr의 left 부터 차례대로 덮어쓰며 진행
    // -> 때문에 오른쪽 원소가 먼저 선택되는 경우 아직 사용하지 않은 왼쪽 원소는 arr에서 이미 덮어써져 사라질 수 있음
    // -> 때문에 남아 있는 왼쪽 원소(temp[i ~ mid])는 다시 arr로 복사해야 함
    //
    // 반면 오른쪽 원소가 남아 있는 경우(temp[j] ~ temp[right])의 해당 위치는
    // 아직 k가 j 위치까지 도달하지 않았거나 도달하더라도 이후 값들은 아직 덮어쓰지 않은 상태로
    // 원래 값이 그대로 남아 있어 별도의 복사가 필요하지 않아 왼쪽만 복사해주면 됨
    while (i <= mid) {
      arr[k++] = temp[i++];

      if (isAllowPrint) {
        System.out.println("Left Copy : " + Arrays.toString(Arrays.copyOfRange(arr, left, right + 1)));
      }
    }
  }
}
