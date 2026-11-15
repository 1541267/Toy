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
                Random r = new Random(1);
                        
                for (int t = 0; t < 1000; t++) {
                  DynamicArray<Integer> mine = new DynamicArray<>(1);
                  List<Integer> ref = new ArrayList<>();
                        
                  for (int step = 0; step < 200; step++) {
                    int v = r.nextInt(5);
                    switch (r.nextInt(5)) {
                      case 0 -> {
                        mine.add(v);
                        ref.add(v);
                      }
                      case 1 -> {
                        int idx = r.nextInt(ref.size() + 1);
                        mine.add(idx, v);
                        ref.add(idx, v);
                      }
                      case 2 -> {
                        if (!ref.isEmpty()) {
                          int idx = r.nextInt(ref.size());
                          mine.set(idx, v);
                          ref.set(idx, v);
                        }
                      }
                      case 3 -> {
                        if (!ref.isEmpty()) {
                          int idx = r.nextInt(ref.size());
                          mine.remove(idx);
                          ref.remove(idx);            // int → 인덱스 기준 삭제
                        }
                      }
                      case 4 -> {
                        check(mine.indexOf(v) == ref.indexOf(v), "indexOf");
                        check(mine.lastIndexOf(v) == ref.lastIndexOf(v), "lastIndexOf");
                      }
                    }
                        
                    check(mine.getSize() == ref.size(), "size");
                    for (int i = 0; i < ref.size(); i++) {
                      check(Objects.equals(mine.get(i), ref.get(i)), "get(" + i + ")");
                    }
                  }
                }
                System.out.println("ALL PASSED");
              }
                        
              static void check(boolean cond, String msg) {
                if (!cond) {throw new AssertionError(msg);}
              }
            """);
        break;

      case "2": {
        System.out.println("""
              public static void main(String[] args) {
                Random r = new Random(1);
                        
                for (int t = 0; t < 1000; t++) {
                  SinglyLinkedList<Integer> mine = new SinglyLinkedList<>();
                  List<Integer> ref = new ArrayList<>();
                        
                  for (int step = 0; step < 200; step++) {
                    int v = r.nextInt(5);
                    switch (r.nextInt(6)) {
                      case 0 -> {
                        mine.add(v);
                        ref.add(v);
                      }
                      case 1 -> {
                        int idx = r.nextInt(ref.size() + 1);   // size 포함 (맨 뒤 삽입)
                        mine.add(idx, v);
                        ref.add(idx, v);
                      }
                      case 2 -> {
                        if (!ref.isEmpty()) {
                          int idx = r.nextInt(ref.size());
                          mine.set(idx, v);
                          ref.set(idx, v);
                        }
                      }
                      case 3, 4 -> {                            // 삭제 비중을 높여 빈 리스트 상태를 자주 만듦
                        if (!ref.isEmpty()) {
                          int idx = r.nextInt(ref.size());
                          Integer expected = ref.remove(idx);   // int -> 인덱스 기준 삭제
                          Integer actual = mine.remove(idx);
                          check(Objects.equals(expected, actual), "remove 반환값");
                        }
                      }
                      case 5 -> {
                        int size = ref.size();
                        expectIOOBE(() -> mine.getData(-1), "getData(-1)");
                        expectIOOBE(() -> mine.getData(size), "getData(size)");
                        expectIOOBE(() -> mine.set(-1, v), "set(-1)");
                        expectIOOBE(() -> mine.set(size, v), "set(size)");
                        expectIOOBE(() -> mine.remove(-1), "remove(-1)");
                        expectIOOBE(() -> mine.remove(size), "remove(size)");
                        expectIOOBE(() -> mine.add(-1, v), "add(-1)");
                        expectIOOBE(() -> mine.add(size + 1, v), "add(size+1)");
                      }
                    }
                        
                    check(mine.getSize() == ref.size(), "size");
                    check(mine.isEmpty() == ref.isEmpty(), "isEmpty");
                    for (int i = 0; i < ref.size(); i++) {
                      check(Objects.equals(mine.getData(i), ref.get(i)), "getData(" + i + ")");
                    }
                    checkStructure(mine);
                  }
                }
                System.out.println("ALL PASSED");
              }
                        
              // head ~ tail 연결 구조 검증 (값만 비교해서는 잡히지 않는 tail 버그 확인용)
              static void checkStructure(SinglyLinkedList<Integer> list) {
                int count = 0;
                Node<Integer> cur = list.head;
                Node<Integer> last = null;
                        
                while (cur != null) {
                  last = cur;
                  cur = cur.next;
                  count++;
                  check(count <= list.getSize(), "노드 수가 size보다 많음 (유실/순환 의심)");
                }
                        
                check(count == list.getSize(), "head부터 센 노드 수 != size");
                check(last == list.tail, "마지막 노드 != tail");
                check(list.tail == null || list.tail.next == null, "tail.next != null");
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

