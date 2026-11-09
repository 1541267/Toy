package Study.DataStructure.Practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// Practice 에서 구현 연습에 사용되는 테스트 코드 
public class TestCodes {

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
        break;
      }

      default:
        break;
    }


  }
}

