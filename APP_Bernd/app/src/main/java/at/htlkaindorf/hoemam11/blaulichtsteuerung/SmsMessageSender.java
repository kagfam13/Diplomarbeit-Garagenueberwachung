package at.htlkaindorf.hoemam11.blaulichtsteuerung;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SmsMessageSender
{
    private static final String SENT = "SMS_SENT";

    private static void setupReceiver(final Context context)
    {
        final BroadcastReceiver receiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context arg0, Intent arg1)
            {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(arg0, R.string.sms_sent, Toast.LENGTH_SHORT).show();
                        context.unregisterReceiver(this);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(arg0, R.string.sms_failure, Toast.LENGTH_LONG).show();
                        context.unregisterReceiver(this);
                        MainActivity.sendingAllowed = true;
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(arg0, R.string.sms_no_service, Toast.LENGTH_LONG).show();
                        context.unregisterReceiver(this);
                        MainActivity.sendingAllowed = true;
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(arg0, R.string.sms_null_pdu, Toast.LENGTH_LONG).show();
                        context.unregisterReceiver(this);
                        MainActivity.sendingAllowed = true;
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(arg0, R.string.sms_radio_off, Toast.LENGTH_LONG).show();
                        context.unregisterReceiver(this);
                        MainActivity.sendingAllowed = true;
                        break;
                    default:
                        context.unregisterReceiver(this);
                    break;
                }
            }
        };
        context.registerReceiver(receiver, new IntentFilter(SENT));
    }

    public static void send(String message, String number, Context context)
    {
        SmsManager sms = SmsManager.getDefault();

        setupReceiver(context);

        final PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);

        sms.sendTextMessage(number, null, message, sentPI, null);
    }
}
