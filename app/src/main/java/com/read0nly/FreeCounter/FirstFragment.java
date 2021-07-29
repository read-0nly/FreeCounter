package com.read0nly.FreeCounter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.navigation.fragment.NavHostFragment;

import com.read0nly.FreeCounter.databinding.FragmentFirstBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;

public class FirstFragment extends Fragment {
    private FragmentFirstBinding binding;
    TextView KeyBox;
    TextView ValueBox;
    FirstFragment me = this;
    int counterIndex = 0;
    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState){
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.seeDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CounterSet.self.updateCounters();
                String dayLog = "";
                for(int i =0;i<10;i++){
                    int len = CounterSet.self.Counters.toArray().length;
                    dayLog+="["+Counter.getKey(0-i)+"]:"+
                            CounterSet.self.getCounter(counterIndex%len).
                                    getValue(0-i)+"  |  ";
                }
                Snackbar.make(view, dayLog, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                SharedPreferences.Editor editor = CounterSet.self.ParamSP.edit();
                editor.putInt("GraphIndex", counterIndex);
                editor.apply();
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
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
                int len = CounterSet.self.Counters.toArray().length;
                if(counterIndex>0) {
                    counterIndex--;
                    refreshView();
                }else {
                    counterIndex=len;
                    refreshView();
                }
            }
        });
        binding.AddCounterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(me.getContext());
                builder.setTitle("New Counter");
                final EditText input = new EditText(me.getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
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
        KeyBox.setText(String.valueOf(getKeyByIndex()));
        ValueBox.setText(String.valueOf(getValueByIndex()));

    }
    public void addKey(String newKey){
        CounterSet.self.addCounter(newKey);
        refreshView();
    }
    public void removeKey(String newKey){
        CounterSet.self.removeCounter(newKey);
        refreshView();
    }
    public String getValueByIndex(){
        int len = CounterSet.self.Counters.toArray().length;
        int r = CounterSet.self.getCounter(counterIndex%len).getValue(0);
        return String.valueOf(r);
    }
    public String getKeyByIndex(){
        int len = CounterSet.self.Counters.toArray().length;
        return CounterSet.self.Keys.get(counterIndex%len);

    }
    public void incrementValue(){
        int len = CounterSet.self.Counters.toArray().length;
        CounterSet.self.getCounter(counterIndex%len).changeValue(1);
        refreshView();
    }
    public void decrementValue(){
        int len = CounterSet.self.Counters.toArray().length;
        CounterSet.self.getCounter(counterIndex%len).changeValue(-1);
        refreshView();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
