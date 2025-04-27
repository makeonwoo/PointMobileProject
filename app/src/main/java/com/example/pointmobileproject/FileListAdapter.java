package com.example.pointmobileproject;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {

    private List<String> fileList;
    private OnDirectoryClickListener listener;

    public interface OnDirectoryClickListener {
        void onDirectoryClick(String directoryName);
    }

    public FileListAdapter(List<String> fileList, OnDirectoryClickListener listener) {
        this.fileList = fileList;
        this.listener = listener;
    }
    @NonNull
    @Override
    public FileListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileListAdapter.ViewHolder holder, int position) {
        String fileInfo = fileList.get(position);

        boolean isDirectory = fileInfo.startsWith("d");

        String[] fileInfoList = fileInfo.split("\\s+");

        String fileName = fileInfoList[fileInfoList.length - 1];

        holder.textViewFileName.setText(fileName);
        holder.textViewFileInfo.setText(fileInfo.replace(fileName,""));

        if (isDirectory) {
            holder.icDirectory.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDirectoryClick(fileName);
                }
            });
        } else {
            holder.icDirectory.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(null);
        }
    }

    public void updateFileList(List<String> newFileList) {
        this.fileList.clear();
        this.fileList.addAll(newFileList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewFileName;
        TextView textViewFileInfo;

        ImageView icDirectory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFileName = itemView.findViewById(R.id.textViewFileName);
            textViewFileInfo = itemView.findViewById(R.id.textViewFileInfo);
            icDirectory = itemView.findViewById(R.id.icDirectory);
        }
    }
}
