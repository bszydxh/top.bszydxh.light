package top.bszydxh.light.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ResponseListener extends Thread {
    private int port;
    private Context context;
    DatagramSocket socket;
    DataBackListener dataBackListener;
    ;
    List<String> list = new ArrayList<>();

    public interface DataBackListener {
        void onDataBack(String message, String ip);
    }

    public ResponseListener(int port) {
        this.port = port;
    }

    public void setDataBackListener(DataBackListener dataBackListener) {
        this.dataBackListener = dataBackListener;
    }

    @Override
    public void run() {
        dataBackListener.onDataBack(null, null);
        while (true) {
            super.run();
            try {
                if (socket == null) {
                    socket = new DatagramSocket(null);
                    socket.setReuseAddress(true);
                    socket.bind(new InetSocketAddress(port));
                }
                //监听回送端口
                byte[] buf = new byte[10240];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                //拿数据
                socket.receive(packet);
                //拿到发送端的一些信息
                String ip = packet.getAddress().getHostAddress();
                int port = packet.getPort();
                int length = packet.getLength();
                String msg = new String(buf, 0, length);
                //Log.i("udp", ip + ":" + port + "(" + length + ")->\n" + msg);
                if (msg.length() > 0) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        dataBackListener.onDataBack(msg, ip);
                    });
                }
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("udp", "fail");
            }

        }
    }
}

