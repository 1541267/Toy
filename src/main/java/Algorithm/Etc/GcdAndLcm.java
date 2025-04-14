package Algorithm.Etc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;

public class GcdAndLcm {
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		long n = Integer.parseInt(br.readLine());
		long m = Integer.parseInt(br.readLine());

		BigInteger n1 = BigInteger.valueOf(n);
		BigInteger n2 = BigInteger.valueOf(m);

		long gcd = n1.gcd(n2).intValue();
		long lcm = n1.multiply(n2).divide(n1.gcd(n2)).intValue();

		System.out.println("Big gcd = " + gcd + ", lcm = " + lcm);
		System.out.println("GCD(n, m) : " + GCD(n, m) + ", LCM(n, m) : " + LCM(n, m));
		System.out.println("LCM2(n, m) : " + LCM2(n, m));
	}

	private static long GCD(long n, long m) {
		if (m == 0) return n;
		return GCD(m, n % m);
	}

	public static long LCM(long n, long m) {
		return n * m / GCD(n, m);
	}

	public static long LCM2(long n, long m) {

		long a = n, b = m;
		while (m != 0) {
			long temp = m;
			m = n % m;
			n = temp;
		}
		return a * b / n;
	}
}
