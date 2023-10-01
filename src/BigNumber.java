import java.util.ArrayList;
import java.util.Objects;

public final class BigNumber {

	//private final boolean negative;

	/**
	 * A list of numbers that make up the larger number. They are ordered from
	 * least significant to most.
	 * */
	private final ArrayList<Integer> digits;

	/**
	 * Creates a BigNumber from a string of integers, optionally proceeded
	 * by a minus sign for negatives.
	 * @param text The string to parse into the BigNumber.
	 */
	public static BigNumber fromString(String text) {
		ArrayList<Integer> digits = new ArrayList<>();

		char nextDigit = text.charAt(0);
		boolean isNegative = (nextDigit == '-');

		for (int reverseStringIndex = 0; reverseStringIndex < text.length() - (isNegative ? 1 : 0); reverseStringIndex++) {
			nextDigit = text.charAt(text.length() - reverseStringIndex - 1);
			digits.add(Integer.parseInt(Character.toString(nextDigit)));
		}

		return new BigNumber(digits);
	}

	/**
	 * Creates a BigNumber from preformatted data.
	 * @param digits The preformatted list of numbers to assign to the internal list.
	 */
	private BigNumber(ArrayList<Integer> digits) {
		this.digits = digits;
	}

	/**
	 * Adds other and this BigNumber.
	 * @param other The BigNumber added to this BigNumber.
	 * @return A new BigNumber that's the result of adding other to this BigNumber.
	 */
	public BigNumber add(BigNumber other) {
		// If A and B don't share a sign (A + -B or -A + B) you can do subtraction
		// instead and just invert the right hand argument (A + -B -> A - B or
		// -A + B -> -A - -B). This means that addition can be simplified, only
		// handling cases where A and B share a sign and therefor increase in magnitude.
		ArrayList<Integer> addedDigits = new ArrayList<>();

		// This finds the smaller and bigger number list between the left and right
		// hand inputs, making it easy to loop through them fully regardless of which
		// is which.
		int thisSize = this.digits.size();
		int otherSize = other.digits.size();

		//todo: Get rid of this. An addition method has no right to mutate it's two additives. Were this any other language I would just copy the two objects but Java is terrible.
		int largerSize;

		if (thisSize < otherSize) largerSize =  otherSize;
		else largerSize = thisSize;

		// This is essential for preventing wierd issues with 10's complements. It basically lets us ignore the final carry bit. It can sometimes be redundant but that's not a huge problem.
		largerSize += 1;
		this.extendSelf(largerSize);
		other.extendSelf(largerSize);

		int carryBit = 0; // Should always equal 0 or 1, so we can be a bit hacky with it for brevity & a tiny bit of performance.

		for(int i = 0; i < digits.size(); i ++ ){
			int nextDigit = 0;
			nextDigit += this.digits.get(i);
			nextDigit += other.digits.get(i);
			nextDigit += carryBit;
			if (nextDigit > 9){
				carryBit = 1;
				nextDigit -= 10;
			}
			else carryBit = 0;
			addedDigits.add(nextDigit);
		}
		carryBit = 0;
		return new BigNumber(addedDigits).truncate();
	}

	private void extendSelf(int digitCount){
		int extensionDigit;
		if (this.isNegative()) extensionDigit = 9;
		else extensionDigit = 0;
		while(this.digits.size() < digitCount){
			this.digits.add(extensionDigit);
		}
	}


	/**
	 * Subtracts rh and this BigNumber.
	 * @param other The BigNumber subtracted from this BigNumber.
	 * @return A new BigNumber that's the result of subtracting rh from this BigNumber.
	 */
	public BigNumber subtract(BigNumber other) {
		BigNumber negativeOther = other.inverse();
		return this.add(negativeOther);
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
		ArrayList<Integer>    invertedDigits = new ArrayList<Integer>();
		int firstNonZeroDigit = -1;
		for(int digitIndex = 0; digitIndex < this.digits.size(); digitIndex ++ ){
			Integer nextDigit = this.digits.get(digitIndex);
			if (nextDigit != 0){
				firstNonZeroDigit = digitIndex;
				break;
			}
			else{
				invertedDigits.add(nextDigit);
			}
		}
		if(firstNonZeroDigit == -1) return new BigNumber(invertedDigits);
		invertedDigits.add(10 - this.digits.get(firstNonZeroDigit));
		for (int digitIndex = firstNonZeroDigit + 1; digitIndex < this.digits.size(); digitIndex ++){
			invertedDigits.add(9 - this.digits.get(digitIndex));
		}

		return new BigNumber(invertedDigits);
	}

	private BigNumber truncate() {
		int targetedDigit;
		if (this.isNegative()) targetedDigit = 9;
		else targetedDigit = 0;

		for(int i = (this.digits.size() - 1); i >= 0; i -- ){
			if (this.digits.get(i) != targetedDigit) break;
			else this.digits.remove(i);
		}

		return this;
	}

	/**
	 * todo: ensure that this doesn't give false negatives with multiple zero digits
	 * @return
	 */
	private boolean isZero() {
		return digits.size() == 1 && digits.get(0) == 0;
	}

	private boolean isNegative(){
		int highOrder = this.digits.get(this.digits.size() - 1);
		return (highOrder > 4);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BigNumber)) return false;
		BigNumber other = (BigNumber) obj;

		if (this.digits.size() != other.digits.size()) return false;

		for (int i = 0; i < other.digits.size(); i++){
			if (!this.digits.get(i).equals(other.digits.get(i))) return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(digits);
	}

	@Override
	public String toString() {
		if(isZero())
			return "0";

		StringBuilder builder = new StringBuilder(this.digits.size() + (this.isNegative() ? 1 : 0));
		if(this.isNegative())
			builder.append('-');
		BigNumber printedNumber;
		if (this.isNegative()) printedNumber = this.inverse();
		else printedNumber = this;

		for(int i = 0; i < printedNumber.digits.size(); i++) {
			builder.append(printedNumber.digits.get(printedNumber.digits.size() - 1 - i));
		}
		return builder.toString();
	}

}
