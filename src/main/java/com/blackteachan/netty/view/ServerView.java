package com.blackteachan.netty.view;

import com.blackteachan.netty.server.ServerHandler;
import com.blackteachan.netty.server.TimeServer;
import io.netty.channel.ChannelHandlerContext;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerView {

    private static ServerHandlerCallback mServerHandlerCallback;//ServerHandler回调
    private static OnCallback mOnCallback;//当前类的回调
    private static SendCallback mSendCallback;//当前类的回调
    private static String rIP = null;

    private JPanel panel1;
    private JLabel label1;
    private JLabel label2;
    private JTextField ipTextField;
    private JTextField portTextField;
    private JButton openButton;
    private JButton closeButton;
    private JButton refreshButton;
    private JButton sendButton;
    private JTextArea sendTextArea;
    private JComboBox chanelComboBox;

    public ServerView() {
        try {
            String address = InetAddress.getLocalHost().getHostAddress().toString();
            ipTextField.setText(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        mServerHandlerCallback = new ServerHandlerCallback();

        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                portTextField.setEnabled(false);
                openButton.setEnabled(false);
                closeButton.setEnabled(true);
                //开启服务
                int port = Integer.parseInt(portTextField.getText());
                mOnCallback.onOpen(port);
            }
        });
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                portTextField.setEnabled(true);
                openButton.setEnabled(true);
                closeButton.setEnabled(false);
                //关闭服务
                mOnCallback.onClose();
            }
        });
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(rIP != null) {
                    String s = sendTextArea.getText();
                    mSendCallback.onSend(rIP, s);
                }
            }
        });
        chanelComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = chanelComboBox.getSelectedIndex();
                System.out.println("select: " + index);
                rIP = chanelComboBox.getItemAt(index).toString();
                System.out.println(rIP);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Socket Server");
        frame.setContentPane(new ServerView().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        //绑定TimeServer
        TimeServer timeServer = new TimeServer();
    }

    public static void setCallback(final OnCallback onCallback){
        mOnCallback = onCallback;
    }
    public static abstract class OnCallback{

        public abstract void onOpen(int port);

        public abstract void onClose();

    }

    public static void setSendCallback(final SendCallback sendCallback){
        mSendCallback = sendCallback;
    }
    public static abstract class SendCallback{
        public abstract void onSend(String rIP, String text);
    }


    /**
     * 设置ServerHandler回调<br/>因为ServerHandler类是在ServerView之后调用的,此方法
     */
    public static void setServerHandlerCallback(){
        ServerHandler.setOnCallback(mServerHandlerCallback);
    }
    class ServerHandlerCallback extends ServerHandler.OnCallback{

        public void addChannel(ChannelHandlerContext ctx) {
            chanelComboBox.addItem(ctx.channel().remoteAddress().toString());
        }

        public void removeChannel(ChannelHandlerContext ctx) {
            chanelComboBox.removeItem(ctx.channel().remoteAddress().toString());
        }
    }

}
