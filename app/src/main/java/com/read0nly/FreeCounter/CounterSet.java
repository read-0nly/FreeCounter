package com.read0nly.FreeCounter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class CounterSet {
    ArrayList<Counter> Counters;
    Context Parent;
    SharedPreferences ParamSP;
    ArrayList<String>  Keys;
    public static CounterSet self;

    public CounterSet(Context c){
        Parent = c;
        ParamSP = Parent.getSharedPreferences("PARAM", MODE_PRIVATE);
        updateCounters();
    };
    public CounterSet(Activity a){
        Parent = a;
        ParamSP = Parent.getSharedPreferences("PARAM", MODE_PRIVATE);
        updateCounters();
    };
    public Counter getCounter(String k){
        for(Counter c : Counters){
            if(c.Key==k){
                return c;
            }
        }
        return null;
    }
    public Counter getCounter(int i){
        if(!(Counters==null)&&!Counters.isEmpty()){
            return Counters.get(i%Counters.toArray().length);
        }
        return null;
    }
    public void updateCounters(){
        Counters = new ArrayList<Counter>();
        Keys = splitKeys();
        for(String s : Keys){
            Counters.add(new Counter(s,Parent));
        }
    };
    private ArrayList<String>  splitKeys(){
        String keyString = ParamSP.getString("Keys","Counter");
        ArrayList<String> r= new ArrayList<String>();
        if(keyString.contains(":")) {
            for(String s :keyString.split(":")){
                if(s!="") { r.add(s); }
            }
        }else{
            if(keyString!=""){ r.add(keyString); }
        }
        return r;
    }
    private void joinKeys(){
        String r = "";
        for(String s : Keys){
            r+=s+":";
        }
        SharedPreferences.Editor editor = ParamSP.edit();
        editor.putString("Keys",r);
        editor.apply();
    }
    public void addCounter(String k){
        Keys = splitKeys();
        if(!Keys.contains(k)){ Keys.add(k); }
        joinKeys();
        updateCounters();
    }
    public void removeCounter(String k){
        Keys = splitKeys();
        if(!Keys.contains(k)){ Keys.remove(k); }
        joinKeys();
        updateCounters();
    }
}
