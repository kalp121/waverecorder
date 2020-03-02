package com.danielkim.soundrecorder.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danielkim.soundrecorder.R;
import com.danielkim.soundrecorder.listeners.HolderItemListener;
import com.danielkim.soundrecorder.listeners.SelectFileListener;
import com.danielkim.soundrecorder.model.ServerFile;
import com.danielkim.soundrecorder.viewHolders.FileSelectorViewHolder;

import java.util.ArrayList;

public class FileSelecterAdapter extends RecyclerView.Adapter<FileSelectorViewHolder> implements HolderItemListener {
    SelectFileListener listener;
    String selected;

    public FileSelecterAdapter(SelectFileListener listener, String selected) {
        this.listener = listener;
        this.selected = selected;
    }

    private ArrayList<ServerFile> filesList = new ArrayList<>();

    @Override
    public FileSelectorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FileSelectorViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_file, parent, false), this);
    }

    @Override
    public void onBindViewHolder(FileSelectorViewHolder holder, int position) {
        ServerFile serverFile = filesList.get(position);
        holder.setName(serverFile.fileName);
        if (selected.equalsIgnoreCase("")) {
            if (serverFile.fileName.equalsIgnoreCase("Default")) {
                holder.setSelected(true);
            }else{
                holder.setSelected(false);
            }
        }else{
            holder.setSelected(serverFile.path.contains(selected));
        }
    }

    @Override
    public int getItemCount() {
        return filesList.size();
    }

    public void setFilesList(ArrayList<ServerFile> filesList) {
        this.filesList = filesList;
        notifyDataSetChanged();
    }

    @Override
    public void onViewClick(View view, int position) {
        switch (view.getId()) {
            case R.id.itemSelectFile: {
                listener.fileSelected(filesList.get(position));
                break;
            }
        }
    }
}
