package br.edu.imepac.clinica.enums;

public enum EnumStatusUsuario {
    ATIVO,
    INATIVO,
    BLOQUEADO;

    public static EnumStatusUsuario fromString(String value) {
        if (value == null) return null;
        return Enum.valueOf(EnumStatusUsuario.class, value.toUpperCase());
    }
}
