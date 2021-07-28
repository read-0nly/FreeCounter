package com.example.myapplication;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.databinding.FragmentFirstBinding;
import com.google.android.material.snackbar.Snackbar;

import org.xml.sax.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    TextView KeyBox;
    TextView ValueBox;
    FirstFragment me = this;
    int counterIndex = 0;
    String Keys;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);

        ;
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("PARAMS", MODE_PRIVATE);
        Keys= sharedPref.getString("Keys","Counter");


        binding.seeDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] keyArr = Keys.split(":");
                SharedPreferences privateSP = getActivity().
                        getSharedPreferences(keyArr[counterIndex%keyArr.length], MODE_PRIVATE);
                //SharedPreferences.Editor editor = privateSP.edit();

                // creating a calendar
                Calendar c = Calendar.getInstance();
                String key = String.valueOf(c.get(Calendar.YEAR))+
                        String.format("%2d",c.get(Calendar.MONTH)+1).replace(' ', '0');

                //Snackbar.make(view, String.valueOf(privateSP.getInt(key,0)), Snackbar.LENGTH_LONG)
                //       .setAction("Action", null).show();

                //Snackbar.make(view, String.valueOf(privateSP.getAll().keySet().toString()), Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                String dayLog = "";
                ArrayList<String> keys =  new ArrayList<String>();
                for(String i : privateSP.getAll().keySet()){
                    keys.add(i);
                }
                ArrayList<String> goodKeys = new ArrayList();
                for(String i : keys){
                    if(i.startsWith(key)){
                        goodKeys.add(i.substring(4,6)+"/"+i.substring(6,8));
                    };
                }
                Collections.sort(goodKeys);
                Collections.reverse(goodKeys);
                for(String i : goodKeys){
                    dayLog+="["+i+"]:"+String.format("%3d",privateSP.getInt(i,0))+"  |  ";
                }
                Snackbar.make(view, dayLog, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
        binding.saveDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] keyArr = Keys.split(":");
                // creating a calendar
                Calendar c = Calendar.getInstance();
                String key = String.valueOf(c.get(Calendar.YEAR))+
                        String.format("%2d",c.get(Calendar.MONTH)+1).replace(' ', '0')+
                        String.valueOf(c.get(Calendar.DATE)+1);

                SharedPreferences privateSP = getActivity().
                        getSharedPreferences(keyArr[counterIndex%keyArr.length], MODE_PRIVATE);
                SharedPreferences.Editor editor = privateSP.edit();
                editor.putInt(key, privateSP.getInt("Value", 0)+privateSP.getInt(key,0));
                editor.putInt("Value",0);
                editor.apply();
                refreshView();


            }
        });
        binding.DecrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decrementValue();
            }
        });
        binding.IncrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incrementValue();
            }
        });
        binding.nextCounterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterIndex++;
                refreshView();
            }
        });
        binding.prevCounterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] keyArr = Keys.split(":");
                if(counterIndex>0) {
                    counterIndex--;
                    refreshView();
                }else {
                    counterIndex=keyArr.length-1;
                    refreshView();
                }
            }
        });
        binding.AddCounterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(me.getContext());
                builder.setTitle("New Counter");

// Set up the input
                final EditText input = new EditText(me.getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addKey(input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        binding.deleteCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeKey(getKeyByIndex());
            }
        });


        /*
deleteCounter
         */
        ValueBox= (TextView) getView().findViewById(R.id.textview_first);
        KeyBox= (TextView) getView().findViewById(R.id.textview_first2);
        refreshView();

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }
    @Override
    public void onPause() {
        super.onPause();
        TestWidget.updateWidgets();
        refreshView();
    }
    public void refreshView() {
        KeyBox.setText("Current Key: "+getKeyByIndex());
        ValueBox.setText("Current Value: "+getValueByIndex());

    }
    public void addKey(String newKey){
        if(!Keys.contains(newKey)) {
            Keys += ":" + newKey;
            SharedPreferences sharedPref = getActivity().getSharedPreferences(newKey, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("Value", sharedPref.getInt(newKey, 0)+1);
            sharedPref = getActivity().getSharedPreferences("PARAMS", MODE_PRIVATE);
            editor = sharedPref.edit();
            editor.putString("Keys", Keys);
            editor.apply();
        }
        refreshView();
    }
    public void removeKey(String newKey){
        if(Keys.contains(newKey)) {
            if(Keys.contains(":")) {
                if(Keys.contains(":"+newKey)) {
                    Keys = Keys.replace((":" + newKey), "");
                }else {
                    Keys = Keys.replace((newKey+":"), "");
                }
            }
            else {
                Keys = Keys.replace(newKey, "");
            }
            SharedPreferences sharedPref = getActivity().getSharedPreferences("PARAMS", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove(newKey);
            editor.putString("Keys", Keys);
            editor.apply();

        }
        refreshView();
    }
    public String getValueByIndex(){
        String[] keyArr = Keys.split(":");
        SharedPreferences sharedPref = getActivity().getSharedPreferences(keyArr[counterIndex%keyArr.length], MODE_PRIVATE);
        return String.valueOf(sharedPref.getInt("Value", 0));
    }
    public String getKeyByIndex(){
        String[] keyArr = Keys.split(":");
        return keyArr[counterIndex%keyArr.length];
    }
    public void incrementValue(){
        String[] keyArr = Keys.split(":");
        SharedPreferences sharedPref = getActivity().getSharedPreferences(keyArr[counterIndex%keyArr.length], MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("Value", sharedPref.getInt("Value", 0)+1);
        editor.apply();
        refreshView();
    }
    public void decrementValue(){
        String[] keyArr = Keys.split(":");
        SharedPreferences sharedPref = getActivity().getSharedPreferences(keyArr[counterIndex%keyArr.length], MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("Value", sharedPref.getInt("Value", 0)-1);
        editor.apply();
        refreshView();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
