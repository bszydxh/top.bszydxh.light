package top.bszydxh.light.model.dto.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import top.bszydxh.light.model.dto.StateResponseDTO;

public interface SettingDao {
    static String getAddress(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences("address", Context.MODE_PRIVATE);
//        return sharedPreferences.getString("address", "255.255.255.255");
        return "255.255.255.255";
    }

    static void setAddress(Context context, String address) {
        SharedPreferences sharedPref = context.getSharedPreferences("address", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("address", address);
        editor.apply();
    }

    static StateResponseDTO getStateData(Context context) {//保存密码到本地
        SharedPreferences sharedPreferences =
                context.getSharedPreferences("state", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("state", "");
        ObjectMapper objectMapper = new ObjectMapper();
        StateResponseDTO stateVO = new StateResponseDTO();
        try {
            stateVO = objectMapper.readValue(json, StateResponseDTO.class);
        } catch (JsonProcessingException e) {
            Log.e("setStateData", "json error");
            e.printStackTrace();
        }
        return stateVO;
    }

    static void setStateData(Context context, StateResponseDTO stateVO) {
        SharedPreferences sharedPref = context.getSharedPreferences("state", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        ObjectMapper postMapper = new ObjectMapper();
        try {
            if (stateVO != null) {
                editor.putString("state", postMapper.writeValueAsString(stateVO));
            } else {
                editor.putString("state", postMapper.writeValueAsString(new StateResponseDTO()));
            }
            editor.apply();
        } catch (JsonProcessingException e) {
            Log.e("setUser", "json error");
            e.printStackTrace();
        }
    }
}
