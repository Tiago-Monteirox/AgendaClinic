package br.edu.imepac.clinica.enums;

public enum EnumStatusConsulta {
    AGENDADA,
    REALIZADA,
    CANCELADA;

    public static EnumStatusConsulta fromString(String value) {
        if (value == null) {
            return null;
        }
        return Enum.valueOf(EnumStatusConsulta.class, value.toUpperCase());
    }
}
