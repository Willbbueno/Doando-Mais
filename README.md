# Doando+ ❤️

<div align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="Logo Doando+" width="120">
  <br>
  <h3>Conectando doadores, pacientes e hemocentros para salvar vidas.</h3>
</div>

<div align="center">

  ![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
  ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
  ![Firebase](https://img.shields.io/badge/Firebase-039BE5?style=for-the-badge&logo=Firebase&logoColor=white)
  ![Status](https://img.shields.io/badge/Status-Em_Desenvolvimento-yellow?style=for-the-badge)

</div>

---

## 📋 Sobre o Projeto

**Doando+** é um aplicativo móvel desenvolvido como parte do Projeto Integrador **UPX 5** do Centro Universitário **Facens** (Sorocaba-SP).

O objetivo principal é solucionar a dificuldade na organização e divulgação de campanhas de doação de sangue. O aplicativo centraliza pedidos de ajuda, facilita o agendamento nos hemocentros (como a Colsan) e engaja doadores através de um sistema de perfil e histórico de "Vidas Salvas".

---

## 📱 Telas e Funcionalidades

| Feed de Campanhas | Detalhes da Campanha | Perfil do Doador |
|:-----------------:|:--------------------:|:----------------:|
| <img src="https://github.com/user-attachments/assets/b910bdc3-b9d4-42f8-a21f-082aa099d233" width="200" /> | <img src="https://github.com/user-attachments/assets/f4736d64-4630-4cee-8001-afb1d83c5808" width="200" /> | <img src="https://github.com/user-attachments/assets/3c63b168-9b4a-4812-b5b5-dd1c2cb9a0c1" width="200" /> |
| *Visualização rápida de campanhas urgentes e públicas.* | *Informações completas, local e botão de ação.* | *Gestão de dados e contador de vidas salvas.* |

| Criar Campanha | Registrar Doação | Orientações |
|:--------------:|:----------------:|:-----------:|
| <img src="https://github.com/user-attachments/assets/39d0b522-7f29-4e70-b6c0-42453449ba62" width="200" /> | <img src="https://github.com/user-attachments/assets/b0199f17-6638-4c6a-bbbc-fb191debd275" width="200" /> | <img src="https://github.com/user-attachments/assets/58aca4d9-8c8b-4e31-b532-a939bb76e126" width="200" /> |
| *Fluxo simples para solicitar doações.* | *Envio de comprovante e histórico.* | *Requisitos básicos para doação.* |

---

## 🛠️ Tecnologias Utilizadas

Este projeto foi construído utilizando as melhores práticas de desenvolvimento Android nativo:

* **Linguagem:** Java
* **Arquitetura:** MVVM (Model-View-ViewModel)
* **Interface (UI):** XML com Material Design 3
* **Navegação:** Jetpack Navigation Component
* **Banco de Dados & Auth:** Firebase (Firestore, Authentication)
* **Armazenamento de Arquivos:** Firebase Storage (para fotos de perfil e comprovantes)
* **Segurança:** Firebase App Check
* **Carregamento de Imagens:** Glide Library

---

## 🚀 Funcionalidades Principais

* **Autenticação:** Login e Cadastro seguro (E-mail/Senha) com validação.
* **Feed Inteligente:** Listagem de campanhas com distinção visual (Vermelho para Pacientes, Turquesa para Campanhas Públicas).
* **Criação de Campanhas:** Formulários específicos para mobilização de pacientes ou campanhas gerais de estoque.
* **Perfil Gamificado:** Contador automático de doações realizadas e "Vidas Salvas".
* **Registro de Doação:** Envio de foto do comprovante para validar e incrementar o histórico.
* **Info:** Aba dedicada com requisitos e impedimentos para doação (Baseado na Colsan).

---

## 🔧 Como Executar o Projeto

Para rodar este projeto localmente, você precisará do Android Studio e de uma configuração do Firebase:

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/SEU_USUARIO/Doando-Mais.git](https://github.com/SEU_USUARIO/Doando-Mais.git)
    ```
2.  **Abra no Android Studio.**
3.  **Configuração do Firebase:**
    * Crie um projeto no Console do Firebase.
    * Habilite Authentication (Email/Password), Firestore e Storage.
    * Baixe o arquivo `google-services.json` e coloque-o na pasta `app/`.
4.  **App Check (Debug):**
    * Ao rodar em emulador/celular físico, filtre o Logcat por `AppCheck`.
    * Copie o token de depuração gerado e adicione no Console do Firebase > App Check > Gerenciar Tokens.
5.  **Execute o App!**

---

## 👥 Autores (Grupo 06 - UPX 5)

* **Daniel Carro Andrade** - Desenvolvimento & Backend
* **Gabriel Vinicius Monteiro** - UX/UI & Banco de Dados
* **José Eduardo Rolim Junior** - UX/UI & Cibersegurança
* **Luan Diego Cavalcante Carvalho** - Gestão & Backend
* **William Borges Bueno** - Desenvolvimento & Documentação

---

<div align="center">
  <small>Desenvolvido com ❤️ para salvar vidas.</small>
</div>
