package Study.Algorithm.Sort.Code.NonComparsionSort;

// 기수(Radix) 정렬: 데이터를 구성하는 기본 요소(Radix)를 이용해 정렬을 진행하는 방식, 분할 정복은 아님, 아래의 방법은 LSD
// 입력 데이터의 최대값에 따라 Counting Sort의 비효율성을 개선하기 위해 Radix Sort 사용 가능
// ex) 자릿수의 값 별로 정렬 하므로 나올 수 있는 값의 최대 사이즈는 9
// 공간복잡도: output 배열(n) & countArr(b) LSD = O(n + b), MDS는 추가로 재귀 호출 스택(스택 깊이는 최대 자릿수 d) 필요로 O(n + b + d)
// 장점: 안정 정렬
// 단점: 자릿수가 없는 것은 정렬 불가 (부동 소숫점), 중간 결과를 저장할 bucket 공간이 필요함

// - LSD
// -> 안정 정렬, 구현 단순, 실제로 가장 많이 쓰임
// -> 시간복잡도: d = 자릿수(10진수 고정), 각 반복 마다 Counting Sort = O(n + b) => O(d * (n + b))
// -> 공간복잡도: output 배열 = O(n), counting 배열 O(b) => O(n + b) => O(n + b)
// LSD 장점: 구현이 단순, 분기 없는 일관된 흐름(Branch-free), 항상 전체 배열을 순차 접근하므로 캐시 지역성이 좋음,
//           데이터 분포와 무관하게 성능이 일정 (예측 가능) -> 실시간/하드웨어성 구현에 유리
// LSD 단점: 자릿수(d)만큼 무조건 전부 순회, 데이터가 이미 상위 자릿수 기준으로 갈려도 이를 활용 못 함,
//           고정 길이 키에 적합하고 가변 길이 키(ex. 길이가 다른 문자열)에는 패딩 등 추가 처리가 필요

// - MSD
// -> 조기종료 가능, 작은 bucket은 삽입 정렬로 수정 가능
// -> prefix 기반 분할(QuickSort 느낌), 재귀 구조, 부분 정렬 -> 불필요한 자릿수 탐색을 줄일 수 있음
// -> 시간복잡도
//  - avg = 각 단계에서 n개를 나누고 각 bucket을 재귀적으로 처리(b = 재귀호출 비용), O(n * b)
//  - worst = 재귀가 깊어지면 결국 모든 digit를 다 탐색, O(n * b)
// -> 공간복잡도
//  - bucket 배열 & 재귀 stack, O(n + b + d)
//  - 보통 d는 매우 작거나 log 범위라 실질적으로 O(n + b)
// MSD 장점: 상위 자릿수만으로 그룹이 좁혀지면 조기 종료 가능 -> 랜덤 데이터에서 평균적으로 더 빠를 수 있음,
//           가변 길이 키(문자열, prefix 비교)에 자연스럽게 적용 가능, 정렬 도중에도 상위 자릿수 기준 순서를 알 수 있어
//           Top-K, 사전식 prefix 검색 등 "완전 정렬까지 필요 없는" 상황에 유리
// MSD 단점: 재귀 호출과 그룹별 분기가 있어 알고리즘 흐름이 일관되지 않음, 데이터 분포에 따라 성능 편차가 큼(최악의 경우 LSD와 비슷하거나 더 느림),
//           재귀 스택 공간이 추가로 필요, 구현이 상대적으로 복잡 (경계 배열 관리 필요)
//
// 낮은 자리수 부터 정렬하는 이유: MSD(Most Significant Digit) 큰 자리수 부터, LSD(Least Siginificant Digit) 작은 자리수 부터 의 차이, 둘 다 가능
// LSD의 경우 1600000과 1을 비교할 때 Digit의 갯수만큼 따져야하는 단점이 있음, 그에 반해 MSD는 마지막 자리수 까지 확인할 필요 없음
// LSD는 중간 정렬 결과를 알 수 없음 (ex 10004와 70002의 비교), MSD는 중간에 중요한 숫자를 알 수 있음, 따라서 시간 줄이기 가능하나
// -> 정렬이 됐는지 확인하는 과정이 필요, 이 때문에 메모리를 더 사용
// LSD는 알고리즘이 일관됨(Branch Free Algorithm), MSD는 일관되지 못함 -> 때문에 Radix는 주로 LSD 언급
// -> MSD는 상위 자릿수부터 내려가기 때문에 하위 자릿수에서의 처리 방식이 상위 자릿수에서의 처리 방식에 영향을 받을 수 있음
// LSD는 자릿수가 정해진 경우 좀 더 빠를 수 있음
//
// - LSD: 정수처럼 자릿수(길이)가 고정/균일하고, 성능 예측 가능성이 중요할 때, 구현 단순성이 중요할 때
// - MSD: 문자열처럼 키 길이가 가변적일 때, 상위 자릿수만으로 대부분 결정되는 데이터(랜덤 정수/텍스트)일 때,
//        전체 정렬을 끝까지 하지 않고 상위 기준 순서/일부 결과만 필요할 때

import Study.Algorithm.Sort.ArrGenerator;
import java.util.Arrays;

public class _2_2_Radix {

  public static void main(String[] args) {
    ArrGenerator a = new ArrGenerator();

    int[] arr = a.initInteger();
    int[] arr2 = arr.clone();

    int n = arr.length;

    long start = System.nanoTime();
    System.out.println("Before Arr: " + Arrays.toString(arr));
    lsdRadixSort(arr, n);
    String LSDEnd = (System.nanoTime() - start) / 1_000_000.0 + "ms";

    start = System.nanoTime();
    msdRadixSort(arr2, n);
    String MSDEnd = (System.nanoTime() - start) / 1_000_000.0 + "ms";

    System.out.println("==========================================================");
    System.out.println("After Arr:  " + Arrays.toString(arr));
    System.out.println("After Arr2: " + Arrays.toString(arr2));
    System.out.println("Radix Sort\nLSD: " + LSDEnd + "\nMSD: " + MSDEnd);

    if (!a.isIntegerArrSorted(arr)) {System.out.println("LSD 정렬 안돼있음");}
    if (!a.isIntegerArrSorted(arr2)) {System.out.println("MSD 정렬 안돼있음");}
  }

  // LSD(가장 낮은 수 부터 정렬)
  private static void lsdRadixSort(int[] arr, int n) {
    if (arr.length == 0) {return;}
    int max = arr[0], offset = arr[0];

    for (int num : arr) {
      max = Math.max(max, num);
      offset = Math.min(offset, num);
    }

    max -= offset;

    negativeValueCorrection(arr, false, -offset);

    int[] output = new int[n];
    int[] countArr = new int[10];

    // 최댓값을 나눴을 때 0이 나오면 모든 숫자가 i의 아래
    for (long exponent = 1; (max / exponent) > 0; exponent *= 10) {countSort(arr, 0, n, exponent, countArr, output);}
    negativeValueCorrection(arr, true, -offset);
  }

  static void msdRadixSort(int[] arr, int n) {
    if (arr.length == 0) {return;}

    // 음수 보정 offset
    int max = arr[0], offset = arr[0];
    for (int num : arr) {
      max = Math.max(max, num);
      offset = Math.min(offset, num);
    }

    negativeValueCorrection(arr, false, -offset);

    // 음수 보정으로 인해 max 값 갱신
    max -= offset;

    int[] output = new int[n];
    int[] countArr = new int[10];

    runMSDRadixSort(arr, 0, n, getMaxDigits(max), countArr, output);

    negativeValueCorrection(arr, true, -offset);
  }

  // MSD
  // 큰 자릿수 부터 Counting Sort 하여 누적합 counting이 되는 범위를 새 그룹의 범위로 설정 & 재귀호출
  static void runMSDRadixSort(int[] arr, int start, int end, int maxDigits, int[] countArr, int[] output) {
    int range = end - start;
    if (range <= 1 || maxDigits < 0) {return;}

    // 자릿수 계산
    long exponent = (long) Math.pow(10, maxDigits);

    // 재귀호출 실행 전 사용 할 누적합 백업
    int[] boundaryArr = countSort(arr, start, end, exponent, countArr, output);

    // 각 버킷마다 재귀호출 실행
    int nextStart = start;
    for (int i = 0; i < 10; i++) {
      int nextEnd = start + boundaryArr[i];

      if (nextEnd > nextStart) {runMSDRadixSort(arr, nextStart, nextEnd, maxDigits - 1, countArr, output);}
      nextStart = nextEnd;
    }
  }

  // 특정 자릿수를 기준으로 counting sort 수행
  // output, countArr 은 호출 전 1회 선언 한 배열을 계속 재사용
  private static int[] countSort(int[] arr, int start, int end, long exponent, int[] countArr, int[] output) {
    int range = end - start;

    // 이전 호출에서 남은 값 초기화
    Arrays.fill(countArr, 0);

    // 빈도 계산, exp = 자릿수
    for (int i = start; i < end; i++) {countArr[(int) (arr[i] / exponent) % 10]++;}

    // 누적 합
    for (int i = 1; i < 10; i++) {countArr[i] += countArr[i - 1];}

    // MSD의 재귀호출 에서 사용할 누적합 백업
    int[] boundaryArr = countArr.clone();

    // 안정 정렬
    for (int i = end - 1; i >= start; i--) {
      int digits = (int) (arr[i] / exponent) % 10;
      output[--countArr[digits]] = arr[i];
    }

    // 원래 배열에 정렬된 결과 복사
    System.arraycopy(output, 0, arr, start, range);
    // for (int i = 0; i < n; i++) {arr[i] = output[i];}

    return boundaryArr;
  }


  private static int getMaxDigits(int max) {
    // 자릿수 계산
    int digits = 0;
    while (max > 0) {
      max /= 10;
      digits++;
    }

    return digits - 1;
  }

  static void negativeValueCorrection(int[] arr, boolean isRollback, int offset) {
    if (isRollback) {
      // 음수 보정 롤백
      for (int i = 0; i < arr.length; i++) {arr[i] -= offset;}
    } else {
      for (int i = 0; i < arr.length; i++) {arr[i] += offset;}
    }
  }
}
