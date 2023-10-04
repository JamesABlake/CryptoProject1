package BigNumber;

import java.util.ArrayList;

public class Helper {
    public static <T> T GetLast(ArrayList<T> list) {
        return list.get(list.size() - 1);
    }
}
