package com.upx.doando_mais.data.model;

public class Doacao {

    private String id;
    private String idUsuario; // Para saber de quem é a doação
    private String dataDoacao;
    private String local;
    private String tipoSanguineo;
    private String urlComprovante;

    // Construtor vazio (Obrigatório para o Firebase)
    public Doacao() {
    }

    public Doacao(String id, String idUsuario, String dataDoacao, String local, String tipoSanguineo, String urlComprovante) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.dataDoacao = dataDoacao;
        this.local = local;
        this.tipoSanguineo = tipoSanguineo;
        this.urlComprovante = urlComprovante;
    }

    // Getters e Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDataDoacao() {
        return dataDoacao;
    }

    public void setDataDoacao(String dataDoacao) {
        this.dataDoacao = dataDoacao;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getTipoSanguineo() {
        return tipoSanguineo;
    }

    public void setTipoSanguineo(String tipoSanguineo) {
        this.tipoSanguineo = tipoSanguineo;
    }

    public String getUrlComprovante() {
        return urlComprovante;
    }

    public void setUrlComprovante(String urlComprovante) {
        this.urlComprovante = urlComprovante;
    }
}