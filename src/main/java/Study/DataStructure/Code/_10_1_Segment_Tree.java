package Study.DataStructure.Code;

import java.util.Arrays;

/*
- 세그먼트 트리 (Segment Tree): 구간 집계값(합, 최솟값, 최댓값 등)을 저장하는 이진 트리
  - 필요한 이유
    -> 배열 순회: query O(n), update O(1) / 누적합 배열: query O(1), update O(n)
    -> 값 변경과 구간 조회가 둘 다 많이 섞이면 어느 한쪽이 O(n)이라 시간 초과
    -> 세그먼트 트리는 query, update 모두 O(log n)으로 균형을 맞춤
    -> 값이 변하지 않는다면 누적합으로 충분, 변경이 있을 때만 세그먼트 트리가 필요

  - 구조
    -> 루트는 전체 구간 [0, n-1], 자식은 [start, mid]와 [mid+1, end], 리프는 원소 하나짜리 구간
    -> 각 노드가 자기 구간의 집계값을 저장 (부모 = 왼쪽 자식 값 + 오른쪽 자식 값)
    -> mid = start + (end - start) / 2 로 구간을 겹치지도 빠지지도 않게 분할
    --> start + end 로 계산하면 큰 값에서 int 오버플로 가능성
    -> 힙처럼 배열로 표현하고 루트를 1번으로 잡으면 자식은 node * 2, node * 2 + 1
    -> 입력 타입과 합의 타입을 분리 (원소는 int여도 합은 long 배열에 저장해서 오버플로 방지)

  - 배열 크기를 4n으로 잡는 이유
    -> 힙은 완전 이진 트리라 인덱스가 0~n-1에 딱 맞지만, 세그먼트 트리는 마지막 레벨이 왼쪽부터 빈틈없이 채워지지 않을 수 있음
    -> 트리의 높이는 ceil(log2 n)이고, 가장 깊은 리프의 인덱스는 최대 2^(높이+1) - 1까지 감
    -> n이 2의 거듭제곱보다 살짝만 커도 높이가 한 단계 늘어나 필요한 크기가 거의 4n에 근접
    --> 3n으로 잡으면 n=36에서 마지막 인덱스가 113인데 배열 길이는 108이라 ArrayIndexOutOfBounds 발생
    -> 정확히는 2 * 2^ceil(log2 n)이면 충분하지만, 단순하고 안전한 4n이 관례

  - build -> O(n)
    -> 리프에서 arr[start]를 저장하고 return
    -> 왼쪽, 오른쪽 자식을 먼저 재귀로 채운 뒤(후위 순서) 부모를 자식 값으로 계산
    -> 부모가 자식 값을 쓰려면 자식이 먼저 채워져 있어야 해서 계산 순서가 중요
    -> 각 노드를 정확히 한 번씩만 방문하고 구간을 다시 훑지 않으므로 O(n)
    --> 노드마다 arr 구간을 처음부터 합산하면 레벨마다 n개를 훑어 O(n log n)이 됨

  - query(left, right) -> O(log n)
    -> 노드 구간 [start, end]와 질의 구간 [left, right]의 관계는 세 가지
    --> 1) 겹치지 않음: left > end || right < start -> 항등원 반환 (합이면 0, 곱 = 1, 최소 = MAX_VALUE, 최대 = MIN_VALUE)
    --> 2) 완전히 포함됨: left <= start && end <= right -> tree[node] 그대로 반환, 더 내려가지 않음
    --> 3) 일부만 겹침: 양쪽 자식을 같은 (left, right)로 호출하고 결과를 합침
    -> 검사 순서는 겹침 여부를 먼저 보고 포함 여부를 나중에 봄 (겹치지 않는 노드를 포함으로 오판 방지)
    -> 질의 구간을 쪼개서 넘기지 않음, 노드 구간만 줄어들고 판단은 각 자식이 분기 1, 2로 알아서 처리
    -> 리프는 구간이 원소 하나라 분기 1이나 분기 2 중 하나로 반드시 끝나므로 별도 종료 조건이 필요 없음
    -> 각 레벨에서 분기 3으로 더 내려가는 노드가 최대 2개라 방문 노드 수가 레벨당 상수, 전체 O(log n)

  - update(idx, v) -> O(log n)
    -> idx와 mid를 비교해 한쪽 자식으로만 내려감 (idx <= mid면 왼쪽, 아니면 오른쪽)
    -> query는 양쪽을 모두 호출하지만 update는 경로가 한 줄이라 높이만큼만 방문
    -> 리프(start == end)에 도달하면 그 원소가 idx이므로 idx를 따로 검사하지 않아도 됨
    -> 재귀에서 돌아오면서 경로 위 노드만 두 자식의 합으로 재계산 (차이를 더하는 방식보다 단순)
    -> 구조가 바뀌지 않고 값만 바뀌므로 노드 추가, 삭제 개념은 없음

  - 범위 검증
    -> update는 0 <= idx <= n-1이 아니면 예외, idx == n이나 음수를 막지 않으면 마지막이나 첫 원소를 조용히 덮어씀
    -> query는 음수, l > r, 배열 범위 초과를 모두 예외 처리
    --> 검증이 없으면 분기 1, 2가 범위 밖 구간을 자연스럽게 걸러내서 에러 없이 일부만 계산한 값이 반환됨
    -> 빈 배열은 생성자에서 예외, 원소가 1개면 루트가 곧 리프

  - 연산 확장
    -> 합치는 연산이 결합법칙을 만족하고 항등원이 있으면 구조는 그대로
    --> 합: 항등원 0, 곱: 1, 최솟값: MAX_VALUE, 최댓값: MIN_VALUE, gcd: 0, XOR: 0
    -> 바꿀 곳은 항등원 반환 값과 합치는 식 두 곳
    -> 한 지점 갱신(point update)과 구간 조회(range query) 조합. 구간 전체를 한 번에 갱신하려면 lazy propagation이 필요 (선택 학습)

  - 복잡도
    -> build O(n) / query O(log n) / update O(log n) / 공간 O(n) (실제 배열은 4n)
    -> 완전 이진 트리인 힙과 달리 부모-자식 순서 보장이 아니라 구간 집계값 보관이 목적이라 노드 값의 의미가 다름
  - 세그먼트 트리 보충 (사용처, 복잡도 근거, update 방식, 연산 확장)

  - 사용하는 상황
    -> 값의 변경과 구간 조회가 둘 다 많이 반복될 때 사용
    -> 값이 안 바뀌고 구간 합만 조회: 누적합 배열 (조회 O(1))
    -> 값이 안 바뀌고 구간 min, max만 조회: Sparse Table (조회 O(1))
    -> 조회는 드물고 변경만 많음: 그냥 배열 순회
    -> 변경과 조회가 둘 다 많음: 세그먼트 트리
    -> 대표 사례: 구간 합 + 값 수정, 구간 min, max, gcd, XOR, 구간 내 개수 세기, k번째 수 찾기, 구간 일괄 갱신(lazy propagation)
    -> 쓸 수 없는 경우: 결합법칙이 성립하지 않는 연산 (중앙값은 두 구간의 중앙값으로 전체 중앙값을 만들 수 없어 불가, 평균은 합과 개수를 따로 저장하면 가능)
    -> 행렬 곱처럼 결합법칙만 성립하고 순서가 중요한 연산은 왼쪽 자식, 오른쪽 자식 순서를 지켜서 합쳐야 함 (현재 구현은 항상 왼쪽, 오른쪽 순서라 그대로 사용 가능)
    -> 백엔드 실무에서 직접 구현할 일은 드묾 (집계는 DB 인덱스, 집계 쿼리, Redis 등이 담당)
    --> 공부하는 이유: 코딩테스트 구간 쿼리 문제에서 O(n) 풀이가 막히는 지점을 푸는 도구, 트리를 배열로 표현하고 구간을 쪼개 집계값을 위로 올리는 사고방식

  - 누적합 배열과 세그먼트 트리 선택 기준
    -> 쿼리 1만 번 + 업데이트 1만 번이 섞여 있고 n = 10만이면
    --> 누적합: 업데이트 1만 번 x O(n) = 약 10억 연산
    --> 세그먼트 트리: 업데이트 1만 번 x 약 17 (log n)
    -> 업데이트가 아예 없다면 누적합이 더 빠르고 가벼움 (메모리 n칸 vs 4n칸)
    -> 업데이트가 섞이는 순간 세그먼트 트리가 유리, 대신 메모리 상수 비용 존재

  - query의 방문 노드가 레벨당 상수 개로 제한되는 이유
    -> 분기 1(겹치지 않음)과 분기 2(완전 포함)는 방문 즉시 종료되어 자식으로 내려가지 않음
    -> 분기 3(일부만 겹침)으로 내려가는 노드는 질의 구간의 경계(l 또는 r)를 노드 구간이 가로지를 때만 발생
    -> 같은 레벨의 노드 구간들은 서로 겹치지 않으므로 l을 가로지르는 노드 최대 1개, r을 가로지르는 노드 최대 1개
    -> 레벨마다 분기 3 노드는 최대 2개, 이들의 자식 최대 4개만 방문하므로 레벨당 상수, 전체 O(log n)
    -> 예시 [2, 5, 1, 4, 3, 6]의 query(1, 4)
    --> 분기 3: [0..5], [0..2], [3..5], [0..1] (4개)
    --> 분기 1: [0..0], [5..5] -> 0 반환
    --> 분기 2: [1..1]=5, [2..2]=1, [3..4]=7 -> 합 13

  - update에서 차이를 더하지 않고 두 자식으로 재계산하는 이유
    -> 합은 역연산(뺄셈)이 있어서 -old +new로 한 원소의 기여분만 갈아끼울 수 있음
    -> min은 역연산이 없고 노드 값이 결과만 들고 있어서, 어느 원소가 그 값을 만들었는지 알 수 없음
    --> 예시: [2, 5]를 담당하는 노드 값은 min = 2, arr[0]을 9로 바꾸면 차이 +7을 더해 9가 되지만 실제는 min(9, 5) = 5
    -> 핵심은 집계 결과에서 한 원소의 영향을 되돌릴 수 있는지 여부 (순서와는 무관)
    -> 차이를 더하는 방식은 역연산이 있는 연산(합, XOR)에서만 성립
    -> 두 자식 값으로 재계산하는 방식은 결합법칙만 성립하면 모든 연산에서 성립 (현재 구현이 이 방식이라 확장성이 좋음)
    -> 펜윅 트리는 update에서 += delta 방식을 사용하므로 역연산이 있는 연산에 자연스럽고, min, max에는 제약이 붙음 (비교 학습 포인트)

  - 현재 구현의 성격과 연산 확장
    -> 합 전용 세그먼트 트리 (한 지점 갱신 point update + 구간 합 조회 range query)
    -> 연산이 코드에 고정된 곳은 합치는 식 3곳과 항등원 1곳
    --> 합치는 식: build 마지막 줄, doUpdate 마지막 줄, doQuery 분기 3의 두 결과 합산
    --> 항등원: doQuery 분기 1의 return 0
    -> 나머지 구조(구간 분할, 분기 순서, 경로 탐색, 4n 배열, 범위 검증)는 연산과 무관
    -> min 버전: 합치는 식을 Math.min으로, 항등원을 Long.MAX_VALUE로 교체
    -> 일반화: 합치는 연산과 항등원을 생성자에서 주입 (LongBinaryOperator op, long identity)
    --> 합: Long::sum, 0 / min: Math::min, Long.MAX_VALUE / max: Math::max, Long.MIN_VALUE

*/
public class _10_1_Segment_Tree {

  static class SegmentTree {

    private final long[] tree;
    private final int originalArrSize;

    public SegmentTree(long[] arr) {
      if (arr == null || arr.length < 1) {throw new IllegalArgumentException("build할 데이터의 배열 크기가 적음");}
      this.originalArrSize = arr.length;
      this.tree = new long[originalArrSize * 4];

      build(arr, 1, 0, arr.length - 1);
    }

    private void build(long[] arr, int node, int start, int end) {
      // 리프
      if (start == end) {
        tree[node] = arr[start];
        return;
      }

      int mid = start + (end - start) / 2;
      build(arr, node * 2, start, mid);
      build(arr, node * 2 + 1, mid + 1, end);
      tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }

    public long query(int left, int right) {
      checkQueryRange(left, right);

      return doQuery(1, 0, originalArrSize - 1, left, right);
    }

    private long doQuery(int node, int start, int end, int left, int right) {
      // 첫 분기, 쿼리(질의)의 범위가 각 노드 구간과 겹치지 않는 경우
      // 왼쪽이 end 보다 크다 = 쿼리 범위가 오른쪽에, 오른쪽이 start 보다 = 반대
      // 항등원(값을 변환하지 않는 요소, 덧셈 = 0, 곱셈 = 1, 최솟값 = MAX_VALUE, 최댓값 = MIN_VALUE)을 반환
      if (left > end || right < start) {return 0;}

      // 두 번째 분기, 질의 범위가 노드 구간에 포함될 경우
      if (left <= start && right >= end) {return tree[node];}

      // 그 외
      int mid = start + (end - start) / 2;
      return doQuery(node * 2, start, mid, left, right) + doQuery(node * 2 + 1, mid + 1, end, left, right);
    }

    public void update(int idx, long value) {
      if (idx > originalArrSize - 1 || idx < 0) {
        throw new IllegalArgumentException("범위를 벗어난 idx 수정 시도, idx: " + idx);
      }

      doUpdate(1, 0, originalArrSize - 1, idx, value);
    }

    private void doUpdate(int node, int start, int end, int idx, long value) {
      // 리프
      if (start == end) {
        tree[node] = value;
        return;
      }

      int mid = start + (end - start) / 2;

      // idx가 왼쪽 범위에 있는 경우
      if (idx <= mid) {
        doUpdate(node * 2, start, mid, idx, value);
      } else {
        // 오른쪽
        doUpdate(node * 2 + 1, mid + 1, end, idx, value);
      }

      // 결과 업데이트
      tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }

    private void checkQueryRange(int left, int right) {
      if (left < 0 || right < 0) {
        throw new IllegalArgumentException("검사 하려는 범위가 음수, left: " + left + ", right: " + right);
      }
      if (left > right) {
        throw new IllegalArgumentException("검사 하려는 왼쪽의 범위가 오른쪽보다 큼,  left: " + left + ", right: " + right);
      }
      if (left > originalArrSize - 1 || right > originalArrSize - 1) {
        throw new IllegalArgumentException("검색 하려는 범위가 기존 배열을 벗어남");
      }
    }
  }

  public static void main(String[] args) {
    SegmentTree segmentTree = new SegmentTree(new long[]{2, 5, 1, 4, 3, 6});
    System.out.println("==========================================================");
    System.out.println("original tree = " + Arrays.toString(segmentTree.tree));

    System.out.println("segmentTree.query(1, 4) = " + segmentTree.query(1, 4));

    segmentTree.update(3, 10);
    System.out.println("after update segmentTree.tree = " + Arrays.toString(segmentTree.tree));
    System.out.println("segmentTree.query(1, 4) = " + segmentTree.query(1, 4));
    System.out.println("segmentTree.query(3, 4) = " + segmentTree.query(3, 4));

    System.out.println("segmentTree.tree[1] = " + segmentTree.tree[1]);
  }
}
