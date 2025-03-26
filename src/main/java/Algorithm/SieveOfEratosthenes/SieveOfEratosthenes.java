package Algorithm.SieveOfEratosthenes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class SieveOfEratosthenes {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		int max = 10_000;
		boolean[] isPrime = new boolean[max + 1];
		Arrays.fill(isPrime, true);
		isPrime[0] = isPrime[1] = false;

		System.out.print("n 번째 소수 찾기: ");
		int n = Integer.parseInt(br.readLine());

		int primeCount = 0;

		for (int i = 2; i <= max; i++) {
			if (isPrime[i])
				primeCount++;
			if(n == primeCount) {
				System.out.println("찾은 n번 째 소수: " + i);
				break;
			}
			for (int j = i * i; j <= max; j += i) {
				isPrime[j] = false;
			}
		}

		for (int i = 0; i < max + 1; i++) {
			if (isPrime[i]) {
				System.out.print(i + " ");
			}
		}

	}
}
