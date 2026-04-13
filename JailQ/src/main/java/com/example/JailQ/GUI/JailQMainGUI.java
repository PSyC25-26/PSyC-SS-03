package com.example.JailQ.GUI;

import javax.swing.*;
import java.awt.*;

public class JailQMainGUI extends JFrame {

    private String username;

    
    public JailQMainGUI() {
        this.username = null;
        initComponents();
    }

 
    public JailQMainGUI(String username) {
        this.username = username;
        initComponents();
    }

    private void initComponents() {
        setTitle("JailQ");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel lbl;

        if (username == null) {
            // 🔥 MODO SIN LOGIN
            lbl = new JLabel("Bienvenido a JailQ (no logueado)", SwingConstants.CENTER);

            JButton btnLogin = new JButton("Iniciar sesión");

            btnLogin.addActionListener(e -> {
                new LoginGUI().setVisible(true);
                this.dispose();
            });

            add(lbl, BorderLayout.CENTER);
            add(btnLogin, BorderLayout.SOUTH);

        } else {
            // 🔥 MODO CON LOGIN
            lbl = new JLabel("Bienvenido, " + username, SwingConstants.CENTER);

            JButton btnLogout = new JButton("Cerrar sesión");

            btnLogout.addActionListener(e -> {
                new JailQMainGUI().setVisible(true);
                this.dispose();
            });

            add(lbl, BorderLayout.CENTER);
            add(btnLogout, BorderLayout.SOUTH);
        }

        lbl.setFont(new Font("Arial", Font.BOLD, 18));
    }
}