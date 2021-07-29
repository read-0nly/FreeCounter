package com.read0nly.FreeCounter;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.read0nly.FreeCounter.databinding.FragmentFirstBinding;

import static android.content.Context.MODE_PRIVATE;

/**
 * Implementation of App Widget functionality.
 */
public class TestWidget extends AppWidgetProvider {
    static Context con;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.test_widget);
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
                nextPage(id);
                updateWidgets();
            }
        else if(action.startsWith("incrementCounter")){
                int id = Integer.parseInt(action.split(":")[1]);
                incrementValue(id);
                updateWidgets();
            }
        else if(action.startsWith("decrementCounter")){
                int id = Integer.parseInt(action.split(":")[1]);
                decrementValue(id);
                updateWidgets();

        }
    }
    public static void updateWidgets() {
        String Keys;
        SharedPreferences sharedPref;
        AppWidgetManager awm;
        int[] awids;
        RemoteViews views = new RemoteViews(CounterSet.self.Parent.getPackageName(), R.layout.test_widget);

        awm = AppWidgetManager.getInstance(CounterSet.self.Parent);
        awids = awm.getAppWidgetIds(new ComponentName(CounterSet.self.Parent, TestWidget.class));
        for (int i : awids) {
            int counterIndex = CounterSet.self.ParamSP.getInt(("WidgetCounter:"+String.valueOf(i)),0);
            views.setTextViewText(R.id.w_LabelTextView,CounterSet.self.getCounter(counterIndex).Key);
            int value = CounterSet.self.getCounter(counterIndex).getValue(0);
            views.setTextViewText(R.id.w_ValueTextView,String.valueOf(value));
            awm.updateAppWidget(i, views);
        }
    }
    @Override
    public void onEnabled(Context context) {


    }

    public void refreshView(Context context,AppWidgetManager appWidgetManager,
                            int appWidgetId) {

        int counterIndex = CounterSet.self.ParamSP.getInt(
                "WidgetCounter:"+String.valueOf(appWidgetId),0
        );
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.test_widget);
        Intent intent = new Intent(context, TestWidget.class);
        intent.setAction("nextPage:"+appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        views.setOnClickPendingIntent(R.id.w_NextKey, pendingIntent);

        intent = new Intent(context, TestWidget.class);
        intent.setAction("incrementCounter:"+appWidgetId);
        pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        views.setOnClickPendingIntent(R.id.w_IncButton, pendingIntent);

        intent = new Intent(context, TestWidget.class);
        intent.setAction("decrementCounter:"+appWidgetId);
        pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        views.setOnClickPendingIntent(R.id.w_DecButton, pendingIntent);

        views.setTextViewText(R.id.w_LabelTextView,CounterSet.self.getCounter(counterIndex).Key);
        views.setTextViewText(R.id.w_ValueTextView,
                String.valueOf(CounterSet.self.getCounter(counterIndex).getValue(0))
        );
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public void nextPage(int awid) {
        SharedPreferences.Editor editor = CounterSet.self.ParamSP.edit();
        int oldVal = CounterSet.self.ParamSP.getInt(
                "WidgetCounter:"+String.valueOf(awid),0
        );
        editor.putInt("WidgetCounter:"+String.valueOf(awid), oldVal+1);
        editor.apply();
    }
    public void incrementValue(int awid){
        int counterIndex = CounterSet.self.ParamSP.
                getInt(("WidgetCounter:"+String.valueOf(awid)),0);
        int len = CounterSet.self.Counters.toArray().length;
        CounterSet.self.getCounter(counterIndex%len).changeValue(1);
    }
    public void decrementValue(int awid){
        int counterIndex = CounterSet.self.ParamSP.
                getInt(("WidgetCounter:"+String.valueOf(awid)),0);
        int len = CounterSet.self.Counters.toArray().length;
        CounterSet.self.getCounter(counterIndex%len).changeValue(-1);
    }
    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}