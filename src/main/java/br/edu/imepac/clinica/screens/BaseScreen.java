/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.imepac.clinica.screens;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author evertonhf
 */
public class BaseScreen extends JFrame {

    protected void setImageIcon(String fileName, JLabel component) {
        URL resource = getClass().getClassLoader().getResource("images/" + fileName);

        if (resource != null) {
            ImageIcon icon = new ImageIcon(resource);
            Image imagem = icon.getImage().getScaledInstance(
                    component.getWidth(), // largura do JLabel
                    component.getHeight(), // altura do JLabel
                    Image.SCALE_SMOOTH // suaviza a imagem
            );
            component.setIcon(new ImageIcon(imagem));
        } else {
            System.err.println("⚠️ Imagem não encontrada em: images/" + fileName);
        }
    }

}
