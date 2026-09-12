package Study.DataStructure.Code;
/*

- 단일 연결리스트 (Singly Linked List): 각 노드가 data와 다음 노드에 대한 참조(next)만 가지는 자료구조
  -> 배열처럼 연속된 메모리 공간이 필요 없고, 삽입/삭제 시 값을 옮기는 대신 노드를 새로 만들고 참조(포인터)만 재연결

  - 주의점: Node 자체를 리스트의 진입점(head)으로 쓰면 안 됨
    -> index 0을 삽입/삭제하려면 head 참조 자체를 교체 할 수 있어야 하는데 Node 객체 스스로는 외부에서 자신을 가리키는 참조를 바꿀 수 없음
    -> head를 필드로 들고 있는 별도의 LinkedList 클래스가 필요 (head = head.next 처럼 리스트 클래스가 참조 교체를 담당)

  - tail 포인터가 없으면 맨 뒤 추가(add())도 매번 head부터 끝까지 순회해야 해서 O(n)
    -> tail 필드를 별도로 관리하면 맨 뒤 추가를 O(1)로 개선 가능
    -> 단, tail 관리는 인덱스 계산보다 구조적 불변조건으로 판단하는 게 안전
    -> (새로 삽입한 노드의 next가 null이면 해당 노드가 tail)

  - 삽입(add(i, e)): i번째 Prev Node를 찾아서 참조를 바꿔야 함 (단방향이라 앞에서부터 조회 시작)
  - 삭제(remove(i)): 도 마찬가지로 이전 노드가 필요 -> 매번 head부터 순회해서 탐색 O(n)

  - 인덱스 접근/삽입/삭제 시 경계 조건 구분 필요
    -> 삽입(add): 맨 끝(i == size)도 유효한 위치 -> i > size 일 때만 예외
    -> 조회/삭제(get, remove): 실제 값이 있는 0 ~ size 범위만 유효, i >= size 면 예외

  - 배열과의 트레이드오프
    배열: 인덱스 접근 O(1), 중간 삽입/삭제 O(n) (값 이동 필요), 캐시 지역성 좋음
    연결리스트: 인덱스 접근 O(n) (순차 탐색), 중간 삽입/삭제는
      -> 이전 노드를 이미 찾았다면 O(1)이지만, 위치를 찾는 과정 자체가 O(n)이라
      -> 실질적으로는 삽입/삭제도 O(n)인 경우가 많음

  - 시간 복잡도 (tail 관리 적용 기준)
    get(i): O(n) - head부터 순차 탐색
    add(e) 맨 뒤 추가: O(1) - tail 덕분
    add(0, e), remove(0): O(1) - head 참조만 교체
    add(i, e), remove(i) (i>0): O(n) - i-1번째까지 순회 필요

- ArrayDeque, 각종 큐/스택, 해시 테이블의 체이닝, 순차적으로 처리하는 작업 목록

-------------------------------------------------------------------------------------

- 이중 연결리스트 (Doubly Linked List): 연결리스트의 각 노드에 prev 참조를 추가한 구조
  -> prev, next 양방향 참조 덕분에 단일 연결리스트의 두 가지 한계를 해결
  -> 이중 연결 리스트는 Prev 포인터를 사용하기 때문에 단일 연결 리스트보다 메모리를 추가로 사용

  - 삭제 시 이전 노드 탐색이 불필요해짐
    - 단일 연결리스트: 삭제하려면 반드시 prevNode를 앞에서부터 찾아야 함
    - 이중 연결리스트: curNode.prev로 즉시 접근 가능
      -> (인덱스로 삭제할 때는 그 인덱스의 노드까지 찾아가는 탐색 자체는 여전히 필요)

  - tail에서부터 역방향 순회 가능
    -> getNode(i) 호출 시 i가 뒤쪽에 가까우면 tail에서 prev를 타고 거꾸로 탐색
    -> i와 (size - i) 중 더 작은 쪽에서 시작 -> 평균 탐색 비용이 절반으로 감소
    -> Big-O 표기상으로는 여전히 O(n)이지만 상수 인자가 개선

  - remove(size-1) = 마지막 원소 삭제는 이중 연결리스트에서 O(1)로 가능
    -> tail.prev로 바로 새로운 tail을 얻을 수 있기 때문
    -> 단일 연결리스트였다면 마지막 노드의 "이전 노드"를 알 방법이 없어 O(n) 필요

  값만 바꾸는 연산(set)은 링크 재구성이 필요 없음
    -> add/remove는 리스트의 연결 구조(순서, 개수) 자체가 바뀌므로 이웃 노드의 참조 갱신이 필수
    -> set은 구조가 그대로이고 노드 내부 데이터만 바뀌므로 getNode(i).data = e 로 충분
    -> 이 연산이 구조를 바꾸는가, 내용만 바꾸는가 불필요한 작업을 피하는 판단 기준

  시간 복잡도 (tail + 양방향 탐색 적용 기준)
    get(i): O(min(i, size-i)) - head/tail 중 가까운 쪽에서 탐색
    add(e) 맨 뒤 추가: O(1)
    add(0, e), remove(0): O(1)
    remove(size-1): O(1) - tail.prev로 즉시 접근 (단일 연결리스트라면 O(n))
    add(i, e), remove(i) (일반 위치): O(min(i, size-i)) - 위치 탐색 비용이 지배적

  - LinkedList가 이 구조를 사용 (Doubly Linked List + head/tail 관리)
    -> Deque 인터페이스를 구현하여 스택/큐로도 사용 가능한 이유 (양쪽 끝 모두 O(1) 삽입/삭제가 가능하기 때문)

*/

// tail(마지막 노드) 추적 개선 & 이중 연결 리스트
public class _2_LinkedList<E> {

  private Node<E> head;
  private Node<E> tail;
  private int size = 0;

  static class Node<E> {

    E data;
    Node<E> next;
    Node<E> prev;

    Node(E data) {
      this.data = data;
      this.next = null;
      this.prev = null;
    }
  }

  public void add(E e) {
    Node<E> newNode = new Node<>(e);

    if (head == null) {
      head = newNode;
    } else {
      tail.next = newNode;
      newNode.prev = tail;
    }

    size++;
    refreshTailTracking(newNode);
  }

  public void add(int i, E e) {
    checkInsertBounds(i);
    Node<E> newNode = new Node<>(e);

    if (i == 0) {
      newNode.next = head;

      if (head != null) {head.prev = newNode;}

      head = newNode;
    } else {
      Node<E> prevNode = getNode(i - 1);
      Node<E> nextNode = prevNode.next;

      prevNode.next = newNode;
      newNode.prev = prevNode;
      newNode.next = nextNode;

      if (nextNode != null) {nextNode.prev = newNode;}
    }

    if (newNode.next == null) {refreshTailTracking(newNode);}

    size++;
  }

  private void set(int i, E e) {
    checkElementBounds(i);
    getNode(i).data = e;
  }

  private void set(int i, Node<E> node) {
    checkElementBounds(i);
    getNode(i).data = node.data;
  }

  public void remove(int i) {
    checkElementBounds(i);

    if (i == 0) {
      head = head.next;

      if (head == null) {
        tail = null;
      } else {
        head.prev = null;
      }
    } else if (i == size - 1) {
      refreshTailTracking(tail.prev);
      tail.next = null;
    } else {
      Node<E> curNode = getNode(i);
      Node<E> prevNode = curNode.prev;
      Node<E> nextNode = curNode.next;

      prevNode.next = nextNode;
      nextNode.prev = prevNode;
    }
    size--;
  }

  private void refreshTailTracking(Node<E> newNode) {tail = newNode;}

  public E getData(int i) {return getNode(i).data;}

  // 인덱스 조회 시 노드 순회를 줄이기 위해 head, tail 중 더 가까운 쪽에서 시작해서 조회
  private Node<E> getNode(int i) {
    checkElementBounds(i);

    if (i == 0) {return head;}
    if (i == size - 1) {return tail;}

    Node<E> curNode;

    // i가 앞쪽에 가까운 경우
    if (i < size / 2) {
      int idx = 0;
      curNode = head;

      while (idx < i) {
        curNode = curNode.next;
        idx++;
      }
    } else {
      int idx = size - 1;
      curNode = tail;

      while (idx > i) {
        curNode = curNode.prev;
        idx--;
      }
    }
    return curNode;
  }

  public int size() {return size;}

  public void checkInsertBounds(int i) {
    if (i < 0 || i > size) {
      throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + size);
    }
  }

  public void checkElementBounds(int i) {
    if (i < 0 || i >= size) {
      throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + size);
    }
  }
/*
  마지막 노드인 tail 추적으로 getLastNode 필요 X
  private Node<E> getLastNode() {
    Node<E> curNode = head;

    while (curNode.next != null) {curNode = curNode.next;}
    return curNode;
  }
*/
}

// 개선 전 단일 연결 리스트
// public class _2_LinkedList<E> {
//
//   private Node<E> head;
//   private int size = 0;

//   static class Node<E> {
//
//     E data;
//     Node<E> next;
//
//     Node(E data) {
//       this.data = data;
//       this.next = null;
//     }
//   }
//
//   public void add(E e) {
//
//     if (head == null) {
//       head = new Node<>(e);
//     } else {
//       getLastNode().next = new Node<>(e);
//     }
//
//     size++;
//   }
//
//   public void add(int i, E e) {
//     checkInsertBounds(i);
//     Node<E> newNode = new Node<>(e);
//
//     if (i == 0) {
//       newNode.next = head;
//       head = newNode;
//     } else {
//       Node<E> prevNode = getNode(i - 1);
//       newNode.next = prevNode.next;
//       prevNode.next = newNode;
//     }
//     size++;
//   }
//
//   public E getData(int i) {
//     return getNode(i).data;
//   }
//
//   private Node<E> getNode(int i) {
//     checkElementBounds(i);
//
//     Node<E> curNode = head;
//     int idx = 0;
//
//     while (idx < i) {
//       curNode = curNode.next;
//       idx++;
//     }
//
//     return curNode;
//   }
//
//   private Node<E> getLastNode() {
//     Node<E> curNode = head;
//
//     while (curNode.next != null) {
//       curNode = curNode.next;
//     }
//
//     return curNode;
//   }
//
//   public void remove(int i) {
//     checkElementBounds(i);
//
//     if (i == 0) {
//       head = head.next;
//     } else {
//       Node<E> prevNode = getNode(i - 1);
//       prevNode.next = prevNode.next.next;
//     }
//
//     size--;
//   }
//
//   public int size() {
//     return size;
//   }
//
//   public void checkInsertBounds(int i) {
//     if (i < 0 || i > size) {ㄴ
//       throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + size);
//     }
//   }
//
//   public void checkElementBounds(int i) {
//     if (i < 0 || i >= size) {
//       throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + size);
//     }
//   }
// }
