package com.example.miguel.gpstracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by miguel on 22/11/2016.
 */

public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        //---get the SMS message passed in---
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        String number = "";
        if (bundle != null)
        {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                // str += "SMS from " + msgs[i].getOriginatingAddress();
                // str += " :";
                str += msgs[i].getMessageBody().toString();
                number += msgs [i].getOriginatingAddress();
                // str += "\n";
            }
            if (number.equals("+351936144559")){
                //---Split the str in various tokens ---
                String[] tokens = str.split("/");
                try {
                    String json = "";
                    // Build jsonObject
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("homeMobileCountryCode", 268);        //tokens [2]
                    jsonObject.accumulate("homeMobileNetworkCode", 1);        //tokens [3]
                    jsonObject.accumulate("radioType", "gsm");
                    jsonObject.accumulate("carrier", "vodafone");

                    //if(tokens.length >= 2){
                    JSONArray cellTowersArray = new JSONArray();
                    JSONObject cellTowerObject1 = new JSONObject();
                    cellTowerObject1.accumulate("cellId", 18816);                //tokens [0]
                    cellTowerObject1.accumulate("locationAreaCode", 8);        //tokens [1]
                    cellTowerObject1.accumulate("mobileCountryCode", 268);       //tokens [2]
                    cellTowerObject1.accumulate("mobileNetworkCode", 1);       //tokens [3]
                    cellTowerObject1.accumulate("age", 0);
                    cellTowerObject1.accumulate("signalStrength", -55);          //tokens [4]
                    cellTowerObject1.accumulate("timingAdvance", 15);
                    cellTowersArray.put(cellTowerObject1);
                    //}

                    //if(tokens.length >= 4){
                    JSONObject cellTowerObject2 = new JSONObject();
                    cellTowerObject2.accumulate("cellId", 18814);                //tokens [0]
                    cellTowerObject2.accumulate("locationAreaCode", 8);        //tokens [1]
                    cellTowerObject2.accumulate("mobileCountryCode", 268);       //tokens [2]
                    cellTowerObject2.accumulate("mobileNetworkCode", 1);       //tokens [3]
                    cellTowerObject2.accumulate("age", 0);
                    cellTowerObject2.accumulate("signalStrength", -55);          //tokens [4]
                    cellTowerObject2.accumulate("timingAdvance", 15);
                    cellTowersArray.put(cellTowerObject2);
                    //}

                    //if(tokens.length >= 6){
                    JSONObject cellTowerObject3 = new JSONObject();
                    cellTowerObject3.accumulate("cellId", 18815);                //tokens [0]
                    cellTowerObject3.accumulate("locationAreaCode", 8);        //tokens [1]
                    cellTowerObject3.accumulate("mobileCountryCode", 268);       //tokens [2]
                    cellTowerObject3.accumulate("mobileNetworkCode", 1);       //tokens [3]
                    cellTowerObject3.accumulate("age", 0);
                    cellTowerObject3.accumulate("signalStrength", -57);          //tokens [4]
                    cellTowerObject3.accumulate("timingAdvance", 15);
                    cellTowersArray.put(cellTowerObject3);
                    //}

                    jsonObject.accumulate("cellTowers", cellTowersArray);

                    JSONArray wifiAccessPointsArray = new JSONArray();
                    JSONObject wifiAccessPointObject = new JSONObject();
                    wifiAccessPointObject.accumulate("macAddress", "01:23:45:67:89:AB");
                    wifiAccessPointObject.accumulate("age", 0);
                    wifiAccessPointObject.accumulate("channel", 11);
                    wifiAccessPointObject.accumulate("signalToNoiseRatio", 40);
                    wifiAccessPointsArray.put(wifiAccessPointObject);
                    jsonObject.accumulate("wifiAccessPoints", wifiAccessPointsArray);

                    // Convert JSONObject to JSON to String
                    json = jsonObject.toString();

                    //---send a broadcast intent to update the SMS received in the activity---
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("SMS_RECEIVED_ACTION");
                    broadcastIntent.putExtra("JSONObject", json);
                    context.sendBroadcast(broadcastIntent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}