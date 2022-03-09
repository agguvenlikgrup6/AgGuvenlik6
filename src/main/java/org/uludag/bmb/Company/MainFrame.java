package org.uludag.bmb.Company;

import javax.swing.JFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame{
    final private Font mainFont = new Font("Segoe print", Font.BOLD, 18);
    JTextField tfEmail, tfSifre;
    JLabel lbWelcome;
    SecondFrame secondFrame1 = new SecondFrame();


    public void initialize(){
        /* FORM PANEL*/
        JLabel lbEmail = new JLabel("E-mail");
        lbEmail.setFont(mainFont);
        tfEmail = new JTextField();
        tfEmail.setFont(mainFont);

        JLabel lbSifre = new JLabel("Sifre");
        lbSifre.setFont(mainFont);
        tfSifre = new JTextField();
        tfSifre.setFont(mainFont);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4,1,5,5));
        formPanel.setOpaque(false);
        formPanel.add(lbEmail);
        formPanel.add(tfEmail);
        formPanel.add(lbSifre);
        formPanel.add(tfSifre);

        /* WELCOME*/
        lbWelcome = new JLabel();
        lbWelcome.setFont(mainFont);

        /* BUTTON*/
        JButton btnOK = new JButton("Giris");
        btnOK.setFont(mainFont);
        btnOK.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfEmail.getText();
                String sifre = tfSifre.getText();
                lbWelcome.setText("Hello " + email + " " + sifre);
                secondFrame1.initialize();
            }
        });

        JButton btnClear = new JButton("Temizle");
        btnClear.setFont(mainFont);
        btnClear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                tfEmail.setText("");
                tfSifre.setText("");
                lbWelcome.setText("");   
            }

        });

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1,2,5,5));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(btnOK);
        buttonsPanel.add(btnClear);


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(128,128,255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(lbWelcome, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setTitle("Welcome");
        setSize(500,600);
        setMinimumSize(new Dimension(300,400));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }
    public String getEmail(){
        String email = tfEmail.getText();
        return email;
    }
    public String getSifre(){
        String sifre = tfSifre.getText();
        return sifre;
    }
}