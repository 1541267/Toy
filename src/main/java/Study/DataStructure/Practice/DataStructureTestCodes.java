package Study.DataStructure.Practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// Practice 에서 구현 연습에 사용되는 테스트 코드 
public class DataStructureTestCodes {

  public static void main(String[] args) throws IOException {

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    System.out.println("1: Array, 2: Singly LinkedList, 3: Doubly LinkedList, 4: Stack");

    switch (br.readLine()) {
      case "1":
        System.out.println("""
              public static void main(String[] args) {
                testEmpty();            // 빈 배열
                testDuplicates();       // 중복 값의 indexOf / lastIndexOf
                testExhaustiveSmall();  // 작은 용량·크기에서 모든 위치의 삽입/삭제/수정 (전수 검사)
                testRandom();           // ArrayList 와 비교하는 랜덤 테스트
                testLarge();            // 대량 데이터 + 성능(용량 증가 정책)
                // testZeroCapacity();  // (선택) 용량 0 생성을 허용하도록 구현했다면 주석 해제
                System.out.println("ALL PASSED");
              }

              // ---------- 시나리오 테스트 ----------

              static void testEmpty() {
                for (int cap = 1; cap <= 4; cap++) {
                  DynamicArray<Integer> a = new DynamicArray<>(cap);
                  check(a.getSize() == 0, "empty: size");
                  check(a.indexOf(1) == -1, "empty: indexOf");
                  check(a.lastIndexOf(1) == -1, "empty: lastIndexOf");
                  checkBounds(a, 0, "empty cap=" + cap);
                }
              }

              static void testDuplicates() {
                DynamicArray<Integer> a = new DynamicArray<>(1);
                List<Integer> ref = new ArrayList<>();
                for (int v : new int[]{1, 2, 1, 2, 1, 3}) {
                  a.add(v);
                  ref.add(v);
                }
                verifyAll(a, ref, "dup 초기");

                a.remove(0);
                ref.remove(0);
                verifyAll(a, ref, "dup remove(0)");

                a.set(1, 1);
                ref.set(1, 1);
                verifyAll(a, ref, "dup set(1,1)");

                a.add(0, 1);
                ref.add(0, 1);
                verifyAll(a, ref, "dup add(0,1)");
              }

              // (선택) 용량 0 으로 만들어도 증가가 되어야 함 (0 * 2 = 0 이 되는 증가 버그 확인)
              static void testZeroCapacity() {
                DynamicArray<Integer> a = new DynamicArray<>(0);
                List<Integer> ref = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                  a.add(i + 10);
                  ref.add(i + 10);
                }
                verifyAll(a, ref, "cap=0 add");

                DynamicArray<Integer> b = new DynamicArray<>(0);
                b.add(0, 1);
                check(b.getSize() == 1 && b.get(0) == 1, "cap=0 add(0,v)");
              }

              // cap 1~8, n 0~20 의 모든 조합에서 모든 위치에 대해 연산을 검사한다.
              // (용량이 꽉 찬 상태 / 한 칸 남은 상태 / 여유 있는 상태가 모두 포함됨)
              static void testExhaustiveSmall() {
                for (int cap = 1; cap <= 8; cap++) {
                  for (int n = 0; n <= 20; n++) {
                    String base = "cap=" + cap + ", n=" + n;

                    // 모든 위치에 삽입 (맨 앞, 중간, 맨 뒤)
                    for (int idx = 0; idx <= n; idx++) {
                      List<Integer> ref = new ArrayList<>();
                      DynamicArray<Integer> mine = newFilled(cap, n, ref);
                      mine.add(idx, 99);
                      ref.add(idx, 99);
                      verifyAll(mine, ref, base + " add(" + idx + ",99)");
                      mine.add(98);                 // 삽입 후에도 내부 상태가 정상인지
                      ref.add(98);
                      verifyAll(mine, ref, base + " add(" + idx + ",99) 후 add(98)");
                      mine.add(0, 97);
                      ref.add(0, 97);
                      verifyAll(mine, ref, base + " add(" + idx + ",99) 후 add(0,97)");
                    }

                    // 모든 위치 삭제 / 수정
                    for (int idx = 0; idx < n; idx++) {
                      List<Integer> ref = new ArrayList<>();
                      DynamicArray<Integer> mine = newFilled(cap, n, ref);
                      mine.remove(idx);
                      ref.remove(idx);
                      verifyAll(mine, ref, base + " remove(" + idx + ")");
                      mine.add(97);                 // 삭제 후 size 가 올바르게 줄어 있는지
                      ref.add(97);
                      verifyAll(mine, ref, base + " remove(" + idx + ") 후 add(97)");
                      checkBounds(mine, ref.size(), base + " remove(" + idx + ") 후");

                      ref = new ArrayList<>();
                      mine = newFilled(cap, n, ref);
                      mine.set(idx, 95);
                      ref.set(idx, 95);
                      verifyAll(mine, ref, base + " set(" + idx + ",95)");
                    }

                    // 앞 / 뒤 / 중간에서부터 전부 삭제
                    for (int mode = 0; mode < 3; mode++) {
                      List<Integer> ref = new ArrayList<>();
                      DynamicArray<Integer> mine = newFilled(cap, n, ref);
                      while (!ref.isEmpty()) {
                        int idx = (mode == 0) ? 0 : (mode == 1 ? ref.size() - 1 : ref.size() / 2);
                        mine.remove(idx);
                        ref.remove(idx);
                        verifyAll(mine, ref, base + " 전부 삭제 mode=" + mode + " idx=" + idx);
                      }
                      checkBounds(mine, 0, base + " 전부 삭제 후 mode=" + mode);
                      mine.add(1);                  // 비워진 뒤 재사용
                      ref.add(1);
                      mine.add(0, 2);
                      ref.add(0, 2);
                      verifyAll(mine, ref, base + " 전부 삭제 후 재사용 mode=" + mode);
                    }

                    // 범위 밖 index (용량 > size 인 경우 포함)
                    checkBounds(newFilled(cap, n, new ArrayList<>()), n, base);
                  }
                }
              }

              // ArrayList 와 결과를 비교하는 랜덤 테스트 (seed 고정 → 항상 같은 결과로 재현 가능)
              static void testRandom() {
                Random r = new Random(1);
                int[] caps = {1, 2, 3, 5, 16};

                for (int t = 0; t < 1000; t++) {
                  int cap = caps[t % caps.length];
                  DynamicArray<Integer> mine = new DynamicArray<>(cap);
                  List<Integer> ref = new ArrayList<>();
                  List<String> log = new ArrayList<>();

                  try {
                    for (int step = 0; step < 300; step++) {
                      boolean growing = (step / 50) % 2 == 0;   // 50스텝마다 추가 위주 <-> 삭제 위주 (빈 배열 / 재확장 유도)
                      int v = r.nextInt(5);                     // 값 범위를 좁혀 중복을 자주 만듦
                      int op = r.nextInt(8);
                      if (growing && op >= 4 && op <= 6 && r.nextInt(3) != 0) {
                        op = r.nextInt(3);
                      }
                      if (!growing && op <= 2 && r.nextInt(3) != 0) {
                        op = 4 + r.nextInt(3);
                      }

                      switch (op) {
                        case 0 -> {                              // add(v)
                          log.add("add(" + v + ")");
                          mine.add(v);
                          ref.add(v);
                        }
                        case 1 -> {                              // add(idx, v) : 0 ~ size 전체
                          int idx = r.nextInt(ref.size() + 1);
                          log.add("add(" + idx + "," + v + ")");
                          mine.add(idx, v);
                          ref.add(idx, v);
                        }
                        case 2 -> {                              // add(0, v) : 맨 앞 삽입
                          log.add("add(0," + v + ")");
                          mine.add(0, v);
                          ref.add(0, v);
                        }
                        case 3 -> {                              // set
                          if (!ref.isEmpty()) {
                            int idx = r.nextInt(ref.size());
                            log.add("set(" + idx + "," + v + ")");
                            mine.set(idx, v);
                            ref.set(idx, v);
                          }
                        }
                        case 4, 5, 6 -> {                        // remove : 임의 위치 / 맨 앞 / 맨 뒤
                          if (!ref.isEmpty()) {
                            int idx = (op == 4) ? r.nextInt(ref.size()) : (op == 5 ? 0 : ref.size() - 1);
                            log.add("remove(" + idx + ")");
                            mine.remove(idx);
                            ref.remove(idx);                     // int → 인덱스 기준 삭제
                            // remove 가 삭제한 값을 반환하도록 구현했다면 위 두 줄을 아래처럼 바꿔서 반환값도 검증:
                            // Integer expected = ref.remove(idx);
                            // check(Objects.equals(mine.remove(idx), expected), "remove 반환값");
                          }
                        }
                        case 7 -> {                              // 범위 밖 index 는 예외, 상태는 그대로
                          log.add("bounds");
                          checkBounds(mine, ref.size(), "bounds");
                        }
                      }

                      verify(mine, ref, "step " + step);
                    }
                  } catch (AssertionError | RuntimeException e) {
                    throw new AssertionError("[trial=" + t + ", cap=" + cap + ", 연산 " + log.size() + "개 실행] 원인: " + e
                        + " | 마지막 연산: " + recent(log) + " | ref 상태: " + ref, e);
                  }
                }
              }

              static void testLarge() {
                int n = 300_000;
                DynamicArray<Integer> a = new DynamicArray<>(1);

                long start = System.nanoTime();
                for (int i = 0; i < n; i++) {
                  a.add(i);
                }
                long ms = (System.nanoTime() - start) / 1_000_000;
                check(ms < 1000, "add " + n + "번에 " + ms + "ms: 용량 증가가 2배 방식(amortized O(1))이 아닐 수 있음");
                check(a.getSize() == n, "large: size");
                for (int i = 0; i < n; i++) {
                  check(a.get(i) == i, "large: get(" + i + ")");
                }

                for (int i = n - 1; i >= 0; i--) {         // 뒤에서부터 전부 삭제
                  a.remove(i);
                }
                check(a.getSize() == 0, "large: 전부 삭제 후 size");
                checkBounds(a, 0, "large 삭제 후");

                int m = 5_000;                             // 앞쪽 삽입/삭제는 O(n) 이라 개수를 줄임
                DynamicArray<Integer> b = new DynamicArray<>(1);
                for (int i = 0; i < m; i++) {
                  b.add(0, i);
                }
                for (int i = 0; i < m; i++) {
                  check(b.get(i) == m - 1 - i, "large: add(0,v) 결과 순서 get(" + i + ")");
                }
                for (int i = 0; i < m; i++) {
                  check(b.get(0) == m - 1 - i, "large: remove(0) 순서 " + i);
                  b.remove(0);
                }
                check(b.getSize() == 0, "large: remove(0) 반복 후 size");
              }

              // ---------- 검증 도구 ----------

              static DynamicArray<Integer> newFilled(int cap, int n, List<Integer> ref) {
                DynamicArray<Integer> a = new DynamicArray<>(cap);
                for (int i = 0; i < n; i++) {
                  a.add(i + 10);                           // 서로 다른 값 → 밀림/덮어쓰기 버그가 드러남
                  ref.add(i + 10);
                }
                return a;
              }

              // size, 모든 get, 일부 값의 indexOf/lastIndexOf 비교 (값 -1, 5 는 존재하지 않는 값)
              static void verify(DynamicArray<Integer> mine, List<Integer> ref, String where) {
                check(mine.getSize() == ref.size(), where + ": size " + mine.getSize() + " != " + ref.size());
                for (int i = 0; i < ref.size(); i++) {
                  check(Objects.equals(mine.get(i), ref.get(i)), where + ": get(" + i + ")");
                }
                for (int v = -1; v <= 5; v++) {
                  check(mine.indexOf(v) == ref.indexOf(v), where + ": indexOf(" + v + ")");
                  check(mine.lastIndexOf(v) == ref.lastIndexOf(v), where + ": lastIndexOf(" + v + ")");
                }
              }

              // verify + 모든 원소 값에 대한 indexOf/lastIndexOf (작은 크기에서만 사용)
              static void verifyAll(DynamicArray<Integer> mine, List<Integer> ref, String where) {
                verify(mine, ref, where);
                for (Integer x : ref) {
                  check(mine.indexOf(x) == ref.indexOf(x), where + ": indexOf(" + x + ")");
                  check(mine.lastIndexOf(x) == ref.lastIndexOf(x), where + ": lastIndexOf(" + x + ")");
                }
                for (int v = 9; v <= 100; v++) {            // 삭제/덮어쓴 값이 내부에 남아 찾아지지 않는지 확인
                  check(mine.indexOf(v) == ref.indexOf(v), where + ": indexOf(" + v + ")");
                  check(mine.lastIndexOf(v) == ref.lastIndexOf(v), where + ": lastIndexOf(" + v + ")");
                }
              }

              // 범위 밖 index 는 모두 예외여야 하고, 실패한 연산이 size 를 바꾸면 안 됨
              static void checkBounds(DynamicArray<Integer> mine, int size, String where) {
                expectIOOBE(() -> mine.get(-1), where + ": get(-1)");
                expectIOOBE(() -> mine.get(size), where + ": get(size)");
                expectIOOBE(() -> mine.get(Integer.MAX_VALUE), where + ": get(MAX)");
                expectIOOBE(() -> mine.set(-1, 0), where + ": set(-1)");
                expectIOOBE(() -> mine.set(size, 0), where + ": set(size)");
                expectIOOBE(() -> mine.remove(-1), where + ": remove(-1)");
                expectIOOBE(() -> mine.remove(size), where + ": remove(size)");
                expectIOOBE(() -> mine.add(-1, 0), where + ": add(-1)");
                expectIOOBE(() -> mine.add(size + 1, 0), where + ": add(size+1)");
                expectIOOBE(() -> mine.add(Integer.MAX_VALUE, 0), where + ": add(MAX)");
                check(mine.getSize() == size, where + ": 예외 발생 후 size 가 변함");
              }

              static String recent(List<String> log) {
                return String.join(", ", log.subList(Math.max(0, log.size() - 8), log.size()));
              }

              static void expectIOOBE(Runnable action, String msg) {
                try {
                  action.run();
                } catch (IndexOutOfBoundsException e) {
                  return;
                }
                throw new AssertionError(msg + ": 예외가 발생하지 않음");
              }

              static void check(boolean cond, String msg) {
                if (!cond) {throw new AssertionError(msg);}
              }
            """);
        break;

      case "2": {
        System.out.println("""
              public static void main(String[] args) {
                testEmpty();            // 빈 리스트
                testExhaustiveSmall();  // 작은 크기에서 모든 위치의 삽입/삭제/수정 (전수 검사)
                testRandom();           // ArrayList 와 비교하는 랜덤 테스트
                testLarge();            // 대량 데이터 + 성능(tail 활용)
                System.out.println("ALL PASSED");
              }

              // ---------- 시나리오 테스트 ----------

              static void testEmpty() {
                SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
                check(list.isEmpty(), "empty: isEmpty");
                check(list.getSize() == 0, "empty: size");
                check(list.head == null && list.tail == null, "empty: head/tail 은 null 이어야 함");
                checkBounds(list, 0, "empty");
                checkStructure(list, "empty");
              }

              // n 0~12 에서 모든 위치에 대해 연산을 검사한다.
              // head 삭제 / tail 삭제 / 마지막 1개 삭제 / 빈 리스트에 삽입 / 맨 뒤 삽입 등 경계가 전부 포함됨
              static void testExhaustiveSmall() {
                for (int n = 0; n <= 12; n++) {
                  String base = "n=" + n;

                  // 모든 위치에 삽입 (맨 앞, 중간, 맨 뒤)
                  for (int idx = 0; idx <= n; idx++) {
                    List<Integer> ref = new ArrayList<>();
                    SinglyLinkedList<Integer> mine = newFilled(n, ref);
                    mine.add(idx, 99);
                    ref.add(idx, 99);
                    verify(mine, ref, base + " add(" + idx + ",99)");
                    mine.add(98);                   // 삽입 직후 tail 이 올바른지 (맨 뒤 삽입 후 add 로 확인)
                    ref.add(98);
                    verify(mine, ref, base + " add(" + idx + ",99) 후 add(98)");
                    mine.add(0, 97);
                    ref.add(0, 97);
                    verify(mine, ref, base + " add(" + idx + ",99) 후 add(0,97)");
                  }

                  // 모든 위치 삭제 / 수정
                  for (int idx = 0; idx < n; idx++) {
                    List<Integer> ref = new ArrayList<>();
                    SinglyLinkedList<Integer> mine = newFilled(n, ref);
                    Integer expected = ref.remove(idx);
                    Integer actual = mine.remove(idx);
                    check(Objects.equals(expected, actual), base + " remove(" + idx + ") 반환값: 기대=" + expected + ", 실제=" + actual);
                    verify(mine, ref, base + " remove(" + idx + ")");
                    mine.add(97);                   // tail 을 지운 뒤에도 add 가 정상인지
                    ref.add(97);
                    verify(mine, ref, base + " remove(" + idx + ") 후 add(97)");
                    mine.add(0, 96);
                    ref.add(0, 96);
                    verify(mine, ref, base + " remove(" + idx + ") 후 add(0,96)");
                    checkBounds(mine, ref.size(), base + " remove(" + idx + ") 후");

                    ref = new ArrayList<>();
                    mine = newFilled(n, ref);
                    mine.set(idx, 95);
                    ref.set(idx, 95);
                    verify(mine, ref, base + " set(" + idx + ",95)");
                    mine.add(94);                   // set 이 구조를 건드리지 않았는지
                    ref.add(94);
                    verify(mine, ref, base + " set(" + idx + ") 후 add(94)");
                  }

                  // 앞 / 뒤 / 중간에서부터 전부 삭제 → 빈 리스트 → 재사용
                  for (int mode = 0; mode < 3; mode++) {
                    List<Integer> ref = new ArrayList<>();
                    SinglyLinkedList<Integer> mine = newFilled(n, ref);
                    while (!ref.isEmpty()) {
                      int idx = (mode == 0) ? 0 : (mode == 1 ? ref.size() - 1 : ref.size() / 2);
                      Integer expected = ref.remove(idx);
                      Integer actual = mine.remove(idx);
                      check(Objects.equals(expected, actual), base + " 전부 삭제 mode=" + mode + " remove(" + idx + ") 반환값");
                      verify(mine, ref, base + " 전부 삭제 mode=" + mode + " idx=" + idx);
                    }
                    checkBounds(mine, 0, base + " 전부 삭제 후 mode=" + mode);
                    mine.add(1);                    // 비워진 뒤 재사용 (stale head/tail 확인)
                    ref.add(1);
                    verify(mine, ref, base + " 전부 삭제 후 add(1) mode=" + mode);
                    mine.add(0, 2);
                    ref.add(0, 2);
                    verify(mine, ref, base + " 전부 삭제 후 add(0,2) mode=" + mode);
                    mine.add(3);
                    ref.add(3);
                    verify(mine, ref, base + " 전부 삭제 후 add(3) mode=" + mode);
                  }

                  checkBounds(newFilled(n, new ArrayList<>()), n, base);
                }
              }

              // ArrayList 와 결과를 비교하는 랜덤 테스트 (seed 고정 → 항상 같은 결과로 재현 가능)
              static void testRandom() {
                Random r = new Random(1);

                for (int t = 0; t < 1000; t++) {
                  SinglyLinkedList<Integer> mine = new SinglyLinkedList<>();
                  List<Integer> ref = new ArrayList<>();
                  List<String> log = new ArrayList<>();

                  try {
                    for (int step = 0; step < 300; step++) {
                      boolean growing = (step / 50) % 2 == 0;   // 50스텝마다 추가 위주 <-> 삭제 위주 (빈 리스트 자주 발생)
                      int v = r.nextInt(5);
                      int op = r.nextInt(8);
                      if (growing && op >= 4 && op <= 6 && r.nextInt(3) != 0) {
                        op = r.nextInt(3);
                      }
                      if (!growing && op <= 2 && r.nextInt(3) != 0) {
                        op = 4 + r.nextInt(3);
                      }

                      switch (op) {
                        case 0 -> {                              // add(v)
                          log.add("add(" + v + ")");
                          mine.add(v);
                          ref.add(v);
                        }
                        case 1 -> {                              // add(idx, v) : 0 ~ size 전체 (맨 뒤 삽입 포함)
                          int idx = r.nextInt(ref.size() + 1);
                          log.add("add(" + idx + "," + v + ")");
                          mine.add(idx, v);
                          ref.add(idx, v);
                        }
                        case 2 -> {                              // add(0, v) : 맨 앞 삽입
                          log.add("add(0," + v + ")");
                          mine.add(0, v);
                          ref.add(0, v);
                        }
                        case 3 -> {                              // set
                          if (!ref.isEmpty()) {
                            int idx = r.nextInt(ref.size());
                            log.add("set(" + idx + "," + v + ")");
                            mine.set(idx, v);
                            ref.set(idx, v);
                          }
                        }
                        case 4, 5, 6 -> {                        // remove : 임의 위치 / head / tail
                          if (!ref.isEmpty()) {
                            int idx = (op == 4) ? r.nextInt(ref.size()) : (op == 5 ? 0 : ref.size() - 1);
                            log.add("remove(" + idx + ")");
                            Integer expected = ref.remove(idx);  // int → 인덱스 기준 삭제
                            Integer actual = mine.remove(idx);
                            check(Objects.equals(expected, actual), "remove 반환값: 기대=" + expected + ", 실제=" + actual);
                          }
                        }
                        case 7 -> {                              // 범위 밖 index 는 예외, 상태는 그대로
                          log.add("bounds");
                          checkBounds(mine, ref.size(), "bounds");
                        }
                      }

                      verify(mine, ref, "step " + step);
                    }
                  } catch (AssertionError | RuntimeException e) {
                    throw new AssertionError("[trial=" + t + ", 연산 " + log.size() + "개 실행] 원인: " + e
                        + " | 마지막 연산: " + recent(log) + " | ref 상태: " + ref, e);
                  }
                }
              }

              static void testLarge() {
                int n = 100_000;
                SinglyLinkedList<Integer> mine = new SinglyLinkedList<>();

                long start = System.nanoTime();
                for (int i = 0; i < n; i++) {
                  mine.add(i);
                }
                long ms = (System.nanoTime() - start) / 1_000_000;
                check(ms < 2000, "add(v) " + n + "번에 " + ms + "ms: O(1) 이어야 함 (tail 활용 확인)");
                check(mine.getSize() == n, "large: size");
                check(Objects.equals(mine.getData(0), 0), "large: getData(0)");
                check(Objects.equals(mine.getData(n / 2), n / 2), "large: getData(n/2)");
                check(Objects.equals(mine.getData(n - 1), n - 1), "large: getData(n-1)");
                checkStructure(mine, "large add");

                for (int i = 0; i < n; i++) {              // 앞에서부터 전부 삭제: 반환 순서 확인
                  Integer got = mine.remove(0);
                  check(Objects.equals(got, i), "large: remove(0) 순서 " + i);
                }
                check(mine.isEmpty(), "large: 전부 삭제 후 isEmpty");
                checkStructure(mine, "large drain");

                start = System.nanoTime();
                for (int i = 0; i < n; i++) {
                  mine.add(0, i);
                }
                ms = (System.nanoTime() - start) / 1_000_000;
                check(ms < 2000, "add(0, v) " + n + "번에 " + ms + "ms: O(1) 이어야 함");
                check(Objects.equals(mine.getData(0), n - 1), "large: add(0,v) getData(0)");
                check(Objects.equals(mine.getData(n - 1), 0), "large: add(0,v) getData(n-1)");
                checkStructure(mine, "large add(0,v)");

                for (int i = 0; i < 300; i++) {            // 뒤에서 삭제는 O(n) 이라 개수를 줄임
                  Integer got = mine.remove(mine.getSize() - 1);
                  check(Objects.equals(got, i), "large: 마지막 노드 remove 순서 " + i);
                }
                check(mine.getSize() == n - 300, "large: 마지막 노드 삭제 후 size");
                checkStructure(mine, "large 마지막 노드 삭제 후");
              }

              // ---------- 검증 도구 ----------

              static SinglyLinkedList<Integer> newFilled(int n, List<Integer> ref) {
                SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
                for (int i = 0; i < n; i++) {
                  list.add(i + 10);                        // 서로 다른 값 → 순서/연결 버그가 드러남
                  ref.add(i + 10);
                }
                return list;
              }

              // size, isEmpty, 모든 getData, 내부 연결 구조까지 한 번에 비교
              static void verify(SinglyLinkedList<Integer> mine, List<Integer> ref, String where) {
                check(mine.getSize() == ref.size(), where + ": size " + mine.getSize() + " != " + ref.size());
                check(mine.isEmpty() == ref.isEmpty(), where + ": isEmpty");
                for (int i = 0; i < ref.size(); i++) {
                  check(Objects.equals(mine.getData(i), ref.get(i)), where + ": getData(" + i + ")");
                }
                checkStructure(mine, where);
              }

              // head ~ tail 연결 구조 검증 (값만 비교해서는 잡히지 않는 tail 버그 확인용)
              static void checkStructure(SinglyLinkedList<Integer> list, String where) {
                int count = 0;
                Node<Integer> cur = list.head;
                Node<Integer> last = null;

                while (cur != null) {
                  last = cur;
                  cur = cur.next;
                  count++;
                  check(count <= list.getSize(), where + ": 노드 수가 size 보다 많음 (유실/순환 의심)");
                }

                check(count == list.getSize(), where + ": head 부터 센 노드 수(" + count + ") != size(" + list.getSize() + ")");
                check(last == list.tail, where + ": 마지막 노드 != tail");
                check(list.tail == null || list.tail.next == null, where + ": tail.next != null");
              }

              // 범위 밖 index 는 모두 예외여야 하고, 실패한 연산이 size / 구조를 바꾸면 안 됨
              static void checkBounds(SinglyLinkedList<Integer> mine, int size, String where) {
                expectIOOBE(() -> mine.getData(-1), where + ": getData(-1)");
                expectIOOBE(() -> mine.getData(size), where + ": getData(size)");
                expectIOOBE(() -> mine.getData(Integer.MAX_VALUE), where + ": getData(MAX)");
                expectIOOBE(() -> mine.set(-1, 0), where + ": set(-1)");
                expectIOOBE(() -> mine.set(size, 0), where + ": set(size)");
                expectIOOBE(() -> mine.remove(-1), where + ": remove(-1)");
                expectIOOBE(() -> mine.remove(size), where + ": remove(size)");
                expectIOOBE(() -> mine.add(-1, 0), where + ": add(-1)");
                expectIOOBE(() -> mine.add(size + 1, 0), where + ": add(size+1)");
                expectIOOBE(() -> mine.add(Integer.MAX_VALUE, 0), where + ": add(MAX)");
                check(mine.getSize() == size, where + ": 예외 발생 후 size 가 변함");
                checkStructure(mine, where + " (예외 발생 후)");
              }

              static String recent(List<String> log) {
                return String.join(", ", log.subList(Math.max(0, log.size() - 8), log.size()));
              }

              static void expectIOOBE(Runnable action, String msg) {
                try {
                  action.run();
                } catch (IndexOutOfBoundsException e) {
                  return;
                }
                throw new AssertionError(msg + ": 예외가 발생하지 않음");
              }

              static void check(boolean cond, String msg) {
                if (!cond) {throw new AssertionError(msg);}
              }
            """);
        break;
      }
      case "3": {
        System.out.println("""
              static final Class<? extends RuntimeException> NULL_EXCEPTION = IllegalArgumentException.class;
            
              public static void main(String[] args) {
                testEmpty();               // 빈 리스트
                testNullPolicy();          // null 입력 거부 + 거부 후 상태 유지
                testRegression();          // 구현 중 실제로 나왔던 버그 재현 시나리오
                testExhaustiveSmall();     // 작은 크기에서 모든 위치의 삽입/삭제/수정 (전수 검사)
                testExhaustivePairs();     // 연산 2개 연속의 모든 (i, j) 조합 (연쇄 버그 확인)
                testRemovedNodeDetached(); // 삭제된 노드의 prev / next 정리
                testRandom();              // ArrayList 와 비교하는 랜덤 테스트
                testLarge();               // 대량 데이터 + 성능(tail 활용, 양방향 탐색)
                System.out.println("ALL PASSED");
              }

              // ---------- 시나리오 테스트 ----------

              static void testEmpty() {
                DoublyLinkedList<Integer> list = new DoublyLinkedList<>();
                check(list.isEmpty(), "empty: isEmpty");
                check(list.getSize() == 0, "empty: size");
                check(list.head == null && list.tail == null, "empty: head/tail 은 null 이어야 함");
                checkBounds(list, 0, "empty");
                checkStructure(list, "empty");
              }

              // n 0~6 에서 모든 위치에 대해 null 입력이 거부되고, 거부된 연산이 상태를 바꾸지 않는지 확인
              static void testNullPolicy() {
                for (int n = 0; n <= 6; n++) {
                  String base = "n=" + n;
                  List<Integer> ref = new ArrayList<>();
                  DoublyLinkedList<Integer> mine = newFilled(n, ref);

                  expectThrows(NULL_EXCEPTION, () -> mine.add(null), base + ": add(null)");
                  for (int idx = 0; idx <= n; idx++) {
                    final int i = idx;
                    expectThrows(NULL_EXCEPTION, () -> mine.add(i, null), base + ": add(" + i + ",null)");
                  }
                  for (int idx = 0; idx < n; idx++) {
                    final int i = idx;
                    expectThrows(NULL_EXCEPTION, () -> mine.set(i, null), base + ": set(" + i + ",null)");
                  }
                  verify(mine, ref, base + " null 거부 후");

                  mine.add(1);                              // 거부 이후에도 정상 동작하는지
                  ref.add(1);
                  mine.add(0, 2);
                  ref.add(0, 2);
                  verify(mine, ref, base + " null 거부 후 add");
                }
              }

              // 구현 중 실제로 나왔던 버그를 그대로 재현하는 시나리오 (실패 위치가 바로 원인을 가리키도록 따로 분리)
              static void testRegression() {
                // 빈 리스트에 add(0, v): head 가 null 이어도 동작해야 함
                DoublyLinkedList<Integer> a = new DoublyLinkedList<>();
                a.add(0, 1);
                expectList(a, "regression: 빈 리스트 add(0,1)", 1);

                // size 1 에서 앞 삽입
                a.add(0, 2);
                expectList(a, "regression: size 1 에서 add(0,2)", 2, 1);

                // index == size - 1 은 '맨 뒤'가 아니라 '마지막 원소 앞' 삽입
                a.add(1, 3);
                expectList(a, "regression: add(size-1, v)", 2, 3, 1);

                // index == size 는 맨 뒤 삽입
                a.add(3, 4);
                expectList(a, "regression: add(size, v)", 2, 3, 1, 4);

                // getNode 의 뒤쪽 탐색(tail 에서 prev 로 이동)
                DoublyLinkedList<Integer> b = newFilled(10, new ArrayList<>());
                for (int i = 0; i < 10; i++) {
                  check(Objects.equals(b.getData(i), 10 + i), "regression: getData(" + i + ") 뒤쪽 탐색 방향");
                }

                // 중간 삭제 연속 2회: 삭제 뒤 노드의 prev 가 갱신되지 않으면 두 번째에서 깨짐
                DoublyLinkedList<Integer> c = newFilled(5, new ArrayList<>());
                check(Objects.equals(c.remove(1), 11), "regression: remove(1) 반환값");
                expectList(c, "regression: remove(1) 1회", 10, 12, 13, 14);
                check(Objects.equals(c.remove(1), 12), "regression: remove(1) 2회 반환값");
                expectList(c, "regression: remove(1) 2회", 10, 13, 14);

                // 마지막 노드 삭제 후 새 tail.next 가 null 이어야 함 (값 비교만으로는 안 잡힘)
                DoublyLinkedList<Integer> d = newFilled(4, new ArrayList<>());
                check(Objects.equals(d.remove(3), 13), "regression: tail 삭제 반환값");
                check(d.tail.next == null, "regression: tail 삭제 후 tail.next 가 null 이 아님");
                d.add(3, 50);
                expectList(d, "regression: tail 삭제 후 add(size,50)", 10, 11, 12, 50);
                d.remove(3);
                d.add(2, 51);
                expectList(d, "regression: tail 삭제 후 add(2,51)", 10, 11, 51, 12);
              }

              // n 0~12 에서 모든 위치에 대해 연산을 검사한다.
              // head 삭제 / tail 삭제 / 마지막 1개 삭제 / 빈 리스트에 삽입 / 맨 뒤 삽입 / 앞·뒤 절반 탐색 경계가 전부 포함됨
              static void testExhaustiveSmall() {
                for (int n = 0; n <= 12; n++) {
                  String base = "n=" + n;

                  // 모든 위치에 삽입 (맨 앞, 중간, 맨 뒤)
                  for (int idx = 0; idx <= n; idx++) {
                    List<Integer> ref = new ArrayList<>();
                    DoublyLinkedList<Integer> mine = newFilled(n, ref);
                    mine.add(idx, 99);
                    ref.add(idx, 99);
                    verify(mine, ref, base + " add(" + idx + ",99)");
                    mine.add(98);                       // 삽입 직후 tail 이 올바른지
                    ref.add(98);
                    verify(mine, ref, base + " add(" + idx + ",99) 후 add(98)");
                    mine.add(0, 97);                    // 삽입 직후 head 가 올바른지
                    ref.add(0, 97);
                    verify(mine, ref, base + " add(" + idx + ",99) 후 add(0,97)");
                    mine.add(ref.size(), 96);           // index == size 로 맨 뒤 삽입
                    ref.add(ref.size(), 96);
                    verify(mine, ref, base + " add(" + idx + ",99) 후 add(size,96)");
                  }

                  // 모든 위치 삭제 / 수정
                  for (int idx = 0; idx < n; idx++) {
                    List<Integer> ref = new ArrayList<>();
                    DoublyLinkedList<Integer> mine = newFilled(n, ref);
                    Integer expected = ref.remove(idx);
                    Integer actual = mine.remove(idx);
                    check(Objects.equals(expected, actual), base + " remove(" + idx + ") 반환값: 기대=" + expected + ", 실제=" + actual);
                    verify(mine, ref, base + " remove(" + idx + ")");
                    mine.add(97);                       // tail 을 지운 뒤에도 add 가 정상인지
                    ref.add(97);
                    verify(mine, ref, base + " remove(" + idx + ") 후 add(97)");
                    mine.add(0, 96);                    // head 를 지운 뒤에도 앞 삽입이 정상인지
                    ref.add(0, 96);
                    verify(mine, ref, base + " remove(" + idx + ") 후 add(0,96)");
                    checkBounds(mine, ref.size(), base + " remove(" + idx + ") 후");

                    ref = new ArrayList<>();
                    mine = newFilled(n, ref);
                    mine.set(idx, 95);
                    ref.set(idx, 95);
                    verify(mine, ref, base + " set(" + idx + ",95)");
                    mine.add(94);                       // set 이 구조를 건드리지 않았는지
                    ref.add(94);
                    verify(mine, ref, base + " set(" + idx + ") 후 add(94)");
                  }

                  // 앞 / 뒤 / 중간 / 앞뒤 번갈아 전부 삭제 → 빈 리스트 → 재사용
                  for (int mode = 0; mode < 4; mode++) {
                    List<Integer> ref = new ArrayList<>();
                    DoublyLinkedList<Integer> mine = newFilled(n, ref);
                    while (!ref.isEmpty()) {
                      int idx;
                      if (mode == 0) {
                        idx = 0;
                      } else if (mode == 1) {
                        idx = ref.size() - 1;
                      } else if (mode == 2) {
                        idx = ref.size() / 2;
                      } else {
                        idx = (ref.size() % 2 == 0) ? 0 : ref.size() - 1;
                      }
                      Integer expected = ref.remove(idx);
                      Integer actual = mine.remove(idx);
                      check(Objects.equals(expected, actual), base + " 전부 삭제 mode=" + mode + " remove(" + idx + ") 반환값");
                      verify(mine, ref, base + " 전부 삭제 mode=" + mode + " idx=" + idx);
                    }
                    check(mine.isEmpty(), base + " 전부 삭제 후 isEmpty mode=" + mode);
                    checkBounds(mine, 0, base + " 전부 삭제 후 mode=" + mode);
                    mine.add(1);                        // 비워진 뒤 재사용 (stale head/tail 확인)
                    ref.add(1);
                    verify(mine, ref, base + " 전부 삭제 후 add(1) mode=" + mode);
                    mine.add(0, 2);
                    ref.add(0, 2);
                    verify(mine, ref, base + " 전부 삭제 후 add(0,2) mode=" + mode);
                    mine.add(3);
                    ref.add(3);
                    verify(mine, ref, base + " 전부 삭제 후 add(3) mode=" + mode);
                  }

                  checkBounds(newFilled(n, new ArrayList<>()), n, base);
                }
              }

              // 연산 2개를 연속으로 수행하는 모든 (i, j) 조합 (첫 연산의 포인터 오류가 두 번째 연산에서 드러나는 버그 확인)
              static void testExhaustivePairs() {
                for (int n = 0; n <= 8; n++) {
                  String base = "n=" + n;

                  // 삽입 -> 삽입
                  for (int i = 0; i <= n; i++) {
                    for (int j = 0; j <= n + 1; j++) {
                      List<Integer> ref = new ArrayList<>();
                      DoublyLinkedList<Integer> mine = newFilled(n, ref);
                      mine.add(i, 91);
                      ref.add(i, 91);
                      mine.add(j, 92);
                      ref.add(j, 92);
                      verify(mine, ref, base + " add(" + i + ") -> add(" + j + ")");
                    }
                  }

                  // 삭제 -> 삭제 (연속 중간 삭제로 prev 연결이 깨지는 버그)
                  for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n - 1; j++) {
                      List<Integer> ref = new ArrayList<>();
                      DoublyLinkedList<Integer> mine = newFilled(n, ref);
                      Integer e1 = ref.remove(i);
                      Integer a1 = mine.remove(i);
                      check(Objects.equals(e1, a1), base + " remove(" + i + ") -> remove(" + j + ") 첫 반환값");
                      Integer e2 = ref.remove(j);
                      Integer a2 = mine.remove(j);
                      check(Objects.equals(e2, a2), base + " remove(" + i + ") -> remove(" + j + ") 둘째 반환값");
                      verify(mine, ref, base + " remove(" + i + ") -> remove(" + j + ")");
                    }
                  }

                  // 삽입 -> 삭제
                  for (int i = 0; i <= n; i++) {
                    for (int j = 0; j <= n; j++) {
                      List<Integer> ref = new ArrayList<>();
                      DoublyLinkedList<Integer> mine = newFilled(n, ref);
                      mine.add(i, 91);
                      ref.add(i, 91);
                      Integer expected = ref.remove(j);
                      Integer actual = mine.remove(j);
                      check(Objects.equals(expected, actual), base + " add(" + i + ") -> remove(" + j + ") 반환값");
                      verify(mine, ref, base + " add(" + i + ") -> remove(" + j + ")");
                    }
                  }

                  // 삭제 -> 삽입
                  for (int i = 0; i < n; i++) {
                    for (int j = 0; j <= n - 1; j++) {
                      List<Integer> ref = new ArrayList<>();
                      DoublyLinkedList<Integer> mine = newFilled(n, ref);
                      mine.remove(i);
                      ref.remove(i);
                      mine.add(j, 92);
                      ref.add(j, 92);
                      verify(mine, ref, base + " remove(" + i + ") -> add(" + j + ")");
                    }
                  }
                }
              }

              // 삭제된 노드는 prev / next 가 모두 null 이어야 함 (정책 테스트)
              static void testRemovedNodeDetached() {
                for (int n = 1; n <= 6; n++) {
                  for (int idx = 0; idx < n; idx++) {
                    DoublyLinkedList<Integer> mine = newFilled(n, new ArrayList<>());
                    List<Node<Integer>> nodes = new ArrayList<>();
                    for (Node<Integer> cur = mine.head; cur != null; cur = cur.next) {
                      nodes.add(cur);
                    }

                    Node<Integer> removed = nodes.get(idx);
                    mine.remove(idx);
                    check(removed.prev == null, "n=" + n + " remove(" + idx + "): 삭제된 노드의 prev 가 정리되지 않음");
                    check(removed.next == null, "n=" + n + " remove(" + idx + "): 삭제된 노드의 next 가 정리되지 않음");
                  }
                }
              }

              // ArrayList 와 결과를 비교하는 랜덤 테스트 (seed 고정 → 항상 같은 결과로 재현 가능)
              static void testRandom() {
                Random r = new Random(1);

                for (int t = 0; t < 1000; t++) {
                  DoublyLinkedList<Integer> mine = new DoublyLinkedList<>();
                  List<Integer> ref = new ArrayList<>();
                  List<String> log = new ArrayList<>();

                  try {
                    for (int step = 0; step < 300; step++) {
                      boolean growing = (step / 50) % 2 == 0;   // 50스텝마다 추가 위주 <-> 삭제 위주 (빈 리스트 자주 발생)
                      int v = r.nextInt(5);
                      int op = r.nextInt(10);
                      if (growing && op >= 4 && op <= 6 && r.nextInt(3) != 0) {
                        op = r.nextInt(3);
                      }
                      if (!growing && (op <= 2 || op == 8) && r.nextInt(3) != 0) {
                        op = 4 + r.nextInt(3);
                      }

                      switch (op) {
                        case 0 -> {                              // add(v)
                          log.add("add(" + v + ")");
                          mine.add(v);
                          ref.add(v);
                        }
                        case 1 -> {                              // add(idx, v) : 0 ~ size 전체
                          int idx = r.nextInt(ref.size() + 1);
                          log.add("add(" + idx + "," + v + ")");
                          mine.add(idx, v);
                          ref.add(idx, v);
                        }
                        case 2 -> {                              // add(0, v) : 맨 앞 삽입
                          log.add("add(0," + v + ")");
                          mine.add(0, v);
                          ref.add(0, v);
                        }
                        case 3 -> {                              // set
                          if (!ref.isEmpty()) {
                            int idx = r.nextInt(ref.size());
                            log.add("set(" + idx + "," + v + ")");
                            mine.set(idx, v);
                            ref.set(idx, v);
                          }
                        }
                        case 4, 5, 6 -> {                        // remove : 임의 위치 / head / tail
                          if (!ref.isEmpty()) {
                            int idx = (op == 4) ? r.nextInt(ref.size()) : (op == 5 ? 0 : ref.size() - 1);
                            log.add("remove(" + idx + ")");
                            Integer expected = ref.remove(idx);  // int → 인덱스 기준 삭제
                            Integer actual = mine.remove(idx);
                            check(Objects.equals(expected, actual), "remove 반환값: 기대=" + expected + ", 실제=" + actual);
                          }
                        }
                        case 7 -> {                              // 범위 밖 index 는 예외, 상태는 그대로
                          log.add("bounds");
                          checkBounds(mine, ref.size(), "bounds");
                        }
                        case 8 -> {                              // add(size, v) : index 로 맨 뒤 삽입
                          log.add("add(size=" + ref.size() + "," + v + ")");
                          mine.add(ref.size(), v);
                          ref.add(ref.size(), v);
                        }
                        case 9 -> {                              // null 입력은 거부, 상태는 그대로
                          log.add("null");
                          int idx = r.nextInt(ref.size() + 1);
                          expectThrows(NULL_EXCEPTION, () -> mine.add(null), "null: add(null)");
                          expectThrows(NULL_EXCEPTION, () -> mine.add(idx, null), "null: add(" + idx + ",null)");
                          if (!ref.isEmpty()) {
                            int setIdx = r.nextInt(ref.size());
                            expectThrows(NULL_EXCEPTION, () -> mine.set(setIdx, null), "null: set(" + setIdx + ",null)");
                          }
                        }
                      }

                      verify(mine, ref, "step " + step);
                    }
                  } catch (AssertionError | RuntimeException e) {
                    throw new AssertionError("[trial=" + t + ", 연산 " + log.size() + "개 실행] 원인: " + e
                        + " | 마지막 연산: " + recent(log) + " | ref 상태: " + ref, e);
                  }
                }
              }

              static void testLarge() {
                int n = 100_000;
                DoublyLinkedList<Integer> mine = new DoublyLinkedList<>();

                long start = System.nanoTime();
                for (int i = 0; i < n; i++) {
                  mine.add(i);
                }
                long ms = (System.nanoTime() - start) / 1_000_000;
                check(ms < 2000, "add(v) " + n + "번에 " + ms + "ms: O(1) 이어야 함 (tail 활용 확인)");
                check(mine.getSize() == n, "large: size");
                check(Objects.equals(mine.getData(0), 0), "large: getData(0)");
                check(Objects.equals(mine.getData(n / 2), n / 2), "large: getData(n/2)");
                check(Objects.equals(mine.getData(n - 1), n - 1), "large: getData(n-1)");
                checkStructure(mine, "large add");

                // 뒤쪽 원소 접근이 tail 쪽에서 탐색하는지 (head 에서만 탐색하면 매우 느려짐)
                start = System.nanoTime();
                for (int i = 0; i < 5_000; i++) {
                  int idx = n - 1 - (i % 50);
                  check(Objects.equals(mine.getData(idx), idx), "large: getData(" + idx + ")");
                }
                ms = (System.nanoTime() - start) / 1_000_000;
                check(ms < 1000, "뒤쪽 원소 getData 5000번에 " + ms + "ms: 양방향 탐색이 아닐 수 있음");

                // 뒤쪽 중간 삭제도 tail 쪽에서 탐색해야 함
                DoublyLinkedList<Integer> tailSide = new DoublyLinkedList<>();
                for (int i = 0; i < n; i++) {
                  tailSide.add(i);
                }
                start = System.nanoTime();
                for (int i = 0; i < 5_000; i++) {
                  Integer got = tailSide.remove(tailSide.getSize() - 2);
                  check(Objects.equals(got, n - 2 - i), "large: remove(size-2) 반환값 " + i);
                }
                ms = (System.nanoTime() - start) / 1_000_000;
                check(ms < 1000, "뒤쪽 remove(size-2) 5000번에 " + ms + "ms: 양방향 탐색이 아닐 수 있음");
                check(tailSide.getSize() == n - 5_000, "large: remove(size-2) 후 size");
                check(Objects.equals(tailSide.getData(tailSide.getSize() - 1), n - 1), "large: remove(size-2) 후 마지막 값");
                checkStructure(tailSide, "large remove(size-2)");

                for (int i = 0; i < n; i++) {              // 앞에서부터 전부 삭제: 반환 순서 확인
                  Integer got = mine.remove(0);
                  check(Objects.equals(got, i), "large: remove(0) 순서 " + i);
                }
                check(mine.isEmpty(), "large: 전부 삭제 후 isEmpty");
                checkStructure(mine, "large drain");

                start = System.nanoTime();
                for (int i = 0; i < n; i++) {
                  mine.add(0, i);
                }
                ms = (System.nanoTime() - start) / 1_000_000;
                check(ms < 2000, "add(0, v) " + n + "번에 " + ms + "ms: O(1) 이어야 함");
                check(Objects.equals(mine.getData(0), n - 1), "large: add(0,v) getData(0)");
                check(Objects.equals(mine.getData(n - 1), 0), "large: add(0,v) getData(n-1)");
                checkStructure(mine, "large add(0,v)");

                start = System.nanoTime();
                for (int i = 0; i < n; i++) {              // 뒤에서부터 전부 삭제: tail 덕분에 O(1) 이어야 함
                  Integer got = mine.remove(mine.getSize() - 1);
                  check(Objects.equals(got, i), "large: 마지막 노드 remove 순서 " + i);
                }
                ms = (System.nanoTime() - start) / 1_000_000;
                check(ms < 2000, "마지막 노드 remove " + n + "번에 " + ms + "ms: O(1) 이어야 함");
                check(mine.isEmpty(), "large: 뒤에서 전부 삭제 후 isEmpty");
                checkStructure(mine, "large 뒤에서 전부 삭제 후");
              }

              // ---------- 검증 도구 ----------

              static DoublyLinkedList<Integer> newFilled(int n, List<Integer> ref) {
                DoublyLinkedList<Integer> list = new DoublyLinkedList<>();
                for (int i = 0; i < n; i++) {
                  list.add(i + 10);                        // 서로 다른 값 → 순서/연결 버그가 드러남
                  ref.add(i + 10);
                }
                return list;
              }

              static void expectList(DoublyLinkedList<Integer> mine, String where, Integer... expected) {
                verify(mine, Arrays.asList(expected), where);
              }

              // size, isEmpty, 모든 getData, 정방향/역방향 순회 값, 내부 연결 구조까지 한 번에 비교
              static void verify(DoublyLinkedList<Integer> mine, List<Integer> ref, String where) {
                check(mine.getSize() == ref.size(), where + ": size " + mine.getSize() + " != " + ref.size());
                check(mine.isEmpty() == ref.isEmpty(), where + ": isEmpty");
                for (int i = 0; i < ref.size(); i++) {
                  check(Objects.equals(mine.getData(i), ref.get(i)), where + ": getData(" + i + ")");
                }
                checkStructure(mine, where);                // 노드 수가 size 와 같음이 보장된 뒤에 값 순회

                int k = 0;
                for (Node<Integer> cur = mine.head; cur != null; cur = cur.next) {
                  check(Objects.equals(cur.data, ref.get(k)), where + ": 정방향 순회 값 (" + k + ")");
                  k++;
                }
                k = ref.size() - 1;
                for (Node<Integer> cur = mine.tail; cur != null; cur = cur.prev) {
                  check(Objects.equals(cur.data, ref.get(k)), where + ": 역방향 순회 값 (" + k + ")");
                  k--;
                }
              }

              // head ~ tail 연결 구조 검증 (값만 비교해서는 잡히지 않는 prev / tail 버그 확인용)
              // head.prev == null, tail.next == null, 정방향·역방향 노드 수 == size, 모든 노드의 prev/next 가 서로 대칭
              static void checkStructure(DoublyLinkedList<Integer> list, String where) {
                int size = list.getSize();

                if (size == 0) {
                  check(list.head == null && list.tail == null, where + ": 빈 리스트인데 head/tail 이 null 이 아님");
                  return;
                }

                check(list.head != null && list.tail != null, where + ": size > 0 인데 head/tail 이 null");
                check(list.head.prev == null, where + ": head.prev != null");
                check(list.tail.next == null, where + ": tail.next != null");

                int count = 0;
                Node<Integer> cur = list.head;
                Node<Integer> last = null;

                while (cur != null) {
                  check(cur.prev == last, where + ": 정방향 " + count + "번째 노드의 prev 가 직전 노드와 다름");
                  last = cur;
                  cur = cur.next;
                  count++;
                  check(count <= size, where + ": 정방향 노드 수가 size 보다 많음 (유실/순환 의심)");
                }
                check(count == size, where + ": head 부터 센 노드 수(" + count + ") != size(" + size + ")");
                check(last == list.tail, where + ": 정방향 마지막 노드 != tail");

                count = 0;
                cur = list.tail;
                Node<Integer> after = null;

                while (cur != null) {
                  check(cur.next == after, where + ": 역방향 " + count + "번째 노드의 next 가 직후 노드와 다름");
                  after = cur;
                  cur = cur.prev;
                  count++;
                  check(count <= size, where + ": 역방향 노드 수가 size 보다 많음 (유실/순환 의심)");
                }
                check(count == size, where + ": tail 부터 센 노드 수(" + count + ") != size(" + size + ")");
                check(after == list.head, where + ": 역방향 마지막 노드 != head");
              }

              // 범위 밖 index 는 모두 예외여야 하고, 실패한 연산이 size / 구조를 바꾸면 안 됨
              static void checkBounds(DoublyLinkedList<Integer> mine, int size, String where) {
                expectIOOBE(() -> mine.getData(-1), where + ": getData(-1)");
                expectIOOBE(() -> mine.getData(size), where + ": getData(size)");
                expectIOOBE(() -> mine.getData(Integer.MAX_VALUE), where + ": getData(MAX)");
                expectIOOBE(() -> mine.getData(Integer.MIN_VALUE), where + ": getData(MIN)");
                expectIOOBE(() -> mine.set(-1, 0), where + ": set(-1)");
                expectIOOBE(() -> mine.set(size, 0), where + ": set(size)");
                expectIOOBE(() -> mine.set(Integer.MAX_VALUE, 0), where + ": set(MAX)");
                expectIOOBE(() -> mine.remove(-1), where + ": remove(-1)");
                expectIOOBE(() -> mine.remove(size), where + ": remove(size)");
                expectIOOBE(() -> mine.remove(Integer.MAX_VALUE), where + ": remove(MAX)");
                expectIOOBE(() -> mine.add(-1, 0), where + ": add(-1)");
                expectIOOBE(() -> mine.add(size + 1, 0), where + ": add(size+1)");
                expectIOOBE(() -> mine.add(Integer.MAX_VALUE, 0), where + ": add(MAX)");
                expectIOOBE(() -> mine.add(Integer.MIN_VALUE, 0), where + ": add(MIN)");
                check(mine.getSize() == size, where + ": 예외 발생 후 size 가 변함");
                checkStructure(mine, where + " (예외 발생 후)");
              }

              static String recent(List<String> log) {
                return String.join(", ", log.subList(Math.max(0, log.size() - 8), log.size()));
              }

              static void expectIOOBE(Runnable action, String msg) {
                expectThrows(IndexOutOfBoundsException.class, action, msg);
              }

              static void expectThrows(Class<? extends RuntimeException> type, Runnable action, String msg) {
                try {
                  action.run();
                } catch (RuntimeException e) {
                  if (type.isInstance(e)) {
                    return;
                  }
                  throw new AssertionError(msg + ": 기대 예외=" + type.getSimpleName() + ", 실제=" + e, e);
                }
                throw new AssertionError(msg + ": 예외가 발생하지 않음");
              }

              static void check(boolean cond, String msg) {
                if (!cond) {throw new AssertionError(msg);}
              }
            """);
        break;
      }

      default:
        break;
    }
  }
}