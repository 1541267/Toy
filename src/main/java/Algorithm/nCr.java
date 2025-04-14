package Algorithm;

import java.util.ArrayList;
import java.util.List;

public class nCr {

	// 갯수만 체크
	// public static void main(String[] args) throws IOException {
	//
	// 	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	// 	int n = Integer.parseInt(br.readLine());
	// 	int r = Integer.parseInt(br.readLine());
	//
	// 	// n = 전체 개수, m = 선택 하는 개수
	//
	// 	// nCr = nC(n-r)
	// 	if (r > n - r) r = n - r;
	//
	// 	long result = 1;
	// 	for (int i = 1; i <= r; i++) {
	// 		result = result * (n - i + 1) / i;
	// 	}
	//
	// 	System.out.println("result = " + result);
	// }


	// 조합 까지
	static List<List<Integer>> result = new ArrayList<>();
	public static void main(String[] args) {
		int n = 5;
		int r = 3;

		getCombinations(n, r);

		System.out.println(result);
	}

	public static void getCombinations(int n, int r) {
		combine(n, r, 0, new ArrayList<>());
	}

	private static void combine(int n, int r, int start, List<Integer> current) {
		if (current.size() == r) {
			result.add(new ArrayList<>(current));
			return;
		}

		for (int i = start; i < n; i++) {
			current.add(i);                         // 원소 선택
			combine(n, r, i + 1, current);          // 다음 탐색
			current.remove(current.size() - 1);     // 백트래킹
		}
	}
}
