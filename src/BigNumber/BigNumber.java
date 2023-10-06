package BigNumber;

import java.util.ArrayList;
import java.util.Stack;

import static BigNumber.Helper.*;

public final class BigNumber implements Comparable<BigNumber> {
	public static final BigNumber Zero = BigNumber.fromString("0");
	public static final BigNumber One = BigNumber.fromString("1");

	/**
	 * A list of numbers that make up the larger number. They are ordered from
	 * least significant to most.
	 * */
	private final FinalList<Integer> digits;

	/**
	 * Creates a BigNumber from preformatted data.
	 * @param digits The preformatted list of numbers to assign to the internal list.
	 */
	private BigNumber(ArrayList<Integer> digits) {
		this.digits = new FinalList<>(digits);
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
			return new BigNumber(digits).negate();
		else
        	return new BigNumber(digits);
	}

	/**
	 * Adds other and this BigNumber.
	 * @param other The BigNumber added to this BigNumber.
	 * @return A new BigNumber that's the result of adding other to this BigNumber.
	 */
	public BigNumber add(BigNumber other) {
		int thisSize = this.digits.length;
		int otherSize = other.digits.length;

		// This finds the smaller and bigger number list between the left and right
		// hand inputs, making it easy to loop through them fully regardless of which
		// is which.
		boolean thisIsSmaller = thisSize < otherSize;

		int smallSize = thisIsSmaller ? thisSize : otherSize;
		int bigSize = thisIsSmaller ? otherSize : thisSize;

		FinalList<Integer> smallList = thisIsSmaller ? this.digits : other.digits;
		FinalList<Integer> bigList = thisIsSmaller ? other.digits : this.digits;

		boolean shouldBeNegative = this.digits.getLast() > 4 && other.digits.getLast() > 4;
		boolean shouldBePositive = this.digits.getLast() <= 4 && other.digits.getLast() <= 4;

		// The first loop adds the smaller number parts to the bigger number parts,
		// carrying as needed until the smaller one runs out. The second loop then
		// continues with just the larger number and relevant padding for 10's
		// compliment.

		int carry = 0;
		int pad = smallList.getLast() > 4 ? 9 : 0;
		ArrayList<Integer> addedDigits = new ArrayList<>(bigSize);

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
	 * Subtracts other from this BigNumber.
	 * @param other The BigNumber subtracted from this BigNumber.
	 * @return A new BigNumber that's the result of subtracting other from this BigNumber.
	 */
	public BigNumber subtract(BigNumber other) {
		return add(other.negate());
	}

	/**
	 * Multiplies other with this BigNumber
	 * @param other The BigNumber to multiply by.
	 * @return A new BigNumber that's the result of multiplying other with this BigNumber.
	 */
	public BigNumber multiply(BigNumber other) {
		// We will add X to itself Y times.
		BigNumber x = this;
		BigNumber y = other;

		BigNumber increment = BigNumber.One;
		Stack<BigNumber[]> previous = new Stack<>();

		boolean yIsNegative = y.isNegative();

		if(yIsNegative)
			y = y.negate();

		// While the increment is less than y
		while(increment.compareTo(y) <= 0) {
			previous.add(new BigNumber[]{x, increment});
			// Double the increment and double the X
			increment = increment.add(increment);
			x = x.add(x);
		}

		BigNumber result = BigNumber.Zero;
		while (!y.isZero()){
			// 0 is x * (2 ^ increment), 1 is the increment used.
			BigNumber[] popped = previous.pop();
			// While y is greater than the increment
			while(y.compareTo(popped[1]) >= 0) {
				y = y.subtract(popped[1]);
				if(yIsNegative)
					result = result.subtract(popped[0]);
				else
					result = result.add(popped[0]);
			}
		}
		return result;
	}

	/**
	 * Divides other from this BigNumber
	 * @param other The BigNumber to divide by.
	 * @return A BigNumber array of length 2, where [0] is the quotient and [1] is the mod.
	 */
	public BigNumber[] divide(BigNumber other) {
		BigNumber dividend = this;
		BigNumber divisor = other;

		boolean dividendIsNegative = dividend.isNegative();
		boolean divisorIsNegative = divisor.isNegative();

		if(dividendIsNegative)
			dividend = dividend.negate();
		if(divisorIsNegative)
			divisor = divisor.negate();

		BigNumber quotient = BigNumber.Zero;
		BigNumber mod = dividend;
		Stack<BigNumber[]> previous = new Stack<>();


		BigNumber change = BigNumber.One;
		while(divisor.compareTo(dividend) < 0) {
			previous.add(new BigNumber[]{divisor, change});
			change = change.add(change);
			divisor = divisor.add(divisor);
		}

		while(dividend.compareTo(other) >= 0 && !previous.isEmpty()) {
			// 0 is divisor, 1 is quotient multiplier
			BigNumber[] popped = previous.pop();
			while(dividend.compareTo(popped[0]) >= 0) {
				dividend = dividend.subtract(popped[0]);
				quotient = quotient.add(popped[1]);
				mod = dividend;
			}
		}

		if(dividendIsNegative ^ divisorIsNegative)
			return new BigNumber[] {quotient.negate(), mod.negate()};
		else
			return new BigNumber[] {quotient, mod};
	}

	/**
	 * Gets the inverse of a number.
	 * @return The negative of this BigNumber.
	 */
	public BigNumber negate() {
		ArrayList<Integer> invertedDigits = new ArrayList<>(digits.length);

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

	/**
	 * Checks if this BigNumber equals zero.
	 * @return True if it equals zero, otherwise false.
	 */
	public boolean isZero() {
		return digits.length == 1 && digits.get(0) == 0;
	}

	/**
	 * Checks if this BigNumber is negative.
	 * @return True if it is negative, otherwise false.
	 */
	public boolean isNegative(){
		return digits.getLast() > 4;
	}

	/**
	 * Converts a character (0123456789) to its equivalent integer.
	 * @param character The character to convert.
	 * @return The equivalent integer.
	 */
	private static int charToInt(char character) {
		if(character < '0' || character > '9')
			throw new NumberFormatException("Character " + character + " is not an integer.");
		return character - '0';
	}

	/**
	 * Truncates the list, removing and leading 0's or 9's.
	 * @param digits The list to truncate.
	 */
	private static void truncate(ArrayList<Integer> digits) {
		int last = GetLast(digits);
		if(last != 0 && last != 9)
			return;

		int size = digits.size();

		// Remove leading 9's or 0's, leaving one if necessary
		for(int i = size - 1; i > 0; i--) {
			int digit = digits.get(i - 1);
			if(digit != last) {
				if(last == 0 && digit <= 4)
					digits.remove(i);
				if(last == 9 && digit >= 5)
					digits.remove(i);
				break;
			}
			digits.remove(i);
		}
	}

	@Override
	public int compareTo(BigNumber other) {
		if(this.isNegative()) {
			if(!other.isNegative())
				return -1;
			// Other is also negative
			if(this.digits.length > other.digits.length)
				return -1;
			if(this.digits.length < other.digits.length)
				return 1;
			if(this.digits.getLast() < other.digits.getLast())
				return -1;
			if(this.digits.getLast() > other.digits.getLast())
				return 1;
		} else {
			if(other.isNegative())
				return 1;
			if(this.digits.length > other.digits.length)
				return 1;
			if(this.digits.length < other.digits.length)
				return -1;
			if(this.digits.getLast() < other.digits.getLast())
				return -1;
			if(this.digits.getLast() > other.digits.getLast())
				return 1;
		}

		BigNumber result = this.subtract(other);
		if(result.isZero())
			return 0;
		if(result.isNegative())
			return -1;
		else
			return 1;
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

		StringBuilder builder = new StringBuilder(digits.length + (isNegative ? 1 : 0));

		if(isNegative) {
			builder.append('-');
			builder.append(this.negate());
		} else {
			boolean skipLast = digits.getLast() == 0;
			for(int i = digits.length - (skipLast ? 1 : 0); i > 0; i--) {
				builder.append(digits.get(i - 1));
			}
		}

		return builder.toString();
	}
}
