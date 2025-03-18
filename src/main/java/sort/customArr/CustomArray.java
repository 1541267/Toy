package sort.customArr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class CustomArray {
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	static int n;

	static {
		try {
			n = Integer.parseInt(br.readLine());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static int[] arr = new int[n];

	public CustomArray() {
		RandomGenerator rg = RandomGeneratorFactory.getDefault().create();

		HashSet<Integer> temp = new HashSet<>();

		while (temp.size() != n) {
			temp.add(rg.nextInt(1, n * 6));
		}

		int i = 0;
		for (Integer num : temp) {
			arr[i++] = num;
		}
	}

	public int[] generateArr() {
		return arr;
	}
}
