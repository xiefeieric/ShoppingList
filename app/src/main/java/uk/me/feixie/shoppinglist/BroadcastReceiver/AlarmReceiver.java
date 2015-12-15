package uk.me.feixie.shoppinglist.BroadcastReceiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import uk.me.feixie.shoppinglist.R;
import uk.me.feixie.shoppinglist.activity.AddEditActivity;
import uk.me.feixie.shoppinglist.model.Item;
import uk.me.feixie.shoppinglist.model.ShopList;
import uk.me.feixie.shoppinglist.utils.UIUtils;

public class AlarmReceiver extends BroadcastReceiver {

    private PendingIntent resultPendingIntent;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        UIUtils.showToast(context, "Time Up!");

        ShopList shopList = (ShopList) intent.getSerializableExtra("shop_list");
        Item item = (Item) intent.getSerializableExtra("item");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_warning_white_24dp)
                        .setVibrate(new long[]{100,200,300})
                        .setContentTitle(item.getName()+" is Expiring Soon")
                        .setContentText("Touch for more information");

        //set to default notification sound
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);

        Intent resultIntent = new Intent(context, AddEditActivity.class);
        resultIntent.putExtra("shop_list",shopList);

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
