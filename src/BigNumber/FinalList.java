package BigNumber;

import java.util.*;

public final class FinalList<T> implements Iterable<T> {
	public final int length;
	private final T[] data;

	public FinalList(final ArrayList<T> data) {
		this.data = (T[]) data.toArray();
		length = this.data.length;
	}

	public T get(final int index) {
		return data[index];
	}

	public T getLast() {
		return data[length - 1];
	}

	@Override
	public Iterator<T> iterator() {
		return Arrays.stream(data).iterator();
	}
}
