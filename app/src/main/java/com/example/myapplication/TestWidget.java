package com.example.myapplication;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.myapplication.databinding.FragmentFirstBinding;

import static android.content.Context.MODE_PRIVATE;

/**
 * Implementation of App Widget functionality.
 */
public class TestWidget extends AppWidgetProvider {
/*
    static String Keys;
    static SharedPreferences sharedPref;
    static AppWidgetManager awm;
    static int[] awids;
*/
    static Context con;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.test_widget);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        String Keys;
        SharedPreferences sharedPref;
        AppWidgetManager awm;
        int[] awids;
        awm=appWidgetManager;
        awids = appWidgetIds;
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            //updateAppWidget(context, appWidgetManager, appWidgetId);
            refreshView(context,appWidgetManager,appWidgetId);
        }
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if(action.startsWith("nextPage")){
                int id = Integer.parseInt(action.split(":")[1]);
                nextPage(context,id);
                updateWidgets(context);
            }
        else if(action.startsWith("incrementCounter")){
                int id = Integer.parseInt(action.split(":")[1]);
                incrementValue(context,id);
                updateWidgets(context);
            }
        else if(action.startsWith("decrementCounter")){
                int id = Integer.parseInt(action.split(":")[1]);
                decrementValue(context,id);
                updateWidgets(context);

        }
    }
    public static void updateWidgets() {
        String Keys;
        SharedPreferences sharedPref;
        AppWidgetManager awm;
        int[] awids;

        sharedPref = con.getSharedPreferences("PARAMS", MODE_PRIVATE);
        Keys= sharedPref.getString("Keys","Counter");
        String[] keyArr = Keys.split(":");

        RemoteViews views = new RemoteViews(con.getPackageName(), R.layout.test_widget);

        awm = AppWidgetManager.getInstance(con);
        awids = awm.getAppWidgetIds(new ComponentName(con, TestWidget.class));
        for (int i : awids) {
            int counterIndex = sharedPref.getInt(("WidgetCounter:"+String.valueOf(i)),0);
            SharedPreferences privateSP = con.getSharedPreferences(keyArr[counterIndex%keyArr.length], MODE_PRIVATE);
            views.setTextViewText(R.id.w_LabelTextView,keyArr[counterIndex%keyArr.length]);
            int value = privateSP.getInt("Value",0);
            views.setTextViewText(R.id.w_ValueTextView,String.valueOf(value));
            awm.updateAppWidget(i, views);
        }
    }
    private void updateWidgets(Context context){
        String Keys;
        SharedPreferences sharedPref;
        AppWidgetManager awm;
        int[] awids;

        sharedPref = context.getSharedPreferences("PARAMS", MODE_PRIVATE);
        Keys= sharedPref.getString("Keys","Counter");
        String[] keyArr = Keys.split(":");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.test_widget);

        awm = AppWidgetManager.getInstance(context);
        awids = awm.getAppWidgetIds(new ComponentName(context, TestWidget.class));
        for (int i : awids) {
            int counterIndex = sharedPref.getInt(("WidgetCounter:"+String.valueOf(i)),0);
            SharedPreferences privateSP = context.getSharedPreferences(keyArr[counterIndex%keyArr.length], MODE_PRIVATE);
            views.setTextViewText(R.id.w_LabelTextView,keyArr[counterIndex%keyArr.length]);
            int value = privateSP.getInt("Value",0);
            views.setTextViewText(R.id.w_ValueTextView,String.valueOf(value));
            awm.updateAppWidget(i, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        con = context;

    }

    public void refreshView(Context context,AppWidgetManager appWidgetManager,
                            int appWidgetId) {
        String Keys;
        SharedPreferences sharedPref;
        con = context;
        sharedPref = context.getSharedPreferences("PARAMS", MODE_PRIVATE);
        Keys= sharedPref.getString("Keys","Counter");
        int counterIndex = sharedPref.getInt(("WidgetCounter:"+String.valueOf(appWidgetId)),0);
        String[] keyArr = Keys.split(":");
        SharedPreferences privateSP = context.getSharedPreferences(keyArr[counterIndex%keyArr.length], MODE_PRIVATE);
        SharedPreferences.Editor editor = privateSP.edit();
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.test_widget);

        Intent intent = new Intent(context, TestWidget.class);
        intent.setAction("nextPage:"+appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.w_NextKey, pendingIntent);

        intent = new Intent(context, TestWidget.class);
        intent.setAction("incrementCounter:"+appWidgetId);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.w_IncButton, pendingIntent);

        intent = new Intent(context, TestWidget.class);
        intent.setAction("decrementCounter:"+appWidgetId);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.w_DecButton, pendingIntent);

        views.setTextViewText(R.id.w_LabelTextView,keyArr[counterIndex%keyArr.length]);
        views.setTextViewText(R.id.w_ValueTextView,String.valueOf(privateSP.getInt("Value",0)));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public void nextPage(Context context, int awid) {
        SharedPreferences sharedPref;
        sharedPref = context.getSharedPreferences("PARAMS", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(("WidgetCounter:"+String.valueOf(awid)), sharedPref.getInt(("WidgetCounter:"+String.valueOf(awid)),-1)+1);
        editor.apply();
    }
    public void incrementValue(Context context, int awid){
        String Keys;
        SharedPreferences sharedPref;
        sharedPref = context.getSharedPreferences("PARAMS", MODE_PRIVATE);
        Keys= sharedPref.getString("Keys","Counter");
        String[] keyArr = Keys.split(":");
        int counterIndex = sharedPref.getInt(("WidgetCounter:"+String.valueOf(awid)),0);
        sharedPref = context.getSharedPreferences(keyArr[counterIndex%keyArr.length], MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("Value", sharedPref.getInt("Value", 0)+1);
        editor.apply();
    }
    public void decrementValue(Context context, int awid){
        String Keys;
        SharedPreferences sharedPref;
        sharedPref = context.getSharedPreferences("PARAMS", MODE_PRIVATE);
        Keys= sharedPref.getString("Keys","Counter");
        String[] keyArr = Keys.split(":");
        int counterIndex = sharedPref.getInt(("WidgetCounter:"+String.valueOf(awid)),0);
        sharedPref = context.getSharedPreferences(keyArr[counterIndex%keyArr.length], MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("Value", sharedPref.getInt("Value", 0)-1);
        editor.apply();
    }
    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}