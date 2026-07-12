package Study.Algorithm.Sort.Code.NonComparsionSort;

// 버킷 정렬: 데이터를 여러 개의 버킷으로 분산 후 각 버킷 내부에서 정렬을 수행, 버킷을 순서대로 합침
// 시간 복잡도 best & avg = O(n + k), k = 버킷 개수
// -> worst = 모든 데이터가 하나의 bucket에 몰리는 경우, 내부 정렬이 O(n²)인 경우 일반 정렬 알고리즘 보다 느려질 수 있음
//
// - 버킷
//  -> List<List<<>>: index 기반 접근으로 O(1), cache locality 가 좋아 성능 우수
//  -> Map<Integer, List<<>>: 동적 버킷이 생성 가능하지만 HashMap은 순서 보장 X & 해시 오버헤드 존재
//  -> & TreeMap은 key 순서가 보장되지만 O(log n) 오버헤드 추가
// 분산 단계: n개의 균등한 크기의 버킷으로 나눔, 정렬 단계: 각 버킷 내부를 다른 정렬 알고리즘 으로 정렬, 수집 단계: 모든 버킷을 순서대로 합쳐 배열 생성
//
// 핵심: bucket sort 핵심은 값을 index로 변환 (mapping)
// doubleVer -> bucketIdx = (num - min) / (max - min) * (n - 1) 
// -> (num - min) / (max - min) : 전체 범위에서 몇 % 위치인지, (n - 1) : bucket 개수만큼 늘림(0 ~ 1 좌표를 bucket 칸 번호로 변환)
// integerVer -> bucketIdx = (int) ((long) (num - min) * (n - 1) / max - min + 1)
// -> (num - min) : 기준을 0부터 시작으로 맞추기
// -> ~ / range(max - min + 1) 전체 길이 나누기(정규화: 해당 값의 백분율 위치)
// -> double 과 동일하게 bucket index로 변환 & 어떤 값이던 0 ~ n - 1 범위로 압축
// 두 과정 동일하게 n - 1 곱하는 과정은 bucket index 를 0 ~ n - 1 로 만드는 과정 ex) 0.0 ~ 1.0 -> 0 ~ n-1
// 범위정규화: 서로 다른 범위의 데이터를 동일한 bucket 구조에 넣기 위한 과정, 값의 상대적 위치를 기준으로 bucket에 배치
// -> 나머지 과정은 동일

// 성능이 O(n)에 가까워지는 조건
// -> 데이터가 균등 분포(uniform distribution)일 때
// -> 각 bucket에 들어가는 원소 수가 O(1)에 가까울 때
// -> bucket 내부 정렬 비용이 작을 때 (insertion sort 적합)
//
// bucket 내부 데이터는 매우 적다는 가정 하에 insertion sort 를 사용
// cache 친화적 & 구현 단순하며 적은 데이터의 경우 quick/merge sort 보다 빠름
//
// 실수 정렬에선 부동소수점 오차로 인해 bucketIdx == n 발생이 가능, 조정이 필요
// bucketIdx = Math.min(bucketIdx, n - 1)
//
// 주로 실수와 같이 연속적인 값이 일정 범위 내에 균등하게 분포되어 있는 데이터에 가장 잘 작동 특히 부동 소숫점 숫자, 정수에서도 사용은 가능
// 입력 데이터의 분포에 따라 성능이 크게 달라짐, 추가 메모리 필요
// 실수: ( 특히 0~1 범위의 값): O(n) 시간 복잡도에 가까운 성능 가능, 각 버킷에 들어가는 원소들이 비교적 적어지기 때문에 내부적으로 빠른 정렬 가능
// 정수: 정수의 범위가 너무 크면 버킷의 크기를 설정하는 데 문제 발생 가능 -> 입력이 너무 크면 메모리 낭비 -> ex 1~1_000_000 의 범위

import Study.Algorithm.Sort.ArrGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class _2_3_Bucket {

  public static void main(String[] args) {

    ArrGenerator a = new ArrGenerator();

    int[] integerArr = a.initInteger();
    double[] doubleArr = a.initDouble();

    System.out.println("Before Integer Arr: " + Arrays.toString(integerArr));
    long start = System.nanoTime();
    bucketSortIntegerVer(integerArr);
    String integerVerEnd = (System.nanoTime() - start) / 1_000_000.0 + "ms";

    System.out.println("Before Double Arr: " + Arrays.toString(doubleArr));
    start = System.nanoTime();
    bucketSortDoubleVer(doubleArr);
    String doubleVerEnd = (System.nanoTime() - start) / 1_000_000.0 + "ms";

    System.out.println("==========================================================");
    System.out.println("after doubleArr = " + Arrays.toString(doubleArr));
    System.out.println("after integerArr = " + Arrays.toString(integerArr));
    System.out.println("==========================================================");
    System.out.println("Bucket Sort\nInteger Ver: " + integerVerEnd + ", Double Ver: " + doubleVerEnd);
    if (!a.isIntegerArrSorted(integerArr)) {System.out.println("정렬 안돼있음");}
    if (!a.isDoubleArrSorted(doubleArr)) {System.out.println("정렬 안돼있음");}
  }

  private static void bucketSortIntegerVer(int[] arr) {
    int n = arr.length;
    if (n <= 1) {return;}

    int min = arr[0], max = arr[0];

    for (int val : arr) {
      if (val < min) {min = val;}
      if (val > max) {max = val;}
    }

    if (max == min) {return;}

    int range = max - min + 1;

    // List<List<>>가 기본이지만 데이터 분포가 좁으면 비어있는 bucket 이 많아 Map으로 해결 가능 하지만
    // 이점이 없음 (overhead, 캐시 효율 낮음)
    List<List<Integer>> buckets = new ArrayList<>();
    // Map<Integer, List<Integer>> buckets = new TreeMap<>();

    // List<List<>> 사용 시 버킷 준비
    for (int i = 0; i < n; i++) {buckets.add(new ArrayList<>());}

    // 분산 단계
    for (int num : arr) {
      int bucketIdx = (int) ((long) (num - min) * (n - 1) / range);
      // buckets.putIfAbsent(bucketIdx, new ArrayList<>());
      buckets.get(bucketIdx).add(num);
    }

    // 정렬 단계, 버킷 내부는 삽입 정렬이 기본적으로 많이 쓰임, 소규모라 O(n)에 가까움
    for (List<Integer> bucket : buckets) {insertionSortInteger(bucket);}
    // for (Entry<Integer, List<Integer>> entry : buckets.entrySet()) {
    //   insertionSortInteger(entry.getValue());
    //   insertionSortInteger(entry.getValue());
    // }

    // 수집 단계
    int idx = 0;
    for (List<Integer> bucket : buckets) {
      for (Integer num : bucket) {arr[idx++] = num;}
    }
    // for (Entry<Integer, List<Integer>> entry : buckets.entrySet()) {
    //   for (Integer num : entry.getValue()) {
    //     arr[idx++] = num;
    //   }
    // }
  }

  static void bucketSortDoubleVer(double[] arr) {
    int n = arr.length;
    if (n <= 1) {return;}

    // 0 ~ 1 범위 위한 정규화
    double min = arr[0], max = arr[0];

    for (int i = 1; i < n; i++) {
      max = Math.max(max, arr[i]);
      min = Math.min(min, arr[i]);
    }

    if (max == min) {return;}

    List<List<Double>> buckets = new ArrayList<>();
    // Map<Integer, List<Double>> buckets = new TreeMap<>();

    for (int i = 0; i < n; i++) {buckets.add(new ArrayList<>());}

    // 분산 과정, 값 자체가 0 ~ 1 이므로 n 으로 곱
    for (double num : arr) {
      // 범위 정규화로 이뤄진 버킷 인덱스로 분산
      int bucketIdx = (int) ((num - min) / (max - min) * (n - 1));
      // 부동소수점 오차 보장을 위해 ex) bucketIdx = n 가능성
      bucketIdx = Math.min(bucketIdx, n - 1);
      buckets.get(bucketIdx).add(num);
    }

    // for (double num : arr) {
    // int bucketIdx = (int) ((num - min) / (max - min) * (n - 1));
    // buckets.putIfAbsent(bucketIdx, new ArrayList<>());
    // buckets.get(bucketIdx).add(num);
    // }

    for (List<Double> bucket : buckets) {insertionSortDouble(bucket);}
    // for (Entry<Integer, List<Double>> entry : buckets.entrySet()) {
    //   insertionSortDouble(entry.getValue());
    // }

    int idx = 0;
    for (List<Double> bucket : buckets) {
      for (Double num : bucket) {arr[idx++] = num;}
    }
    // for (Entry<Integer, List<Double>> entry : buckets.entrySet()) {
    //   for (Double num : entry.getValue()) {
    //     arr[idx++] = num;
    //   }
    // }
  }

  static void insertionSortDouble(List<Double> list) {
    int n = list.size();

    for (int i = 1; i < n; i++) {
      double temp = list.get(i);
      int prev = i - 1;

      while (prev >= 0 && list.get(prev) > temp) {
        list.set(prev + 1, list.get(prev));
        prev--;
      }

      list.set(prev + 1, temp);
    }
  }

  static void insertionSortInteger(List<Integer> list) {
    int n = list.size();

    for (int i = 1; i < n; i++) {
      int temp = list.get(i);
      int prev = i - 1;

      while (prev >= 0 && list.get(prev) > temp) {
        list.set(prev + 1, list.get(prev));
        prev--;
      }

      list.set(prev + 1, temp);
    }
  }
}

/*
  private static void bucketSort(int[] arr) {
    int n = arr.length;
    List<Integer>[] buckets = new ArrayList[n];

    for (int i = 0; i < n; i++) {buckets[i] = new ArrayList<>();}

    // 버킷에 원소 배치, 부동 소숫점
    // for (int i = 0; i < n; i++) {
    // 	int bucketIndex = (n * arr[i]);
    // 	buckets[bucketIndex].add(arr[i]);
    // }

    // 정수 배치
    int min = arr[0], max = arr[0];
    for (int i = 1; i < n; i++) {
      if (arr[i] < min) {min = arr[i];}
      if (arr[i] > max) {max = arr[i];}
    }

    // 각 원소를 적절한 버킷에 배치 (정수)
    int range = max - min + 1;
    for (int i = 0; i < n; i++) {
      // 버킷 인덱스 계산 공식: (값 - 최소값) * 버킷 수 -1) / (최대값 - 최소값)
      int bucketIndex = ((arr[i] - min) * (n - 1) / range);
      buckets[bucketIndex].add(arr[i]);
    }

    // 각 버킷 정렬
    for (int i = 0; i < n; i++) {Collections.sort(buckets[i]);}

    // 버킷 합치기
    int index = 0;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < buckets[i].size(); j++) {
        arr[index++] = buckets[i].get(j);
      }
    }
  }
}
*/
