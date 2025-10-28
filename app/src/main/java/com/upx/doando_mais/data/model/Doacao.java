package com.upx.doando_mais.data.model;


import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Doacao {

    private String id; // ID do doc no firebase, gerado automaticamente.
    private String tipoSanguineo; // Tipo de sangue doado
    private Date dataDoacao;
    private String cidade;
    private String estado;
    private String comprovanteUrl;
    private Date dataRegistro;

    // Construtor vazio para firebase
    public Doacao() {}

    // Getters e setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipoSanguineo() {
        return tipoSanguineo;
    }

    public void setTipoSanguineo(String tipoSanguineo) {
        this.tipoSanguineo = tipoSanguineo;
    }

    public Date getDataDoacao() {
        return dataDoacao;
    }

    public void setDataDoacao(Date dataDoacao) {
        this.dataDoacao = dataDoacao;
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

    public String getComprovanteUrl() {
        return comprovanteUrl;
    }

    public void setComprovanteUrl(String comprovanteUrl) {
        this.comprovanteUrl = comprovanteUrl;
    }

    @ServerTimestamp // Anotação para data de registro automática

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

}
