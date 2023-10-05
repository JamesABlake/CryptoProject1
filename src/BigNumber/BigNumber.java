package BigNumber;

import java.util.ArrayList;
import static BigNumber.Helper.*;

public final class BigNumber {

	/**
	 * A list of numbers that make up the larger number. They are ordered from
	 * least significant to most.
	 * */
	private final ArrayList<Integer> digits;

	/**
	 * Creates a BigNumber from preformatted data.
	 * @param digits The preformatted list of numbers to assign to the internal list.
	 */
	private BigNumber(ArrayList<Integer> digits) {
		this.digits = digits;
	}

	/**
	 * Creates a BigNumber from a string of integers, optionally proceeded
	 * by a minus sign for negatives.
	 * @param text The string to parse into the BigNumber.
	 */
	public static BigNumber fromString(String text) {
		ArrayList<Integer> digits = new ArrayList<>();
		int length = text.length();
		char nextDigit;

		// Gets the digit starting at the end of the string and working backwards. Skips
		// the first character of the string so that we can check if it's negative
		for(int i = length; i > 1; i--) {
			nextDigit = text.charAt(i - 1);
			digits.add(charToInt(nextDigit));
		}

		nextDigit = text.charAt(0);
		boolean isNegative = nextDigit == '-';

		if(!isNegative)
			digits.add(charToInt((nextDigit)));

		// If the most significant digit is greater than 4, we have to pad it
		// with a zero to for 10's compliment
		if(GetLast(digits) > 4)
			digits.add(0);

		if(isNegative)
			return new BigNumber(digits).inverse();
		else
        	return new BigNumber(digits);
	}

	/**
	 * Adds other and this BigNumber.
	 * @param other The BigNumber added to this BigNumber.
	 * @return A new BigNumber that's the result of adding other to this BigNumber.
	 */
	public BigNumber add(BigNumber other) {
		ArrayList<Integer> addedDigits = new ArrayList<>();
		int thisSize = this.digits.size();
		int otherSize = other.digits.size();

		// This finds the smaller and bigger number list between the left and right
		// hand inputs, making it easy to loop through them fully regardless of which
		// is which.
		boolean thisIsSmaller = thisSize < otherSize;

		int smallSize = thisIsSmaller ? thisSize : otherSize;
		int bigSize = thisIsSmaller ? otherSize : thisSize;

		ArrayList<Integer> smallList = thisIsSmaller ? this.digits : other.digits;
		ArrayList<Integer> bigList = thisIsSmaller ? other.digits : this.digits;

		boolean shouldBeNegative = GetLast(this.digits) > 4 && GetLast(other.digits) > 4;
		boolean shouldBePositive = GetLast(this.digits) <= 4 && GetLast(other.digits) <= 4;

		// The first loop adds the smaller number parts to the bigger number parts,
		// carrying as needed until the smaller one runs out. The second loop then
		// continues with just the larger number and relevant padding for 10's
		// compliment.

		int carry = 0;
		int pad = GetLast(smallList) > 4 ? 9 : 0;

		// First loop
		for (int i = 0; i < smallSize; i++) {
			int nextDigit = bigList.get(i) + smallList.get(i) + carry;
			carry = nextDigit / 10;
			nextDigit = nextDigit % 10;
			addedDigits.add(nextDigit);
		}

		// Second loop
		for(int i = smallSize; i < bigSize; i++) {
			int nextDigit = bigList.get(i) + pad + carry;
			carry = nextDigit / 10;
			nextDigit = nextDigit % 10;
			addedDigits.add(nextDigit);
		}
		// End loops

		// Add the leading 0 or 9 for numbers that exceed 10's compliment range
		if(shouldBeNegative && GetLast(addedDigits) <= 4) {
			addedDigits.add(9);
		} else if(shouldBePositive && GetLast(addedDigits) > 4) {
			addedDigits.add(0);
		}

		truncate(addedDigits);

		return new BigNumber(addedDigits);
	}

	/**
	 * Truncates the list, removing and leading 0's or 9's.
	 * @param digits The list to truncate.
	 */
	private static void truncate(ArrayList<Integer> digits) {
		int last = GetLast(digits);
		if(last != 0 && last != 9)
			return;

		int truncateUntil = 0;
		int size = digits.size();

		// Remove leading 9's or 0's, leaving at least one.
		for(int i = size - 1; i > 0; i--) {
			if(digits.get(i - 1) != last) {
				break;
			}
			digits.remove(i);
		}
	}

	/**
	 * Subtracts rh and this BigNumber.
	 * @param other The BigNumber subtracted from this BigNumber.
	 * @return A new BigNumber that's the result of subtracting rh from this BigNumber.
	 */
	public BigNumber subtract(BigNumber other) {
		return add(other.inverse());
	}

	public BigNumber multiply(BigNumber other) {

		BigNumber increment;

		if(other.isNegative())
			increment = BigNumber.fromString("-1");
		else increment = BigNumber.fromString("1");

		BigNumber otherCopy = other.copy();
		BigNumber result = BigNumber.fromString("0");

		while (!otherCopy.isZero()){
			otherCopy = otherCopy.subtract(increment);
			if (other.isNegative()) result = result.subtract(this);
			else result = result.add(this);
		}
		return result;
	}

	private BigNumber copy(){
		return BigNumber.fromString(this.toString());
	}

	public BigNumber divide(BigNumber rh) {
		return null;
	}

	public BigNumber getQuotient() {
		return null;
	}

	public BigNumber getMod() {
		return null;
	}

	/**
	 * Gets the inverse of a number.
	 * @return The negative of this BigNumber.
	 */
	public BigNumber inverse() {
		ArrayList<Integer> invertedDigits = new ArrayList<>(digits.size());

		// The inverse of 10's compliment is the number subtracted from all 9's, plus one.
		int carry = 1;
        for (Integer digit : digits) {
            int nextDigit = 9 - digit + carry;
            carry = nextDigit / 10;
            nextDigit = nextDigit % 10;
            invertedDigits.add(nextDigit);
        }

		truncate(invertedDigits);

		return new BigNumber(invertedDigits);
	}

	private boolean isZero() {
		return digits.size() == 1 && digits.get(0) == 0;
	}

	private boolean isNegative(){
		return GetLast(digits) > 4;
	}

	private static int charToInt(char character) {
		if(character < '0' || character > '9')
			throw new NumberFormatException("Character " + character + " is not an integer.");
		return character - '0';
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BigNumber other)
			return this.digits.equals(other.digits);

		return false;
	}

	@Override
	public int hashCode() {
		return digits.hashCode();
	}

	@Override
	public String toString() {
		if(isZero())
			return "0";

		boolean isNegative = isNegative();

		StringBuilder builder = new StringBuilder(digits.size() + (isNegative ? 1 : 0));

		if(isNegative) {
			builder.append('-');
			builder.append(this.inverse());
		} else {
			boolean skipLast = GetLast(digits) == 0;
			for(int i = digits.size() - (skipLast ? 1 : 0); i > 0; i--) {
				builder.append(digits.get(i - 1));
			}
		}

		return builder.toString();
	}
}
