import java.util.ArrayList;
import java.util.Objects;

public final class BigNumber {
	private final boolean negative;

	/**
	 * A list of numbers that make up the larger number. They are ordered from
	 * least significant to most.
	 * */
	private final ArrayList<Integer> numbers;

	/**
	 * Creates a BigNumber from a string of integers, optionally proceeded
	 * by a minus sign for negatives.
	 * @param text The string to parse into the BigNumber.
	 */
	public BigNumber(String text) {
		numbers = new ArrayList<>();

		char c = text.charAt(0);
		negative = (c == '-');

		for (int i = 0; i < text.length() - (negative ? 1 : 0); i++) {
			c = text.charAt(text.length() - i - 1);
			numbers.add(Integer.parseInt(Character.toString(c)));
		}
	}

	/**
	 * Creates a BigNumber from preformatted data.
	 * @param numbers The preformatted list of numbers to assign to the internal list.
	 * @param negative If the BigNumber is negative or not.
	 */
	private BigNumber(ArrayList<Integer> numbers, boolean negative) {
		this.numbers = numbers;
		this.negative = negative;
	}

	/**
	 * Adds rh and this BigNumber.
	 * @param rh The BigNumber added to this BigNumber.
	 * @return A new BigNumber that's the result of adding rh to this BigNumber.
	 */
	public BigNumber add(BigNumber rh) {
		// If A and B don't share a sign (A + -B or -A + B) you can do subtraction
		// instead and just invert the right hand argument (A + -B -> A - B or
		// -A + B -> -A - -B). This means that addition can be simplified, only
		// handling cases where A and B share a sign and therefor increase in magnitude.
		if(negative ^ rh.negative)
			return subtract(rh.inverse());

		ArrayList<Integer> added = new ArrayList<>();
		int size = numbers.size();
		int rhSize = rh.numbers.size();

		// This finds the smaller and bigger number list between the left and right
		// hand inputs, making it easy to loop through them fully regardless of which
		// is which.
		boolean leftIsSmaller = size < rhSize;

		int smallSize = leftIsSmaller ? size : rhSize;
		int bigSize = leftIsSmaller ? rhSize : size;

		ArrayList<Integer> smallList = leftIsSmaller ? numbers : rh.numbers;
		ArrayList<Integer> bigList = leftIsSmaller ? rh.numbers : numbers;

		// The first loop adds the smaller number parts to the bigger number parts,
		// carrying as needed until the smaller one runs out. If there is still a carry,
		// after the first loop ends, the second loop continues with just the larger number
		// until there is no longer a carry or the bigger number parts run out.
		boolean carry = false;
		// First loop
		for (int i = 0; i < smallSize; i++) {
			int part = bigList.get(i) + smallList.get(i) + (carry ? 1 : 0);
			if(part >= 10) {
				carry = true;
				part -= 10;
			} else {
				carry = false;
			}

			added.add(part);
		}
		// Second loop
		for(int i = smallSize; i < bigSize; i++) {
			int part = bigList.get(i) + (carry ? 1 : 0);
			if(part >= 10) {
				carry = true;
				part -= 10;
				added.add(part);
			} else {
				carry = false;
				added.add(part);
				added.addAll(bigList.subList(i + 1, bigSize));
				break;
			}
		}
		// End loops

		// If there is still a carry left over after both number parts run out,
		// it gets added to the end. (95 + 20 = >1<25)
		if(carry)
			added.add(1);
		return new BigNumber(added, negative).truncateZeros();
	}

	/**
	 * Subtracts rh and this BigNumber.
	 * @param rh The BigNumber subtracted from this BigNumber.
	 * @return A new BigNumber that's the result of subtracting rh from this BigNumber.
	 */
	public BigNumber subtract(BigNumber rh) {
		// If A and B don't share a sign (A - -B or -A - B) you can do addition
		// instead and just invert the right hand argument (A - -B -> A + B or
		// -A - B -> -A + -B). This means that subtraction can be simplified, only
		// handling cases where A and B share a sign and therefor decrease in magnitude.
		if(negative ^ rh.negative)
			return add(rh.inverse());

		ArrayList<Integer> subtracted = new ArrayList<>();
		int size = numbers.size();
		int rhSize = rh.numbers.size();

		// Order for subtraction matters, so if it's easier to subtract the right from the left
		// (such as when the right number is larger) we need to invert everything, including the
		// result after. This is because `a - b = -(b - a)`.
		boolean mustInvert = size < rhSize || (size == rhSize && numbers.get(size - 1) <= rh.numbers.get(size - 1));

		int smallSize = mustInvert ? size : rhSize;
		int bigSize = mustInvert ? rhSize : size;

		ArrayList<Integer> smallList = mustInvert ? numbers : rh.numbers;
		ArrayList<Integer> bigList = mustInvert ? rh.numbers : numbers;

		// The first loop subtracts the smaller number parts from the bigger number parts,
		// carrying as needed until the smaller one runs out. If there is still a carry,
		// after the first loop ends, the second loop continues with just the larger number
		// until there is no longer a carry or the bigger number parts run out.
		boolean carry = false;
		// First loop
		for (int i = 0; i < smallSize; i++) {
			int part = bigList.get(i) - smallList.get(i) - (carry ? 1 : 0);
			if(part < 0) {
				carry = true;
				part += 10;
			} else {
				carry = false;
			}

			subtracted.add(part);
		}
		// Second loop
		for(int i = smallSize; i < bigSize; i++) {
			int part = bigList.get(i) - (carry ? 1 : 0);
			if(part < 0) {
				carry = true;
				part += 10;
				subtracted.add(part);
			} else {
				subtracted.add(part);
				subtracted.addAll(bigList.subList(i + 1, bigSize));
				break;
			}
		}
		// End loops

		return new BigNumber(subtracted, negative ^ mustInvert).truncateZeros();
	}

	public BigNumber multiply(BigNumber rh) {
		return null;
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

	public BigNumber inverse() {
		return new BigNumber(numbers, !negative);
	}

	private BigNumber truncateZeros() {
		int size = numbers.size();
		for(int i = 1; i < size; i++) {
			if(numbers.get(size - i) != 0)
				break;
			numbers.remove(size - i);
		}
		return this;
	}

	private boolean isZero() {
		return numbers.size() == 1 && numbers.get(0) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(negative, numbers);
	}

	@Override
	public String toString() {
		if(isZero())
			return "0";

		StringBuilder builder = new StringBuilder(numbers.size() + (negative ? 1 : 0));
		if(negative)
			builder.append('-');
		for(int i = 0; i < numbers.size(); i++) {
			builder.append(numbers.get(numbers.size() - 1 - i));
		}
		return builder.toString();
	}
}
