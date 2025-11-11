/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.imepac.clinica.screens;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
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

    /**
     * Ajusta a largura da janela para a largura total da tela, mantendo a
     * altura atual.
     */
    public void ajustarLarguraTela() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) screenSize.getWidth(), this.getHeight());
    }

    /**
     * Centraliza a janela na tela.
     */
    public void centralizar() {
        this.setLocationRelativeTo(null);
    }

    /**
     * Posiciona o JFrame horizontalmente centralizado e ajusta a posição
     * vertical.
     *
     * @param distanciaTopo Distância em pixels do topo da tela. Se for 0,
     * ficará no topo da tela.
     */
    public void posicionarTopo(int distanciaTopo) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int x = (screenSize.width - getWidth()) / 2;
        int y = Math.max(0, distanciaTopo); // evita valores negativos

        setLocation(x, y);
    }

}
