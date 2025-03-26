package Algorithm.Greedy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class greedy {
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int[] coins = {500, 100, 50, 10};

		int price = Integer.parseInt(br.readLine());
		int count = 0;

		for (int c : coins) {
			count += (price/c);
			price %= c;
		}
		System.out.println(count);
	}
}
