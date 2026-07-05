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

  static int[] integerArr = new int[n];
  static double[] doubleArr = new double[n];

  public ArrGenerator() {
    RandomGenerator rg = RandomGeneratorFactory.getDefault().create();

    // HashSet<Integer> temp = new HashSet<>();
    ArrayList<Integer> temp = new ArrayList<>();
    ArrayList<Double> temp2 = new ArrayList<>();

    while (temp.size() != n) {temp.add(rg.nextInt(1, n * 6));}
    while (temp2.size() != n) {temp2.add(rg.nextDouble(0, 1));}

    int i = 0, j = 0;
    for (Integer num : temp) {integerArr[i++] = num;}
    for (Double num : temp2) {doubleArr[j++] = num;}
  }

  public int[] initInteger() {return integerArr;}
  public double[] initDouble() {return doubleArr;}
  public boolean isAllowPrint() {return isAllowPrint;}

  public boolean isIntegerArrSorted(int[] arr) {
    for (int i = 1; i < arr.length; i++) {
      if (arr[i - 1] > arr[i]) {return false;}
    }
    return true;
  }

  public boolean isDoubleArrSorted(double[] arr) {
    for (int i = 1; i < arr.length; i++) {
      if (arr[i - 1] > arr[i]) {return false;}
    }
    return true;
  }
}
