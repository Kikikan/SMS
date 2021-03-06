package net.kikikan.sms;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;
    final String SENT = "SMS_SENT";
    final String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;

    EditText number;
    EditText message;
    ImageButton button;

    //ListView listView;
    final String[] telefonszamok = new String[] {"06 30 513 3238", "06 20 257 2393", "06 30 222 2222"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        number = findViewById(R.id.phoneNumberEditText);
        message = findViewById(R.id.messageEditText);
        button = findViewById(R.id.sendButton);
        button.setImageResource(R.drawable.snapchat36);

        if (!hasPermission(Manifest.permission.SEND_SMS))
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
        }

        sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        MyAdapter myAdapter = new MyAdapter(telefonszamok, this);

        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(myAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                number.setVisibility(View.VISIBLE);
                Log.i("Tel clicked ", telefonszamok[position]);
                number.setText(telefonszamok[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //listView = (ListView) findViewById(R.id.listview);
        //listView.setAdapter(myAdapter);

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                number.setVisibility(View.VISIBLE);
                Log.i("Tel clicked ", telefonszamok[position]);
                number.setText(telefonszamok[position]);
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        smsSentReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {

                switch(getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getApplicationContext(), "SMS elküldve!", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getApplicationContext(), "Generic failure!", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getApplicationContext(), "Nincs térerő!", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getApplicationContext(), "Null PDU!", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getApplicationContext(), "Radio off!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        smsDeliveredReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getApplicationContext(), "SMS kézbesítve!", Toast.LENGTH_SHORT).show();
                        break;

                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getApplicationContext(), "SMS nem lett kézbesítve!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        registerReceiver(smsSentReceiver, new IntentFilter(SENT));
        registerReceiver(smsDeliveredReceiver, new IntentFilter(DELIVERED));
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }

    public boolean hasPermission(String perm)
    {
        int id = ContextCompat.checkSelfPermission(this, perm);
        return (id == PackageManager.PERMISSION_GRANTED);
    }

/*
    public void HideTextBox(View view)
    {
        listView.setVisibility(View.GONE);
    }
*/

    public void onClick(View v)
    {
        String phoneN = number.getText().toString();
        String smsM = message.getText().toString();
        //listView.setVisibility(View.VISIBLE);

        if (phoneN == null || phoneN.length() == 0 || smsM == null || smsM.length() == 0)
        {
            Toast.makeText(this, "Irj SMS-t valakinek", Toast.LENGTH_SHORT).show();
            return;
        }

        if (hasPermission(Manifest.permission.SEND_SMS))
        {
            SmsManager sm = SmsManager.getDefault();
            sm.sendTextMessage(phoneN, null, smsM, sentPI, deliveredPI);
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
            Toast.makeText(getApplicationContext(), "Permission megtagadva!", Toast.LENGTH_SHORT).show();
        }
    }
}
