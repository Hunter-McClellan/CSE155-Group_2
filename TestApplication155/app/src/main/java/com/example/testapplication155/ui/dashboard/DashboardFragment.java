package com.example.testapplication155.ui.dashboard;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.nio.ByteBuffer;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.testapplication155.MainActivity;
import com.example.testapplication155.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    //private Bitmap bitmapImage = null;
    private String untrans;
    private String trans;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        

        untrans = ((MainActivity)requireActivity()).originalText;
        trans = ((MainActivity)requireActivity()).translatedText;

        binding.textDashboard.setText(untrans);
        binding.translateview.setText(trans);

        /*Image image = ((MainActivity)requireActivity()).img;
        if (image != null) {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
        }
        if (bitmapImage != null) {
            binding.imageprev.setImageBitmap(bitmapImage);
        }*/

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}