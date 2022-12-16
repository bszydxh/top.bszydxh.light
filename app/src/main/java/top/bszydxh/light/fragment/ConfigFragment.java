package top.bszydxh.light.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.fasterxml.jackson.databind.ObjectMapper;
import top.bszydxh.light.R;
import top.bszydxh.light.activity.MainActivity;
import top.bszydxh.light.model.dto.dao.SettingDao;
import top.bszydxh.light.databinding.FragmentConfigBinding;
import top.bszydxh.light.model.dto.ConfigRequestDTO;
import top.bszydxh.light.model.dto.StateResponseDTO;

public class ConfigFragment extends Fragment {
    FragmentConfigBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentConfigBinding.inflate(getLayoutInflater());
        binding.lightButton.setClickable(false);
        binding.lightButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_gray));
        binding.lightButton.setOnClickListener(v -> {
            new Thread(() -> {
                Log.e("udp", "send start");
                try {
                    String ssid = binding.ssid.getText().toString();
                    String pwd = binding.pwd.getText().toString();
                    MainActivity mainActivity = (MainActivity) getActivity();
                    ObjectMapper objectMapper = new ObjectMapper();
                    ConfigRequestDTO configRequestDTO = new ConfigRequestDTO();
                    configRequestDTO.setSsid(ssid);
                    configRequestDTO.setPwd(pwd);
                    mainActivity.getControlFragment().setSendMsg(
                            objectMapper.writeValueAsString(configRequestDTO)
                    );
                } catch (Exception e) {
                    Log.i("udp", "send error");
                }
            }).start();
        });
        new Thread(() -> {
            Log.i("udp", "send start");
            while (true) {
                StateResponseDTO stateResponseDTO = SettingDao.getStateData(requireContext());
                if (stateResponseDTO.getState() == -1) {
                    binding.textView.setText("设备正在等待配网");
                    binding.lightButton.setClickable(true);
                    binding.lightButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue));
                } else if (stateResponseDTO.getState() == -9) {
                    binding.textView.setText("设备未连接");
                    binding.lightButton.setClickable(false);
                    binding.lightButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_gray));
                } else {
                    binding.textView.setText("设备未进入配网状态");
                    binding.lightButton.setClickable(false);
                    binding.lightButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_gray));
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    Log.i("udp", "thread stop");
                    break;
                }
            }
        }).start();
        return binding.getRoot();
    }
}
