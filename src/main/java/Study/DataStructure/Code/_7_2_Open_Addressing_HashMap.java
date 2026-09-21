package Study.DataStructure.Code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class _7_2_Open_Addressing_HashMap {

  enum SlotState {EMPTY, OCCUPIED, DELETED}

  enum Logic {LINEAR_PROBING, QUADRATIC_PROBING, DOUBLE_HASHING}

  static class OpenAddressingHashMap<K, V> {

    private K[] key;
    private V[] value;
    private SlotState[] states;
    private int size;             // OCCUPIED만 or tombstone(DELETED)까지 포함해서 셀지 구분 필요
    private int capacity;
    private final static double LOAD_FACTOR = 0.5;
    private int PRIME;

    private final Logic logic;

    @SuppressWarnings("unchecked")
    public OpenAddressingHashMap(Logic logic) {
      if (logic == null) {throw new IllegalArgumentException("Open Addressing Logic이 null");}
      this.capacity = 16;
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
      this.capacity = capacity;
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

        capacity *= 2;

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
