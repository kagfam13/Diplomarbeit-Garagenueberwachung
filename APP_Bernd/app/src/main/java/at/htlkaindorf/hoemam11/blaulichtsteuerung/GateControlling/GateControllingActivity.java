package at.htlkaindorf.hoemam11.blaulichtsteuerung.GateControlling;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import at.htlkaindorf.hoemam11.blaulichtsteuerung.R;

public class GateControllingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_controlling);
        Intent intent = getIntent();
        String addresss = intent.getStringExtra("ADRESS");
        Toast.makeText(getBaseContext(), addresss, Toast.LENGTH_LONG).show();
    }
}
