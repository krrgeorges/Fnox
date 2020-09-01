package com.example.fnox;

import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class NoLayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_layer);

        Intent i = getIntent();
        String action = i.getAction();
        String type = i.getType();
        if(action.equals(Intent.ACTION_SEND) && "text/plain".equals(type)){
            String text = i.getStringExtra(Intent.EXTRA_TEXT);
            if(Functions.validateText(text) == true) {
                i = new Intent(this, FNService.class);
                i.putExtra("fn_text", text);
                startService(i);
            }
            else{
                Toasty.error(getApplicationContext(),"Text isn't valid as a news item.").show();
            }
        }
        finish();
    }
}
