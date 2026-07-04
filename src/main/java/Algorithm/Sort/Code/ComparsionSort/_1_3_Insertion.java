package Algorithm.Sort.Code.ComparsionSort;

// Select, Bubble과 유사, 2번째 요소부터 시작 해 앞의 요소들과 비교 후 삽입 할 위치 지정, 앞의 원소를 뒤로 옮기고 지정된 자리에 삽입
// 시간복잡도: Best(정렬이 되어 있는경우) O(n), Worst(역으로 정렬되어 있을 경우) & Avg O(n^2), 공간복잡도: 주어진 배열 안에서 Swap O(1)
// 장점: 알고리즘 단순, 정렬되어 있는경우 매우 효과적, 추가 메모리 공간 필요X (제자리 정렬), 안정 정렬(Stable Sort), Select|Bubble 과 같은 O(n^2)보다 상대적으로 빠름
// 단점: Worst & Avg = O(n^2), 배열의 길이가 길 수록 비효율적
// 팁: _2_Selection, Insertion 들은 K번째 반복 이후 첫번쨰 K 요소가 정렬된 순서로 온다는 점으로 유사
// -> 하지만 Selection는 K+1요소를 찾기 위해 모든 요소를 탐색, Insertion는 K+1 요소를 배치하는 데 필요한 만큼의 요소만 탐색 (더 효율적)
// 삽입 정렬은 단독으로는 잘 안 쓰지만, 다른 정렬 알고리즘 속에서 보조 역할로 많이 사용

import Algorithm.Sort.ArrGenerator;
import java.io.IOException;
import java.util.Arrays;

public class _1_3_Insertion {

  static boolean isAllowPrint;

  public static void main(String[] args) throws IOException {
    ArrGenerator a = new ArrGenerator();

    int[] arr = a.init();
    isAllowPrint = a.isAllowPrint();

    System.out.println("Before: " + Arrays.toString(arr));
    System.out.println("==========================================================");

    long start = System.nanoTime();
    for (int i = 1; i < arr.length; i++) {
      int temp = arr[i];
      int prev = i - 1;

      while (prev >= 0 && arr[prev] > temp) {
        arr[prev + 1] = arr[prev];
        prev--;
      }
      arr[prev + 1] = temp;
      if (isAllowPrint) {
        System.out.println("==========================================================");
        System.out.println(Arrays.toString(arr));
      }
    }
    System.out.println("After:  " + Arrays.toString(arr));
    System.out.println("==========================================================");
    System.out.println("Insertion Sort | " + (System.nanoTime() - start) / 1_000_000.0 + "ms");
    if (!a.isSorted(arr)) {System.out.println("정렬 안돼있음");}
  }
}
