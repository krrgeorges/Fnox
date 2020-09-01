package com.example.fnox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import es.dmoral.toasty.Toasty;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends AppCompatActivity {
    Context c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        c = this;

        gradientTV(((TextView) findViewById(R.id.ma_header)),"Fnox","#1FA2FF","#12D8FA");

        ((Button) findViewById(R.id.ma_gfr)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = ((EditText) findViewById(R.id.ma_text)).getText().toString();

                if(Functions.validateText(text) == true) {
                    Intent i = new Intent(c, FNService.class);
                    i.putExtra("fn_text", text);
                    startService(i);
                }
                else{
                    Toasty.error(getApplicationContext(),"Text isn't valid as a news item.").show();
                }
            }
        });





    }

    public void gradientTV(TextView textView, String text,String color1, String color2){
        textView.setText(text);

        TextPaint paint = textView.getPaint();
        float width = paint.measureText(text);

        Shader textShader = new LinearGradient(0, 0, width, textView.getTextSize(),
                new int[]{
                        Color.parseColor(color1),
                        Color.parseColor(color2)
                }, null, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(textShader);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
