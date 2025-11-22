package agluzhin.pull_request_system.core.utils;

import java.util.ArrayList;

public class DataValidator {
    public static boolean isNullOrEmptyString(String... strings) {
        for(String element : strings) {
            if (element == null || element.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean isNullOrEmptyArrayList(ArrayList<T> value) {
        return value == null || value.isEmpty();
    }

    public static <T> boolean isNull(T value) {
        return value == null;
    }
}
