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

        ftpManager = new FTPManager();

        // RecyclerView 기본 세팅
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 파일 목록 불러오기
        new Thread(() -> {
            List<String> fileList = ftpManager.getFileList(); // FTP 서버에서 파일 리스트 가져오기

            requireActivity().runOnUiThread(() -> {
                adapter = new FileListAdapter(fileList);
                binding.recyclerView.setAdapter(adapter);
                binding.loading.setVisibility(View.GONE);
            });
        }).start();

        // 뒤로가기 버튼
        binding.buttonSecond.setOnClickListener(view1 -> {
            //1안 데이터 정리하기 -> 파일서버 연결중인 쓰레드 종료해버리면됨
            // -> 쓰레드 핸들러로 변경... 관리? 흐음
            // 2안 ui제한 -> 안드로이드는 ui제한보다 사용자 편의성이 우선 1안으로?


            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FirstFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
