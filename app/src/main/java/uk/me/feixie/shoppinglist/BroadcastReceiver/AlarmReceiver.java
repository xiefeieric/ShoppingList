package uk.me.feixie.shoppinglist.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import uk.me.feixie.shoppinglist.utils.UIUtils;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        UIUtils.showToast(context,"Time Up!");
    }
}
