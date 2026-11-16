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
              // 필요한 import: java.util.ArrayList, java.util.List, java.util.Objects, java.util.Random
              // 가정: SinglyLinkedList<E>(), add(E), add(int, E), set(int, E), remove(int) -> E, getData(int),
              //       getSize(), isEmpty(), 필드 head / tail, Node<E>.next
              // 범위를 벗어난 index 는 IndexOutOfBoundsException 을 던져야 함

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

      default:
        break;
    }


  }
}