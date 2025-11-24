/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.imepac.clinica.utils;
import br.edu.imepac.clinica.exceptions.ValidationException;
import java.time.LocalDate;

/**
 *
 * @author tiago-monteiro
 */
public final class Validators {
    private Validators() {
    }
    
    public static void notBlank(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("campo obrigatório: " + fieldName);
        }
    }
    
    public static void minLenght(String value, int min, String fieldName) throws ValidationException {
        if (value == null || value.length() < min) {
            throw new ValidationException(
                    String.format("%s deve ter pelo menos %d caracteres", fieldName, min)
            );
        }
    }
    
    public static void cpf(String cpf) throws ValidationException {
        notBlank(cpf, "CPF");
        String onlyDigits = cpf.replaceAll("\\D", "");
        if (!onlyDigits.matches("\\d{11}")) {
            throw new ValidationException("CPF inválido");
        }
    }
    
    public static void cnpj(String cnpj) throws ValidationException {
    notBlank(cnpj, "CNPJ");
    String onlyDigits = cnpj.replaceAll("\\D", "");
    if (!onlyDigits.matches("\\d{14}")) {
        throw new ValidationException("CNPJ inválido");
        } 
    }
    
    public static void futureDate(LocalDate date, String fieldName) throws ValidationException {
     if (date != null && date.isAfter(LocalDate.now())) {
         throw new ValidationException(fieldName + " não pode ser uma data futura");
     }
 }
    
    public static void positiveLong(Long value, String fieldName) throws ValidationException {
    if (value == null || value <= 0) {
        throw new ValidationException(fieldName + " deve ser maior que zero");
        }
    }
}
