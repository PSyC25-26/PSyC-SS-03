package com.example.JailQ;

import javax.swing.SwingUtilities;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.JailQ.GUI.JailQMainGUI;

@SpringBootApplication
public class JailQApplication {

	public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
		SpringApplication.run(JailQApplication.class, args);

		SwingUtilities.invokeLater(() -> {
        	new JailQMainGUI().setVisible(true);
        });
	}

}