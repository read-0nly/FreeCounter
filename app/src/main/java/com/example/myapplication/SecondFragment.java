package com.example.myapplication;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.databinding.FragmentSecondBinding;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }
    public void refreshGraph(){

        SharedPreferences sharedPref = getActivity().getSharedPreferences("PARAMS", MODE_PRIVATE);
        String Keys = sharedPref.getString("Keys", "Counter");
        String[] keyArr = Keys.split(":");
        int counterIndex = sharedPref.getInt("GraphIndex",0);

        SharedPreferences privateSP = getActivity().
                getSharedPreferences(keyArr[counterIndex%keyArr.length], MODE_PRIVATE);

        // creating a calendar
        Calendar c = Calendar.getInstance();
        String key = String.valueOf(c.get(Calendar.YEAR))+
                String.format("%2d",c.get(Calendar.MONTH)+1).replace(' ', '0');

        ArrayList<String> keys =  new ArrayList<String>();
        for(String i : privateSP.getAll().keySet()){
            keys.add(i);
        }
        ArrayList<String> goodKeys = new ArrayList();
        for(String i : keys){
            if(i.startsWith(key)){
                goodKeys.add(i);
            };
        }
        Collections.sort(goodKeys);
        Collections.reverse(goodKeys);

        RelativeLayout rl = (RelativeLayout) getView().findViewById(R.id.ResultLayout);
        final int childCount = rl.getChildCount();
        HashMap<String,Integer> bars = new HashMap<String,Integer>();
        for (int i = 0; i < childCount; i++) {
            View v = rl.getChildAt(i);
            int x =0;// Do something with v.
            bars.put(getResources().getResourceEntryName(v.getId()),i);
        }
        for(int i =0;i<30;i++){
            TextView tv = (TextView)rl.getChildAt(bars.get("Bar"+(i+1)));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
            params.topMargin = 2;
            params.setMarginStart(10);
            //SecondFragLayout
            int newwidth = ((RelativeLayout)getView().findViewById(R.id.ResultLayout)).getWidth()-((TextView)getView().findViewById(R.id.textView31)).getLeft();
            if(i<goodKeys.toArray().length){
                params.setMarginEnd(newwidth-privateSP.getInt(goodKeys.get(i),0)*10);
                tv.setText(""+privateSP.getInt(goodKeys.get(i),0));
            }else {
                params.setMarginEnd(newwidth);
                tv.setText("0");
            }
            tv.setLayoutParams(params);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshGraph();
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((ConstraintLayout)getView().findViewById(R.id.SecondFragLayout)).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ((ConstraintLayout)getView().findViewById(R.id.SecondFragLayout)).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    //noinspection deprecation
                    ((ConstraintLayout)getView().findViewById(R.id.SecondFragLayout)).getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                //
                // mycode
                //
                refreshGraph();
            }
        });


/*

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}