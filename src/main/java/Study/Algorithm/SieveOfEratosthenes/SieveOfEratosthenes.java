package Study.Algorithm.SieveOfEratosthenes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class SieveOfEratosthenes {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		int max = 100_000_000;
		boolean[] isPrime = new boolean[max + 1];
		Arrays.fill(isPrime, true);
		isPrime[0] = isPrime[1] = false;

		System.out.print("1~1000 범위, n 번째 소수 찾기: ");
		int n = Integer.parseInt(br.readLine());
		int primeCount = 0;

		StringBuilder sb = new StringBuilder();
		double startTime = System.nanoTime() / 1_000_000.0;

		// 맨 아래와 똑같으나 더 효율적, 반복 횟수 감소
		// 🔹 1단계: 배수 제거 (소수 여부 결정)
		// for (int i = 2; i * i <= max; i++) {
		// 	if (isPrime[i]) {
		// 		for (int j = i * i; j <= max; j += i) { // j = i * i로 최적화
		// 			isPrime[j] = false;
		// 		}
		// 	}
		// }
		// // 🔹 2단계: 소수 찾기
		// for (int i = 2; i <= max; i++) {
		// 	if (isPrime[i]) {
		// 		sb.append(i).append(" ");
		// 		primeCount++;
		// 	}
		// 	if (n == primeCount) {
		// 		sb.append("\n").append("찾은 ").append(primeCount).append(" 번째 소수: ").append(i);
		// 		break;
		// 	}
		// }
		// 더 빠르고 읽기 좋은?
		for (int i = 2; i <= max; i++) {  // 🔹 소수 찾으면서 배수 제거
			if (isPrime[i]) {
				sb.append(i).append(" ");
				primeCount++;

				if (n == primeCount) {
					sb.append("\n").append("찾은 ").append(primeCount).append(" 번째 소수: ").append(i);
					break;
				}

				for (long j = (long)i * i; j <= max; j += i) {  // 🔹 소수를 찾을 때 바로 배수 제거
					isPrime[(int)j] = false;
				}
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
		double endTime = System.nanoTime() / 1_000_000.0;
		System.out.println(sb);
		System.out.printf("소요 시간 %.2f", (endTime - startTime));
	}
}
