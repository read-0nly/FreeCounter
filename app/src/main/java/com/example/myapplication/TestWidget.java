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

    static int counterIndex = 0;
    static String Keys;
    static SharedPreferences sharedPref;
    static AppWidgetManager awm;
    static int[] awids;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.test_widget);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
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
        switch(action){
            case "nextPage":{
                nextPage();
                updateWidgets(context);
                break;
            }
            case "incrementCounter":{
                incrementValue(context);
                updateWidgets(context);
                break;
            }
            case "decrementCounter":{
                decrementValue(context);
                updateWidgets(context);
                break;
            }
        }
    }
    private void updateWidgets(Context context){

        sharedPref = context.getSharedPreferences("PARAMS", MODE_PRIVATE);
        Keys= sharedPref.getString("Keys","Counter");
        String[] keyArr = Keys.split(":");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.test_widget);
        SharedPreferences privateSP = context.getSharedPreferences(keyArr[counterIndex%keyArr.length], MODE_PRIVATE);
        views.setTextViewText(R.id.w_LabelTextView,keyArr[counterIndex%keyArr.length]);
        int value = privateSP.getInt("Value",0);
        views.setTextViewText(R.id.w_ValueTextView,String.valueOf(value));

        awm = AppWidgetManager.getInstance(context);
        awids = awm.getAppWidgetIds(new ComponentName(context, TestWidget.class));
        for (int i : awids) {
            awm.updateAppWidget(i, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is createdcontext.packageName + "_preferences"

    }

    public void refreshView(Context context,AppWidgetManager appWidgetManager,
                            int appWidgetId) {
        sharedPref = context.getSharedPreferences("PARAMS", MODE_PRIVATE);
        Keys= sharedPref.getString("Keys","Counter");
        String[] keyArr = Keys.split(":");
        SharedPreferences privateSP = context.getSharedPreferences(keyArr[counterIndex%keyArr.length], MODE_PRIVATE);
        SharedPreferences.Editor editor = privateSP.edit();
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.test_widget);

        Intent intent = new Intent(context, TestWidget.class);
        intent.setAction("nextPage");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.w_NextKey, pendingIntent);

        intent = new Intent(context, TestWidget.class);
        intent.setAction("incrementCounter");
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.w_IncButton, pendingIntent);

        intent = new Intent(context, TestWidget.class);
        intent.setAction("decrementCounter");
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.w_DecButton, pendingIntent);

        views.setTextViewText(R.id.w_LabelTextView,keyArr[counterIndex%keyArr.length]);
        views.setTextViewText(R.id.w_ValueTextView,String.valueOf(privateSP.getInt("Value",0)));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public void nextPage() {
        counterIndex++;
    }
    public void incrementValue(Context context){
        String[] keyArr = Keys.split(":");
        SharedPreferences sharedPref = context.getSharedPreferences(keyArr[counterIndex%keyArr.length], MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("Value", sharedPref.getInt("Value", 0)+1);
        editor.apply();
    }
    public void decrementValue(Context context){
        String[] keyArr = Keys.split(":");
        SharedPreferences sharedPref = context.getSharedPreferences(keyArr[counterIndex%keyArr.length], MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("Value", sharedPref.getInt("Value", 0)-1);
        editor.apply();
    }
    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}