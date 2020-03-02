package com.danielkim.soundrecorder.fragments;


import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.danielkim.soundrecorder.MySharedPreferences;

import com.danielkim.soundrecorder.R;
import com.danielkim.soundrecorder.activities.MainActivity;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Grouping extends Fragment {

    private Button mGroup1= null;
    private Button mGroup2 = null;
    private Button mGroup3 = null;
    private Button mGroup4 = null;
    private TextView title= null;
    private static final String ARG_POSITION = "position";
    static MainActivity.MainPageFragmentListener pageListener;


    public Grouping() {
        // Required empty public constructor
    }

    public static Grouping newInstance(int position, MainActivity.MainPageFragmentListener mainPageListener) {
        pageListener =mainPageListener;
        Grouping f = new Grouping();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);

        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View groupView=inflater.inflate(R.layout.fragment_grouping, container, false);
        mGroup1=(Button)groupView.findViewById(R.id.group1);
        mGroup2=(Button)groupView.findViewById(R.id.group2);
        mGroup3=(Button)groupView.findViewById(R.id.group3);
        mGroup4=(Button)groupView.findViewById(R.id.group4);
        title=(TextView)groupView.findViewById(R.id.title);

        mGroup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageListener.onSwitchToNextFragment(0);
            }
        });

        mGroup2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageListener.onSwitchToNextFragment(1);
            }
        });

        mGroup3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageListener.onSwitchToNextFragment(2);

            }
        });

        mGroup4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageListener.onSwitchToNextFragment(3);

            }
        });

        return groupView;


    }

}
