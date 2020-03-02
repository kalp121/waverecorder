package com.danielkim.soundrecorder.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.danielkim.soundrecorder.MySharedPreferences;
import com.danielkim.soundrecorder.R;

public class SelectDataLanguageFragment extends DialogFragment {

    OnFragmentInteractionListener listener;

    public static SelectDataLanguageFragment newInstance() {
        SelectDataLanguageFragment fragment = new SelectDataLanguageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // initialize parameters here
        }
    }

    public void setListener(OnFragmentInteractionListener listener) {
        this.listener = listener;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_data_language, container, false);
        CardView english = view.findViewById(R.id.cvEnglish);
        CardView hindi = view.findViewById(R.id.cvHindi);
        CardView gujarati = view.findViewById(R.id.cvGujarati);

        RadioButton rbEnglish = view.findViewById(R.id.rbEnglish);
        RadioButton rbHindi = view.findViewById(R.id.rbHindi);
        RadioButton rbGujarati = view.findViewById(R.id.rbGujarati);

        int index = MySharedPreferences.getColumnIndex(getActivity());
        switch (index) {
            case 2: {
                rbEnglish.setChecked(true);
                rbHindi.setChecked(false);
                rbGujarati.setChecked(false);
                break;
            }
            case 3: {
                rbEnglish.setChecked(false);
                rbHindi.setChecked(false);
                rbGujarati.setChecked(true);
                break;
            }
            case 4: {
                rbEnglish.setChecked(false);
                rbHindi.setChecked(true);
                rbGujarati.setChecked(false);
                break;
            }
        }

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySharedPreferences.setLastReadIndex(getActivity(), 0);
                MySharedPreferences.setColumnIndex(getActivity(), 2);
                Toast.makeText(getActivity(), "Language selected successfully", Toast.LENGTH_SHORT).show();
                SelectDataLanguageFragment.this.dismiss();
                if (listener != null) {
                    listener.onFragmentInteraction(null);
                }
            }
        });

        gujarati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySharedPreferences.setLastReadIndex(getActivity(), 0);
                MySharedPreferences.setColumnIndex(getActivity(), 3);
                Toast.makeText(getActivity(), "Language selected successfully", Toast.LENGTH_SHORT).show();
                SelectDataLanguageFragment.this.dismiss();
                if (listener != null) {
                    listener.onFragmentInteraction(null);
                }
            }
        });


        hindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySharedPreferences.setLastReadIndex(getActivity(), 0);
                MySharedPreferences.setColumnIndex(getActivity(), 4);
                Toast.makeText(getActivity(), "Language selected successfully", Toast.LENGTH_SHORT).show();
                SelectDataLanguageFragment.this.dismiss();
                if (listener != null) {
                    listener.onFragmentInteraction(null);
                }
            }
        });


        return view;
    }
}
