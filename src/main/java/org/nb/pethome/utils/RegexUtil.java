package org.nb.pethome.utils;

public class RegexUtil {
    public static boolean isPhoneValid(String phone){
        String regex = "^1[3456789]\\d{9}$";
        return phone.matches(regex);
    }
}
