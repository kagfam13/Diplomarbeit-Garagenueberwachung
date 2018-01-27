package at.htlkaindorf.hoemam11.blaulichtsteuerung;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.Patterns;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;

import at.htlkaindorf.hoemam11.blaulichtsteuerung.GateControlling.GateControllingActivity;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener //implements AdapterView.OnItemSelectedListener
{
    private InetAddress address;
    // String-Konstanten für onSaveInstanceState
    private static final String USER_ON_SIMULATIONS_PAGE = "simulations";
    private static final String SENDING_ALLOWED = "sending_allowed";
    private static final String TAG = "MainActivity";

    // Variable statisch, dass auch von der SmsMessageSender-Klasse darauf zugegriffen werden kann
    public static Boolean sendingAllowed = true;

    private Boolean userOnSimulationsPage = false;
    private IncomingSms receiver = null;
    private String lastMessage;

    private SharedPreferences sharedPreferences;

    private Scene mainScene;
    private Scene lightTestScene;
    private final Transition mFadeTransition = new Fade();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null)
        {
            userOnSimulationsPage = savedInstanceState.getBoolean(USER_ON_SIMULATIONS_PAGE);
            sendingAllowed = savedInstanceState.getBoolean(SENDING_ALLOWED);
        }

        setContentView(R.layout.activity_main);
        ViewGroup mSceneRoot = (ViewGroup)findViewById(R.id.scene_root);

        mainScene = Scene.getSceneForLayout(mSceneRoot, R.layout.layout_main, this);
        lightTestScene = Scene.getSceneForLayout(mSceneRoot, R.layout.layout_lighttests, this);

        if(userOnSimulationsPage)
        {
            TransitionManager.go(lightTestScene, mFadeTransition);
        }

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");

        if(receiver==null)
            receiver = new IncomingSms();

        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        unregisterReceiver(receiver);
    }

    // Speichern der Variablen, wenn Bildschirm gedreht wird
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putBoolean(USER_ON_SIMULATIONS_PAGE, userOnSimulationsPage);
        outState.putBoolean(SENDING_ALLOWED, sendingAllowed);
    }

    public void onButtons(final View view) // final um in innerer Klasse verwenden zu können
    {
        if(view.getId() == R.id.alarm)
        {
            MainDialogFragment.getInstance().init(getString(R.string.dialog_alarmDescription), getString(R.string.dialog_alarmTitle),R.layout.dialog_simulations).show(getFragmentManager(),"tag");
        }
        else if(view.getId() == R.id.lightTestPage)
        {
            TransitionManager.go(lightTestScene, mFadeTransition);
            userOnSimulationsPage = true;
        }
        else if(view.getId() == R.id.mailHistoryOrErrors)
        {
            MainDialogFragment.getInstance().init(getString(R.string.dialog_mailDescription),getString(R.string.button_mail_history),R.layout.dialog_mail_history_or_errors).show(getFragmentManager(), "tag");
        }
        else if(view.getId() == R.id.button_back)
        {
            TransitionManager.go(mainScene, mFadeTransition);
            userOnSimulationsPage = false;
        }
        else if(view.getId() == R.id.lights_on_button)
        {
            MainDialogFragment.getInstance().init(getString(R.string.dialog_lights_onDescription),getString(R.string.button_lights_on),R.layout.dialog_lights_on).show(getFragmentManager(), "tag");
        }
        else if(view.getId() == R.id.lights_off_button)
        {
            MainDialogFragment.getInstance().init(getString(R.string.dialog_lights_offDescription),getString(R.string.button_lights_off),R.layout.dialog_lights_off).show(getFragmentManager(), "tag");
        }
        else if(view.getId() == R.id.door)
        {
            MainDialogFragment.getInstance().init(getString(R.string.dialog_doorDescription),getString(R.string.button_door),R.layout.dialog_door).show(getFragmentManager(), "tag");
        }
        else if (view.getId() == R.id.nicht_belegt)
        {
            if (address != null) {
                Intent intent = new Intent(this, GateControllingActivity.class);
                intent.putExtra("ADDRESS",address.getAddress());
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(),"Zuerst IP-Adresse eingeben.",Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            String message, title;
            switch(view.getId())
            {
                case R.id.blaulicht_on:
                    message = getString(R.string.dialog_blaulicht_onDescription);
                    title = getString(R.string.button_blaulicht_on); break;
                case R.id.blaulicht_off:
                    message = getString(R.string.dialog_blaulicht_offDescription);
                    title = getString(R.string.button_blaulicht_off); break;
                case R.id.test_connection:
                    message = getString(R.string.dialog_test_connectionDescription);
                    title = getString(R.string.button_test_connection); break;
                // obsolet
                /*case R.id.test_leds:
                    message = getString(R.string.help_leds);
                    title = getString(R.string.button_test_leds); break;
                case R.id.test_blink:
                    message = getString(R.string.help_blink);
                    title = getString(R.string.button_test_blink); break;
                case R.id.test_circle:
                    message = getString(R.string.help_circle);
                    title = getString(R.string.button_test_circle); break;
                case R.id.test_all:
                    message = getString(R.string.help_all);
                    title = getString(R.string.button_test_all); break;
                case R.id.lights_on:
                    title = getString(R.string.button_lights_on);
                    message = getString(R.string.help_lights_on); break;*/
                case R.id.lights_off:
                    title = getString(R.string.button_lights_off);
                    message = getString(R.string.help_lights_off); break;
                case R.id.stop_all:
                    title = getString(R.string.button_stop_all);
                    message = getString(R.string.help_stop); break;
                default: message = "error";
                    title = "error";
            }
            if(view.getId() != R.id.nicht_belegt)
                MainDialogFragment.getInstance().init(message,title, view.getId()).show(getFragmentManager(), "tag");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch(requestCode)
        {
            case Constants.PERMISSION_READ_PHONE_STATE:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendCommand(lastMessage);
                } else {
                    showToast(R.string.error_permission_sms);
                    Log.e(TAG,getString(R.string.error_permission_sms));
                }
                break;
            }
            case Constants.PERMISSION_SEND_SMS:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendCommand(lastMessage);
                } else {
                    showToast(R.string.error_permission_sms);
                    Log.e(TAG,getString(R.string.error_permission_sms));
                }
                break;
            }
            case Constants.PERMISSION_READ_CONTACTS:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    MainDialogFragment.getInstance().startActivityForResult(intent, Constants.PICK_CONTACT);
                } else {
                    showToast(R.string.error_permission_contacts);
                    Log.e(TAG, getString(R.string.error_permission_contacts));
                }
                break;
            }
        }
    }

    private void sendCommand(String message)
    {
        if(sendingAllowed)
        {
            lastMessage = message;

            String number = sharedPreferences.getString(Constants.PHONE_NUMBER, "");
            if (Patterns.PHONE.matcher(number).matches() && number.length() > 10 && number.length() < 15)
            {
                int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
                // Fix für oneplus. Funktioniert!
                if (permissionCheck != PackageManager.PERMISSION_GRANTED)
                    //if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_PHONE_STATE))
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, Constants.PERMISSION_READ_PHONE_STATE);

                if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.SEND_SMS))
                    {
                        // Berechtigung zwingend erforderlich
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, Constants.PERMISSION_SEND_SMS);

                    }
                    else
                    {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},Constants.PERMISSION_SEND_SMS);
                    }
                }
                else
                {
                    // Sicherstellen, dass Nummer geändert wird, falls sie geändert wird
                    SmsMessageSender.send(message,number,this);
                    sendingAllowed = false;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            sendingAllowed = true;
                        }
                    }, 15000);
                }


            } else
            {
                if (number.length() < 10)
                    showToast(R.string.too_short);
                else if (number.length() > 14)
                    showToast(R.string.too_long);
                else
                    showToast( R.string.wrong_format);
                changeNumber(null);
            }
        }
        else
            showToast(R.string.wait_for_answer);
    }

    public void onDialogPositiveClick(MainDialogFragment dialog)
    {
        switch(dialog.getLayoutOrButtonId())
        {
            case R.layout.dialog_simulations:
                if(sharedPreferences.getBoolean(Constants.SILENT_ALARM_CHECKED,false))
                    // workaround: Resource-IDs dürfen keine Leerzeichen enthalten, und Blaulicht überprüft, ob "Stiller Alarm" in der SMS vorkommt
                    sendCommand("Stiller Alarm");
                else
                    sendCommand(getResources().getResourceEntryName(R.id.Sirenenalarm));
                break;
            case R.layout.dialog_door_temporary:
                int pos = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE).getInt(Constants.SELECTED_TIME_SPINNER_ITEM,0);
                pos++;
                String befehl = "door_" + pos;
                sendCommand(befehl);
                break;
            case R.layout.dialog_door:
                    int doorModeId = sharedPreferences.getInt(Constants.CHECKED_DOOR_MODE,0);
                    switch(doorModeId)
                    {
                        case R.id.open_door:
                            MainDialogFragment.getInstance().init(getString(R.string.dialog_door_temporaryDescription), getString(R.string.dialog_door_temporary), R.layout.dialog_door_temporary).show(getFragmentManager(), "tag");
                            break;
                        case R.id.door_unlock:
                            sendCommand("door_unlock"); break;
                        case R.id.door_lock:
                            sendCommand("door_lock"); break;
                    }
                break;
            case R.layout.dialog_lights_on:
                int lightsOnId = sharedPreferences.getInt(Constants.CHECKED_LIGHT_MODE_ON,0);
                sendCommand(getResources().getResourceEntryName(lightsOnId));
                break;

            case R.layout.dialog_lights_off:
                int lightsOffId = sharedPreferences.getInt(Constants.CHECKED_LIGHT_MODE_OFF,0);
                sendCommand(getResources().getResourceEntryName(lightsOffId));
                break;

            case R.layout.dialog_mail:
                EditText dialogInput = (EditText)dialog.getDialog().findViewById(R.id.input);
                saveMailAddress(dialogInput.getText().toString());
                break;
            case R.layout.dialog_number:
                dialogInput = (EditText)dialog.getDialog().findViewById(R.id.input);
                savePhoneNumber(dialogInput.getText().toString().trim());
                break;
            case R.layout.dialog_mail_history_or_errors:
                String inputAddress = sharedPreferences.getString(Constants.EMAIL_ADDRESS, "");
                //  überprüfen ob gültige Email
                if (Patterns.EMAIL_ADDRESS.matcher(inputAddress).matches())
                {
                    if(sharedPreferences.getBoolean(Constants.MAIL_HISTORY_CHECKED,false))
                        // workaround: Resource-IDs dürfen keine Leerzeichen enthalten, und Blaulicht überprüft, ob "Stiller Alarm" in der SMS vorkommt
                        sendCommand(String.format("%s|%s|",getResources().getResourceEntryName(R.id.mail_history),inputAddress));
                    else
                        sendCommand(String.format("%s|%s|",getResources().getResourceEntryName(R.id.mail_errors),inputAddress));
                }
                // ungültige E-Mail-Adresse: z.B. wenn noch keine E-Mail-Adresse eingegeben wurde
                else
                {
                    showToast(R.string.invalid_email);
                    changeMail(null);
                }
                break;
            // wenn einer der Buttons gedrückt wurde
            default:
                sendCommand(getResources().getResourceEntryName(dialog.getLayoutOrButtonId()));
        }
    }

    private void savePhoneNumber(String number)
    {
        sharedPreferences.edit().putString(Constants.PHONE_NUMBER, number).apply();
    }

    private void saveMailAddress(String mail)
    {
        sharedPreferences.edit().putString(Constants.EMAIL_ADDRESS, mail).apply();
    }

    public void changeNumber(MenuItem mi)
    {
        MainDialogFragment.getInstance().init(getString(R.string.dialog_changeNumberDescription), getString(R.string.dialog_changeNumber), R.layout.dialog_number).show(getFragmentManager(), "tag");
    }

    public void changeMail(MenuItem mi)
    {
        MainDialogFragment.getInstance().init(getString(R.string.dialog_enterEmailDescription),getString(R.string.dialog_enterEmail), R.layout.dialog_mail).show(getFragmentManager(), "tag");
    }

    public void reset_rspi(MenuItem mi)
    {
        MainDialogFragment.getInstance().init(getString(R.string.dialog_restartDescription), getString(R.string.dialog_restart), R.id.reset_rspi).show(getFragmentManager(),"tag");
    }

    public void shutdown_rspi(MenuItem mi)
    {
        MainDialogFragment.getInstance().init(getString(R.string.dialog_shutdownDescription), getString(R.string.dialog_shutdown), R.id.shutdown_rspi).show(getFragmentManager(),"tag");
    }

    public void prepaid_credit(MenuItem mi)
    {
        MainDialogFragment.getInstance().init(getString(R.string.dialog_prepaid_creditDescription), getString(R.string.dialog_prepaid_credit), R.id.prepaid_credit).show(getFragmentManager(),"tag");
    }

    public void showAboutDialog(MenuItem mi)
    {
        try
        {
            String versionName;
            versionName = getString(R.string.app_name) + " v" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

            MainDialogFragment.getInstance().init(versionName, getString(R.string.about), R.layout.dialog_about).show(getFragmentManager(), "tag");
        }
        catch(Exception ex)
        {
            Log.e(TAG, getString(R.string.error_version_name));
        }
    }
    public void onIpMi(MenuItem mi)
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Bitte geben sie die Addresse ein");
        final EditText input = new EditText(this);

        alert.setView(input);

        alert.setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                new AsyncTask<Void, Void, InetAddress>()
                {

                    @Override
                    protected InetAddress doInBackground(Void... voids) {
                        try {
                            return InetAddress.getByName(input.getText().toString());
                        } catch (UnknownHostException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(InetAddress inetAddress) {
                        if (inetAddress == null) {
                            Toast.makeText(getApplicationContext(), "Falsche Adresse.", Toast.LENGTH_LONG).show();
                            System.out.println("*** Falsche Adresse");
                        }
                        else {
                            address = inetAddress;
                        }
                    }
                }.execute();

                /*try
                {
                    address = InetAddress.getByName(input.getText().toString());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }*/
                // Toast.makeText(getBaseContext(), address.toString(), Toast.LENGTH_LONG).show();
            }
        });

        alert.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private void showToast(int resId)
    {
        Toast toast = Toast.makeText(this, getString(resId), Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        Toast.makeText(this, (int) v, Toast.LENGTH_LONG).show();
        return false;
    }

    public class IncomingSms extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            SmsMessage smsMessage = Telephony.Sms.Intents.getMessagesFromIntent(intent)[0];

            String receivedPhoneNumber = smsMessage.getDisplayOriginatingAddress();
            String savedPhoneNumber = sharedPreferences.getString(Constants.PHONE_NUMBER, "");
            String message = smsMessage.getDisplayMessageBody();

                    if(receivedPhoneNumber.equals(savedPhoneNumber)
                            || receivedPhoneNumber.contains(savedPhoneNumber.substring(1)))
                    {
                        sendingAllowed = true;
                        MainDialogFragment.getInstance().init(message, getString(R.string.response), 0)
                                .show(getFragmentManager(), "tag");
                    }
        }
    }
/*
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // do nothing
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }*/
}