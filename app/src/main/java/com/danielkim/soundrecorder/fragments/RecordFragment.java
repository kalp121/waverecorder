package com.danielkim.soundrecorder.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.danielkim.soundrecorder.Helper;
import com.danielkim.soundrecorder.MySharedPreferences;
import com.danielkim.soundrecorder.R;
import com.danielkim.soundrecorder.activities.MainActivity;
import com.danielkim.soundrecorder.model.FileModel;
import com.danielkim.soundrecorder.services.RecordingService;
import com.melnykov.fab.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POSITION = "position";
    private static final String LOG_TAG = RecordFragment.class.getSimpleName();
    public final static int INTERVAL = 5000;
    static MainActivity.MainPageFragmentListener pageListener;

    private int position;
    public static int groupIndex;
    public static int stringIndex;

    //Recording controls
    private FloatingActionButton mRecordButton = null;
    private Button mPauseButton = null;
    private ProgressBar mProgressBar = null;
    private int mProgressStatus=0;

    private Button mNextButton = null;
    private Button mPrevButton = null;

    private TextView mRecordingPrompt;
    private int mRecordPromptCount = 0;

    private boolean mStartRecording = true;
    private boolean mPauseRecording = true;

    //private ImageButton mNextButton;

    private Chronometer mChronometer = null;
    long timeWhenPaused = 0; //stores time when user clicks pause button

    private TextSwitcher mTextswitch = null;

    public ArrayList<FileModel> row = new ArrayList<>();
    private TextView textView = null;
    private String folderName;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Record_Fragment.
     */
    public static RecordFragment newInstance(int position, MainActivity.MainPageFragmentListener mainPageListener ,int index) {
        pageListener =mainPageListener;
        RecordFragment f = new RecordFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        b.putInt("groupIndex",index);
        f.setArguments(b);

        return f;
    }

    public RecordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        groupIndex=getArguments().getInt("groupIndex");
        MySharedPreferences.setLastReadIndex(getActivity(), groupIndex);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View recordView = inflater.inflate(R.layout.fragment_record, container, false);

        mChronometer = (Chronometer) recordView.findViewById(R.id.chronometer);




        mTextswitch = (TextSwitcher) recordView.findViewById(R.id.textswitch);



        //update recording prompt text
        mRecordingPrompt = (TextView) recordView.findViewById(R.id.recording_status_text);
        //mNextButton =(ImageButton) recordView.findViewById(R.id.next_button);
        mProgressBar= (ProgressBar)recordView.findViewById(R.id.progressBar1);


        Button btnReset = (Button) recordView.findViewById(R.id.btnCancel);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure want to reset file");
                builder.setNegativeButton("No", null);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onRecord(false);
                        setVisibleWord();
                        pageListener.onSwitchToNextFragment(0);
                    }
                });
                builder.create().show();
            }
        });

        mRecordButton = (FloatingActionButton) recordView.findViewById(R.id.btnRecord);
        mRecordButton.setColorNormal(getResources().getColor(R.color.material_blue_600));
        mRecordButton.setColorPressed(getResources().getColor(R.color.material_blue_grey_800));
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;
            }
        });


        mPauseButton = (Button) recordView.findViewById(R.id.btnPause);
        mPauseButton.setVisibility(View.GONE); //hide pause button before recording starts
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPauseRecord(mPauseRecording);
                mPauseRecording = !mPauseRecording;
            }
        });


        mNextButton = (Button) recordView.findViewById(R.id.btnNext);
        mNextButton.setVisibility(View.GONE);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTextswitch.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_previous));
                mTextswitch.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_next));
                mProgressStatus=mProgressStatus+1;

                mProgressBar.setProgress(mProgressStatus);


                if (stringIndex == row.size() - 1) {
                    stringIndex = 0;
                    mTextswitch.setText(row.get(stringIndex).displayName);
                } else {
                    mTextswitch.setText(row.get(++stringIndex).displayName);
                }

                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;
                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;
            }
        });





        mPrevButton = (Button) recordView.findViewById(R.id.btnPrev);
        mPrevButton.setVisibility(View.GONE);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mTextswitch.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_next));
                mTextswitch.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_previous));
                mProgressStatus=mProgressStatus-1;
                mProgressBar.setProgress(mProgressStatus);

                if (stringIndex == 0) {
                    stringIndex = row.size() - 1;
                    mTextswitch.setText(row.get(stringIndex).displayName);
                } else {
                    mTextswitch.setText(row.get(--stringIndex).displayName);
                }
                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;
                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;
            }
        });


        mTextswitch.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                textView = new TextView(getActivity());
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(40);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                return textView;
            }
        });
        mTextswitch.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_previous));
        mTextswitch.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_next));
        return recordView;
    }

    @Override
    public void onPause() {
        super.onPause();
        MySharedPreferences.setLastReadIndex(getActivity(), stringIndex);
    }

    @Override
    public void onResume() {
        super.onResume();
        readFile();
        setVisibleWord();
    }

    private void setVisibleWord() {

        stringIndex = MySharedPreferences.getLastReadIndex(getActivity());
        mTextswitch.setText(row.get(stringIndex).displayName);
    }

    // Recording Start/Stop
    //TODO: recording pause
    private void onRecord(boolean start) {
        Intent intent = new Intent(getActivity(), RecordingService.class);
        if (start) {

            intent.putExtra("name", String.valueOf(System.currentTimeMillis())+ row.get(stringIndex).fileName);
            intent.putExtra("folderName", folderName);
            // start recording
            mRecordButton.setImageResource(R.drawable.ic_media_stop);

            mPauseButton.setVisibility(View.VISIBLE);

//            Toast.makeText(getActivity(), R.string.toast_recording_start, Toast.LENGTH_SHORT).show();
            File folder = new File(Environment.getExternalStorageDirectory() + Helper.getSimplePath(getActivity(), folderName));
            if (!folder.exists()) {
                //folder /SoundRecorder doesn't exist, create the folder
                folder.mkdirs();
            }

            mRecordPromptCount = -1;
            //start Chronometer
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if (mRecordPromptCount == 1) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");

                    } else if (mRecordPromptCount == 2) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + "..");


                    } else if (mRecordPromptCount == 3) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + "...");
                        mRecordPromptCount = -1;
                        if (mPauseButton.getVisibility() == View.VISIBLE) {
                            mNextButton.setVisibility(View.VISIBLE);
                            mPrevButton.setVisibility(View.VISIBLE);
                        }
                    }

                    mRecordPromptCount++;
                }
            });

            //start RecordingService
            getActivity().startService(intent);
            //keep screen on while recording
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");
            mRecordPromptCount++;

        } else {
            //stop recording

            mRecordButton.setImageResource(R.drawable.ic_mic_white_36dp);

            mPauseButton.setVisibility(View.GONE);
            mNextButton.setVisibility(View.GONE);
            mPrevButton.setVisibility(View.GONE);

            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            mRecordingPrompt.setText(getString(R.string.record_prompt));
            getActivity().stopService(intent);
            //allow the screen to turn off again once recording is finished
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    //TODO: implement pause recording
    private void onPauseRecord(boolean pause) {
        if (pause) {
            //pause recording
            mPauseButton.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.ic_media_play, 0, 0, 0);
            mRecordingPrompt.setText((String) getString(R.string.resume_recording_button).toUpperCase());
            timeWhenPaused = mChronometer.getBase() - SystemClock.elapsedRealtime();
            mChronometer.stop();
        } else {
            //resume recording
            mPauseButton.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.ic_media_pause, 0, 0, 0);
            mRecordingPrompt.setText((String) getString(R.string.pause_recording_button).toUpperCase());
            mChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenPaused);
            mChronometer.start();
        }
    }


    private void readFile() {
        String path = MySharedPreferences.getSelectedFilePath(getActivity());
        if (!path.isEmpty()) {
            readFileFromSource(path);
            return;
        }
        InputStream is = getResources().openRawResource(R.raw.product_names_gujarati);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        readFile(reader);
    }

    private void readFile(BufferedReader reader) {
        String line;
        try {
            int count = 0;
            row.clear();
            while ((line = reader.readLine()) != null) {
                count++;
                String[] name = line.split(",");
                if (name.length > 0) {
                    String text = name[MySharedPreferences.getColumnIndex(getActivity())].trim();
                    if (count == 2) {
                        folderName = text;
                    } else if (count > 2) {
                        String fileName = name[MySharedPreferences.getFileNameColumnIndex(getActivity())].trim();
                        FileModel fileModel = new FileModel();
                        fileModel.displayName = text;
                        fileModel.fileName = fileName;
                        row.add(fileModel);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFileFromSource(String path) {
        File f = new File(path);
        if (!f.exists()) {
            MySharedPreferences.setSelectedFilePath(getActivity(), "");
            MySharedPreferences.setSelectedFileName(getActivity(), "Default");
            readFile();
            return;
        }

        InputStream is = getResources().openRawResource(R.raw.product_names_gujarati);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            readFile(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}