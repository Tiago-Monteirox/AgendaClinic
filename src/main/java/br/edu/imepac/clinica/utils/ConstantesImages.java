/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.imepac.clinica.utils;

/**
 * A classe {@code ConstantesImages} centraliza as referências a arquivos de
 * imagem utilizados na aplicação da clínica, permitindo que caminhos e nomes de
 * arquivos sejam gerenciados de forma centralizada e padronizada.
 * <p>
 * Essa abordagem melhora a manutenção do código, evitando a repetição de
 * strings em diferentes pontos do sistema e facilitando futuras alterações nos
 * nomes ou localizações das imagens.
 * </p>
 *
 * <p>
 * <b>Exemplo de uso:</b></p>
 * <pre>{@code
 * JLabel label = new JLabel();
 * setImageIcon(ConstantesImages.especialidadeAddForm, label);
 * }</pre>
 *
 * @author Everton
 * @version 1.0
 * @since 2025-11-10
 */
public class ConstantesImages {

    /**
     * Nome do arquivo de imagem utilizado no formulário de adição de
     * especialidades.
     * <p>
     * O arquivo deve estar localizado dentro do diretório de recursos
     * (resources) da aplicação, normalmente em
     * {@code src/main/resources/imagens/}.
     * </p>
     */
    public static final String ESPECIALIDADE_ADD_FORM = "especialidades.jpg";
    public static final String MEDICO_ADD_FORM = "especialidades.jpg";
}
