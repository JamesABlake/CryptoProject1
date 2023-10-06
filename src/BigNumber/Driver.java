/**  Test the BigNumber.BigNumber class.
 *   Cryptography Project 1
 *   @author (sdb)
 *   @version (Sep 2012)
 */
package BigNumber;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Driver
{
	public static void main (String [] args)
	{
		Scanner scanner = new Scanner (System.in);
		BigNumber x,y;
		File newFile = new File("testoutput" + System.currentTimeMillis() + ".txt");
		try{newFile.createNewFile();}
		catch (Exception e){
			e.printStackTrace();
		}
		PrintWriter writer;
		try {
			writer = new PrintWriter(newFile.getName(), "UTF-8");

		System.out.println ("Enter two BigNumbers, on separate lines, or hit Enter to terminate");
		String line = scanner.nextLine();

		while (!line.isEmpty())
		{	x = BigNumber.fromString(line);
			System.out.println ("Enter a second BigNumber");
			line = scanner.nextLine();
			y = BigNumber.fromString(line);

		 	writer.println("First: " x.tostring()); 
		 	writer.println("Second: " y.tostring()); 
			writer.println("Sum: " + x.add(y));
			writer.println ("Sum: " + y.add(x));
			writer.println ("First - Second: " + x.subtract(y));
			writer.println ("Second - First: " + y.subtract(x));
			writer.println ("Product: " + x.multiply(y));
			writer.println ("Product: " + y.multiply(x));
			if(!x.isZero() && !y.isZero()) {
				writer.println("First / Second: " + x.divide(y)[0]);
				writer.println("Second / First: " + y.divide(x)[0]);
				writer.println("First % Second: " + x.divide(y)[1]);
				writer.println("Second % First: " + y.divide(x)[1]);
			}
			writer.println("Sqrt of first: " + GetSqrt(x));
			writer.println("Sqrt of second: " + GetSqrt(y));
			writer.println("Factors of first: " + GetFactors(x));
			writer.println("Factors of second: " + GetFactors(y));
			line = scanner.nextLine();
			writer.close();
		}
		} catch(Exception e){
			e.printStackTrace();
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
	
