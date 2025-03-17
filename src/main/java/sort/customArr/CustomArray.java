package sort.customArr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class CustomArray {
	int[] arr = new int[10];

	public CustomArray() {
		RandomGenerator rg = RandomGeneratorFactory.getDefault().create();
		HashSet<Integer> temp = new HashSet<Integer>();

		while (temp.size() != 10) {
			temp.add(rg.nextInt(1, 80));
		}

		int i = 0;
		for (Integer num : temp) {
			this.arr[i++] = num;
		}
	}

	public int[] generateArr() {
		return this.arr;
	}
}
