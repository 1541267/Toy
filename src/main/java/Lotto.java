import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class Lotto {
	public static void main(String[] args) throws IOException {
		RandomGenerator rg = RandomGeneratorFactory.getDefault().create();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		int count = 0;
		System.out.println("몇 세트 생성?");
		int n = Integer.parseInt(br.readLine());

		List<Set<Integer>> numList = new ArrayList<>();
		for (int i = 0; i < n; i++) {

			Set<Integer> numbers = new TreeSet<>();

			while (numbers.size() < 6) {
				numbers.add(rg.nextInt(1, 46));
			}
			numList.add(numbers);
		}

		System.out.println(n + "개 생성, 이중에 한 개씩 고르기");
		StringBuilder sb = new StringBuilder();

		while (count < 5) {
			sb.append("==================================================").append("\n");
			sb.append(numList.get(Integer.parseInt(br.readLine()))).append("\n");
			count++;
		}

		System.out.println(sb);
	}
}
// 	public static void main(String[] args) throws IOException {
// 		RandomGenerator rg = RandomGeneratorFactory.getDefault().create();
// 		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//
// 		System.out.println("몇 번 반복?");
// 		int n = Integer.parseInt(br.readLine());
//
// 		for (int i = 0; i < n; i++) {
//
// 			Set<Integer> numbers = new TreeSet<>();
//
// 			while (numbers.size() < 6) {
// 				numbers.add(rg.nextInt(1, 46));
// 			}
//
// 			System.out.println(numbers);
// 		}
// 	}
// }

// public class Lotto {
// 	public static void main(String[] args) {
//
// 		int[] number = new int[6];
// 		int count = 0;
//
// 		while(count < 5) {
// 			for (int i = 0; i < 6; i++) {
//
// 				boolean checkingNumber = true;
//
// 				while (checkingNumber) {
//
// 					int selectedNumber = (int)(Math.random() * 45) + 1;
//
// 					checkingNumber = false;
//
// 					for (int j = 0; j < i; j++) {
// 						if (selectedNumber == number[j]) {
// 							checkingNumber = true;
// 							break;
// 						}
// 					}
//
// 					if (!checkingNumber) {
// 						number[i] = selectedNumber;
// 					}
// 				}
// 			}
// 			Arrays.sort(number);
//
// 			for (int k : number) {
// 				System.out.print(k + " ");
// 			}
// 			System.out.println();
// 			count++;
// 		}
// 	}
// }
//
