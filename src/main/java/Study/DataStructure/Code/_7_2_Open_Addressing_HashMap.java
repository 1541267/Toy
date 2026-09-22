package Study.DataStructure.Code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/*
- Open Addressing (개방 주소법): 모든 원소를 배열(테이블) 안에 직접 저장, 충돌 시 다른 빈 슬롯을 탐색(probing)해서 저장
  - Chaining과의 근본적 차이
    -> Chaining: 버킷 하나에 연결리스트/트리로 여러 원소를 매담 (포인터 오버헤드 + 메모리 흩어짐)
    -> Open Addressing: 슬롯 하나 = 원소 하나, 연결 구조 없이 배열 하나로만 구성 -> 캐시 지역성(locality) 우수
    -> 단, load factor가 1을 절대 못 넘음 (배열 칸 수만큼만 저장 가능) -> Chaining보다 훨씬 이른 시점에 resize 필요

  - 슬롯 상태 3단계 관리가 핵심 설계 포인트
    -> EMPTY: 한 번도 채워진 적 없음 / OCCUPIED: 현재 값이 있음 / DELETED(tombstone): 삭제되었지만 지나가야 하는 자리
    -> key[], value[], states[] 배열을 분리해서 관리 (SlotState enum으로 상태 표현)
    -> EMPTY만이 탐색을 멈추는 신호, DELETED는 멈추지 않고 계속 지나가야 함

  - Probing 전략 3가지 index = (hash(key) + f(i)) % capacity, i = 0,1,2,...
    -> Linear Probing: (f(i) = i), 바로 다음 칸부터 순차 탐색, 구현 단순 + 캐시 친화적
       --> 1차 클러스터링(Primary Clustering): 한 번 몇 칸이 연달아 채워지면 그 뒤로 충돌 확률이 계속 증가, 특정 구간에 원소 뭉침
    -> Quadratic Probing: (f(i) = i²), 1, 4, 9, 16칸씩 건너뛰며 탐색
       --> 1차 클러스터링은 완화되지만 2차 클러스터링(Secondary Clustering): 최초 hash값이 같은 키들은 여전히 동일한 probe 순서를 그대로 따라감
    -> Double Hashing: f(i) = i * hash2(key), 건너뛰는 간격 자체를 키마다 다르게 만듦 -> 1차/2차 클러스터링 모두 크게 완화(이론상 가장 이상적)
       --> hash2(key) 결과가 절대 0이 되면 안 됨(무한루프 방지) -> PRIME - (hashCode % PRIME) 형태로 항상 1~PRIME 범위 보장

  - capacity가 2의 거듭제곱일 때 Quadratic/Double Hashing이 전체 슬롯을 못 도는 문제 (실제 리뷰에서 발견된 함정)
    -> Quadratic: i²와 (capacity-i)²가 mod capacity 기준 항상 같은 값 -> 커버리지가 최대 capacity/4 수준으로 줄어듦
    -> Double Hashing: i*hash2 % capacity가 만들어내는 서로 다른 값의 개수 = capacity / GCD(hash2, capacity)
       --> hash2가 짝수면(꽤 흔함) GCD≥2 -> 일부 슬롯은 영원히 후보로 안 나옴, 최악은 hash2가 capacity의 배수면 단 1칸만 순환
    -> 해결책: capacity를 소수(prime)로 유지 -> 소수의 정의상 1~capacity-1의 어떤 정수와도 GCD=1이 자동 보장
       --> resize 시 2배보다 큰 첫 소수(nextPrime)로 capacity 결정, 생성자도 초기 capacity를 소수로 보정
       --> 주의: capacity를 먼저 소수로 확정한 뒤에 그 크기로 배열을 생성해야 함 (순서 바뀌면 배열 길이와 capacity 필드가 어긋나 인덱스 예외 발생)
    -> (대안) capacity를 2의 거듭제곱으로 유지하고 싶다면: Quadratic은 (i²+i)/2 삼각수 공식, Double Hashing은 hash2를 강제로 홀수화(| 1)

  - 삭제(delete)가 Chaining보다 까다로운 이유 -> Tombstone(삭제 표시)
    -> 그냥 슬롯을 EMPTY로 비워버리면: 그 뒤 probing으로 들어가 있던 다른 키가 탐색 시 조기에 EMPTY를 만나 탐색이 멈춰버려 존재하는데 못 찾는 상황 발생
    -> 해결: 삭제 시 EMPTY가 아닌 DELETED로만 표시 (물리적 삭제 아님)
       --> search(get)는 DELETED를 만나도 계속 탐색, EMPTY를 만나야 진짜 탐색 종료
       --> insert(put)는 DELETED 슬롯도 재사용 가능한 빈 자리로 취급 (단, 순회 중 처음 만난 DELETED 위치를 기억해뒀다가, 이후 EMPTY를 만나는 시점에 최종적으로 그 자리에 삽입 - 불필요한 탐색 단축)
    -> tombstone이 쌓이면 실제 원소는 적어도 search가 그 자리들을 계속 지나쳐야 해서 성능 저하 -> resize(rehash) 시 tombstone을 전부 제거하고 OCCUPIED만 골라 새 테이블에 재배치하며 청소

  - size 필드의 의미를 명확히 정의해야 함 (설계 선택 사항, 헷갈리기 쉬운 지점)
    -> EMPTY가 아닌 슬롯 수(OCCUPIED+DELETED) 기준으로 채택 시
       --> delete()에서 size를 감소시키면 안 됨 (OCCUPIED->DELETED는 여전히 EMPTY 아닌 슬롯이므로 유지)
       --> tombstone도 probe 체인 길이를 그대로 늘리므로, 실질적으로 못 쓰는 칸"이라는 관점에서 load factor 계산에 포함시키는 게 합리적
       --> 단, resize()에서 tombstone을 청소하고 나면 size도 그만큼 줄어야 하므로, 재배치 완료 후 실제로 새 테이블에 들어간 OCCUPIED 개수로 size를 재설정 필요 (size=0으로 리셋 후 재배치하며 다시 카운트)
    -> load factor 임계값은 Chaining보다 낮게(예: 0.5) 설정 -> 배열이 다 차면(load factor=1) insert 자체가 불가능해지는 것을 여유 있게 방지

  - 평균/최악 시간복잡도
    -> put/get/remove 평균: O(1) (load factor를 임계값 이하로 유지 시)
    -> 최악: O(n) 또는 O(capacity) - 클러스터링이 심하거나(Linear), probe 커버리지 문제(capacity가 2의 거듭제곱 + Quadratic/Double Hashing)로 인해 발생 가능

  - 실무 활용
    -> Python dict, C++ unordered_map 일부 구현, Rust HashMap 등 (Java HashMap은 Chaining 기반과 대비됨)
    -> 캐시 성능이 중요하고 삽입/삭제보다 탐색이 빈번한 경우에 유리
*/

public class _7_2_Open_Addressing_HashMap {

  enum SlotState {EMPTY, OCCUPIED, DELETED}

  enum Logic {LINEAR_PROBING, QUADRATIC_PROBING, DOUBLE_HASHING}

  static class OpenAddressingHashMap<K, V> {

    private K[] key;
    private V[] value;
    private SlotState[] states;
    private int size;             // OCCUPIED만 or tombstone(DELETED)까지 포함해서 셀지 구분 필요
    private int capacity;
    private final static double LOAD_FACTOR = 0.7;
    private int PRIME;

    private final Logic logic;

    @SuppressWarnings("unchecked")
    public OpenAddressingHashMap(Logic logic) {
      if (logic == null) {throw new IllegalArgumentException("Open Addressing Logic이 null");}
      this.capacity = nextPrime(capacity);
      this.key = (K[]) new Object[capacity];
      this.value = (V[]) new Object[capacity];
      this.states = new SlotState[capacity];
      this.size = 0;
      this.logic = logic;

      initStates();
      initPrime();
    }

    @SuppressWarnings("unchecked")
    public OpenAddressingHashMap(Logic logic, int capacity) {
      if (capacity <= 0) {throw new IllegalArgumentException("Capacity 초기화 값이 0 이하");}
      if (logic == null) {throw new IllegalArgumentException("Open Addressing Logic이 null");}
      this.capacity = nextPrime(capacity);
      this.key = (K[]) new Object[capacity];
      this.value = (V[]) new Object[capacity];
      this.states = new SlotState[capacity];
      this.size = 0;
      this.logic = logic;

      initStates();
      initPrime();
    }

    private void initStates() {
      Arrays.fill(states, SlotState.EMPTY);
    }

    public void put(K incomingKey, V incomingValue) {

      int hashIdx = firstHashing(incomingKey);
      int firstDeletedIdx = -1;

      boolean isBreakPoint = false;
      boolean isNeedSizeUp = false;

      int curIdx = hashIdx;
      for (int i = 0; i < capacity; i++) {
        SlotState curSlotState = states[curIdx];

        if (curSlotState.equals(SlotState.EMPTY)) {
          // 빈 슬롯 발견
          // 이전에 삭제된 슬롯이 발견 됐었다면 삭제된 슬롯에 덮어쓰기
          // 없으면 빈 칸에 삽입
          int insertIdx = firstDeletedIdx != -1 ? firstDeletedIdx : curIdx;

          isNeedSizeUp = insertToEmptyOrDeleteSlot(incomingKey, incomingValue, insertIdx);

          isBreakPoint = true;
        } else if (curSlotState.equals(SlotState.DELETED)) {
          // 삭제 되어있는 슬롯 발견
          // 이전에 발견 된 적이 있으면 스킵
          // 없으면 인덱스 기억 후 이후 EMPTY 슬롯 발견 시 기억해둔 DELETED 슬롯에 삽입
          if (firstDeletedIdx == -1) {
            firstDeletedIdx = curIdx;
          }
        } else {
          // 두 슬롯을 제외한 이미, 존재하는 슬롯 발견
          // 키값 비교 후 같으면 값 갱신 & 종료, 다르면 계속 진행
          isBreakPoint = checkAndInsertOccupiedSlot(incomingKey, incomingValue, curIdx);
        }

        if (isBreakPoint) {break;}

        // 다음 방문 인덱스 계산
        curIdx = findSlot(hashIdx, i + 1, Logic.DOUBLE_HASHING.equals(logic) ? incomingKey : null);
      }

      // capacity 전체를 탐색했는데 EMPTY를 만나지 못한 경우
      // firstDeletedIdx가 있으면 재사용
      if (!isBreakPoint && firstDeletedIdx != -1) {
        isNeedSizeUp = insertToEmptyOrDeleteSlot(incomingKey, incomingValue, firstDeletedIdx);
      }

      if (isNeedSizeUp) {
        size++;
        resize();
      }
    }

    public V get(K incomingKey) {
      int startHashIdx = firstHashing(incomingKey);
      int nextIdx = startHashIdx;
      for (int i = 0; i < capacity; i++) {
        SlotState curSlotState = states[nextIdx];

        // ex) 10: A, 11: DELETED, 12: EMPTY, 13: B
        // 이 상태에서 B를 찾으려고 12부터 탐색해도 12가 EMPTY라면 13에 B 존재 불가
        if (curSlotState.equals(SlotState.EMPTY)) {break;}

        if (curSlotState.equals(SlotState.OCCUPIED) && key[nextIdx].equals(incomingKey)) {
          return value[nextIdx];
        }

        nextIdx = findSlot(startHashIdx, i + 1, incomingKey);
      }
      return null;
    }

    public V remove(K incomingKey) {
      int startHashIdx = firstHashing(incomingKey);
      int nextIdx = startHashIdx;

      for (int i = 0; i < capacity; i++) {
        SlotState curSlotState = states[nextIdx];

        if (curSlotState.equals(SlotState.EMPTY)) {
          break;
        }

        if (curSlotState.equals(SlotState.OCCUPIED) && key[nextIdx].equals(incomingKey)) {
          V removedValue = value[nextIdx];

          delete(nextIdx);
          return removedValue;
        }

        nextIdx = findSlot(startHashIdx, i + 1, incomingKey);
      }

      return null;
    }

    private void delete(int hashIdx) {
      states[hashIdx] = SlotState.DELETED;
      // DELETE도 size에 포함 되도록 put()을 구현했기 때문에
      // size--;
    }

    private boolean insertToEmptyOrDeleteSlot(K incomingKey, V incomingValue, int hashIdx) {
      boolean isNeedSizeUp = states[hashIdx].equals(SlotState.EMPTY);

      states[hashIdx] = SlotState.OCCUPIED;
      key[hashIdx] = incomingKey;
      value[hashIdx] = incomingValue;

      return isNeedSizeUp;
    }

    private boolean checkAndInsertOccupiedSlot(K incomingKey, V incomingValue, int hashIdx) {
      K existKey = key[hashIdx];

      if (existKey.equals(incomingKey)) {
        value[hashIdx] = incomingValue;
        return true;
      }

      return false;
    }


    private int findSlot(int startIdx, int idx, K incomingKey) {
      int nextIdx = 0;

      switch (logic) {
        case LINEAR_PROBING -> {
          nextIdx = startIdx + idx;
        }
        case QUADRATIC_PROBING -> {
          nextIdx = startIdx + (idx * idx);
        }
        case DOUBLE_HASHING -> {
          nextIdx = startIdx + (idx * secondHashing(incomingKey));
        }
      }

      return Math.floorMod(nextIdx, capacity);
    }

    private int firstHashing(K incomingKey) {
      if (incomingKey == null) {
        throw new NullPointerException("FirstHashing: 해싱 할 key가 null");
      }
      return Math.floorMod(incomingKey.hashCode(), capacity);
    }

    private int secondHashing(K incomingKey) {
      if (incomingKey == null) {
        throw new NullPointerException("SecondHashing: 해싱 할 key가 null");
      }

      return PRIME - Math.floorMod(incomingKey.hashCode(), PRIME);
    }

    @SuppressWarnings("unchecked")
    private void resize() {
      if (Double.compare(((double) size / capacity), LOAD_FACTOR) > 0) {

        int oldCapacity = capacity;

        capacity = nextPrime(capacity * 2);

        K[] oldKey = key;
        V[] oldValue = value;
        SlotState[] oldStates = states;

        key = (K[]) new Object[capacity];
        value = (V[]) new Object[capacity];
        states = new SlotState[capacity];

        initStates();
        initPrime();

        size = 0;

        // 기존 값들 재해싱 & 재배치
        for (int i = 0; i < oldCapacity; i++) {
          if (oldStates[i].equals(SlotState.OCCUPIED)) {
            int hashIdx = firstHashing(oldKey[i]);
            boolean inserted = false;

            for (int j = 0; j < capacity; j++) {
              int curIdx;

              if (j == 0) {
                curIdx = hashIdx;
              } else {
                curIdx = findSlot(
                    hashIdx,
                    j,
                    Logic.DOUBLE_HASHING.equals(logic)
                        ? oldKey[i] : null
                );
              }

              if (states[curIdx].equals(SlotState.EMPTY)) {
                states[curIdx] = SlotState.OCCUPIED;
                key[curIdx] = oldKey[i];
                value[curIdx] = oldValue[i];
                size++;
                inserted = true;
                break;
              }
            }

            if (!inserted) {
              throw new IllegalStateException("Refresh 중 삽입할 슬롯을 찾지 못함");
            }
          }
        }
      }
    }

    private int nextPrime(int num) {
      int candidate = num;
      while (!isPrime(candidate)) {
        candidate++;
      }
      return candidate;
    }

    private void initPrime() {
      // capacity 변화로 인한 secondHash 계산용 PRIME 갱신
      for (int i = capacity - 1; i >= 2; i--) {
        if (isPrime(i)) {
          this.PRIME = i;
          break;
        }
      }
    }

    private boolean isPrime(int num) {
      if (num < 2) {return false;}

      for (int i = 2; i <= num / i; i++) {
        if (num % i == 0) {return false;}
      }

      return true;
    }

    public void print() {
      System.out.println("==========================================================");
      System.out.println("Logic: " + logic);
      for (int i = 0; i < capacity; i++) {
        System.out.println("I: " + i + ", State: " + states[i] + ", [" + key[i] + ", " + value[i] + "]");
      }
    }
  }

  public static void main(String[] args) throws IOException {

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    System.out.println("로직 선택\n1: LINEAR_PROBING\n2: QUADRATIC_PROBING\n3: DOUBLE_HASHING");

    Logic logic = switch (Integer.parseInt(br.readLine())) {
      case 1 -> Logic.LINEAR_PROBING;
      case 2 -> Logic.QUADRATIC_PROBING;
      case 3 -> Logic.DOUBLE_HASHING;
      default -> null;
    };

    OpenAddressingHashMap<Integer, String> map = new OpenAddressingHashMap<>(logic);

    System.out.println("==========================================================");
    System.out.println("선택된 로직: " + logic);
    System.out.println("==========================================================");
    map.put(10, "A");
    map.print();
    // 충돌 발생
    map.put(26, "B");
    map.print();
    // 같은 key, 값 갱신
    map.put(26, "B-UPDATE");
    map.print();
    // 삭제된 슬롯이 있는 경우
    map.put(42, "C");
    map.remove(42);
    map.print();

    System.out.println("map.get(26) = " + map.get(26));
  }

}
