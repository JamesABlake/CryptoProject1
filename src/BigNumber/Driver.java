/**  Test the BigNumber.BigNumber class.
 *   Cryptography Project 1
 *   @author (sdb)
 *   @version (Sep 2012)
 */
package BigNumber;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Driver
{
	public static void main (String [] args)
	{
		Scanner scanner = new Scanner (System.in);
		BigNumber x,y;

		System.out.println ("Enter two BigNumbers, on separate lines, or hit Enter to terminate");
		String line = scanner.nextLine();

		while (!line.isEmpty())
		{	x = BigNumber.fromString(line);
			System.out.println ("Enter a second BigNumber");
			line = scanner.nextLine();
			y = BigNumber.fromString(line);

			System.out.println ("Sum: " + x.add(y));
			System.out.println ("Sum: " + y.add(x));
			System.out.println ("First - Second: " + x.subtract(y));
			System.out.println ("Second - First: " + y.subtract(x));
			System.out.println ("Product: " + x.multiply(y));
			System.out.println ("Product: " + y.multiply(x));
			System.out.println ("First / Second: " + x.divide(y)[0]);
			System.out.println ("Second / First: " + y.divide(x)[0]);
			System.out.println ("First % Second: " + x.divide(y)[1]);
			System.out.println ("Second % First: " + y.divide(x)[1]);
			System.out.println("Sqrt of first: " + GetSqrt(x));
			System.out.println("Sqrt of second: " + GetSqrt(y));
			System.out.println("Factors of first: " + GetFactors(x));
			System.out.println("Factors of second: " + GetFactors(y));

			line = scanner.nextLine();
		}
	}

	public static HashSet<BigNumber> GetFactors(BigNumber number) {
		BigNumber divisor = BigNumber.One;
		BigNumber sqrt = GetSqrt(number);
		HashSet<BigNumber> factors = new HashSet<>();
		BigNumber check = sqrt.divide(BigNumber.fromString("100"))[0];
		BigNumber check2 = check;

		while(divisor.compareTo(sqrt) <= 0) {
			BigNumber[] result = number.divide(divisor);
			if(result[1].isZero()) {
				factors.add(divisor);
				factors.add(result[0]);
			}
			divisor = divisor.add(BigNumber.One);
			if(divisor.compareTo(check) > 0) {
				System.out.println("Factor at " + (check.divide(check2)[0]) + "%");
				check = check.add(check2);
			}
		}

		return factors;
	}

	public static BigNumber GetSqrt(BigNumber number) {
		BigNumber x = number;
		BigNumber y = BigNumber.One;
		BigNumber two = BigNumber.fromString("2");

		while(x.compareTo(y) > 0){
			x = x.add(y).divide(two)[0];
			y = number.divide(x)[0];
		}

		return x;
	}
}
	