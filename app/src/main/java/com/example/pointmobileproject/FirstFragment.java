package com.example.pointmobileproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pointmobileproject.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private FTPManager ftpManager;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ftpManager = new FTPManager();

        binding.btnUpload.setOnClickListener(v -> {
            new Thread(() -> {

                String fileName = binding.editTextFileTitle.getText().toString() + ".txt";
                String fileContent = binding.editTextFileContent.getText().toString();

                boolean result = ftpManager.uploadFile(fileName, fileContent);

                requireActivity().runOnUiThread(() -> {
                    if (result) {
                        binding.textviewFirst.setText(getString(R.string.upload_sucess_text));
                    } else {
                        binding.textviewFirst.setText(getString(R.string.upload_failed_text));
                    }
                    binding.editTextFileTitle.setText("");
                    binding.editTextFileContent.setText("");
                });
            }).start();
        });

        binding.btnGetList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SecondFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}