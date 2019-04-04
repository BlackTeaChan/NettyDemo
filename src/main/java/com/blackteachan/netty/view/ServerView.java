package com.blackteachan.netty.view;

import com.blackteachan.netty.server.TimeServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerView {

    private static OnCallback mOnCallback;

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
                String s = sendTextArea.getText();
//                mOnCallback.onSend(s);
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

    public static abstract class ChannelCallback{

        public abstract void addChannel();
        
    }

}
