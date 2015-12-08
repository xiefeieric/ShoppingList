package uk.me.feixie.shoppinglist.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Fei on 06/12/2015.
 */
public class NumberHelper {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
