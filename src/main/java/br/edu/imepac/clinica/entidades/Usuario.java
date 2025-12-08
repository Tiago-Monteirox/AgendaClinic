package br.edu.imepac.clinica.entidades;

import br.edu.imepac.clinica.enums.EnumStatusUsuario;
import br.edu.imepac.clinica.exceptions.ValidationException;
import br.edu.imepac.clinica.utils.Validators;

import java.time.LocalDateTime;

public class Usuario extends BaseEntity {

    private String nomeLogin;
    private String senha;
    private String status; // esperado: ATIVO, INATIVO, BLOQUEADO
    private LocalDateTime ultimoLogin;
    private boolean bloqueado;
    private int tentativasFalhas;

    private Secretaria secretaria;
    private Perfil perfil;

    // novo: vínculo com médico (para login de médico)
    private Medico medico;

    // ============================================================
    // Validação de entidade
    // ============================================================
    @Override
    public void validar() throws ValidationException {
        Validators.notBlank(nomeLogin, "Login");
        Validators.minLenght(senha, 6, "Senha");

        if (perfil == null) {
            throw new ValidationException("Perfil do usuário é obrigatório.");
        }
        // médico é opcional (usuário pode ser secretaria, admin, etc.), então não força aqui
    }

    // ============================================================
    // Regras de negócio
    // ============================================================
    public void registrarFalhaLogin() {
        tentativasFalhas++;
        if (tentativasFalhas >= 5) {
            bloqueado = true;
        }
    }

    public void resetarTentativas() {
        tentativasFalhas = 0;
    }

    public boolean isAtivo() {
        return "ATIVO".equalsIgnoreCase(status) && !bloqueado;
    }

    // ============================================================
    // Getters e setters
    // ============================================================
    public String getNomeLogin() {
        return nomeLogin;
    }

    public void setNomeLogin(String nomeLogin) {
        this.nomeLogin = nomeLogin;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public int getTentativasFalhas() {
        return tentativasFalhas;
    }

    public void setTentativasFalhas(int tentativasFalhas) {
        this.tentativasFalhas = tentativasFalhas;
    }

    public Secretaria getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(Secretaria secretaria) {
        this.secretaria = secretaria;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    // novo: médico associado ao usuário (para agenda/prontuário)
    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    // ============================================================
    // Enum helpers
    // ============================================================
    public EnumStatusUsuario getStatusEnum() {
        return EnumStatusUsuario.fromString(status);
    }

    public void setStatusEnum(EnumStatusUsuario statusEnum) {
        if (statusEnum != null) {
            this.status = statusEnum.name();
        }
    }
}
