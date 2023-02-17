package top.bszydxh.light.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import top.bszydxh.light.R;
import top.bszydxh.light.activity.MainActivity;
import top.bszydxh.light.model.dto.dao.SettingDao;
import top.bszydxh.light.databinding.FragmentControlBinding;
import top.bszydxh.light.model.dto.StateResponseDTO;
import top.bszydxh.light.utils.ResponseListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ControlFragment extends Fragment {
    FragmentControlBinding binding;
    DatagramSocket socket;
    Long lastConnectionTimestamp;
    ResponseListener.DataBackListener dataBackListener;
    volatile String sendMsg = "state";

    public void sendTask(String str) {
        new Thread(() -> {
            try {
                byte[] buffer = str.getBytes();
                InetAddress groupAddress = InetAddress.getByName(SettingDao.getAddress(requireContext()));
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, groupAddress, 1145);
                socket.send(packet);
                Log.d("udp", str + "->" + SettingDao.getAddress(requireContext()) + ":1145");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("udp", str + "-/>" + SettingDao.getAddress(requireContext()) + ":error");
            }
        }).start();
    }

    public void setSendMsg(String msg) {
        synchronized (this) {
            sendMsg = msg;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        lastConnectionTimestamp = System.currentTimeMillis();
        binding = FragmentControlBinding.inflate(getLayoutInflater());
        SettingDao.setAddress(requireContext(), "255.255.255.255");
        try {
            socket = new DatagramSocket(1145);
        } catch (SocketException e) {
            Log.e("udp", "listen error");
        }
        new Thread(() -> {
            Log.d("udp", "send start");
            int sleep_time;
            while (true) {
                synchronized (this) {
                    StateResponseDTO stateData = SettingDao.getStateData(requireContext());
                    sendTask(sendMsg);
                    switch (sendMsg) {
                        case "turn_on":
                            if (stateData.getState() != 0) {
                                sendMsg = "state";
                            }
                            sleep_time = 600;
                            break;
                        case "turn_off":
                            if (stateData.getState() != 1) {
                                sendMsg = "state";
                            }
                            sleep_time = 600;
                            break;
                        case "computer":
                            if (stateData.getRgb() != 0) {
                                sendMsg = "state";
                            }
                            sleep_time = 600;
                            break;
                        case "normal_light":
                            if (stateData.getRgb() != 1) {
                                sendMsg = "state";
                            }
                            sleep_time = 600;
                            break;
                        default:
                            sleep_time = 20;
                            sendMsg = "state";
                    }

                }
                try {
                    Thread.sleep(sleep_time);
                } catch (Exception e) {
                    Log.d("udp", "thread stop");
                    break;
                }
                Log.d("udp", "send ok");
                if (System.currentTimeMillis() - lastConnectionTimestamp >= 3000) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        dataBackListener.onDataBack(null, null);
                    });
                }
            }
        }).start();
        ResponseListener responseListener = new ResponseListener(8081);
        binding.ledsViewLayout.addLedView(48, true);
        binding.ledsViewLayout.addLedView(48, false);
        binding.ledsViewLayout.addLedView(24, false);
        dataBackListener = (data, ip) -> {
            MainActivity main = (MainActivity) ControlFragment.this.getActivity();
            if (data == null || ip == null) {
                binding.textView.setText("设备未连接");
                binding.color.setClickable(false);
                binding.color.setBackgroundColor(ContextCompat.getColor(ControlFragment.this.requireContext(), R.color.dark_gray));
                binding.light.setClickable(false);
                binding.light.setBackgroundColor(ContextCompat.getColor(ControlFragment.this.requireContext(), R.color.dark_gray));
                SettingDao.setStateData(binding.textView.getContext(), null);
                binding.ledsViewLayout.clear();
                main.setTitle("极光智能灯带", "离线");
                return;
            }
            lastConnectionTimestamp = System.currentTimeMillis();
            main.setTitle("极光智能灯带", "在线");
            binding.color.setClickable(true);
            binding.color.setBackgroundColor(ContextCompat.getColor(ControlFragment.this.requireContext(), R.color.sky_blue));
            binding.light.setClickable(true);
            binding.light.setBackgroundColor(ContextCompat.getColor(ControlFragment.this.requireContext(), R.color.sky_blue));
            ObjectMapper objectMapper = new ObjectMapper();
            StateResponseDTO stateResponseDTO = new StateResponseDTO();
            try {
                stateResponseDTO = objectMapper.readValue(data, StateResponseDTO.class);
            } catch (JsonProcessingException e) {
                Log.e("jackson", "jackson unpack error:" + e);
            }
            SettingDao.setStateData(binding.textView.getContext(), stateResponseDTO);
            if (stateResponseDTO.getLeds() != null && stateResponseDTO.getLeds().size() != 0) {
                binding.ledsViewLayout.setColor(stateResponseDTO.getLeds());
            }
            if (stateResponseDTO.getState() == 0) {
                binding.light.setText("开灯");
                binding.light.setOnClickListener(v -> {
                    Log.d("turn", "turn_on");
                    setSendMsg("turn_on");
                });
            } else if (stateResponseDTO.getState() == 1) {
                binding.light.setText("关灯");
                binding.light.setOnClickListener(v -> {
                    Log.d("turn", "turn_off");
                    setSendMsg("turn_off");
                });
            } else if (stateResponseDTO.getState() == -1) {
                binding.light.setText("配网");
                binding.color.setText("配网");
                binding.light.setOnClickListener(v -> {
                    main.pageTo(1);
                });
                binding.color.setOnClickListener(v -> {
                    main.pageTo(1);
                });
            }
            if (stateResponseDTO.getRgb() == 0 && stateResponseDTO.getState() != -1) {
                binding.color.setText("开启流光溢彩");
                binding.color.setOnClickListener(v -> {
                    setSendMsg("computer");
                });
            } else if (stateResponseDTO.getRgb() == 1 && stateResponseDTO.getState() != -1) {
                binding.color.setText("关闭流光溢彩");
                binding.color.setOnClickListener(v -> {
                    setSendMsg("normal_light");
                });
            }
            if (stateResponseDTO.getStr1() != null) {
                binding.textView.setText(String.format("%s\n%s\n%s\n%s\n",
                        stateResponseDTO.getStr1(),
                        stateResponseDTO.getStr2(),
                        stateResponseDTO.getStr3(),
                        stateResponseDTO.getStr4()));
            }
            SettingDao.setAddress(ControlFragment.this.requireContext(), ip);
        };
        responseListener.setDataBackListener(dataBackListener);
        new Thread(responseListener).start();

        return binding.getRoot();
    }
}
