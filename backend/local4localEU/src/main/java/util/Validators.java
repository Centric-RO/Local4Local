package util;

import java.util.regex.Pattern;

public class Validators {

    public static boolean isKvkValid(String number) {
        String regexPattern = "^[0-9]{8}$";

        return Pattern.compile(regexPattern)
                .matcher(number)
                .matches();
    }

    public static boolean isValidUrl(String url) {
        String regexPattern = "^https://[^\s/$.?#].[^\s]*$";

        return Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE)
                .matcher(url)
                .matches();
    }
}
