package Study.DataStructure.Code;

/*
- 해시테이블 (Hash Table): key를 인덱스로 직접 변환(계산)하여 O(1) 평균 접근을 달성하는 구조, 연결리스트 사용
  - 핵심 아이디어: BST가 비교하며 찾아가는 방식(O(log n))이라면, 해시테이블은 계산해서 바로 도착하는 방식(O(1) 평균)
  - 연산: put(k,v) & get(k) & remove(k) & containsKey(k) -> 평균 O(1), 최악 O(n) (모든 키가 한 버킷에 몰릴 때)
  - Queue로도 구현이 가능 하나 queue는 enqueue(), dequeue() 사용해 HashMap의 특정 key를 사용한 put(), remove()와 맞지 않음

  - 해시 함수 (Hash Function)
    -> index = hash(key) % capacity 형태로 key를 버킷 인덱스로 변환
    -> 좋은 해시 함수 조건: 결정적(같은 key -> 같은 해시값), 균등 분포(충돌 최소화), O(1) 계산
    -> key가 null이면 hashCode() 호출 자체가 NPE
    -> key.hashCode()는 음수가 나올 수 있음 -> 일반 % 연산자는 음수를 그대로 반환하므로 배열 인덱스로 사용 불가
    --> Math.floorMod(key.hashCode(), capacity) 사용: 두 번째 인자가 양수면 결과가 항상 0 ~ capacity 범위 보장

  - 충돌(Collision): 비둘기집 원리에 의해 서로 다른 key가 같은 인덱스로 매핑되는 상황은 필연적으로 발생
    -> 충돌 해결 전략이 해시테이블 구현의 핵심 설계 포인트

  - 체이닝 (Separate Chaining) - 현재 구현 방식
    -> 각 버킷에 연결리스트(Entry 체인)를 두고, 충돌한 원소들을 그 체인에 이어붙임
    -> 버킷 배열은 동적 배열처럼 resize 대상, 각 버킷 내부는 단일 연결리스트 구조(Entry.next)
    -> put(): 버킷이 비어있으면 새 Entry 삽입 / 비어있지 않으면 체인 순회하며 동일 key 존재 여부 확인
      --> 동일 key 존재 시 value만 교체 (덮어쓰기), 없으면 체인 끝에 새 Entry 연결
      --> key 비교는 ==이 아닌 .equals() 사용 (참조 동일성이 아닌 논리적 동일성 비교가 목적)
    -> get()/containsKey(): 버킷 접근 후 체인을 순회하며 key.equals() 비교
    -> remove(): 체인에서 대상 Entry를 찾아 이전 노드(prevEntry)의 next를 대상의 next로 연결(링크 스킵)
      --> 대상이 체인의 첫 노드(prevEntry == null)인 경우 bucket[idx] 자체를 갱신해야 함 (연결리스트 head 삭제와 동일 로직)
    -> 장점: 구현이 직관적, load factor가 높아져도 성능 저하가 완만함
    -> 단점: 포인터(next) 오버헤드, 체인이 길어지면 캐시 비친화적(연결리스트 특성)

  - Load Factor & Resize (재해싱)
    -> load factor = size / capacity, 임계치(LOAD_FACTOR = 0.75) 이상이면 resize 수행
    -> capacity가 바뀌면 hash(key) % capacity 결과 자체가 달라지므로, 기존 모든 Entry를 새 배열에 재삽입(재해싱) 필요
    -> put()으로 size가 늘어날 때만 체크
    -> 비용: resize 발생 시 O(n), 발생 빈도가 점점 줄어들어 amortized O(1) (동적 배열 resize와 동일한 근거)
    -> load factor를 너무 높게 두면 충돌 증가로 성능 저하, 너무 낮게 두면 메모리 낭비 -> 트레이드오프

  - 개방 주소법 (Open Addressing) - 체이닝의 대안
    -> 충돌 시 배열 내 다른 빈 슬롯을 찾아 저장 (버킷 하나에 하나의 원소만, 연결리스트 없음)
    -> Linear Probing: 충돌 시 index+1, index+2... 순차 탐색 -> 구현 쉽지만 군집화(clustering) 문제
    -> Quadratic Probing: index + 1², index + 2²... -> 군집화 완화
    -> Double Hashing: 두 번째 해시함수로 이동 폭 결정 -> 분포 가장 균등
    -> 장점: 포인터 없이 배열만 사용 -> 캐시 친화적, 메모리 효율적
    -> 단점: load factor 높아지면 성능 급격히 저하, 삭제 시 tombstone(삭제 표시) 필요
      --> 단순히 null로 비우면 이후 probing 체인이 끊겨서 실제로 존재하는 키도 탐색 실패 가능

  - 체이닝 / 개방주소법 선택 기준
    - 체이닝: 삽입/삭제가 빈번, load factor가 높아질 가능성이 있는 경우, 구현 단순성 중시
    - 개방주소법: 메모리 효율/캐시 성능이 중요, load factor를 낮게 유지 가능한 경우

  - 체이닝(Chaining) 삽입 위치: Head vs Tail(구현된 코드)
    -> Head 삽입: 새 Entry를 버킷의 맨 앞에 꽂고, 기존 체인 전체를 그 뒤에 연결
      --> newEntry.next = bucket[idx]; bucket[idx] = newEntry;
      --> 삽입 자체는 순회 없이 O(1) (단, 이미 있는 key인지 확인하는 탐색은 별도로 필요 -> 그 부분은 여전히 체인 길이만큼 순회)
      --> 체인 내 순서가 삽입 역순이 됨(최근 삽입이 항상 맨 앞)
    -> Tail 삽입 (현재 구현 방식): 체인 끝(next == null)까지 순회 후 새 Entry 연결
      --> 삽입 위치를 찾기 위해 O(체인 길이) 순회 필요
      --> 체인 내 순서가 삽입 순서 그대로 유지됨

    -> 실제 Java HashMap의 선택: Java 7까지는 head 삽입 -> Java 8부터 tail 삽입으로 변경
      --> 이유: head 삽입 방식은 멀티스레드 환경에서 resize(재해싱) 도중 동시 접근 시 체인에 순환 참조(cycle)가 생겨 무한루프에 빠지는 유명한 버그가 있었음
      --> tail 삽입은 이 문제를 원천 차단 + 삽입 순서 유지라는 부가 이점도 있음
      --> 단일 스레드 기준 순수 성능만 보면 head가 유리하지만 실무 HashMap은 안전성/예측가능성을 우선시해 tail을 선택
    -> Java 8의 실질적 성능 개선은 head/tail이 아니라 트리화(Treeify)에서 옴
      --> 한 버킷의 체인 길이가 8개 이상이 되면 연결리스트 -> 레드-블랙 트리로 변환 (O(n) -> O(log n))
      --> 원소 수가 6개 이하로 줄어들면 다시 연결리스트로 되돌림(untreeify)

  - 실무 연결
    -> Java HashMap: 체이닝 기반, load factor 기본값 0.75
    -> JDK 8+: 한 버킷의 체인이 임계 길이(8개) 이상으로 길어지면 연결리스트 -> 레드-블랙 트리로 변환 (O(n) -> O(log n) 방어, 해시 공격(hash flooding) 대비)
    -> HashSet은 내부적으로 HashMap<E, Object>를 감싸 구현 (value 자리를 더미 값으로 고정)
*/

public class _7_1_Chaining_Based_HashMap {

  static class ChainingBasedHashMap<K, V> {

    private Entry<K, V>[] bucket;
    private int size;
    private int capacity;
    private static final double LOAD_FACTOR = 0.75;

    static class Entry<K, V> {

      private K key;
      private V value;
      private Entry<K, V> next;

      public Entry(K key, V value) {
        this.key = key;
        this.value = value;
        this.next = null;
      }
    }

    @SuppressWarnings("unchecked")
    public ChainingBasedHashMap() {
      this.capacity = 16;
      this.size = 0;

      // 동일, 스타일, 가독/명시성 차이
      // this.bucket = new Entry[capacity];
      this.bucket = (Entry<K, V>[]) new Entry[capacity];
    }

    @SuppressWarnings("unchecked")
    public ChainingBasedHashMap(int capacity) {
      if (capacity <= 0) {throw new IllegalArgumentException("생성자 Capacity가 0 이하");}
      this.capacity = capacity;
      this.size = 0;
      this.bucket = (Entry<K, V>[]) new Entry[capacity];
    }

    public void put(K incomingKey, V inComingValue) {

      int hashedIdx = hash(incomingKey);

      Entry<K, V> entry = bucket[hashedIdx];

      // 추가가 되었을 때만 resize() 할 플래그
      boolean isAdded = false;

      if (entry == null) {
        bucket[hashedIdx] = new Entry<>(incomingKey, inComingValue);
        isAdded = true;
      } else {

        while (true) {
          if (entry.key.equals(incomingKey)) {
            entry.value = inComingValue;
            break;
          }

          if (entry.next == null) {
            entry.next = new Entry<>(incomingKey, inComingValue);
            isAdded = true;
            break;
          }
          entry = entry.next;
        }
      }
      if (isAdded) {
        size++;
        resize();
      }
    }

    public V get(K incomingKey) {

      int hashedIdx = hash(incomingKey);

      if (bucket[hashedIdx] == null) {
        return null;
      }

      Entry<K, V> entry = bucket[hashedIdx];

      while (entry != null) {
        if (entry.key.equals(incomingKey)) {
          return entry.value;
        }
        entry = entry.next;
      }

      return null;
    }

    public V remove(K incomingKey) {
      int hashedIdx = hash(incomingKey);

      if (bucket[hashedIdx] == null) {
        return null;
      }

      Entry<K, V> prevEntry = null;
      Entry<K, V> entry = bucket[hashedIdx];

      while (true) {
        if (entry.key.equals(incomingKey)) {
          V value = entry.value;

          // 시작 지점이 아닐 때
          if (prevEntry != null) {
            prevEntry.next = entry.next;
          } else {

            // 시작 지점
            bucket[hashedIdx] = entry.next;
          }
          size--;
          return value;
        }

        prevEntry = entry;
        entry = entry.next;
        if (entry == null) {return null;}
      }
    }

    public boolean containsKey(K incomingKey) {
      int hashedIdx = hash(incomingKey);

      if (bucket[hashedIdx] != null) {
        Entry<K, V> entry = bucket[hashedIdx];

        while (true) {
          if (entry.key.equals(incomingKey)) {
            return true;
          } else {

            if (entry.next == null) {
              break;
            }
            entry = entry.next;
          }
        }
      }
      return false;
    }

    public int size() {return size;}

    // key -> 버킷 인덱스로 변환
    private int hash(K incomingKey) {
      if (incomingKey == null) {
        throw new NullPointerException("해싱 할 key가 null");
      }

      // 나눗셈의 나머지를 반환하는 floorMod
      // hashCode()가 음수를 반환하는 경우가 있어 일반 % 연산자 사용 시 사용불가
      // Math.floorMod() 의 두 번쨰 인자가 양수면 결과는 항상 0 ~ capacity
      return Math.floorMod(incomingKey.hashCode(), capacity);
    }

    // load factor 이상 용량 확장 & 재해싱
    @SuppressWarnings("unchecked")
    private void resize() {

      if (Double.compare((double) size / capacity, LOAD_FACTOR) >= 0) {
        capacity *= 2;

        Entry<K, V>[] newBucket = (Entry<K, V>[]) new Entry[capacity];

        for (Entry<K, V> curEntry : bucket) {

          if (curEntry == null) {continue;}

          while (curEntry != null) {
            K curKey = curEntry.key;

            Entry<K, V> nextEntry = curEntry.next;
            curEntry.next = null;

            int newHashedIdx = hash(curKey);

            if (newBucket[newHashedIdx] == null) {
              newBucket[newHashedIdx] = curEntry;
            } else {
              Entry<K, V> newBucketEntry = newBucket[newHashedIdx];
              while (true) {
                if (newBucketEntry.next == null) {
                  newBucketEntry.next = curEntry;
                  break;
                } else {
                  newBucketEntry = newBucketEntry.next;
                }
              }
            }
            curEntry = nextEntry;
          }
        }
        this.bucket = newBucket;
      }
    }
  }

  public static void main(String[] args) {

    ChainingBasedHashMap<Integer, String> map = new ChainingBasedHashMap<>(4);
    map.put(1, "A");
    map.put(5, "B"); // 충돌, 체인 연결
    map.put(9, "C"); // 충돌, 체인 추가 연결

    System.out.println(map.get(1)); // A
    System.out.println(map.get(5)); // B
    System.out.println(map.get(9)); // C
  }

}
