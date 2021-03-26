package com.blackteachan.netty.view;

import com.blackteachan.netty.client.TimeClient;
import com.blackteachan.netty.client.TimeClientHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Log4j
public class ClientView {
    @Setter
    private static Channel channel = null;
    //当前类的回调
    private static SendCallback mSendCallback;

    private JPanel panel1;
    private JTextField tf_ip;
    private JTextField tf_port;
    private JLabel label1;
    private JLabel label2;
    private JButton btn_connect;
    private JButton btn_disconnect;
    private JButton btn_send;
    private JTextArea ta_send_text;
    private JTextArea ta_show;

    public ClientView() {
        TimeClient.setStateCallback(new TimeClientStateCallback());
        TimeClientHandler.setCallback(new TimeClientHandlerCallback());
        //连接
        btn_connect.addActionListener(e -> {
            setEnabled(btn_disconnect);
            setDisabled(btn_connect);
            setDisabled(tf_ip);
            setDisabled(tf_port);
            String ip = tf_ip.getText();
            int port = Integer.parseInt(tf_port.getText());
            try {
                TimeClient.start(ip, port);
            } catch (Exception e1) {
                setEnabled(btn_connect);
                setEnabled(tf_ip);
                setEnabled(tf_port);
                setDisabled(btn_disconnect);
                log.error(e1.getMessage());
                addShowText("连接服务端出错，请检查IP端口");
            }
        });
        //断开连接
        btn_disconnect.addActionListener(e -> TimeClient.shutdown());
        //发送
        btn_send.addActionListener(e -> {
            if(channel != null){
                String msg = ta_send_text.getText();
                channel.writeAndFlush(Unpooled.copiedBuffer(msg.getBytes()));
                addShowText(msg);
                log.info("发送: " + msg);
            }
        });
    }

    public void initView(){
        JFrame frame = new JFrame("Client");
        frame.setContentPane(new ClientView().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        //初始化按钮
        setEnabled(btn_connect);
        setDisabled(btn_disconnect);
    }

    private void setDisabled(JComponent jComponent){
        jComponent.setEnabled(false);
    }

    private void setEnabled(JComponent jComponent){
        jComponent.setEnabled(true);
    }

    private static String getBeforeLable(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + '\n';
    }

    private void addShowText(String text){
        ta_show.append(getBeforeLable() + text + '\n');
    }

    class TimeClientStateCallback implements TimeClient.StateCallback {
        @Override
        public void connected(Channel channel) {
            ClientView.setChannel(channel);
            setEnabled(btn_disconnect);
            setDisabled(btn_connect);
            setDisabled(tf_ip);
            setDisabled(tf_port);
            log.info(channel.remoteAddress().toString() + "连接成功");
        }
        @Override
        public void disconnected(Channel channel) {
            ClientView.setChannel(null);
            setEnabled(btn_connect);
            setEnabled(tf_ip);
            setEnabled(tf_port);
            setDisabled(btn_disconnect);
            log.info(channel.remoteAddress().toString() + "断开连接");
        }
    }

    class TimeClientHandlerCallback implements TimeClientHandler.Callback{
        @Override
        public void receive(String string) {
            addShowText(string);
        }
    }

    public static void setSendCallback(final SendCallback sendCallback){
        mSendCallback = sendCallback;
    }
    public static abstract class SendCallback{
        public abstract void onSend(String rIP, String text);
    }
}
