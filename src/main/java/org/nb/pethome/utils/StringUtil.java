package org.nb.pethome.utils;

public class StringUtil {

    public  static boolean isEmpty(String s){
        return  s==null||s.isEmpty();

    }

    public static boolean isNullOrNullStr(String s){
        return s==null||s.equals("null");
    }
}
