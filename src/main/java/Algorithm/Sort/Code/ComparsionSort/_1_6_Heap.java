package Algorithm.Sort.Code.ComparsionSort;

// 힙 정렬: 완전 이진 트리 형태의 Heap(힙) 자료구조를 이용한 정렬 방식
// 시간 복잡도: Best: O(n log n), Worst: O(n log n), Avg: O(n log n)
// 최대 힙(Max Heap)을 구성하여 루트(배열의 첫 번째 요소)에 최댓값을 위치시킨 뒤 마지막 요소와 Swap
// Heap 의 크기가 1이 될 때까지 감소시키며 Heapify & Swap 반복하며 오름차순 정렬

// 장점: 시간복잡도 모두 O(n log n), 제자리 정렬 O(1)의 공간 복잡도, 부분 정렬에 효율적(가장 큰 or 작은) k개의 요소만 필요할 때
// Heap 자료구조를 사용하는 우선순위 큐(Priority Queue)의 원리를 이해
// 최악의 경우에도 O(n log n)이 보장되어 성능 예측이 중요한 환경에 적합 or 메모리 제약이 있는 환경에서 유용

// 단점: 불안정 정렬, 구현 복잡, 캐시 지역성 낮음(메모리 접근이 불규칙, 캐시 효율성이 낮음)
// 실제 구현에서 상수 요소가 높음 같은 O(n log n) 퀵 정렬, 합병 정렬에 비해 더 많은 연산 필요
// 입력 데이터의 상태(정렬 여부)에 관계없이 항상 비슷한 수행 시간을 가짐

// 최악의 성능 보장이 필요할 때 사용 O(n log n)
// 최댓값 or 최솟값을 여러번 꺼내야 하는 경우 ex) 게임 랭킹, 우선순위 큐, 스케줄러 등

// 추가 설명 - Bottom-Up Heapify (Floyd)
// -> 일반 Heapify의 최적화 기법
// -> 먼저 리프까지 내려간 뒤, 원래 루트 값이 들어갈 위치를 한 번에 찾아 삽입
// -> 비교 횟수와 데이터 이동을 줄일 수 있으나 구현이 복잡하여 일반 Heapify가 더 널리 사용됨

import Algorithm.Sort.ArrGenerator;
import java.io.IOException;
import java.util.Arrays;

public class _1_6_Heap {

  static boolean isAllowPrint;

  public static void main(String[] args) throws IOException {
    ArrGenerator a = new ArrGenerator();

    int[] arr = a.init();

    isAllowPrint = a.isAllowPrint();
    System.out.println("==========================================================");
    System.out.println("Before Arr: " + Arrays.toString(arr));

    long start = System.nanoTime();

    heapSort(arr);

    System.out.println("==========================================================");
    System.out.println("After Arr: " + Arrays.toString(arr));
    System.out.println("==========================================================");
    System.out.println("Heap Sort | " + (System.nanoTime() - start) / 1_000_000.0 + "ms");
  }

  private static void heapSort(int[] arr) {
    int n = arr.length;

    // Build Max Heap & 마지막 부모 노드부터 Heaspify를 수행해 최대 힙 구성
    // 부모 인덱스 i 기준: 왼쪽 자식 (2i + 1), 오른쪽 자식 (2i + 2)
    if (isAllowPrint) {
      System.out.println("==========================================================");
      System.out.println("Build Heap");
    }

    for (int i = n / 2 - 1; i >= 0; i--) {
      if (isAllowPrint) {
        System.out.println("----------------------------------------------------------");
        System.out.printf("heapify(%d): %s\n", i, Arrays.toString(arr));
      }

      // heapify: O(log n), 초기 힙 구성 전체: O(n)
      heapify(arr, n, i);
    }

    // Heap의 루트(최대값)를 마지막 원소와 교환
    // Heap 크기를 1 감소해 최대값을 제외하고 Heapify 수행
    // 반복 & 오름차순 정렬 완료
    if (isAllowPrint) {
      System.out.println("==========================================================");
      System.out.println("Sort");
    }

    for (int i = n - 1; i > 0; i--) {
      if (isAllowPrint) {
        System.out.println("----------------------------------------------------------");
        System.out.println("Before swap : " + Arrays.toString(arr));
      }

      // 최댓값이 마지막 위치로 이동
      swap(arr, 0, i);
      if (isAllowPrint) {System.out.printf("swap root <-> %d : %s \n", i, Arrays.toString(arr));}

      // 요소가 하나 제거된 이후에 다시 최대 힙을 구성, 루트 기준으로 진행
      // 이 과정 반복으로 최대값이 순서대로 정렬
      heapify(arr, i, 0);
      if (isAllowPrint) {System.out.printf("heapify(%d) : %s \n", i, Arrays.toString(arr));}
    }
  }

  // 현재 노드를 기준으로 최대 힙 속성을 만족하도록 아래 방향으로 재구성
  // 시간복잡도 : O(log n)
  private static void heapify(int[] arr, int n, int i) {
    int p = i;
    int l = i * 2 + 1;
    int r = i * 2 + 2;

    // 왼쪽 자식 노드
    if (l < n && arr[p] < arr[l]) {p = l;}

    // 오른쪽 자식 노드
    if (r < n && arr[p] < arr[r]) {p = r;}

    // 부모노드 < 자식노드
    if (i != p) {
      swap(arr, p, i);
      heapify(arr, n, p);
    }
  }

  private static void swap(int[] arr, int a, int b) {
    int temp = arr[a];
    arr[a] = arr[b];
    arr[b] = temp;
  }
}
