/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.imepac.clinica.exceptions;

import java.util.List;

/**
 *
 * @author evertonhf
 */
public class CampoObrigatorioException extends Exception {
    private final List<String> campos;
    
    public CampoObrigatorioException(List<String> campos){
        this.campos= campos;
    }
    
}
