package com.upx.doando_mais.data.model;

// Esta classe representará uma campanha de doação no Firestore. Conterá as informações dos formulários.


import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
public class Campanha {

    private String id; //Id do documento no FireStore, gerado automaticamente
    private String criadorUid; // Id do usuário Firestore Auth, quem criou a campanha
    private String tipoCampanha; // As campanhas podem ser criadas para Paciente específic ou aberta/geral
    private String titulo; // Títula da campanha, pode ser o nome do paciente ou da organização
    private String descricao; // Mensagem de recrutamente inserido pelo criador da campanha
    private String nomePaciente; // Campo obrigatório apenas para tipoCapanha == Paciente
    private String cidadePaciente; // Campo obrigatório apenas para tipoCapanha == Paciente
    private String estadoPaciente; // Campo obrigatório apenas para tipoCapanha == Paciente
    private String tipoSanguineoNecessario; // Ex.: O+, A-, Todos e etc.
    private int metaDoadores; // Quantidade de doadores necessários, obrigatório para pacientes.
    private String nomeHemocentro;
    private String enderecoHemocentro;
    private String cidadeHemocentro;
    private String estadoHemocentro;
    private String contatoWhatsApp; // Obrigatório apenas se tipoCampanha == Paciente
    private Date dataCriacao; // Data e hora da criação da campanha
    private Date dataTermino; // Data limite da campanha
    private String status; // Ativa, concluída, expirada
    private int contadorIntencoes; // Qtdd de cliques em "Vou doar"
    private int contadorCompartilhamentos; // Qtdd de cliques em compartilhar
    private String nomeOrganizador; // Nome do usuário ou organização que criou a campanha, será exibido.


    // Construtor vazio obrigatório do Firebase

    public Campanha (){}

    // Construtor completo (exemplo, pode precisar de ajustes)
    public Campanha(String id, String criadorUid, String tipoCampanha, String titulo, String descricao, String nomePaciente, String cidadePaciente, String estadoPaciente, String tipoSanguineoNecessario, int metaDoadores, String nomeHemocentro, String enderecoHemocentro, String cidadeHemocentro, String estadoHemocentro, String contatoWhatsApp, Date dataCriacao, Date dataTermino, String status, int contadorIntencoes, int contadorCompartilhamentos, String nomeOrganizador) {
        this.id = id;
        this.criadorUid = criadorUid;
        this.tipoCampanha = tipoCampanha;
        this.titulo = titulo;
        this.descricao = descricao;
        this.nomePaciente = nomePaciente;
        this.cidadePaciente = cidadePaciente;
        this.estadoPaciente = estadoPaciente;
        this.tipoSanguineoNecessario = tipoSanguineoNecessario;
        this.metaDoadores = metaDoadores;
        this.nomeHemocentro = nomeHemocentro;
        this.enderecoHemocentro = enderecoHemocentro;
        this.cidadeHemocentro = cidadeHemocentro;
        this.estadoHemocentro = estadoHemocentro;
        this.contatoWhatsApp = contatoWhatsApp;
        this.dataCriacao = dataCriacao;
        this.dataTermino = dataTermino;
        this.status = status;
        this.contadorIntencoes = contadorIntencoes;
        this.contadorCompartilhamentos = contadorCompartilhamentos;
        this.nomeOrganizador = nomeOrganizador;
    }


    // Getter e Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCriadorUid() {
        return criadorUid;
    }

    public void setCriadorUid(String criadorUid) {
        this.criadorUid = criadorUid;
    }

    public String getTipoCampanha() {
        return tipoCampanha;
    }

    public void setTipoCampanha(String tipoCampanha) {
        this.tipoCampanha = tipoCampanha;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNomePaciente() {
        return nomePaciente;
    }

    public void setNomePaciente(String nomePaciente) {
        this.nomePaciente = nomePaciente;
    }

    public String getCidadePaciente() {
        return cidadePaciente;
    }

    public void setCidadePaciente(String cidadePaciente) {
        this.cidadePaciente = cidadePaciente;
    }

    public String getEstadoPaciente() {
        return estadoPaciente;
    }

    public void setEstadoPaciente(String estadoPaciente) {
        this.estadoPaciente = estadoPaciente;
    }

    public String getTipoSanguineoNecessario() {
        return tipoSanguineoNecessario;
    }

    public void setTipoSanguineoNecessario(String tipoSanguineoNecessario) {
        this.tipoSanguineoNecessario = tipoSanguineoNecessario;
    }

    public int getMetaDoadores() {
        return metaDoadores;
    }

    public void setMetaDoadores(int metaDoadores) {
        this.metaDoadores = metaDoadores;
    }

    public String getNomeHemocentro() {
        return nomeHemocentro;
    }

    public void setNomeHemocentro(String nomeHemocentro) {
        this.nomeHemocentro = nomeHemocentro;
    }

    public String getEnderecoHemocentro() {
        return enderecoHemocentro;
    }

    public void setEnderecoHemocentro(String enderecoHemocentro) {
        this.enderecoHemocentro = enderecoHemocentro;
    }

    public String getCidadeHemocentro() {
        return cidadeHemocentro;
    }

    public void setCidadeHemocentro(String cidadeHemocentro) {
        this.cidadeHemocentro = cidadeHemocentro;
    }

    public String getEstadoHemocentro() {
        return estadoHemocentro;
    }

    public void setEstadoHemocentro(String estadoHemocentro) {
        this.estadoHemocentro = estadoHemocentro;
    }

    public String getContatoWhatsApp() {
        return contatoWhatsApp;
    }

    public void setContatoWhatsApp(String contatoWhatsApp) {
        this.contatoWhatsApp = contatoWhatsApp;
    }

    @ServerTimestamp // Anotação especial do FireStore para que o servidor do Firebase preencha data e hora automaticamente na criação
    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Date getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(Date dataTermino) {
        this.dataTermino = dataTermino;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getContadorIntencoes() {
        return contadorIntencoes;
    }

    public void setContadorIntencoes(int contadorIntencoes) {
        this.contadorIntencoes = contadorIntencoes;
    }

    public int getContadorCompartilhamentos() {
        return contadorCompartilhamentos;
    }

    public void setContadorCompartilhamentos(int contadorCompartilhamentos) {
        this.contadorCompartilhamentos = contadorCompartilhamentos;
    }

    public String getNomeOrganizador() {
        return nomeOrganizador;
    }

    public void setNomeOrganizador(String nomeOrganizador) {
        this.nomeOrganizador = nomeOrganizador;
    }
}
