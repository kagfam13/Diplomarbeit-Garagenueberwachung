package at.htlkaindorf.hoemam11.blaulichtsteuerung.GateControlling;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.wimpi.modbus.Modbus;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import at.htlkaindorf.hoemam11.blaulichtsteuerung.GateControlling.easyModbus.EasyModbusMaster;
import at.htlkaindorf.hoemam11.blaulichtsteuerung.GateControlling.easyModbus.GetCoilsResp;
import at.htlkaindorf.hoemam11.blaulichtsteuerung.R;

public class GateControllingActivity extends AppCompatActivity {
    private ConstraintLayout auto1,auto2,auto3,auto4,auto5;
    private TextView tor1,tor2,tor3,tor4,tor5, reaktionszeit;
    private EasyModbusMaster master;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture future;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_controlling);
        Intent intent = getIntent();
            InetAddress address;
            final String ipExtra = intent.getStringExtra("ADDRESS");
            new AsyncTask<Void, Void, InetAddress>() {

                @Override
                protected InetAddress doInBackground(Void... voids) {
                    try {
                        return InetAddress.getByName(ipExtra);
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
                        // Toast.makeText(this,inetAddress,Toast.LENGTH_LONG).show();
                        try {
                            master = new EasyModbusMaster(Modbus.DEFAULT_PORT, 15, inetAddress, 10, 15, 0, 1);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                            finish();
                        }
                    }
                }
            }.execute();

            auto1 = (ConstraintLayout) findViewById(R.id.bg1);
            auto2 = (ConstraintLayout) findViewById(R.id.bg2);
            auto3 = (ConstraintLayout) findViewById(R.id.bg3);
            auto4 = (ConstraintLayout) findViewById(R.id.bg4);
            auto5 = (ConstraintLayout) findViewById(R.id.bg5);


            tor1 = (TextView) findViewById(R.id.twTor1);
            tor2 = (TextView) findViewById(R.id.twTor2);
            tor3 = (TextView) findViewById(R.id.twTor3);
            tor4 = (TextView) findViewById(R.id.twTor4);
            tor5 = (TextView) findViewById(R.id.twTor5);

            reaktionszeit = (TextView) findViewById(R.id.twReaktionszeit);

    }

    @Override
    protected void onStart() {
        super.onStart();
        future = executorService
                .scheduleWithFixedDelay(new BackgroundWorker(), 1, 2000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onStop() {
        future.cancel(true);
        super.onStop();
    }

    public void onButton(View view)
    {
        switch (view.getId())
        {
            case R.id.btAuf1: new writeCoilTask(0).execute(); break;
            case R.id.btZu1: new writeCoilTask(1).execute(); break;
            case R.id.btAuf2: new writeCoilTask(2).execute(); break;
            case R.id.btZu2: new writeCoilTask(3).execute(); break;
            case R.id.btAuf3: new writeCoilTask(4).execute(); break;
            case R.id.btZu3: new writeCoilTask(5).execute(); break;
            case R.id.btAuf4: new writeCoilTask(6).execute(); break;
            case R.id.btZu4: new writeCoilTask(7).execute(); break;
            case R.id.btAuf5: new writeCoilTask(8).execute(); break;
            case R.id.btZu5: new writeCoilTask(9).execute(); break;
            case R.id.bg_back: finish(); break;
        }
    }

    private class writeCoilTask extends AsyncTask<Object,Object,Object>
    {
        private final int coil;

        private writeCoilTask(int coil) {
            this.coil = coil;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            System.out.println("writing coil: " + coil);
            try
            {
                master.writeCoil(coil, true);
                return 0;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }
    }



    private class BackgroundWorker implements Runnable
    {
        private void setCar(ConstraintLayout car, boolean state)
        {
            if (state)
                car.setBackgroundColor(Color.rgb(20, 188, 57));
            else
                car.setBackgroundColor(Color.rgb(214, 63, 21));
        }

        private void setTor(TextView tor, boolean sensorUnten,boolean sensorOben)
        {
            if (!sensorOben && !sensorUnten)
                tor.setText("Tor in der Mitte");
            else if (sensorOben && !sensorUnten)
                tor.setText("Tor ist geöffnet");
            else if (!sensorOben)
                tor.setText("Tor ist geschlossen");
            else
                tor.setText("WTF ist los??");
        }

        private void setTime(TextView textView,int min,int sec)
        {
            textView.setText( + min + " min " + sec + " sec");
        }


        @Override
        public void run() {
            System.out.println("***** START");
            try {
                final GetCoilsResp resp = new GetCoilsResp(master.getCoils());
                System.out.println("***** "+resp.toString());

                auto1.post(
                        new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try {
                                    System.out.println("***" + resp.toString());
                                    setCar(auto1, resp.getCoil(10));
                                    setCar(auto2, resp.getCoil(11));
                                    setCar(auto3, resp.getCoil(12));
                                    setCar(auto4, resp.getCoil(13));
                                    setCar(auto5, resp.getCoil(14));

                                    setTor(tor1, resp.getCoil(15), resp.getCoil(16));
                                    setTor(tor2, resp.getCoil(17), resp.getCoil(18));

                                    setTor(tor3, resp.getCoil(19), resp.getCoil(20));
                                    setTor(tor4, resp.getCoil(21), resp.getCoil(22));
                                    setTor(tor5, resp.getCoil(23), resp.getCoil(24));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );

                final int dauer = master.getRegister(0);

                reaktionszeit.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                setTime(reaktionszeit, dauer/60, dauer%60);
                            }
                        }
                );
            }
            catch (Exception ex)
            {
                System.out.println("***** "+ex.toString());
            }
        }
    }
}
