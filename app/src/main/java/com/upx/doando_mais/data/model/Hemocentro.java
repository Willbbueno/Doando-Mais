package com.upx.doando_mais.data.model;
// Essa classe representa os locais que o usuário pdoerá selecionar ao criar uma campanha.



public class Hemocentro {

    private String id; // ID do documento no Firestore
    private String nome; // Nome oficial do hemocentro (ex: "Colsan Sorocaba")
    private String endereco; // Endereço completo (Rua, Número, Bairro)
    private String cidade;
    private String estado;
    private double latitude; // Coordenada geográfica para exibição no mapa, é útil por que o hemocentro poderá ser inserido manualmente para aparecer no mapa
    private double longitude; // Coordenada geográfica para exibição no mapa
    private String telefone; // Telefone de contato (opcional)

    // Construtor vazio obrigatório para Firestore
    public Hemocentro() {
    }

    // Construtor com parâmetros
    public Hemocentro(String id, String nome, String endereco, String cidade, String estado, double latitude, double longitude, String telefone) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.cidade = cidade;
        this.estado = estado;
        this.latitude = latitude;
        this.longitude = longitude;
        this.telefone = telefone;
    }


    // Getters e Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
