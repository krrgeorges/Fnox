package com.example.fnox;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import es.dmoral.toasty.Toasty;

public class FNService extends Service {

    int NOTIF_ID;
    String NOTIF_CHANNEL_ID = "FNOX_MSS";
    String NOTIF_CHANNEL_NAME = "Fnox Reporting Notifier";
    NotificationCompat.Builder nb = new NotificationCompat.Builder(this);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Toasty.info(getApplicationContext(),"Generating FnoxReport. Check notifications for progress.").show();
            }
        });

        NOTIF_ID = new Random().nextInt(10000);

        NotificationManager nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel nc = new NotificationChannel(NOTIF_CHANNEL_ID,NOTIF_CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH);
        nm.createNotificationChannel(nc);



        nb.setContentTitle("Generating FnoxReport")
                .setContentText("Initializing Modules.....")
                .setChannelId(NOTIF_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_gavel_white_24dp);
        Notification notification = nb.build();
        nm.notify(NOTIF_ID,notification);


        final String text = intent.getStringExtra("fn_text");
        new FNMainTask(getApplicationContext(),text,"RR",nb,NOTIF_ID,NOTIF_CHANNEL_ID).execute();
        return START_STICKY;
    }
}

class FNMainTask extends AsyncTask<Void, Void, ArrayList<Object>> {
    String text,type;
    Context c;
    NotificationCompat.Builder nb;
    String NOTIF_CHANNEL_ID;
    int NOTIF_ID;
    FNMainTask(Context c, String text, String type,NotificationCompat.Builder nb,int NOTIF_ID,String NOTIF_CHANNEL_ID){
        this.c = c;
        this.text = text;
        this.type = type;
        this.nb = nb;
        this.NOTIF_ID = NOTIF_ID;
        this.NOTIF_CHANNEL_ID = NOTIF_CHANNEL_ID;
    }

    @Override
    protected ArrayList<Object> doInBackground(Void... voids) {
        try{
            return new FNDetector(c,text,type,this.nb,this.NOTIF_ID,this.NOTIF_CHANNEL_ID).detect();
        }
        catch (Exception e){
            return new ArrayList<Object>(){{add("0");}};
        }
    }

    @Override
    protected void onPostExecute(final ArrayList<Object> aVoid) {
                if(Integer.valueOf((String)aVoid.get(0)) == 1){
                        NotificationManager nm = (NotificationManager) c.getSystemService(c.NOTIFICATION_SERVICE);
                        nm.cancel(NOTIF_ID);

                        LinkedHashMap<String,ArrayList<String>> news = (LinkedHashMap<String, ArrayList<String>>) aVoid.get(1);
                        String res_text = "";
                        for(String new_h:news.keySet()){
                            String sep = "<><><><><><><>";
                            ArrayList<String> data = news.get(new_h);
                            res_text += new_h+sep;
                            for(int i=0;i<=data.size()-2;i++){
                                res_text += data.get(i)+sep;
                            }
                            res_text += data.get(data.size()-1)+"\n";
                        }
                        Intent i = new Intent(c,FNRActivity.class);
                        i.putExtra("res_text",res_text);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        c.startActivity(i);
                }
                else {

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Toasty.error(c,"FnoxReport could not be generated. Please check your connection and try again.").show();
                        }
                    });


                    Intent i = new Intent(c,FNRActivity.class);
                    i.putExtra("res_text","");
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    c.startActivity(i);
                }

    }
}
