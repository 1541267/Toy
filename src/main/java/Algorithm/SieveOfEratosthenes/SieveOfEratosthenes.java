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

		System.out.print("1~1000 범위, n 번째 소수 찾기: ");
		int n = Integer.parseInt(br.readLine());
		int primeCount = 0;

		StringBuilder sb = new StringBuilder();
		// 아래와 똑같으나 더 효율적, 반복 횟수 감소
		for (int i = 2; i * i <= max; i++) {
			// for (int i = 2; i <= Math.sqrt(max); i++) {
			if (isPrime[i]) {
				sb.append(i).append(" ");
				primeCount++;
			}
			if (n == primeCount) {
				sb.append("\n").append("찾은 ").append(primeCount).append(" 번째 소수: ").append(i);
				break;
			}
			for (int j = i * 2; j <= max; j += i) {
				isPrime[j] = false;
			}
		}
		// for (int i = 2; i <= max; i++) {
		// 	if (isPrime[i]) {
		// 		sb.append(i).append(" ");
		// 		primeCount++;
		// 	}
		// 	if (n == primeCount) {
		// 		sb.append("\n").append("찾은 ").append(primeCount).append(" 번째 소수: ").append(i);
		// 		break;
		// 	}
		// 	for (int j = i * 2; j <= max; j += i) {
		// 		isPrime[j] = false;
		// 	}
		// }
		System.out.println(sb);
	}
}
