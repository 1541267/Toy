package Study.DataStructure.Code;

import java.util.Arrays;
import java.util.NoSuchElementException;

/*
- Queue (FIFO, First In First Out): 먼저 삽입된 원소가 가장 먼저 출력
  - 연산
    -> enqueue(e) & dequeue() & peek() -> O(1), 배열 기반에서는 resize 발생 시 Amortized O(1)
    -> front: 다음에 나갈(dequeue될) 원소의 위치 추적
    -> rear: 다음 원소가 들어갈(enqueue될) 위치 추적

  - 고정 배열 기반 선형 큐 (Fixed Array Linear Queue)
    -> 단순한 구현, front/rear 인덱스를 증가만 시킴
    -> dequeue 시 index만 이동 (앞으로 당기는 shift 없음) -> O(1)
    -> 한계: rear가 배열 끝(capacity)에 도달하면, front 쪽에 실제 빈 공간이 있어도 enqueue 불가 (메모리 낭비)
    -> 실무에서 그대로 쓰기엔 부적합, 문제 인식용/개선 전 단계

  - 동적 배열 기반 선형 큐 (Dynamic Array Linear Queue)
    -> 고정 배열의 한계를 동적 배열 & relocation으로 우선 해결
    -> rear == capacity 이면서 front>0 (앞쪽에 빈 공간 존재) -> relocation(앞으로 당김, O(n))
    -> 진짜 꽉 찼으면(size == capacity) -> resize(용량 2배 증가, O(n))
    -> relocation이 매 enqueue마다 발생하는 건 아니지만, 발생 시점마다 O(n) 비용 존재
    -> 원형 큐로 가기 전 중간 단계 -  매번 물리적 이동이 필요하다는 비효율 존재

  - 동적 배열 + 원형 큐 (Dynamic Array Circular Queue)
    -> index를 (index % capacity) 연산으로 순환(rotate)시켜 배열의 물리적 끝과 논리적 끝을 분리
    -> front/rear가 배열 끝에 도달해도 공간이 남아있으면 0번 인덱스부터 재사용 (relocation 자체가 불필요)
    -> 진짜 꽉 찼을 때만 resize(용량 2배, O(n)) -> Dynamic Array와 동일한 Amortized O(1) 근거
    --> 진짜 꽉 찼을 때만 rotate 수행
    -> "꽉 참"과 "빔"을 구분하는 게 핵심 설계 포인트 (front==rear가 두 상태 모두에 해당할 수 있음)
    --> size 필드를 별도로 둬서 front==rear일 때 size로 꽉 참/빔을 구분 (다른 방법: 한 칸을 일부러 비워두는 방식도 존재)
    -> 배열 기반 중 실무에서 쓰는 표준 방식 (Java ArrayDeque가 이 구조 사용)
    -> 공간복잡도: O(n), 확장 가능

  - 연결리스트 기반 (LinkedList Based Queue)
    -> head: front 역할, tail: rear 역할
    -> enqueue(): tail.next = newNode, tail = newNode (tail 삽입) -> O(1)
    -> dequeue(): head.data 저장, head = head.next, data 반환 (head 제거) -> O(1)
    -> 단일 연결리스트 + tail 포인터만으로 충분 (이중 연결 불필요)
    --> dequeue는 항상 head 방향으로만 이동하지, head의 prev를 알 필요가 없기 때문 (스택의 이중 연결 불필요 이유와 유사한 논리)
    -> head가 null이 되면(큐가 빔) tail도 null로 처리해야 함 (그렇지 않으면 tail이 이미 삭제된 노드를 계속 참조 -> 다음 enqueue 시 유령 연결 발생)
    -> pop 시 참조가 끊긴 노드는 자동으로 GC 대상 (배열 기반의 Loitering Object 문제 없음)
    -> 공간복잡도: O(n), 필요한 만큼만 노드 생성, 대신 노드마다 next 참조 오버헤드 존재

  - 배열(원형)/연결리스트 선택
    - 배열(원형 큐)
      -> 데이터 크기가 대략 예측 가능할 때
      -> 성능 최적화(연속 메모리로 인한 CPU 캐시 친화적)
      -> 메모리 효율(포인터 오버헤드 없음)
      -> 단, resize 시점에 O(n) 스파이크 비용 존재 (amortized로는 O(1)이지만 순간적으로는 무거움)
    - 연결리스트
      -> 데이터 크기 예측이 어려울 때
      -> enqueue/dequeue가 빈번하고 크기 변화가 클 때
      -> 항상 균일한 O(1) 보장 (배열처럼 resize로 인한 순간적 스파이크 없음)
      -> 재할당 없이 필요한 만큼 동적 확장 가능, 대신 노드당 포인터 메모리 오버헤드 + 캐시 미스 가능성
*/

public class _4_Queue {

  // 고정 크기 배열을 사용한 선형 큐 단순 구현
  // rear가 배열 끝 까지 도달하면 front에 공간이 남아있어도 enqueue를 수행하지 못하는 한계
  // -> 메모리 낭비
  static class FixedArrayBasedLinearQueue<E> {

    private final Object[] queue;
    private int front = 0;
    private int rear = 0;
    private int size = 0;
    private final int capacity;

    public FixedArrayBasedLinearQueue() {
      this.capacity = 10;
      this.queue = new Object[capacity];
    }

    public FixedArrayBasedLinearQueue(int capacity) {
      if (capacity <= 0) {throw new IllegalArgumentException("잘못된 용량 초기화 capacity: " + capacity);}
      this.capacity = capacity;
      this.queue = new Object[capacity];
    }

    public void enqueue(E e) {
      checkEnqueueable();

      queue[rear] = e;
      rear++;
      size++;
    }

    @SuppressWarnings("unchecked")
    public E dequeue() {
      checkQueueSize();
      E value = (E) queue[front];
      queue[front] = null;

      size--;
      front++;

      return value;
    }

    @SuppressWarnings("unchecked")
    public E peek() {
      checkQueueSize();
      return (E) queue[front];
    }

    public boolean isFull() {return size == capacity;}

    private void checkEnqueueable() {
      if (isFull()) {throw new IllegalStateException("큐가 가득 참");}

      if (rear == capacity) {
        throw new IllegalStateException("마지막 요소가 큐의 끝에 도달함");
      }
    }

    private void checkQueueSize() {
      if (size == 0) {throw new NoSuchElementException("Empty Queue");}
    }
  }

  // 고정 배열 기반에서의 메모리 낭비 한계 개선을 위해 front ~ rear를 relocation
  // 고정 배열 -> 동적 배열 도입
  static class DynamicArrayBasedLinearQueue<E> {

    private Object[] queue;
    private int front = 0;
    private int rear = 0;
    private int size = 0;
    private int capacity;

    public DynamicArrayBasedLinearQueue() {
      this.capacity = 10;
      this.queue = new Object[capacity];
    }

    public DynamicArrayBasedLinearQueue(int capacity) {
      if (capacity <= 0) {throw new IllegalArgumentException("잘못된 용량 초기화 capacity: " + capacity);}
      this.capacity = capacity;
      this.queue = new Object[capacity];
    }

    public void enqueue(E e) {
      doRelocationOrResizingIfNeeded();

      queue[rear] = e;
      rear++;
      size++;
    }

    @SuppressWarnings("unchecked")
    public E dequeue() {
      checkQueueSize();
      E value = (E) queue[front];
      queue[front] = null;

      size--;
      front++;

      return value;
    }

    @SuppressWarnings("unchecked")
    public E peek() {
      checkQueueSize();
      return (E) queue[front];
    }

    public boolean isFull() {return size == capacity;}

    private void doRelocationOrResizingIfNeeded() {
      if ((rear == capacity && front > 0)) {
        relocation();
      } else if (isFull()) {
        resizing();
      }
    }

    private void resizing() {
      capacity *= 2;
      queue = Arrays.copyOf(queue, capacity);
    }

    private void relocation() {
      System.arraycopy(queue, front, queue, 0, size);
      front = 0;
      rear = size;
    }

    private void checkQueueSize() {
      if (size == 0) {throw new NoSuchElementException("Empty Queue");}
    }
  }

  // 동적 배열 + 환형 큐, 큐가 꽉 찬 경우 이전과 같이 resizing
  // front 혹은 rear가 배열 끝에 도달 및 동시에 큐 공간에 여유가 있는 경우
  // -> 각 인덱스를 (index % capacity) 하여 인덱스를 이동시킴
  static class DynamicArrayBasedCircularQueue<E> {

    private Object[] queue;
    private int front = 0, rear = 0, size = 0, capacity;

    public DynamicArrayBasedCircularQueue() {
      this.capacity = 10;
      this.queue = new Object[capacity];
    }

    public DynamicArrayBasedCircularQueue(int capacity) {
      if (capacity <= 0) {throw new IllegalArgumentException("잘못된 용량 초기화 capacity: " + capacity);}
      this.capacity = capacity;
      this.queue = new Object[capacity];
    }

    public void enqueue(E e) {
      doResizingIfNeeded();

      queue[rear] = e;

      rear++;
      size++;

      if (rear == capacity) {rear = rotateIndex(rear);}
    }

    @SuppressWarnings("unchecked")
    public E dequeue() {
      checkQueueSize();
      E value = (E) queue[front];
      queue[front] = null;

      size--;
      front++;

      if (front == capacity) {front = rotateIndex(front);}

      return value;
    }

    @SuppressWarnings("unchecked")
    public E peek() {
      checkQueueSize();
      return (E) queue[front];
    }

    public boolean isFull() {return size == capacity;}

    public int size() {return size;}

    public int getCapacity() {return capacity;}

    private int rotateIndex(int value) {return value % capacity;}

    private void doResizingIfNeeded() {
      if (isFull()) {
        int prevCapacity = capacity;
        capacity *= 2;
        Object[] newQueue = new Object[capacity];

        for (int i = 0; i < size; i++) {
          newQueue[i] = queue[(front + i) % prevCapacity];
        }

        queue = newQueue;
        front = 0;
        rear = size;
      }
    }

    private void checkQueueSize() {
      if (size == 0) {throw new NoSuchElementException("Empty Queue");}
    }
  }

  // 연결리스트 기반 큐 구현
  // head 와 tail 을 사용한 간단한 구현
  static class LinkedListBasedQueue<E> {

    private Node<E> head;
    private Node<E> tail;
    private int size;

    static class Node<E> {

      private Node<E> next;
      private E data;

      public Node(E data) {
        this.data = data;
        this.next = null;
      }
    }

    public void enqueue(E e) {
      Node<E> newNode = new Node<>(e);

      if (head == null) {
        head = newNode;
      } else {
        tail.next = newNode;
      }

      tail = newNode;
      size++;
    }

    public E dequeue() {
      checkQueueSize();

      Node<E> output = head;
      head = head.next;
      size--;

      // 큐가 비었을 경우 tail 처리
      if (head == null) {tail = null;}

      return output.data;
    }

    public E peek() {
      checkQueueSize();
      return head.data;
    }

    public int size() {return size;}

    private void checkQueueSize() {
      if (size == 0) {throw new NoSuchElementException("Empty Queue");}
    }
  }
}
