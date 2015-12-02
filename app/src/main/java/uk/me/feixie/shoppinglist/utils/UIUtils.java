package uk.me.feixie.shoppinglist.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Fei on 02/12/2015.
 */
public class UIUtils {

    private static Toast mToast;

    public static void showToast(Context context, String msg) {
        if (mToast==null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

}
