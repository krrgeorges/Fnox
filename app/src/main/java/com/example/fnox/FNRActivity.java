package com.example.fnox;

import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;
import io.github.inflationx.calligraphy3.TypefaceUtils;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextPaint;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class FNRActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fnr);

        gradientTV(((TextView) findViewById(R.id.fnr_header)),"FnoxReport","#1FA2FF","#12D8FA");

        Intent i = getIntent();
        String res_text = i.getStringExtra("res_text");

        ((LinearLayout) findViewById(R.id.fnr_news_container)).removeAllViews();

        int n_count = 0;
        for (String sentence : res_text.split("\n")) {
                if (sentence.split("<><><><><><><>").length == 5) {
                    final String[] splits = sentence.split("<><><><><><><>");
                    String link = splits[0];
                    String title = splits[1];
                    String date = splits[2];
                    String source = splits[3];
                    String desc = splits[4];
                    showNews(title,date,source,link,desc);
                    n_count++;
                }
        }
        ((TextView) findViewById(R.id.fnr_count)).setText(n_count+" results(s)");
    }

    public void showNews(String title,String date,String source,String link,String desc){
        LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(lllp);
        ll.setBackground(getDrawable(R.drawable.dr_news_bg));
        int dp = Functions.dpToPx(getApplicationContext(),10);
        ll.setPadding(dp,dp,dp,dp);
        ll.setOrientation(LinearLayout.VERTICAL);

        lllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView tv1 = new TextView(this);
        tv1.setLayoutParams(lllp);
        tv1.setText(title);
        tv1.setTextSize(18);
        tv1.setTypeface(TypefaceUtils.load(getAssets(),"fonts/Rubik-Bold.ttf"));

        TextView tv2 = new TextView(this);
        tv2.setLayoutParams(lllp);
        tv2.setText(desc);
        tv2.setTextSize(16);
        tv2.setTypeface(TypefaceUtils.load(getAssets(),"fonts/Rubik-Regular.ttf"));


        lllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout rl = new RelativeLayout(this);
        rl.setLayoutParams(lllp);

        RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        rllp.addRule(RelativeLayout.ALIGN_PARENT_START);
        TextView tv3 = new TextView(this);
        tv3.setLayoutParams(rllp);
        tv3.setText(source);
        tv3.setTextSize(16);
        tv3.setTypeface(TypefaceUtils.load(getAssets(),"fonts/Rubik-Medium.ttf"));
        tv3.setTextColor(Color.parseColor("#2ECC71"));

        rllp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        rllp.addRule(RelativeLayout.ALIGN_PARENT_END);
        TextView tv4 = new TextView(this);
        tv4.setLayoutParams(rllp);
        tv4.setText(date);
        tv4.setTextSize(16);
        tv4.setTypeface(TypefaceUtils.load(getAssets(),"fonts/Rubik-Medium.ttf"));
        tv4.setTextColor(Color.parseColor("#3498DB"));

        dp = Functions.dpToPx(getApplicationContext(),15);

        Space s1 = new Space(this);
        lllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,dp);
        s1.setLayoutParams(lllp);
        Space s2 = new Space(this);
        s2.setLayoutParams(lllp);

        Space s3 = new Space(this);
        lllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,Functions.dpToPx(getApplicationContext(),10));
        s3.setLayoutParams(lllp);

        rl.addView(tv3);
        rl.addView(tv4);
        ll.addView(tv1);
        ll.addView(s1);
        ll.addView(rl);
        ll.addView(s2);
        ll.addView(tv2);


        ((LinearLayout) findViewById(R.id.fnr_news_container)).addView(ll);
        ((LinearLayout) findViewById(R.id.fnr_news_container)).addView(s3);
    }

    public void gradientTV(TextView textView, String text, String color1, String color2){
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
