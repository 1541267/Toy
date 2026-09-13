package Study.DataStructure.Code;

import java.util.Arrays;
import java.util.NoSuchElementException;

/*
- Stack (LIFO, Last In First Out): 마지막에 삽입된 원소가 가장 먼저 출력
  - 연산
    -> push(e) & pop() & peek() -> O(1), 대신 배열 기반에서 push()는 Dynamic Array와 같이 Amortized O(1)
    -> top: 다음 원소가 들어갈 위치 추적

  - 배열 기반 (Array Stack)
    -> Dynamic Array 구조를 사용하여 배열의 장점중 하나인 캐시 효율이 좋음
    -> top: 다음 원소가 들어갈 인덱스
    -> ArrayList의 addLast()/removeLast()와 동일 구조, 앞쪽을 밀거나 당길 필요 없음
    -> push/pop이 항상 배열 끝쪽 에서만 발생
    -> pop()시 논리적 삭제(top 감소)만으로는 참조가 끊기지 않아 GC가 회수 못하는 Loitering Object (사용되지 않는 객체)발생
    --> stack[pop] = null 처리를 꼭 하여 Loitering Object 및 & 메모리 누수 방지
    -> 공간복잡도: O(n), 확장 가능

  - 연결리스트 기반 (LinkedListStack)
    -> top: head 역할을 하는 참조, 새 노드를 항상 top.next 에 삽입
    -> push(): newNode.next = top, top = newNode (head 삽입)
    -> pop(): top.data 저장, top = top.next, data 반환 (head 제거)
    -> tail 포인터 & 이중 연결 불필요 (스택 특성상 연산이 한쪽 끝에서만 일어나기 때문)
    -> 별도의 null 처리(Loitering) 문제 없음 => 제거된 노드는 참조하는 변수가 없어 자동으로 GC 대상
    -> 공간복잡도: O(n), 배열과 달리 필요한 만큼의 노드만 생성 (추가 여유 공간 없음)
    --> 대신 노드마다 next 참조를 위한 추가 메모리 오버헤드 존재

  - 배열/연결리스트 선택
    - 배열
      -> 데이터 크기가 대략 예측이 가능할 때
      -> 성능 최적화(연속 메모리로 인한 CPU 캐시 친화적)
      -> 메모리 효율(원소 자체에 필요한 메모리 이외의 오버헤드가 적음)
      -> 구현이 단순
    - 연결리스트
      -> 반대로 데이터가 크기 예측이 어려울 때
      -> 데이터의 추가/삭제가 빈번하고 크기 변화가 클 때
      -> 재할당 없이 필요한 만큼 동적으로 확장 가능
*/

public class _3_Stack {

  // 배열 기반
  static class ArrayBasedStack<E> {

    Object[] stack;
    private int capacity, top = 0;

    public ArrayBasedStack() {
      this.capacity = 10;
      this.stack = new Object[capacity];
    }

    public ArrayBasedStack(int capacity) {
      if (capacity < 0) {throw new IllegalArgumentException("잘못된 용량 초기화, capacity: " + capacity);}

      this.stack = new Object[capacity];
      this.capacity = capacity;
    }

    public void push(E e) {
      growIfNeeded();
      stack[top++] = e;
    }

    // pop() 한 top 데이터를 null 처리 해 줘야 GC 대상이 되어 메모리에 남지 않음
    // 배회하는 객체(Loitering Object) 방지
    @SuppressWarnings("unchecked")
    public E pop() {
      if (top == 0) {
        throw new NoSuchElementException("Empty Stack");
      } else {
        E value = (E) stack[--top];
        stack[top] = null;
        return value;
      }
    }

    @SuppressWarnings("unchecked")
    public E peek() {
      if (top == 0) {
        throw new NoSuchElementException("Empty Stack");
      } else {
        return (E) stack[top - 1];
      }
    }

    public int size() {return top;}

    public boolean isEmpty() {return top == 0;}

    private void growIfNeeded() {
      if (top == capacity) {
        capacity = capacity == 0 ? 1 : capacity * 2;
        stack = Arrays.copyOf(stack, capacity);
      }
    }
  }

  // 연결 리스트 기반
  static class LinkedListStack<E> {

    private static class Node<E> {

      private final E data;
      private Node<E> next;

      private Node(E data) {this.data = data;}
    }

    private Node<E> top;
    private int size;

    public void push(E data) {
      Node<E> newNode = new Node<>(data);

      newNode.next = top;
      top = newNode;

      size++;
    }

    public E pop() {
      if (top == null) {throw new NoSuchElementException("Empty Stack");}

      E data = top.data;
      top = top.next;
      size--;

      return data;
    }

    public E peek() {
      if (top == null) {throw new NoSuchElementException("Empty Stack");}
      return top.data;
    }

    public int size() {return size;}

    public boolean isEmpty() {return top == null;}
  }
}
