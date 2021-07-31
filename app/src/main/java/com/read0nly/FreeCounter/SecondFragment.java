package com.read0nly.FreeCounter;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.read0nly.FreeCounter.databinding.FragmentSecondBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

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
        RelativeLayout rl = (RelativeLayout) getView().findViewById(R.id.ResultLayout);
        final int childCount = rl.getChildCount();
        HashMap<String,Integer> bars = new HashMap<String,Integer>();
        int gIndex = CounterSet.self.ParamSP.getInt("GraphIndex",0);
        Counter counter = CounterSet.self.getCounter(gIndex);
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
            int newwidth = ((RelativeLayout)getView().findViewById(R.id.ResultLayout)).getWidth() -
                    ((TextView)getView().findViewById(R.id.textView31)).getLeft();
            params.setMarginEnd(newwidth - (int)counter.getValue(0-i) * (((RelativeLayout)getView().findViewById(R.id.ResultLayout)).getWidth()/50));
            tv.setText(""+counter.getValue(0-i));
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
        ((ConstraintLayout) getView().findViewById(R.id.SecondFragLayout)).getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            ((ConstraintLayout) getView().findViewById(R.id.SecondFragLayout))
                                    .getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            //noinspection deprecation
                            ((ConstraintLayout) getView().findViewById(R.id.SecondFragLayout))
                                    .getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        refreshGraph();
                    }
                });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}