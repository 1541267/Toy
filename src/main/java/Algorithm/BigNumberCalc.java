package Algorithm;

public class BigNumberCalc {
	public static void main(String[] args) {

		String a = "18446744073709551615";
		String b = "287346502836570928366";
		// String a = "123";
		// String b = "456";

		// BigInteger c = new BigInteger(a);
		// BigInteger d = new BigInteger(b);
		System.out.println(sum(a, b));
	}

	private static String sum(String a, String b) {
		StringBuilder sb = new StringBuilder();
		int i = a.length() - 1;
		int j = b.length() - 1;
		int overValue = 0;
		while (i >= 0 || j >= 0 || overValue > 0) {
			int digitA = (i >= 0) ? a.charAt(i--) - '0' : 0;
			int digitB = (j >= 0) ? b.charAt(j--) - '0' : 0;

			int sum = digitA + digitB + overValue;
			sb.append(sum % 10);
			overValue = sum / 10;
		}
		return sb.reverse().toString();
	}
}

