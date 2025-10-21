package com.upx.doando_mais.data.model;

public class Usuario {


    // Construtor vazio - obrigatório para utilizar no Firebase.

    public Usuario(){}

    // Atributos do usuário

    private String uid; //Id único gerado pelo Firebase Authentication, chave primária.
    private String nomeCompleto;
    private String email; //Não armazenaremos a senha aqui, o firebase auth cuidará disso.
    private String cpf;
    private String sexo;
    private String dataNascimento;
    private String cidade;
    private String estado;
    private String tipoSanguineo;
    private String perfil; // "Doador", "Organização", "Mobilizador".
    private int quantidadeDoacoes; // Contador de doações realizadas.
    private String fotoUrl;

    // Construtores, getters e setters


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

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }
}
