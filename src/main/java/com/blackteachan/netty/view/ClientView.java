package com.blackteachan.netty.view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientView {
    private JPanel panel1;
    private JTextField a19216804TextField;
    private JTextField a18902TextField;
    private JLabel label1;
    private JLabel label2;
    private JButton connectButton;
    private JButton disconnectButton;
    private JButton refreshButton;

    public ClientView() {

        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public void initView(){
        JFrame frame = new JFrame("Netty Client");
        frame.setContentPane(new ClientView().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
