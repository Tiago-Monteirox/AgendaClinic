package br.edu.imepac.clinica.testes;

import br.edu.imepac.clinica.daos.*;
import br.edu.imepac.clinica.entidades.*;
import br.edu.imepac.clinica.exceptions.ValidationException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TesteGeralSistema {

    public static void main(String[] args) {
        EspecialidadeDao especialidadeDao = new EspecialidadeDao();
        MedicoDao medicoDao = new MedicoDao();
        ConvenioDao convenioDao = new ConvenioDao();
        PacienteDao pacienteDao = new PacienteDao();
        SecretariaDao secretariaDao = new SecretariaDao();
        PerfilDao perfilDao = new PerfilDao();
        UsuarioDao usuarioDao = new UsuarioDao();
        ConsultaDao consultaDao = new ConsultaDao();
        ProntuarioDao prontuarioDao = new ProntuarioDao();

        try {
            // 1) ESPECIALIDADE
            Especialidade cardio = new Especialidade();
            cardio.setNome("Cardiologia");
            cardio.setArea("Clínica");
            cardio.setDescricao("Cuida do coração");
            especialidadeDao.inserir(cardio);
            System.out.println("Especialidade criada: " + cardio.getId());

            // 2) MÉDICO
            Medico medico = new Medico();
            medico.setNome("Dra. Ana Cardio");
            medico.setCpf("111.222.333-44");
            medico.setTelefone("(34) 99999-0000");
            medico.setCrm("CRM-12345");
            medico.setTempoExperiencia(5);
            medico.setFormacao("Medicina - UFU");
            medico.setEspecialidade(cardio);
            medico.setAtivo(true);
            medicoDao.inserir(medico);
            System.out.println("Médico criado: " + medico.getId());

            // 3) CONVÊNIO
            Convenio convenio = new Convenio();
            convenio.setNome("Unimed Nacional");
            convenio.setCodigo("UNM-NAC-01");
            convenio.setDescricao("Plano nacional");
            convenio.setAtivo(true);
            convenioDao.inserir(convenio);
            System.out.println("Convênio criado: " + convenio.getId());

            // 4) PACIENTE
            Paciente paciente = new Paciente();
            paciente.setNome("Tiago Paciente");
            paciente.setCpf("555.666.777-88");
            paciente.setTelefone("(34) 98888-7777");
            paciente.setPlanoSaude("Plano Ouro");
            paciente.setTipoSanguineo("O+");
            paciente.setDataNascimento(LocalDate.of(2000, 9, 29));
            paciente.setSexo("Masculino");
            paciente.setConvenio(convenio);
            pacienteDao.inserir(paciente);
            System.out.println("Paciente criado: " + paciente.getId());

            // 5) SECRETÁRIA
            Secretaria secretaria = new Secretaria();
            secretaria.setNome("Maria Secretaria");
            secretaria.setCpf("123.456.789-00");
            secretaria.setTelefone("(34) 97777-6666");
            secretaria.setPis("12345678900");
            secretaria.setTurno("Integral");
            secretaria.setSetor("Recepção");
            secretariaDao.inserir(secretaria);
            System.out.println("Secretária criada: " + secretaria.getId());

            // 6) PERFIL
            Perfil perfil = new Perfil();
            perfil.setNome("Administrador");
            perfil.setDescricao("Acesso completo ao sistema");
            perfil.setNivelAcesso(10);
            perfilDao.inserir(perfil);
            System.out.println("Perfil criado: " + perfil.getId());

            // 7) USUÁRIO
            Usuario usuario = new Usuario();
            usuario.setNomeLogin("admin");
            usuario.setSenha("123456"); // em produção seria hash, aqui é demo
            usuario.setStatus("ATIVO");
            usuario.setUltimoLogin(LocalDateTime.now());
            usuario.setBloqueado(false);
            usuario.setTentativasFalhas(0);
            usuario.setSecretaria(secretaria);
            usuario.setPerfil(perfil);
            usuarioDao.inserir(usuario);
            System.out.println("Usuário criado: " + usuario.getId());

            // 8) CONSULTA
            Consulta consulta = new Consulta();
            consulta.setDataHora(LocalDateTime.now().plusDays(1));
            consulta.setRetorno(false);
            consulta.setCarteiraConvenio("CARTEIRA-123");
            consulta.setObservacao("Primeira consulta");
            consulta.setStatus("AGENDADA");
            consulta.setPaciente(paciente);
            consulta.setMedico(medico);
            consulta.setConvenio(convenio);
            consultaDao.inserir(consulta);
            System.out.println("Consulta criada: " + consulta.getId());

            // 9) PRONTUÁRIO
            Prontuario prontuario = new Prontuario();
            prontuario.setResumo("Paciente apresenta dores torácicas leves.");
            prontuario.setAnotacoes("Solicitado exame de sangue e eletrocardiograma.");
            prontuario.setArquivoPdf(null); // depois você pode setar um caminho.
            prontuario.setConsulta(consulta);
            prontuarioDao.inserir(prontuario);
            System.out.println("Prontuário criado: " + prontuario.getId());

            // 10) LISTAGENS RÁPIDAS
            System.out.println("\n=== LISTA DE MÉDICOS ===");
            for (Medico m : medicoDao.listarTodos()) {
                System.out.println(m.getId() + " - " + m.getNome() +
                        " (" + m.getCrm() + ")");
            }

            System.out.println("\n=== LISTA DE PACIENTES ===");
            for (Paciente p : pacienteDao.listarTodos()) {
                System.out.println(p.getId() + " - " + p.getNome() +
                        " - " + p.getCpf());
            }

            System.out.println("\n=== LISTA DE CONSULTAS ===");
            List<Consulta> consultas = consultaDao.listarTodos();
            for (Consulta c : consultas) {
                System.out.println(
                        c.getId() + " - " +
                        c.getStatus() + " - " +
                        "PacienteID=" + c.getPaciente().getId() +
                        " MedicoID=" + c.getMedico().getId()
                );
            }

            System.out.println("\n=== LISTA DE PRONTUÁRIOS ===");
            for (Prontuario pr : prontuarioDao.listarTodos()) {
                System.out.println(pr.getId() + " - " + pr.getResumo() +
                        " (consulta " + pr.getConsulta().getId() + ")");
            }

            System.out.println("\n✅ Fluxo completo executado com sucesso!");

        } catch (ValidationException e) {
            System.err.println("❌ Erro de validação: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Erro de SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
