package com.example.pointmobileproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointmobileproject.databinding.FragmentSecondBinding;

import java.util.List;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private FTPManager ftpManager;
    private FileListAdapter adapter;

    private Thread createAdapterThread;
    private Thread getListThread;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean stopFlag = false;
        ftpManager = new FTPManager();

        // RecyclerView 기본 세팅
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 파일 목록 가져오기
        createAdapterThread = new Thread(() -> {
            ftpManager.connect();
            List<String> fileList = ftpManager.getFileList();

            //데이터 가져왔더라도 쓰레드 종료되면 어댑터 건드리지않기
            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            requireActivity().runOnUiThread(() -> {
                adapter = new FileListAdapter(fileList, directoryName -> {
                    changeDirectory(directoryName);
                });
                binding.recyclerView.setAdapter(adapter);
                binding.loading.setVisibility(View.GONE);
            });
        });

        createAdapterThread.start();

        // 뒤로가기 버튼
        binding.buttonSecond.setOnClickListener(view1 -> {
            if (createAdapterThread != null && createAdapterThread.isAlive()) {
                createAdapterThread.interrupt();
            }

            if (getListThread != null && getListThread.isAlive()) {
                getListThread.interrupt();
            }

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FirstFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void changeDirectory(String directoryName) {
        binding.recyclerView.setVisibility(View.INVISIBLE);
        binding.loading.setVisibility(View.VISIBLE);

        getListThread = new Thread(() -> {
            ftpManager.changeDirectory(directoryName);
            List<String> newFileList = ftpManager.getFileList();

            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            requireActivity().runOnUiThread(() -> {
                adapter.updateFileList(newFileList);
                binding.loading.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
            });
        });
        getListThread.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
