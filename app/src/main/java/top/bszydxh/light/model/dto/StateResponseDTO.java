package top.bszydxh.light.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StateResponseDTO {
    String str1;
    String str2;
    String str3;
    String str4;
    int state = -9;
    int rgb;
    int light_mode;
    int light_brightness;
    List<Integer> leds = new ArrayList<>();
}
