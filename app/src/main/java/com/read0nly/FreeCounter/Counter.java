package com.read0nly.FreeCounter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class Counter {
    public String Key;
    public Context Parent;
    public boolean Carry;
    public int Default;
    public int Goal;
    public boolean GoalDir;
    SharedPreferences ParamSP;
    SharedPreferences ValueSP;
    public static int EMPTYVAL = -987654321;

    public Counter(String k, Activity a){
        Key = k;
        Parent = a;
        ParamSP = Parent.getSharedPreferences(Key+"_Param", MODE_PRIVATE);
        ValueSP = Parent.getSharedPreferences(Key, MODE_PRIVATE);
        Carry=ParamSP.getInt("Carry",0)==1;
        Default=ParamSP.getInt("Default",0);
        Goal=ParamSP.getInt("Goal",0);
        GoalDir=ParamSP.getInt("GoalDir",0)==1;
    }
    public Counter(String k, Context c){
        Key = k;
        Parent = c;
        ParamSP = Parent.getSharedPreferences(Key+"_Param", MODE_PRIVATE);
        ValueSP = Parent.getSharedPreferences(Key, MODE_PRIVATE);
        Carry=ParamSP.getInt("Carry",0)==1;
        Default=ParamSP.getInt("Default",0);
        Goal=ParamSP.getInt("Goal",0);
        GoalDir=ParamSP.getInt("GoalDir",0)==1;
    }

    public void updateSettings(){
        SharedPreferences.Editor editor = ParamSP.edit();
        editor.putInt("Carry",(Carry?1:0));
        editor.putInt("Default",Default);
        editor.putInt("Goal",Goal);
        editor.putInt("GoalDir",(GoalDir?1:0));
        editor.apply();
    }
    //Returns value matching that day
    public int getValue(int dateOffset) {
        String k = getKey(dateOffset);
        int r = ValueSP.getInt(k, EMPTYVAL);
        r = ( r == EMPTYVAL ? 0 : r);
        return r;
    }

    public int changeValue(int offset) {
        String k = getKey(0);
        SharedPreferences.Editor editor = ValueSP.edit();
        int r = ValueSP.getInt(k,(Carry?getValue(-1):Default));
        r =  (r<EMPTYVAL? Default:r);
        r += offset;
        editor.putInt(k,r);
        editor.apply();
        return r;
    }

    public static String getKey(int offset){
        Calendar c = Calendar.getInstance();
        String k = String.valueOf(c.get(Calendar.YEAR))+
                String.format("%2d",c.get(Calendar.MONTH)+1).replace(' ', '0')+
                String.valueOf(c.get(Calendar.DATE)+offset);
        return k;
    }
}
