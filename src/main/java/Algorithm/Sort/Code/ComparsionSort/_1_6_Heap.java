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

// Bottom-Up (Floyd 방식)
//  이미 존재하는 배열을 heapify-down으로 정리, 전체 구조를 한 번에 구성, Build Heap 시간복잡도 O(n)

// Top-Down (Insertion 방식)
// 하나씩 삽입하며 sift-up으로 heap 유지, 실시간 데이터 삽입 구조, Build Heap 시간복잡도 O(n log n)

// 차이 핵심
// Top-Down: 삽입 기반 (Priority Queue, streaming system)
// Bottom-Up: 일괄 구성 기반 (heap sort, batch processing)

// 실제 사용:
// Bottom-Up: 배열 -> Heap 변환, Heap Sort 구현
// Top-Down: Priority Queue, 실시간 top-K, 스케줄링

import Algorithm.Sort.ArrGenerator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class _1_6_Heap {

  static boolean isAllowPrint;

  public static void main(String[] args) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    ArrGenerator a = new ArrGenerator();

    int[] arr = a.init();
    int[] arr2 = arr.clone();

    isAllowPrint = a.isAllowPrint();
    System.out.println("==========================================================");
    System.out.println("Before Arr: " + Arrays.toString(arr));
    System.out.println("Before Arr2: " + Arrays.toString(arr2));

    long start = System.nanoTime();
    heapSort(arr, 1);
    String firstEnd = (System.nanoTime() - start) / 1_000_000.0 + "ms";

    start = System.nanoTime();
    heapSort(arr2, 2);
    String secondEnd = (System.nanoTime() - start) / 1_000_000.0 + "ms";

    System.out.println("==========================================================");
    System.out.println("After Arr: " + Arrays.toString(arr));
    System.out.println("After Arr2: " + Arrays.toString(arr2));
    System.out.println("Heap Sort\nBottom-Up: " + firstEnd + ", Top-Down: " + secondEnd);
    if (!a.isSorted(arr)) {System.out.println("arr 정렬 안돼있음");}
    if (!a.isSorted(arr2)) {System.out.println("arr2 정렬 안돼있음");}
  }

  private static void heapSort(int[] arr, int selectNum) {
    int n = arr.length;

    // Build Max Heap & 마지막 부모 노드부터 Heaspify를 수행해 최대 힙 구성
    // 부모 인덱스 i 기준: 왼쪽 자식 (2i + 1), 오른쪽 자식 (2i + 2)
    if (isAllowPrint) {
      System.out.println("==========================================================");
      System.out.println("Build Heap");
    }

    switch (selectNum) {
      case 1:
        for (int i = n / 2 - 1; i >= 0; i--) {
          if (isAllowPrint) {
            System.out.println("----------------------------------------------------------");
            System.out.printf("heapify(%d): %s\n", i, Arrays.toString(arr));
          }

          // heapify: O(log n), 초기 힙 구성 전체: O(n), bottomUp
          heapify(arr, n, i);
        }
        break;
      case 2:
        for (int i = 1; i < n; i++) {
          if (isAllowPrint) {
            System.out.println("----------------------------------------------------------");
            System.out.printf("siftUp(%d): %s\n", i, Arrays.toString(arr));
          }
          siftUp(arr, i);
        }
        break;
      default:
        throw new IllegalArgumentException("잘못 된 정렬 선택");
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

  // 현재 노드를 기준으로 최대 힙 속성을 만족하도록 아래 방향으로 재구성, bottom-up
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

  // top-down
  private static void siftUp(int[] arr, int i) {
    int child = i;

    while (child > 0) {
      int parent = (child - 1) / 2;

      if (arr[parent] >= arr[child]) {break;}

      swap(arr, parent, child);
      child = parent;
    }
  }

  private static void swap(int[] arr, int a, int b) {
    int temp = arr[a];
    arr[a] = arr[b];
    arr[b] = temp;
  }
}
