package Algorithm.Sort.Code.NonComparsionSort;

// 기수(Radix) 정렬: 데이터를 구성하는 기본 요소(Radix)를 이용해 정렬을 진행하는 방식, 아래의 방법은 LSD
// 입력 데이터의 최대값에 따라 Counting Sort의 비효율성을 개선하기 위해 Radix Sort 사용 가능
// ex) 자릿수의 값 별로 정렬 하므로 나올 수 있는 값의 최대 사이즈는 9
// 장점: 안정 정렬, 시간 복잡도: O(d *(n+b)) or O(nd): d는 정렬할 숫자의 자릿수, b는 10(k와 같으나 10으로 고정), Counting 경우 최댓값 k에 영향, 문자열|정수 가능
// 단점: 자릿수가 없는 것은 정렬 불가 (부동 소숫점), 중간 결과를 저장할 bucket 공간이 필요함

// 낮은 자리수 부터 정렬하는 이유: MSD(Most Significant Digit) 큰 자리수 부터, LSD(Least Siginificant Digit) 작은 자리수 부터 의 차이, 둘 다 가능
// LSD의 경우 1600000과 1을 비교할 때 Digit의 갯수만큼 따져야하는 단점이 있음, 그에 반해 MSD는 마지막 자리수 까지 확인할 필요 없음
// LSD는 중간 정렬 결과를 알 수 없음 (ex 10004와 70002의 비교), MSD는 중간에 중요한 숫자를 알 수 있음, 따라서 시간 줄이기 가능하나
// -> 정렬이 됐는지 확인하는 과정이 필요, 이 때문에 메모리를 더 사용
// LSD는 알고리즘이 일관됨(Branch Free Algorithm), MSD는 일관되지 못함 -> 때문에 Radix는 주로 LSD 언급
// -> MSD는 상위 자릿수부터 내려가기 때문에 하위 자릿수에서의 처리 방식이 상위 자릿수에서의 처리 방식에 영향을 받을 수 있음
// LSD는 자릿수가 정해진 경우 좀 더 빠를 수 있음

import Algorithm.Sort.ArrGenerator;
import java.util.Arrays;

public class _2_2_Radix {

  public static void main(String[] args) {
    ArrGenerator a = new ArrGenerator();

    int[] arr = a.init();
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

    for (int i = 1; i < arr.length; i++) {
      if (arr[i - 1] > arr[i]) {System.out.print("정렬 안된 숫자" + arr[i - 1] + ", " + arr[i]);}
    }
    for (int i = 1; i < arr2.length; i++) {
      if (arr2[i - 1] > arr2[i]) {System.out.print("정렬 안된 숫자" + arr2[i - 1] + ", " + arr2[i]);}
    }
  }

  // LSD(가장 낮은 수 부터 정렬)
  private static void lsdRadixSort(int[] arr, int n) {
    if (arr.length == 0) {return;}
    int max = arr[0], min = arr[0];

    for (int num : arr) {
      max = Math.max(max, num);
      min = Math.min(min, num);
    }

    int offset = negativeValueCorrection(arr, min, false, 0);

    // 최댓값을 나눴을 때 0이 나오면 모든 숫자가 i의 아래
    for (long exponent = 1; (max / exponent) > 0; exponent *= 10) {countSort(arr, 0, n, exponent);}
    negativeValueCorrection(arr, min, true, offset);
  }

  static void msdRadixSort(int[] arr, int n) {
    if (arr.length == 0) {return;}

    // 음수 보정 offset
    int min = arr[0];
    for (int num : arr) {min = Math.min(min, num);}
    int offset = negativeValueCorrection(arr, min, false, 0);

    runMSDRadixSort(arr, 0, n, getMaxDigits(arr));

    negativeValueCorrection(arr, min, true, offset);
  }

  // MSD
  // 큰 자릿수 부터 Counting Sort 하여 누적합 counting이 되는 범위를 새 그룹의 범위로 설정 & 재귀호출
  static void runMSDRadixSort(int[] arr, int start, int end, int maxDigits) {
    int range = end - start;
    if (range <= 1 || maxDigits < 0) {return;}

    // 자릿수 계산
    long exponent = (long) Math.pow(10, maxDigits);

    // 재귀호출 실행 전 사용 할 누적합 백업
    int[] boundaryArr = countSort(arr, start, end, exponent);

    // 각 버킷마다 재귀호출 실행
    int nextStart = start;
    for (int i = 0; i < 10; i++) {
      int nextEnd = start + boundaryArr[i];

      if (nextEnd > nextStart) {runMSDRadixSort(arr, nextStart, nextEnd, maxDigits - 1);}
      nextStart = nextEnd;
    }
  }

  // 특정 자릿수를 기준으로 counting sort 수행
  private static int[] countSort(int[] arr, int start, int end, long exponent) {
    int range = end - start;
    int[] countArr = new int[10];
    int[] output = new int[range];

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


  private static int getMaxDigits(int[] arr) {
    // 자릿수 계산
    int max = 0;
    for (int v : arr) {max = Math.max(max, v);}

    int digits = 0;
    while (max > 0) {
      max /= 10;
      digits++;
    }

    return digits - 1;
  }

  static int negativeValueCorrection(int[] arr, int min, boolean isRollback, int offset) {
    if (isRollback) {
      // 음수 보정 롤백
      if (min < 0) {for (int i = 0; i < arr.length; i++) {arr[i] -= offset;}}
    } else {
      // 음수 보정 (전체를 양수로)
      if (min < 0) {
        offset = -min;
        for (int i = 0; i < arr.length; i++) {arr[i] += offset;}
      }
    }
    return offset;
  }
}
