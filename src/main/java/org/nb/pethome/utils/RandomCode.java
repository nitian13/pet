package org.nb.pethome.utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomCode {
    public static String getCode(){
        return String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
    }
}
