package at.htlkaindorf.kagfam13.androidsimmaster;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ModbusResponse;
import net.wimpi.modbus.msg.ReadCoilsRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.WriteCoilRequest;
import net.wimpi.modbus.net.TCPMasterConnection;

import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {
    String ip = "10.200.112.86";
    int port = Modbus.DEFAULT_PORT;
    int unitId = 15;
    TextView auto1,auto2,auto3,auto4,auto5,tor1,tor2,tor3,tor4,tor5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auto1 = (TextView) findViewById(R.id.twFahrzeug1);
        auto2 = (TextView) findViewById(R.id.twFahrzeug2);
        auto3 = (TextView) findViewById(R.id.twFahrzeug3);
        auto4 = (TextView) findViewById(R.id.twFahrzeug4);
        auto5 = (TextView) findViewById(R.id.twFahrzeug5);

        tor1 = (TextView) findViewById(R.id.twTor1);
        tor2 = (TextView) findViewById(R.id.twTor2);
        tor3 = (TextView) findViewById(R.id.twTor3);
        tor4 = (TextView) findViewById(R.id.twTor4);
        tor5 = (TextView) findViewById(R.id.twTor5);

        new backgroundThread().execute();
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
            System.out.println("*************************************");
            try
            {
                TCPMasterConnection connection = new TCPMasterConnection(InetAddress.getByName(ip));
                connection.setTimeout(3000);
                connection.setPort(port);
                System.out.println("Trying to connect to "+connection.getAddress()+" on port "+connection.getPort());
                connection.connect();

                ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);

                ModbusRequest request = new WriteCoilRequest(coil, true);

                request.setUnitID(unitId);
                transaction.setRequest(request);
                transaction.execute();

                connection.close();

                return 0;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }
    }


    private class backgroundThread extends AsyncTask<Object,Object,Object>
    {
        private boolean getCoil(String bin, int coil)
        {
            if (bin.charAt(bin.length()-coil-1)=='1')
                return true;
            return false;
        }

        private void setCar(TextView car, boolean state)
        {
            if (state == true)
                car.setText("Fahrzeug ist da");
            else
                car.setText("Fahrzeug ist nicht da");
        }

        private void setTor(TextView tor, boolean sensorUnten,boolean sensorOben)
        {
            if (sensorOben == false && sensorUnten == false)
                tor.setText("Tor in der Mitte");
            else if (sensorOben == true && sensorUnten == false)
                tor.setText("Tor ist geöffnet");
            else if (sensorOben == false && sensorUnten == true)
                tor.setText("Tor ist geschlossen");
            else
                tor.setText("WTF ist los??");
        }

        private void setTime(TextView tWeinsatzdauer,int min,int sec)
        {
            tWeinsatzdauer.setText("Dauer des Letzten einsatzes: " + min + " min " + sec + " sec");
        }

        private int calcInt(ModbusResponse response)
        {
            String res = response.getHexMessage();
            System.out.println(res);
            String hexString = response.getHexMessage().substring(response.getHexMessage().length()-3, response.getHexMessage().length()-1);
            return Integer.parseInt(hexString, 16);
        }

        @Override
        protected Object doInBackground(Object... objects) {
            while(true)
            {
                System.out.println("*************************************");
                try
                {
                    TCPMasterConnection connection = new TCPMasterConnection(InetAddress.getByName(ip));
                    connection.setTimeout(3000);
                    connection.setPort(port);
                    System.out.println("Trying to connect to "+connection.getAddress()+" on port "+connection.getPort());
                    connection.connect();

                    ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);

                    ModbusRequest request = new ReadMultipleRegistersRequest(0, 1);
                    request.setUnitID(unitId);
                    transaction.setRequest(request);
                    transaction.execute();
                    ModbusResponse response = transaction.getResponse();
                    int min = calcInt(response);


                    request = new ReadMultipleRegistersRequest(1, 1);
                    request.setUnitID(unitId);
                    transaction.setRequest(request);
                    transaction.execute();
                    response = transaction.getResponse();
                    int sec = calcInt(response);


                    request = new ReadCoilsRequest(0,25);

                    request.setUnitID(unitId);
                    transaction.setRequest(request);
                    transaction.execute();
                    response = transaction.getResponse();

                    connection.close();

                    int intVal = calcInt(response);
                    String bin = Integer.toBinaryString(intVal);
                    while(bin.length()<25)
                    {
                        bin = "0" + bin;
                    }
                    System.out.println(bin);

                    setCar(auto1, getCoil(bin, 10));
                    setCar(auto2, getCoil(bin, 11));
                    setCar(auto3, getCoil(bin, 12));
                    setCar(auto4, getCoil(bin, 13));
                    setCar(auto5, getCoil(bin, 14));

                    setTor(tor1, getCoil(bin, 15),getCoil(bin, 16));
                    setTor(tor2, getCoil(bin, 17),getCoil(bin, 18));
                    setTor(tor3, getCoil(bin, 19),getCoil(bin, 20));
                    setTor(tor4, getCoil(bin, 21),getCoil(bin, 22));
                    setTor(tor5, getCoil(bin, 23),getCoil(bin, 24));
                    Thread.sleep(2000);
                    return 0;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
