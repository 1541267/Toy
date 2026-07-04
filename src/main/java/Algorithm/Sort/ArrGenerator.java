package Algorithm.Sort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class ArrGenerator {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static int n;
  static boolean isAllowPrint = false;

  static {
    try {
      System.out.print("배열 생성, 길이 입력: ");
      n = Integer.parseInt(br.readLine());
      System.out.print("정렬 과정 출력? y or blank: ");

      if (br.readLine().equalsIgnoreCase("y")) {isAllowPrint = true;}
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  static int[] arr = new int[n];

  public ArrGenerator() {
    RandomGenerator rg = RandomGeneratorFactory.getDefault().create();

    // HashSet<Integer> temp = new HashSet<>();
    ArrayList<Integer> temp = new ArrayList<>();

    while (temp.size() != n) {temp.add(rg.nextInt(1, n * 6));}

    int i = 0;
    for (Integer num : temp) {arr[i++] = num;}
  }

  public int[] init() {return arr;}

  public boolean isAllowPrint() {return isAllowPrint;}

  public boolean isSorted(int[] arr) {
    for (int i = 1; i < arr.length; i++) {
      if (arr[i - 1] > arr[i]) {return false;}
    }
    return true;
  }
}
