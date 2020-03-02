package com.danielkim.soundrecorder.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.danielkim.soundrecorder.R;
import com.danielkim.soundrecorder.listeners.HolderItemListener;

public class FileSelectorViewHolder extends RecyclerView.ViewHolder {
    private TextView tvTitle;
    private RadioButton rbFile;

    public FileSelectorViewHolder(View itemView, final HolderItemListener listener) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        rbFile = itemView.findViewById(R.id.rbFile);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onViewClick(view, getPosition());
            }
        });
    }

    public void setName(String fileName) {
        tvTitle.setText(fileName);
    }

    public void setSelected(boolean isSelected) {
        if (isSelected) {
            rbFile.setVisibility(View.VISIBLE);
        } else {
            rbFile.setVisibility(View.GONE);
        }
    }
}
