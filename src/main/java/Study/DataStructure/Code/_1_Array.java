package Study.DataStructure.Code;

import java.util.Arrays;

/*
  - 동적 배열: 고정 크기 배열의 한계(크기 변경 불가)를 극복하기 위한 자료구조
    ->내부적으론 일반 배열(Object[])을 사용, 공간이 부족해지면 더 큰 배열을 새로 만들고 기존 데이터를 복사하는 방식으로 동적 확장을 흉내

  - capacity(배열의 실제 크기) 와 size(실제 요소가 담긴 개수)를 반드시 구분해야함

  - add(i, e): i번째부터 뒤의 원소들을 한 칸 씩 뒤로 이동
   -> 값을 유실하지 않기 위해 오른쪽 끝 인덱스부터 밀어냄

  - set(i, e): O(1) - 인덱스를 통해 바로 값 교체

  - remove(i): i + 1 번째부터 앞으로 한 칸씩 이동, 반대로 진행해도 안전

  - 장점: 인덱스 접근 O(1), 메모리가 연속적이라 캐시 지역성(Cache Locality)이 좋음
  - 단점: 중간 삽입/삭제 시 나머지 원소를 밀어야 해서 O(n), capacity를 초과하면 전체 복사 O(n) 가 발생
    -> 하지만 capacity 초과는 자주 일어나진 않으므로 여러번의 add(e)에 대한 전체 비용을 상각 분석(Amortized analysis)하면 O(1)

  - Capacity 확장
    -> 2배: 확장 횟수가 적어 데이터 복사 비용이 작으나 현재 크기에 비해 여유 메모리가 많이 낭비
    -> 1.5배: 2배 확장보다 메모리 여유가 있으나 capacity가 더 빨리 소진되어 확장이 자주 발생되는 만큼 기존 데이터 복사 비용이 증가

  - 시간복잡도
    -> get(i): O(1)
    -> add(e) 맨 뒤 추가: Amortized(분할 상환)시 O(1), 크기 확장시에만 O(n)
    -> add(i, e) 중간 삽입: O(n) -> 뒤로 밀어야 하는 원소 수에 비례
    -> remove(i): O(n) - 앞으로 당겨야 하는 원소 수에 비례

  - Object[] 배열을 쓰고 제네릭 배열(E[[])를 만들지 않는 이유
    -> 제네릭은 컴파일 시점에만 타입체크, 바이트코드에선 타입 정보가 지워짐
    -> 런타임엔 E를 타입추론을 하지 못해 컴파일이 안됨, Object[]로 저장해두고 (E)로 캐스팅해서 get
    -> 실제 ArrayList<E>도 내부적으로 이 방식을 사용
*/

public class _1_Array {

  // 일반 버전
  static class InnerArr {

    private Object[] arr;
    private int size = 0;

    public InnerArr() {
      this.arr = new Object[10];
    }

    public InnerArr(int capacity) {
      this.arr = new Object[capacity];
    }

    public void add(Object obj) {
      growIfNeeded();
      arr[size++] = obj;
    }

    public void add(int i, Object obj) {
      checkInsertBounds(i);
      growIfNeeded();

      for (int j = size; j > i; j--) {arr[j] = arr[j - 1];}

      arr[i] = obj;
      size++;
    }

    public Object get(int i) {
      checkElementBounds(i);
      return arr[i];
    }

    public void set(int i, Object obj) {
      checkElementBounds(i);
      arr[i] = obj;
    }

    public void remove(int i) {
      checkElementBounds(i);

      for (int j = i; j < size - 1; j++) {
        arr[j] = arr[j + 1];
      }
      arr[size - 1] = null;
      size--;
    }

    public int size() {
      return size;
    }

    public int capacity() {
      return arr.length;
    }

    private void growIfNeeded() {
      if (size == arr.length) {
        int newCapacity = (arr.length * 2) + 1;
        arr = Arrays.copyOf(arr, newCapacity);
      }
    }

    private void checkInsertBounds(int i) {
      if (i < 0 || i > size) {
        throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + size);
      }
    }

    private void checkElementBounds(int i) {
      if (i < 0 || i >= size) {
        throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + size);
      }
    }
  }

  // 제네릭 버전
  static class GenericArray<E> {

    private Object[] arr;
    private int size = 0;

    public GenericArray() {this.arr = new Object[10];}

    public GenericArray(int capacity) {this.arr = new Object[capacity];}

    public void add(E obj) {
      growIfNeeded();
      arr[size++] = obj;
    }

    public void add(int i, E obj) {
      checkInsertBounds(i);
      growIfNeeded();
      for (int j = size; j > i; j--) {arr[j] = arr[j - 1];}
      arr[i] = obj;
      size++;
    }

    public void set(int i, E obj) {
      checkElementBounds(i);
      arr[i] = obj;
    }

    @SuppressWarnings("unchecked")
    public E get(int i) {
      checkElementBounds(i);
      // Object[]로 저장했으니 꺼낼 때 캐스팅 필요
      return (E) arr[i];
    }

    public void remove(int i) {
      checkElementBounds(i);

      for (int j = i; j < size - 1; j++) {
        arr[j] = arr[j + 1];
      }
      arr[size - 1] = null;
      size--;
    }

    public int size() {return size;}

    public int capacity() {return arr.length;}

    private void checkInsertBounds(int i) {
      if (i < 0 || i > size) {
        throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + size);
      }
    }

    private void checkElementBounds(int i) {
      if (i < 0 || i >= size) {
        throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + size);
      }
    }

    private void growIfNeeded() {
      if (size == arr.length) {
        int newCapacity = (arr.length * 2) + 1;
        arr = Arrays.copyOf(arr, newCapacity);
      }
    }
  }
}
