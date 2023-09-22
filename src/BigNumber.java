import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

public final class BigNumber {
    private final boolean negative;
    private final ArrayList<Integer> numbers;
    public BigNumber(String text) {
        numbers = new ArrayList<>();

        char c = text.charAt(0);
        if(c == '-')
            negative = true;
        else {
            negative = false;
            numbers.add(Integer.parseInt(Character.toString(c)));
        }

        for (int i = 1; i < text.length(); i++) {
            c = text.charAt(text.length() - i);
            numbers.add(Integer.parseInt(Character.toString(c)));
        }
    }

    // author
    private BigNumber(ArrayList<Integer> numbers, boolean negative) {
        this.numbers = numbers;
        this.negative = negative;
    }

    public BigNumber add(BigNumber rh) {
        if(negative ^ rh.negative)
            return subtract(rh.inverse());

        ArrayList<Integer> added = new ArrayList<>();
        boolean carry = false;

        int smallSize;
        int bigSize;
        ArrayList<Integer> smallList;
        ArrayList<Integer> bigList;
        if(numbers.size() <= rh.numbers.size()) {
            smallSize = numbers.size();
            smallList = numbers;
            bigSize = rh.numbers.size();
            bigList = rh.numbers;
        } else {
            smallSize = rh.numbers.size();
            smallList = rh.numbers;
            bigSize = numbers.size();
            bigList = numbers;
        }

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

        for(int i = smallSize; i < bigSize; i++) {
            int part = bigList.get(i) + (carry ? 1 : 0);
            if(part >= 10) {
                carry = true;
                part -= 10;
                added.add(part);
            } else {
                added.addAll(bigList.subList(i, bigSize));
                break;
            }
        }
        if(carry)
            added.add(1);

        return new BigNumber(added, negative);
    }

    // 1000 - 1
    public BigNumber subtract(BigNumber rh) {
        if(negative ^ rh.negative)
            return add(rh.inverse());

        ArrayList<Integer> subtracted = new ArrayList<>();
        boolean carry = false;
        int size = numbers.size();
        int rhSize = rh.numbers.size();

        // Order matters so we need to record if we need to inverse the result.
        boolean mustInverse = false;
        // We record the last non-zero number so that we can truncate to it if needed.
        int lastNotZero = 0;
        int smallSize;
        int bigSize;
        ArrayList<Integer> smallList;
        ArrayList<Integer> bigList;
        if(size < rhSize || (size == rhSize && numbers.get(size - 1) <= rh.numbers.get(size - 1))) {
            mustInverse = true;
            smallSize = size;
            smallList = numbers;
            bigSize = rhSize;
            bigList = rh.numbers;
        } else {
            smallSize = rhSize;
            smallList = rh.numbers;
            bigSize = size;
            bigList = numbers;
        }
        // 1000 - 2000
        // -(2000 - 1000)
        for (int i = 0; i < smallSize; i++) {
            int part = bigList.get(i) - smallList.get(i) - (carry ? 1 : 0);
            if(part < 0) {
                carry = true;
                part += 10;
                lastNotZero = i;
            } else {
                if(part != 0)
                    lastNotZero = i;
                carry = false;
            }

            subtracted.add(part);
        }
        for(int i = smallSize; i < bigSize; i++) {
            int part = bigList.get(i) - (carry ? 1 : 0);
            if(part < 0) {
                carry = true;
                part += 10;
                lastNotZero = i;
                subtracted.add(part);
            } else {
                if(part != 0)
                    lastNotZero = i;
                subtracted.addAll(bigList.subList(i, bigSize));
                break;
            }
        }

        subtracted = new ArrayList<>(subtracted.subList(0, lastNotZero + 1));
        return new BigNumber(subtracted, negative ^ mustInverse);
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

    @Override
    public int hashCode() {
        return Objects.hash(negative, numbers);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(numbers.size() + (negative ? 1 : 0));
        if(negative)
            builder.append('-');
        for(int i = 0; i < numbers.size(); i++) {
            builder.append(numbers.get(numbers.size() - 1 - i));
        }
        return builder.toString();
    }
}
