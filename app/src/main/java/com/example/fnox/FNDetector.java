package com.example.fnox;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import androidx.core.app.NotificationCompat;
import java.util.Random;
import java.util.logging.Handler;


public class FNDetector {
    private String text,type;
    int GS_WINDOW_SIZE = 5;
    NotificationCompat.Builder nb;
    int NOTIF_ID;
    String NOTIF_CHANNEL_ID;
    Context c;

    //locally targeted
    private String[] news_sources = {"indiatoday","economictimes","mirror","firstpost",
            "thehindu","ndtv","livemint","news18","financialexpress","cnbctv18","timesofindia"}; // needs more for global impact
    //collect by country
    //ranked news sources?

    FNDetector(Context c, String text, String type,NotificationCompat.Builder nb,int NOTIF_ID,String NOTIF_CHANNEL_ID){
        this.c = c;
        this.text = text;
        this.type = type; // RR WD WR
        this.NOTIF_ID = NOTIF_ID;
        this.nb = nb;
        this.NOTIF_CHANNEL_ID = NOTIF_CHANNEL_ID;
    }

    public ArrayList<Object> detect(){
        ArrayList<String> candidate_tokens = this.customPOS();
        return this.gspq(candidate_tokens);
    }

    private ArrayList<Object> gspq(ArrayList<String> tokens){
        String qt = "";
        for(String token:tokens){
            qt+= '"'+token+'"'+" ";
        }
        qt = qt.trim();
        String url = this.gs_template(qt);
        return this.gatherNews(url);
    }

    private boolean matchLinkWithVS(String link){
        String domain;
        try {
            domain = new URL(link).getHost();
        } catch (MalformedURLException ex) {
            return false;
        }
        for(String vs:this.news_sources){
            if(domain.contains(vs) == true){
                return true;
            }
        }
        return false;
    }

    private String gs_template(String qt){
        if(this.type.equals("WD")){ // week sort by date
            return "https://www.google.com/search?biw=871&bih=608&tbs=sbd%3A1%2Cqdr%3Aw&tbm=nws&ei=TRDKXuKEBKPWz7sP8a--yAM&q="+qt;
        }
        else if(this.type.equals("WR")){ // week sort by relevance
            return "https://www.google.com/search?tbm=nws&tbs=qdr:w&sa=X&ved=0ahUKEwj3pLKZ7svpAhVx73MBHTHJDiUQpwUIJA&biw=869&bih=608&q="+qt;
        }
        else{ // recent sort by relevance
            return "https://www.google.com/search?tbm=nws&sxsrf=ALeKk02_WlQ3-UmgR-Kkam-txm5hYkCt0g%3A1590296240215&ei=sP7JXqvnDPTYz7sPvu2U4AI&q="+qt;
        }
    }

    private void updateNotification(String title,String text){
        nb.setContentTitle(title)
                .setContentText(text)
                .setChannelId(NOTIF_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_gavel_white_24dp)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text));;
        Notification notification = nb.build();
        NotificationManager nm = (NotificationManager) c.getSystemService(c.NOTIFICATION_SERVICE);
        nm.notify(NOTIF_ID,notification);
    }

    private JSONObject retrieveCPOSData(){
        JSONObject jobj = new JSONObject();
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(c.getAssets().open("pos_list.json")));
            String o = "";
            String content = "";
            while((o = br.readLine())!=null){
                content += o;
            }

            return new JSONObject(content);
        }
        catch(Exception e){
            return jobj;
        }
    }

    private ArrayList<String> customPOS(){
        updateNotification("Generating FnoxReport","Identifying primary targets...");

        ArrayList<String> candidate_tokens = new ArrayList<>();

        ArrayList<String> tokens = this.tokenize(true);
        JSONObject jobj = this.retrieveCPOSData();

        for(String token:tokens){
            try {
                boolean added = false;
                JSONArray s = jobj.getJSONArray(token);
                for(int i=0;i<=s.length()-1;i++){
                    try {
                        if((s.getString(i).toLowerCase().contains("noun") == true && s.getString(i).toLowerCase().contains("pronoun") == false)){
                            candidate_tokens.add(token);
                            added = true;
                            break;
                        }
                    } catch (JSONException e) {
                        continue;
                    }
                }
                if(added == false){
                    int count = 0;
                    for(int i=0;i<=s.length()-1;i++){
                        String pos = s.getString(i).toLowerCase();
                        String[] nonos = {"verb","adjective","adverb","preposition","interjection"};
                        for(String nono:nonos){
                            if(pos.contains(nono) == true){
                                count++;
                            }
                        }
                    }
                    if(count < s.length()){
                        candidate_tokens.add(token);
                        added = true;
                    }
                }
                if(s.length() == 0){
                    if(candidate_tokens.contains(token) == false) {
                        candidate_tokens.add(token);
                    }
                }
            } catch (JSONException e) {
                candidate_tokens.add(token);
            }
            catch (Exception e){
                candidate_tokens.add(token);
            }
        }
        return candidate_tokens;
    }

    private ArrayList<String> tokenize(boolean shouldBeLowerCase){
        String temp_text = this.text;
        ArrayList<String> tokens = new ArrayList<String>();
        String symbols = "!@^&*()_+-={}|:<>?[]\\;',./'";
        for(int i=0;i<=symbols.length()-1;i++){
            String symbol = String.valueOf(symbols.charAt(i));
            temp_text = temp_text.replace(symbol," "+symbol+" ");
        }
        if(shouldBeLowerCase == true) {
            temp_text = temp_text.toLowerCase();
        }
        String[] splits = temp_text.split(" ");
        for(int i=0;i<=splits.length-1;i++){
            if(tokens.contains(splits[i]) == false) {
                tokens.add(splits[i]);
            }
        }
        return tokens;
    }

    private boolean monthMatch(String datetext){
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        for(String month:months){
            if(datetext.contains(month) == true){
                return true;
            }
        }
        return false;
    }

    private ArrayList<Object> gatherNews(String source_url){
        LinkedHashMap<String,ArrayList<String>> news = new LinkedHashMap();
        Document doc;

        this.updateNotification("Generating FnoxReport","Gathering News...");

        for(int i=0;i<=this.GS_WINDOW_SIZE-1;i++){
            try {
                doc = Jsoup.parse(new URL(source_url.replace(" ", "+")+"&start="+(i*10)), 1000000);
            } catch (Exception ex) {
                this.updateNotification("FnoxReport not generated","Please check your connection & try again.");
                return new ArrayList<Object>(){{add("0");add(new LinkedHashMap<String,ArrayList<String>>());}};
            }
            Elements elems = doc.getElementsByTag("a");
            for(Element elem:elems){
                String link = elem.attr("href");
                if(this.matchLinkWithVS(link) == true && news.containsKey(elem.attr("href")) == false && elem.text().trim().length() > 0 && elem.text().trim().split(" ").length > 3){
                    ArrayList<String> mdata = new ArrayList<String>();

                    String text = elem.text().trim();
                    String date = "";

                    Element topParent = elem.parent().parent();
                    Elements spans = topParent.getElementsByTag("span");
                    String source = "";
                    for(Element span:spans){
                        if( (span.text().trim().split(" ").length == 3 && (span.text().contains(" hours ago") || span.text().contains(" mins ago") || span.text().contains(" secs ago"))) || (span.text().trim().split("-").length == 3 && this.monthMatch(span.text().trim()) == true)  ){
                            date = span.text().trim();
                            source = span.parent().child(0).text().trim();
                            break;
                        }
                    }
                    String desc = "";
                    Elements divs = topParent.getElementsByTag("div");
                    for(Element div:divs){
                        if(div.text().trim().split(" ").length >= 10){
                            desc = div.text().trim();
                            break;
                        }
                    }

                    mdata.add(text);
                    mdata.add(date);
                    mdata.add(source);
                    mdata.add(desc);
                    System.out.println(text);

                    news.put(link, mdata);
                }
            }
        }

        final LinkedHashMap<String,ArrayList<String>> mnews = news;

        return new ArrayList<Object>(){{add("1");add(mnews);}};

    }


}