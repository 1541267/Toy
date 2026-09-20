package Study.DataStructure.Code;

import java.util.ArrayList;

/*
- Heap: 부모-자식 간 순서만 보장하는 완전 이진 트리 (전체 정렬은 보장 안 함)
  - BST와의 차이
    -> BST: 왼쪽 서브트리 < 노드 < 오른쪽 서브트리 (전체 순서 보장) -> 특정 값 탐색에 최적화 O(log n)
    -> Heap: 부모가 자식보다 크거나(Max) 작음(Min), 형제 간 순서는 무관 -> 최댓값/최솟값 반복 추출에 최적화
    -> Heap은 항상 완전 이진 트리(구조가 절대 불균형해지지 않음), BST는 편향 가능
    -> Heap에서 특정 값 탐색은 O(n) (형제 간 순서가 없어서 전체를 검사해야 함)

  - 완전 이진 트리 + 배열 표현
    -> 완전 이진 트리(마지막 레벨만 왼쪽부터 채워짐, 빈 구멍 없음)라는 성질 덕분에
       포인터(left/right) 없이 배열 인덱스 연산만으로 부모-자식 관계 표현 가능
    -> parent = (i-1)/2, left = 2i+1, right = 2i+2
    -> BST처럼 Node 객체 + 좌우 포인터 불필요 -> 메모리 효율 + 캐시 친화적

  - insert(e) & siftUp
    -> 배열 맨 끝에 추가 후, 부모와 비교하며 Heap 조건 위반 시 swap하며 위로 이동
    -> 위로 올라가는 거리 = 트리 높이 이내 -> O(log n)
    -> maxHeap: 자식이 부모보다 크면 위반 / minHeap: 자식이 부모보다 작으면 위반

  - remove() & siftDown
    -> root(배열[0])를 꺼내고, 배열 맨 끝 원소를 root 자리로 옮긴 뒤 자식과 비교하며 아래로 이동
    -> 맨 끝 원소를 root로 옮기는 이유: 중간 원소를 뽑으면 완전 이진 트리 구조에 빈 구멍이
       생겨 배열 인덱스 공식이 깨짐. 맨 끝 원소는 삭제해도 구조가 안 깨지므로 그걸 옮겨서 재조정
    -> 내려가는 거리도 트리 높이 이내 -> O(log n)

  - remove(int i): 임의 인덱스 삭제 -> siftUp/siftDown 중 하나만 필요한 이유
    -> 맨 끝 원소를 i 위치로 옮겼을 때, 그 원소는 부모 쪽 위반(siftUp 필요) 또는
       자식 쪽 위반(siftDown 필요) 둘 중 하나만 발생 가능, 동시에 발생 불가
    -> 이유: 옮기기 전 맨 끝 원소는 자기 서브트리 기준으로 이미 유효했고,
       i 위치의 원래 값도 자기 부모/자식 관계 기준으로는 유효했었기 때문
    -> 그래서 부모와 딱 한 번만 비교해서 방향(siftUp or siftDown)을 결정하면 충분
       (실제 Java PriorityQueue.removeAt()도 동일한 기법 사용)

  - remove(E e): 값으로 삭제
    -> search(e)로 인덱스를 먼저 찾은 뒤 remove(int i) 재사용
    -> search 자체가 O(n)이므로 remove(E e)도 O(n) (힙 구조의 근본적 한계, 버그 아님)

  - buildHeap(array): 배열을 한 번에 힙으로 구성 (Heap Sort의 첫 단계와 동일)
    -> insert()를 n번 반복 -> O(n log n)
    -> buildHeap: (n/2)-1 (마지막 non-leaf 노드) 부터 0까지 역순으로 각 인덱스에 siftDown 호출 -> O(n)
    -> O(n)인 이유: 리프 노드는 애초에 건드릴 필요 없이 시작하고(리프가 전체 노드의 절반),
       대부분의 노드가 트리 아래쪽(얕은 높이)에 몰려있어 siftDown이 실제로 이동하는 거리가
       짧은 노드가 압도적으로 많음 -> 레벨별 (노드 수 × 최대 이동 높이)의 합이 등비급수로 수렴해 O(n)
    -> 생성자(ArrayList, HeapType)에서 buildHeap 호출 -> 배열로 바로 힙 생성 가능

  - 설계 관련 결정 사항
    -> 생성자/buildHeap에서 외부 ArrayList를 받을 때 참조를 그대로 저장하지 않고
       new ArrayList<>(newArr)로 방어적 복사(defensive copy)
    --> 참조를 그대로 저장하면, 서로 다른 Heap 인스턴스가 내부적으로 같은 배열 객체를
        공유하게 되어(aliasing) 한쪽에서 remove() 등으로 배열을 수정하면 다른 인스턴스도
        모르는 사이에 오염됨 -> 캡슐화가 깨지는 대표적인 버그 패턴
    -> HeapType이 null이면 기본값으로 넘어가지 않고 즉시 IllegalArgumentException (fail-fast)
    --> MAX/MIN 둘 다 동등하게 의미 있는 선택이라 하나를 기본값으로 조용히 정하면
        호출부의 실수(null 전달)를 숨기고 나중에 엉뚱한 결과로 이어져 디버깅이 어려워짐
    --> buildHeap(null)은 예외 대신 빈 배열로 처리 -> 타입 미지정은 대체 불가능하지만
        배열 미지정은 빈 힙이라는 합리적인 기본 동작이 존재하므로 예외 대상이 아님
    -> checkAndThrowAlert 분기 조건을 String 대신 Operation enum으로 관리
    --> 문자열 비교는 오타가 나도 컴파일 타임에 안 잡히지만 enum은 존재하지 않는 값 자체가 불가능

  - 활용
    -> 우선순위 큐(Priority Queue)의 표준 구현체 (Java PriorityQueue가 이 구조 사용)
    -> Heap Sort (buildHeap으로 O(n) 힙 구성 후 remove()를 n번 반복하며 정렬)
    -> 그래프 알고리즘(Dijkstra 등)에서 다음으로 처리할 최소 비용 노드를 꺼낼 때 사용
*/

public class _6_Heap {

  static class Heap<E extends Comparable<E>> {

    enum HeapType {MAX, MIN}

    enum Operation {INSERT, REMOVE_ROOT, REMOVE_IDX, REMOVE_VAL, SEARCH, PEEK, PRINT_HEAP}

    private ArrayList<E> arr;
    private final HeapType type;

    public Heap(HeapType type) {
      if (type == null) {
        throw new IllegalArgumentException("Heap 생성 시 Heap Type 지정 필요");
      }

      this.arr = new ArrayList<>();
      this.type = type;
    }

    // 초기에 생성 된 heap에 insert()를 계속해서 값을 넣으면 insert() 호출 마다 O(log n) => 총 O(n log n)
    // 대신 초기 생성 시 이미 삽입 된 배열을 받아 Build Heap을 하면 O(n)이 가능
    // Heap Sort에서 정렬 안 된 배열을 힙으로 바꾸는 첫 단계도 이 단계
    public Heap(ArrayList<E> arr, HeapType type) {
      if (type == null) {
        throw new IllegalArgumentException("Heap 생성 시 Heap Type 지정 필요");
      }

      this.type = type;

      buildHeap(arr);
    }

    public int getSize() {return arr.size();}

    public void buildHeap(ArrayList<E> newArr) {
      this.arr = newArr == null ?
          new ArrayList<>() : new ArrayList<>(newArr);

      for (int i = (getSize() / 2) - 1; i >= 0; i--) {
        siftDown(i);
      }
    }

    // heap sort와 다르게 맨 뒤에 삽입 후 부모와 값 비교하며 힙 구성
    public void insert(E e) {
      checkAndThrowAlert(Operation.INSERT, e, 0);

      arr.add(e);

      siftUp(getSize() - 1);
    }

    public void remove() {
      checkAndThrowAlert(Operation.REMOVE_ROOT, null, 0);

      if (arr.size() == 1) {
        arr.removeLast();
        return;
      }

      int lastIdx = getSize() - 1;

      arr.set(0, arr.get(lastIdx));
      arr.removeLast();

      siftDown(0);
    }

    public void remove(int i) {
      checkAndThrowAlert(Operation.REMOVE_IDX, null, i);

      int lastIdx = getSize() - 1;

      if (i == lastIdx) {
        arr.removeLast();
        return;
      }

      arr.set(i, arr.get(lastIdx));
      arr.removeLast();

      if (i == 0) {
        siftDown(i);
        return;
      }

      int parentIdx = (i - 1) / 2;

      if (HeapType.MAX.equals(type)) {
        // maxHeap, 현재 값이 부모보다 크면 Heap 조건 위반 -> siftUp
        if (arr.get(i).compareTo(arr.get(parentIdx)) > 0) {
          siftUp(i);
        } else {
          // 위로 올라갈 필요가 없으면 아래쪽 확인
          siftDown(i);
        }
      } else {
        // minHeap, 현재 값이 부모보다 작으면 Heap 조건 위반
        // 위로 올라가야 함
        if (arr.get(i).compareTo(arr.get(parentIdx)) < 0) {
          siftUp(i);
        } else {
          // 위로 올라갈 필요가 없으면 아래쪽 확인
          siftDown(i);
        }
      }
    }

    public void remove(E e) {
      checkAndThrowAlert(Operation.REMOVE_VAL, e, 0);
      remove(search(e));
    }

    public E peek() {
      checkAndThrowAlert(Operation.PEEK, null, 0);
      return arr.getFirst();
    }

    public int search(E e) {
      checkAndThrowAlert(Operation.SEARCH, e, 0);

      int index = arr.indexOf(e);

      if (index == -1) {
        throw new IllegalArgumentException("찾으려는 데이터가 저장 되어있는 데이터가 아님");
      }

      return index;
    }

    // 배열 끝 부터 추가 & 부모와 비교하며 규칙 위반 시 위로 교환(swap) 반복
    // maxHeap은 자식이 부모보다 작은 경우, minHeap 은 반대
    private void siftUp(int childIdx) {
      while (childIdx > 0) {
        int parentIdx = (childIdx - 1) / 2;

        // 자식이 작음 = maxHeap 구조 만족
        if (HeapType.MAX.equals(type)) {
          if (arr.get(childIdx).compareTo(arr.get(parentIdx)) <= 0) {break;}
        } else {
          // 자식이 큼 = minHeap 구조 만족
          if (arr.get(childIdx).compareTo(arr.get(parentIdx)) >= 0) {break;}
        }

        swap(childIdx, parentIdx);
        childIdx = parentIdx;
      }
    }

    // heapify (sift down), siftUp의 반대진행
    private void siftDown(int i) {
      int parent = i;
      int size = getSize();

      while (true) {
        int left = 2 * parent + 1;
        int right = 2 * parent + 2;

        // 왼쪽도 없으면 종료, 완전 이진 트리로 오른쪽 존재 불가
        if (left >= size) {break;}

        int compare = left;

        // maxHeap 경우 부모가 크거나 같아야함
        if (HeapType.MAX.equals(type)) {
          if (right < size && arr.get(right).compareTo(arr.get(compare)) > 0) {
            compare = right;
          }

          if (arr.get(compare).compareTo(arr.get(parent)) <= 0) {
            break;
          }
        } else {
          // minHeap
          if (right < size && arr.get(right).compareTo(arr.get(compare)) < 0) {
            compare = right;
          }

          if (arr.get(compare).compareTo(arr.get(parent)) >= 0) {
            break;
          }
        }

        swap(parent, compare);
        parent = compare;
      }
    }

    private void swap(int i, int j) {
      E temp = arr.get(i);
      arr.set(i, arr.get(j));
      arr.set(j, temp);
    }

    private void checkAndThrowAlert(Operation logic, E e, int i) {
      switch (logic) {
        case INSERT: {
          if (e == null) {
            throw new IllegalStateException("Logic: " + logic + ", 삽입 하려는 데이터가 Null");
          }
          break;
        }

        case REMOVE_ROOT, PEEK, PRINT_HEAP: {
          if (this.arr.isEmpty()) {
            throw new IllegalStateException("Logic: " + logic + ", 현재 Heap 이 empty");
          }
          break;
        }

        case REMOVE_IDX, REMOVE_VAL, SEARCH: {

          if (this.arr.isEmpty()) {
            throw new IllegalStateException("Logic: " + logic + ", 현재 Heap 이 empty");
          }

          if (Operation.REMOVE_VAL.equals(logic) || Operation.SEARCH.equals(logic)) {
            if (e == null) {
              throw new IllegalStateException("Logic: " + logic + ", 삽입 하려는 데이터가 Null");
            }
          }

          if (Operation.REMOVE_IDX.equals(logic)) {
            if (i < 0 || i > getSize() - 1) {
              throw new IllegalStateException("Logic: " + logic + ", 잘못 된 인덱스, 사이즈: " + getSize() + ", index: " + i);
            }
          }
        }
      }
    }

    public void printHeap() {
      checkAndThrowAlert(Operation.PRINT_HEAP, null, 0);

      System.out.println(arr.getFirst());
      printHeap(0, "");

      System.out.println("==========================================================");
    }

    private void printHeap(int index, String prefix) {

      int left = index * 2 + 1;
      int right = index * 2 + 2;

      boolean hasLeft = left < getSize();
      boolean hasRight = right < getSize();

      if (hasLeft) {
        System.out.println(prefix + (hasRight ? "├─ L: " : "└─ L: ") + arr.get(left));

        printHeap(left, prefix + (hasRight ? "│   " : "    "));
      }

      if (hasRight) {
        System.out.println(prefix + "└─ R: " + arr.get(right));

        printHeap(right, prefix + "    ");
      }
    }
  }

  public static void main(String[] args) {

    ArrayList<Integer> maxHeapArray = new ArrayList<>();

    for (int i = 1; i < 36; i += 4) {maxHeapArray.add(i);}
    Heap<Integer> heap = new Heap<>(maxHeapArray, Heap.HeapType.MAX);
    heap.printHeap();

    heap.remove();
    heap.printHeap();

    ArrayList<Integer> minHeapArray = new ArrayList<>();
    for (int i = 1; i < 36; i += 4) {minHeapArray.add(i);}

    Heap<Integer> minHeap = new Heap<>(minHeapArray, Heap.HeapType.MIN);

    minHeap.printHeap();

    minHeap.remove();
    minHeap.printHeap();
  }
}