package BigNumber;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class UnitTests {
	public static void main (String[] args) {

		List<String> checks = Arrays.asList(
			//	A,		B,		A+B,	A-B,	B-A
				"1000",	"1",	"1001",	"999",	"-999",
				"0",	"0",	"0",	"0",	"0",
				"999",	"1",	"1000",	"998",	"-998",
				"-1",	"1",	"0",	"-2",	"2",
				"-999", "-1",	"-1000", "-998", "998",
				"399513", "419143", "818656", "-19630", "19630",
				"1111", "2", "1113", "1109", "-1109"
		);

		boolean totalPass = true;
		for(int i = 0; i < checks.size(); i += 5) {
			boolean passed = true;
			System.out.printf("Test %s\n", i / 5);
			BigNumber A = BigNumber.fromString(checks.get(i));
			BigNumber B = BigNumber.fromString(checks.get(i + 1));

			passed &= DoTest(A, B, () -> A.add(B), '+', checks.get(i + 2));
			passed &= DoTest(B, A, () -> B.add(A), '+', checks.get(i + 2));
			passed &= DoTest(A, B, () -> A.subtract(B), '-', checks.get(i + 3));
			passed &= DoTest(B, A, () -> B.subtract(A), '-', checks.get(i + 4));
			System.out.printf("%s\n\n", passed ? "Passed" : "Failed");
			totalPass &= passed;
		}
		System.out.printf("Overall: %s", totalPass ? "Passed" : "Failed");
	}

	public static boolean DoTest(BigNumber A, BigNumber B, Supplier<BigNumber> test, char operator, String expected) {
		BigNumber result = test.get();
		BigNumber expectedNumber = BigNumber.fromString(expected);

		if(result.equals(expectedNumber)) {
			System.out.printf("Success: %s (A) %c %s (B) = %s == %s\n", A, operator, B, result, expected);
			return true;
		} else {
			System.out.printf("Failure: %s (A) %c %s (B) = %s != %s\n", A, operator, B, result, expected);
			return false;
		}
	}
}
