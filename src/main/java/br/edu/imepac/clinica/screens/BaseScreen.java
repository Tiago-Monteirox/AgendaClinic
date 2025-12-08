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

        int imgW = icon.getIconWidth();
        int imgH = icon.getIconHeight();

        int compW = component.getWidth();
        int compH = component.getHeight();

        // se o componente ainda não tem tamanho, usa a imagem original
        if (compW <= 0 || compH <= 0) {
            component.setIcon(icon);
            return;
        }

        // calcula fator de escala mantendo proporção
        double escala = Math.min(
                (double) compW / imgW,
                (double) compH / imgH
        );

        // não deixa aumentar além do tamanho original (evita pixelar)
        if (escala > 1.0) {
            escala = 1.0;
        }

        int novoW = (int) (imgW * escala);
        int novoH = (int) (imgH * escala);

        Image imagem = icon.getImage().getScaledInstance(
                    novoW,
                novoH,
                Image.SCALE_SMOOTH
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
