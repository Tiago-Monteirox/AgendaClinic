package br.edu.imepac.clinica.enums;

/**
 * Enum que representa as funcionalidades que podem ser atribuídas aos perfis
 * e usadas para habilitar/desabilitar menus.
 *
 * IMPORTANTE:
 * Os valores aqui DEVEM bater exatamente com a coluna
 * perfil_funcionalidade.funcionalidade no banco.
 */
public enum EnumFuncionalidade {

    // Cadastros
    GERENCIAR_MEDICOS,
    GERENCIAR_PACIENTES,
    GERENCIAR_CONVENIOS,
    GERENCIAR_ESPECIALIDADES,
    GERENCIAR_USUARIOS,

    // Agenda
    VISUALIZAR_AGENDA_SECRETARIA,
    VISUALIZAR_AGENDA_MEDICO,

    // Prontuário / Histórico
    REGISTRAR_PRONTUARIO,
    CONSULTAR_PRONTUARIO;

    public static EnumFuncionalidade fromString(String value) {
        if (value == null) return null;
        return Enum.valueOf(EnumFuncionalidade.class, value.toUpperCase());
    }
}
