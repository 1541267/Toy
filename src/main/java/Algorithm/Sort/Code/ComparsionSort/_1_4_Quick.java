package Algorithm.Sort.Code.ComparsionSort;

// 퀵(피벗)정렬: 분할 정복(Divide And Conquer) : 문제를 작은 2개의 문제로 분리 하고 각각 해결
// 불안정 정렬, 다른 원소와의 비교만으로 정렬을 수행, Merge와 달리 배열을 비균등하게 분할
// 하나의 값을 피벗(기준)으로 잡고 피벗보다 왼쪽은 작은 배열, 오른쪽은 큰 배열로 분열(Divide), 재귀적으로 반복
//  -> 이 과정이 partition 과정 (피벗 기준 재배치 & 분할)
// 재귀 호출이 진행될 때마다 하나의 원소는 위치가 정해짐

// 피봇 값을 기준으로 오른쪽에선 작은 값 찾고 킵, 왼쪽에선 큰 값을 찾고 두 값을 스왑
// 이 과정을 반복, 위의 인덱스가 교차하는 (해당 피봇에서 더 바꿀 값이 없으면) 해당 인덱스와 피봇을 스왑, 위 과정 반복

// 개선: 피벗 값이 Min | Max으로 지정되어 파티션이 나누어지지 않았을때 O(n^2) == 정렬하고자 하는 배열이 오름차 | 내림차 정렬 되어 있을 떄 O(n^2)
// 이 때 배열에서 가장 앞에 있는 값과 중간값을 교환해 준다면 확률적으로 시간 복잡도 O(n log2 n)으로 개선 == 최악의 시간 복잡도가 O(n log2 n)이 되는건 아님

// 해결?: Random 피벗이나 중간값 피벗으로 해결 가능 (완전히 줄일 순 없음 O(n log n)에 가깝게), 왼쪽|오른쪽 피벗보다 항상 Random | 중간값이 더 성능이 좋음
// -> Random 보단 중간값으로 해결, 중간값은 이미 or 거의 정렬 or 역정렬 된 상황에서 매우 강함

// 아래 코드 방식은 Hoare Parition (양방향 포인터)이고 Lomuto Parition 방식이 추가로 있음
// Lomuto 방식은 pivot 이 오른쪽에 고정, 포인터인 i 는 partition 도중 pivot 보다 작은 값이 끝나는 인덱스 위치
// 두 번째 포인터인 j 는 검사중인 인덱스 위치
// i = 0 j = 0 부터 검사, j++ 와 pivot 을 비교 후 pivot 보다 작으면 i++ & swap(i, j), 아니면 j++ 로 계속 비교
// j 가 pivot 앞까지 도달하면 swap(i, pivot) 하며 pivot은 위치 확정, 이후 재귀 호출로 분할 정복

// 시간 복잡도: Best = O(n log n), Avg = O(n log n), Worst = O(n^2)
// 공간 복잡도: 주어진 배열 안에서 교환 == O(1), 재귀호출 비용으로 Avg = O(log n), Worst = O(n)
// 장점: 불필요한 데이터의 이동을 줄이고 먼 거리의 데이터를 교환, 한 번 결정되니 피벗들이 추 후 연산에서 제외, 시간 복잡도가 O(n log n)의 다른 알고리즘 보다 가장 빠릅
// 단점: 불안정 정렬, 정렬된 배열에 대해선 Quick Sort의 불균형 분할에 의해 오히려 더 많이 걸림

// 평균적으로 가장 빠른 정렬 & CPU Cache 를 잘 활용해
// Primitive 배열 (메모리상에 데이터를 연속으로 저장하는 타입들, char, int ...),
// 메모리 여유, 불안정 정렬도 괜찮은 경우엔 Quick 을 가장 먼저 고려
// -> 주기억장치(RAM | ROM)에서 데이터를 가져올 때 필요한 데이터뿐만 아닌 주변 메모리(캐시 라인)까지 함께 Cache 에 저장
// -> Quick Sort는 배열이 연속된 인덱스에 주로 접근해 Cpu Cache 히트율이 높다
// -> Heap Sort는 배열을 이진 트리처럼 사용해 부모(0) → 자식(1, 2) → 손자(3, 4, 5, 6) 처럼
// -> 멀리 떨어진 인덱스를 반복 접근하기 떄문에 CPU Cache 히트율이 낮음

import Algorithm.Sort.ArrGenerator;
import java.io.IOException;
import java.util.Arrays;

// 퀵(피벗) 정렬 개선
public class _1_4_Quick {

  static boolean isAllowPrint;

  public static void main(String[] args) throws IOException {

    ArrGenerator a = new ArrGenerator();
    int[] arr = a.init();

    isAllowPrint = a.isAllowPrint();

    // int[] arr = {7, 4, 1, 2, 5, 3, 6};

    int left = 0;
    int right = arr.length - 1;
    System.out.println("Before = " + Arrays.toString(arr));
    System.out.println("==========================================================");
    long start = System.nanoTime();
    quickSort(arr, left, right);
    System.out.println("----------------------------------------------------------");
    System.out.println("result = " + Arrays.toString(arr));
    System.out.println("==========================================================");
    System.out.println("Quick Sort | " + (System.nanoTime() - start) / 1_000_000.0 + "ms");
  }

  // Random 피벗, 최악을 확률적으로 피하고 Random.nextInt() 호출 비용이 있음
  // 실행마다 성능 예측이 달라짐
  //  private static int randomPivot(int left, int right) {
  //    Random rand = new Random();
  //    return left + rand.nextInt(right - left + 1);
  //  }

  private static void quickSort(int[] arr, int left, int right) {
    if (left >= right) {return;}

    // 개선된 피벗 선택
    int pivotIndex = medianOfThree(arr, left, right);

    if (isAllowPrint) {
      System.out.println("Arr after medianOfThree = " + Arrays.toString(arr));
    }

    swap(arr, left, pivotIndex); // 선택된 피벗을 첫 번째 위치로 이동

    int pivot = partition(arr, left, right);

    quickSort(arr, left, pivot - 1);
    quickSort(arr, pivot + 1, right);
  }

  private static int medianOfThree(int[] arr, int left, int right) {
    int mid = left + (right - left) / 2;

    //    // 중간값 찾기
    //    int a = arr[left], b = arr[mid], c = arr[right];
    //    if ((a > b && a < c) || (a < b && a > c)) {
    //      return left;
    //    } else if ((b > a && b < c) || (b < a && b > c)) {
    //      return mid;
    //    } else {
    //      return right;
    //    }

    // 기존의 중앙값 찾기는 엣지 케이스가 발생 ex) 5, 5, 3
    // left, mid, right 가 최소 & 중앙 & 최대 값으로 먼저 정렬
    if (arr[left] > arr[mid]) {swap(arr, left, mid);}
    if (arr[left] > arr[right]) {swap(arr, left, right);}
    if (arr[mid] > arr[right]) {swap(arr, mid, right);}

    // 이 시점에서 arr[mid]가 중간값
    return mid;
  }

  private static void swap(int[] arr, int i, int j) {
    int temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
  }

  private static int partition(int[] arr, int left, int right) {
    int pivot = arr[left];
    int i = left, j = right;

    if (isAllowPrint) {System.out.println("pivot = " + pivot);}

    while (i < j) {
      // 오른쪽에서 pivot보다 작은 값 찾기
      while (i < j && arr[j] > pivot) {j--;}

      // 왼쪽에서 pivot보다 크거나 같은 값 찾기
      while (i < j && arr[i] <= pivot) {i++;}

      if (i < j) {
        if (isAllowPrint) {
          System.out.println("Before Swap = " + Arrays.toString(arr) + ", i = " + i + ", j = " + j);
        }

        swap(arr, i, j);

        if (isAllowPrint) {
          System.out.println("After Swap =  " + Arrays.toString(arr));
          System.out.println("----------------------------------------------------------");
        }
      }
    }

    if (isAllowPrint) {
      System.out.println("부분 정렬 Before = " + Arrays.toString(arr) + "\ni = " + i + ", j = " + j);
    }

    swap(arr, left, i); // 피벗을 최종 위치로 이동

    if (isAllowPrint) {
      System.out.println("부분 정렬 After = " + Arrays.toString(arr) + "\ni = " + i + ", j = " + j);
      System.out.println("==========================================================");
    }
    return i;
  }
}

// 피벗이 왼쪽에 고정 -> 이미 정렬된 경우 비효율, O(n^2)
// public class _1_4_Quick {
// 	public static void main(String[] args) {
//
// 		ArrGenerator a = new ArrGenerator();
//
// 		int[] arr = a.generateArr();
//
// 		int left = 0;
// 		int right = arr.length - 1;
// 		System.out.println("Before = " + Arrays.toString(arr));
// 		System.out.println("==========================================================");
// 		quickSort(arr, left, right);
// 		System.out.println("After = " + Arrays.toString(arr));
//
// 	}
//
// 	private static void quickSort(int[] arr, int left, int right) {
// 		if (left >= right)
// 			return;
//
// 		int pivot = parition(arr, left, right);
//
// 		quickSort(arr, left, pivot - 1);
// 		quickSort(arr, pivot + 1, right);
// 	}
//
// 	private static int parition(int[] arr, int left, int right) {
// 		// 최악 경우 개선 방법
// 		// int mid = (left + right) / 2;
// 		// swap(array, left,mid);
// 		//
//
// 		int pivot = arr[left];
// 		int i = left, j = right;
// 		System.out.println("pivot = " + pivot);
//
// 		while (i < j) {
// 			while (pivot < arr[j]) {
// 				j--;
// 			}
// 			while (i < j && pivot >= arr[i]) {
// 				i++;
// 			}
// 			if (i < j) {
// 				System.out.println("Before Swap = " + Arrays.toString(arr) + ", i = " + i + ", j = " + j);
// 				swap(arr, i, j);
// 				System.out.println("After Swap =  " + Arrays.toString(arr));
// 				System.out.println("==========================================================");
// 			}
// 		}
// 		System.out.println("부분 정렬 Before = " + Arrays.toString(arr) + ", i = " + i + ", j = " + j);
// 		arr[left] = arr[i];
// 		arr[i] = pivot;
// 		System.out.println("부분 정렬 After = " + Arrays.toString(arr) + ", i = " + i + ", j = " + j);
// 		System.out.println("==========================================================");
// 		return i;
// 	}
//
// 	private static void swap(int[] arr, int i, int j) {
// 		int temp = arr[i];
// 		arr[i] = arr[j];
// 		arr[j] = temp;
// 	}
// }
