package at.htlkaindorf.hoemam11.blaulichtsteuerung;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener
{
    // Namen für onSaveInstanceState
    private static final String MESSAGE = "message";
    private static final String TITLE = "title";
    private static final String RESOURCE_ID = "resource_id";
    private static final String USE_LAYOUT = "use_layout";
    private static final String LAST_ID = "last_id";
    private static final String TAG = "MainDialogFragment";

    private Boolean useLayout = false;
    private String message, title;
    private int layoutOrButtonId;
    private static MainDialogFragment dialogFragment = null;

    private Boolean mailHistoryChecked = false;
    private Boolean silentAlarmChecked = false;

    private int checkedDoorMode;
    private int checkedLightModeOn;
    private int checkedLightModeOff;
    private int selectedTimeSpinnerItem;

    private TextView dialogInput;

    public MainDialogFragment()
    {

    }

    public static MainDialogFragment getInstance() {
        if(dialogFragment == null)
            dialogFragment = new MainDialogFragment();

        return dialogFragment;
    }

    @Override
    public void show(FragmentManager manager, String tag)
    {
        // wurde bereits ein Dialog gezeigt, wird er hier geschlossen
        if(isAdded())
            dismiss();

        super.show(manager, tag);
    }

    public MainDialogFragment init(String message, String title, int layoutOrButtonId)
    {
        this.message = message;
        this.title = title;
        this.layoutOrButtonId = layoutOrButtonId;
        return this;
    }

    public int getLayoutOrButtonId()
    {
        return layoutOrButtonId;
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        useLayout = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(RESOURCE_ID, layoutOrButtonId);
        outState.putBoolean(USE_LAYOUT, useLayout);
        outState.putString(MESSAGE, message);
        outState.putString(TITLE, title);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        final int PICK_CONTACT = 0;

        String phoneNumber;
        switch (requestCode)
        {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK)
                {
                    Uri contactData = data.getData();
                    // getContentResolver().query() kann null zurückliefern
                    Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
                    if (c != null)
                    {
                        while (c.moveToNext())
                        {
                            String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                            String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                            if (hasPhone.equalsIgnoreCase("1"))
                                hasPhone = "true";
                            else
                                hasPhone = "false";

                            if (Boolean.parseBoolean(hasPhone))
                            {
                                // könnte einen null-Cursor zurückliefern
                                Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                                if (phones != null)
                                {
                                    while (phones.moveToNext())
                                    {
                                        phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        phoneNumber = phoneNumber.replaceAll(" ", ""); // sonst beinhalten manche Nummern ein Leerzeichen
                                        dialogInput.setText(phoneNumber); // Wert im Dialog aktualisieren
                                    }
                                    phones.close();
                                }
                            }
                        }
                        c.close();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), R.string.error_contacts, Toast.LENGTH_LONG).show();
                        Log.e(TAG,getString(R.string.error_contacts));
                    }
                }
        }
    }
    // Stehengeblieben: http://developer.android.com/guide/topics/ui/dialogs.html#PassingEvents
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Standard Werte
        String positiveButtonText = getString(R.string.dialog_send);
        String negativeButtonText = getString(R.string.dialog_cancel);
        Boolean usePositiveButton = true;
        checkedDoorMode = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(Constants.CHECKED_DOOR_MODE, 0);
        checkedLightModeOn = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(Constants.CHECKED_LIGHT_MODE_ON, 0);
        checkedLightModeOff = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(Constants.CHECKED_LIGHT_MODE_OFF, 0);

        final View view;
        // Initialisieren für Radiogroups
        mailHistoryChecked = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean(Constants.MAIL_HISTORY_CHECKED, false);
        silentAlarmChecked = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean(Constants.SILENT_ALARM_CHECKED, false);

        if(layoutOrButtonId == R.layout.dialog_about
                || layoutOrButtonId == R.layout.dialog_mail
                || layoutOrButtonId == R.layout.dialog_simulations
                || layoutOrButtonId == R.layout.dialog_number
                || layoutOrButtonId == R.layout.dialog_mail_history_or_errors
                || layoutOrButtonId == R.layout.dialog_door
                || layoutOrButtonId == R.layout.dialog_lights_on
                || layoutOrButtonId == R.layout.dialog_lights_off
                || layoutOrButtonId == R.layout.dialog_door_temporary)
            useLayout = true;

        // Speichern des aktuellen Befehls als zuletzt verwendeten
        if(layoutOrButtonId != 0)
            getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE).edit().putInt(LAST_ID,layoutOrButtonId).apply();

        //
        if(savedInstanceState != null)
        {
            layoutOrButtonId = savedInstanceState.getInt(RESOURCE_ID);
            useLayout = savedInstanceState.getBoolean(USE_LAYOUT);
            message = savedInstanceState.getString(MESSAGE);
            title = savedInstanceState.getString(TITLE);
        }

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        if(useLayout)
        {
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            view = inflater.inflate(layoutOrButtonId, null);
            builder.setView(view);
            switch(layoutOrButtonId)
            {
                case R.layout.dialog_door_temporary:
                    Spinner spinner = (Spinner)view.findViewById(R.id.time_spinner);
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.time_values, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    //if(spinner != null)
                    {
                    spinner.setAdapter(adapter);
                    spinner.setOnItemSelectedListener(this);}
                    /*nt pos = spinner.getSelectedItemPosition();
                    pos++;
                    String befehl = "door_" + pos;
                    sendCommand(befehl);*/
                    break;
                case R.layout.dialog_mail:
                    dialogInput = (EditText) view.findViewById(R.id.input);

                    dialogInput.setText((getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)).getString(Constants.EMAIL_ADDRESS, ""));

                    positiveButtonText = getString(R.string.dialog_save);
                    break;
                case R.layout.dialog_number:
                    dialogInput = (EditText) view.findViewById(R.id.input);

                    dialogInput.setText((getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)).getString(Constants.PHONE_NUMBER, ""));

                    final Button contactButton = (Button) view.findViewById(R.id.contactButton);
                    // Permission Abfrage bei Klick auf Kontakt-Button
                    contactButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
                            {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS))
                                {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, Constants.PERMISSION_READ_CONTACTS);
                                } else
                                {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, Constants.PERMISSION_READ_CONTACTS);
                                }
                            } else
                            {
                                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                                startActivityForResult(intent, Constants.PICK_CONTACT);
                            }
                        }
                    });
                    positiveButtonText = getString(R.string.dialog_save);
                    break;
                case R.layout.dialog_about:
                    negativeButtonText = getString(R.string.dialog_close);
                    usePositiveButton = false;
                    break;
                case R.layout.dialog_mail_history_or_errors:
                    if (mailHistoryChecked)
                        ((RadioGroup) view.findViewById(R.id.mailGroup)).check(R.id.mail_history);
                    else
                        ((RadioGroup) view.findViewById(R.id.mailGroup)).check(R.id.mail_errors);
                    break;
                case R.layout.dialog_simulations:
                    if (silentAlarmChecked)
                        ((RadioGroup) view.findViewById(R.id.alarmGroup)).check(R.id.StillerAlarm);
                    else
                        ((RadioGroup) view.findViewById(R.id.alarmGroup)).check(R.id.Sirenenalarm);
                    break;
                case R.layout.dialog_lights_on:
                switch (checkedLightModeOn)
                {
                    case R.id.halle_on:
                        ((RadioGroup) view.findViewById(R.id.lightsOnGroup)).check(R.id.halle_on);
                        break;
                    case R.id.mtf_on:
                        ((RadioGroup) view.findViewById(R.id.lightsOnGroup)).check(R.id.mtf_on);
                        break;
                    case R.id.kdo_on:
                        ((RadioGroup) view.findViewById(R.id.lightsOnGroup)).check(R.id.kdo_on);
                        break;
                    case R.id.herren_on:
                        ((RadioGroup) view.findViewById(R.id.lightsOnGroup)).check(R.id.herren_on);
                        break;
                    case R.id.damen_on:
                        ((RadioGroup) view.findViewById(R.id.lightsOnGroup)).check(R.id.damen_on);
                        break;
                    case R.id.vorraum_on:
                        ((RadioGroup) view.findViewById(R.id.lightsOnGroup)).check(R.id.vorraum_on);
                        break;
                    default:
                        ((RadioGroup) view.findViewById(R.id.lightsOnGroup)).check(R.id.lights_on); break;
                } break;
                    case R.layout.dialog_lights_off:
                    switch (checkedLightModeOff)
                    {
                        case R.id.halle_off:
                            ((RadioGroup) view.findViewById(R.id.lightsOffGroup)).check(R.id.halle_off);
                            break;
                        case R.id.mtf_off:
                            ((RadioGroup) view.findViewById(R.id.lightsOffGroup)).check(R.id.mtf_off);
                            break;
                        case R.id.kdo_off:
                            ((RadioGroup) view.findViewById(R.id.lightsOffGroup)).check(R.id.kdo_off);
                            break;
                        case R.id.herren_off:
                            ((RadioGroup) view.findViewById(R.id.lightsOffGroup)).check(R.id.herren_off);
                            break;
                        case R.id.damen_off:
                            ((RadioGroup) view.findViewById(R.id.lightsOffGroup)).check(R.id.damen_off);
                            break;
                        case R.id.vorraum_off:
                            ((RadioGroup) view.findViewById(R.id.lightsOffGroup)).check(R.id.vorraum_off);
                            break;
                        default:
                            ((RadioGroup) view.findViewById(R.id.lightsOffGroup)).check(R.id.lights_off); break;
                    } break;
                case R.layout.dialog_door:
                    switch (checkedDoorMode)
                    {
                        case R.id.open_door:
                            ((RadioGroup) view.findViewById(R.id.doorGroup)).check(R.id.open_door);
                            break;
                        case R.id.door_unlock:
                            ((RadioGroup) view.findViewById(R.id.doorGroup)).check(R.id.door_unlock);
                            break;
                        case R.id.door_lock:
                            ((RadioGroup) view.findViewById(R.id.doorGroup)).check(R.id.door_lock);
                            break;
                        default:
                            ((RadioGroup) view.findViewById(R.id.doorGroup)).check(R.id.open_door); break;
                    }
            }
        }
        // kein Layout verwendet
        else
        {
            // verhindern, dass die Variable v nicht initialisiert sein könnte
            view = getView();
            // Setzen der Variablen für Antwort vom Blaulicht
            if(title.equals(getString(R.string.response)))
            {
                usePositiveButton = false;
                negativeButtonText = getString(R.string.dialog_close);
            }
        }

        // Überprüfen auf Fehler in der Rückmeldung
        if (message.contains("verfuegbar"))
        {
            builder = new AlertDialog.Builder(getActivity(), R.style.ErrorResponseDialogStyle);
            positiveButtonText = getString(R.string.send_again);
            usePositiveButton = true;
            // Holen des zuletzt verwendeten Befehls
            layoutOrButtonId = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE).getInt(LAST_ID,0);
        }
        if(message.contains("pruefen"))
        {
            builder = new AlertDialog.Builder(getActivity(), R.style.ErrorResponseDialogStyle);
        }

        builder.setMessage(message);
        builder.setTitle(title);
        if(usePositiveButton)
        {
            builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    if (useLayout)
                    {
                        switch(layoutOrButtonId)
                        {
                            case R.layout.dialog_simulations:
                                RadioGroup radioGroup = ((RadioGroup) view.findViewById(R.id.alarmGroup));
                                // merken, welcher Befehl angehakt war
                                silentAlarmChecked = radioGroup.getCheckedRadioButtonId() == R.id.StillerAlarm;
                                getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putBoolean(Constants.SILENT_ALARM_CHECKED, silentAlarmChecked).apply();
                                break;

                            case R.layout.dialog_mail_history_or_errors:
                                radioGroup = ((RadioGroup) view.findViewById(R.id.mailGroup));
                                mailHistoryChecked = radioGroup.getCheckedRadioButtonId() == R.id.mail_history;
                                getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putBoolean(Constants.MAIL_HISTORY_CHECKED, mailHistoryChecked).apply();
                                break;

                            case R.layout.dialog_door:
                                radioGroup = ((RadioGroup) view.findViewById(R.id.doorGroup));
                                checkedDoorMode = radioGroup.getCheckedRadioButtonId();
                                getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putInt(Constants.CHECKED_DOOR_MODE, checkedDoorMode).apply();
                                break;

                            case R.layout.dialog_lights_on:
                                radioGroup = ((RadioGroup) view.findViewById(R.id.lightsOnGroup));
                                checkedLightModeOn = radioGroup.getCheckedRadioButtonId();
                                getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putInt(Constants.CHECKED_LIGHT_MODE_ON, checkedLightModeOn).apply();
                                break;

                            case R.layout.dialog_lights_off:
                                radioGroup = ((RadioGroup) view.findViewById(R.id.lightsOffGroup));
                                checkedLightModeOff = radioGroup.getCheckedRadioButtonId();
                                getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putInt(Constants.CHECKED_LIGHT_MODE_OFF, checkedLightModeOff).apply();
                                break;
                        }
                    }
                    ((MainActivity)getActivity()).onDialogPositiveClick(MainDialogFragment.this);
                }
            });
        }
        builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // do nothing
            }
        });
        return builder.create();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedTimeSpinnerItem = parent.getSelectedItemPosition();
        getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putInt(Constants.SELECTED_TIME_SPINNER_ITEM, selectedTimeSpinnerItem).apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }
}
