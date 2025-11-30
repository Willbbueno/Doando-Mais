package com.upx.doando_mais.data.model;

public class Usuario {

    // Atributos do usuário
    private String uid;
    private String nomeCompleto;
    private String email;
    private String cpf;
    private String sexo;
    private String dataNascimento;
    private String cidade;
    private String estado;
    private String tipoSanguineo;
    private String perfil;
    private int quantidadeDoacoes;
    private String telefone;
    private String urlFotoPerfil;

    // Construtor vazio - obrigatório para utilizar no Firebase.
    public Usuario(){}

    // Construtor principal
    public Usuario(String uid, String nomeCompleto, String email, String cpf, String sexo,
                   String dataNascimento, String cidade, String estado, String tipoSanguineo,
                   String perfil, int quantidadeDoacoes, String urlFotoPerfil, String telefone) {
        this.uid = uid;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.cpf = cpf;
        this.sexo = sexo;
        this.dataNascimento = dataNascimento;
        this.cidade = cidade;
        this.estado = estado;
        this.tipoSanguineo = tipoSanguineo;
        this.perfil = perfil;
        this.quantidadeDoacoes = quantidadeDoacoes;
        this.urlFotoPerfil = urlFotoPerfil;
        this.telefone = telefone;
    }

    // ⬇️ --- CORREÇÃO DO ERRO 1 --- ⬇️
    /**
     * Construtor de Cópia.
     * Cria um novo objeto Usuario baseado em outro.
     */
    public Usuario(Usuario outro) {
        this.uid = outro.uid;
        this.nomeCompleto = outro.nomeCompleto;
        this.email = outro.email;
        this.cpf = outro.cpf;
        this.sexo = outro.sexo;
        this.dataNascimento = outro.dataNascimento;
        this.cidade = outro.cidade;
        this.estado = outro.estado;
        this.tipoSanguineo = outro.tipoSanguineo;
        this.perfil = outro.perfil;
        this.quantidadeDoacoes = outro.quantidadeDoacoes;
        this.urlFotoPerfil = outro.urlFotoPerfil;
        this.telefone = outro.telefone;
    }
    // O construtor quebrado com (Object o, ...) foi removido.


    // --- Getters e Setters ---

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipoSanguineo() {
        return tipoSanguineo;
    }

    public void setTipoSanguineo(String tipoSanguineo) {
        this.tipoSanguineo = tipoSanguineo;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public int getQuantidadeDoacoes() {
        return quantidadeDoacoes;
    }

    public void setQuantidadeDoacoes(int quantidadeDoacoes) {
        this.quantidadeDoacoes = quantidadeDoacoes;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getUrlFotoPerfil() {
        return urlFotoPerfil;
    }

    public void setUrlFotoPerfil(String urlFotoPerfil) {
        this.urlFotoPerfil = urlFotoPerfil;
    }
}