package Algorithm.Etc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class SumConsecutiveNumber {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int n = Integer.parseInt(br.readLine());
		int t = Integer.parseInt(br.readLine());

		int[] arr = new int[n];

		int start = (t / n) - (n - 1) / 2;
		for(int i = 0; i < n; i++){
			arr[i] = start + i;
		}

		System.out.println("arr = " + Arrays.toString(arr));
	}
}
