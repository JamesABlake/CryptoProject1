/**  Test the BigNumber class.
 *   Cryptography Project 1
 *   @author (sdb)
 *   @version (Sep 2012)
 */
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
        {	x = new BigNumber (line);
            System.out.println ("Enter a second BigNumber");
            line = scanner.nextLine();
            y = new BigNumber (line);

            System.out.println ("Sum: " + x.add(y));
            System.out.println ("Sum: " + y.add(x));
            System.out.println ("First - Second: " + x.subtract(y));
            System.out.println ("Second - First: " + y.subtract(x));
            System.out.println ("Product: " + x.multiply(y));
            System.out.println ("Product: " + y.multiply(x));
            System.out.println ("First / Second: " + x.divide(y).getQuotient());
            System.out.println ("Second / First: " + y.divide(x).getQuotient());
            System.out.println ("First % Second: " + x.divide(y).getMod());
            System.out.println ("Second % First: " + y.divide(x).getMod());

            line = scanner.nextLine();
        }
    }
}
	