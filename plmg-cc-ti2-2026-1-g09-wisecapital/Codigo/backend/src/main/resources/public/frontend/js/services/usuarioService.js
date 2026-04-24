import { usuarioService } from "../services/usuarioService.js";

const loginForm = document.querySelector("#login-form");
const registerForm = document.querySelector("#register-form");
const goToRegister = document.querySelector("#go-to-register");
const goToLogin = document.querySelector("#go-to-login");

// Alternar entre Login e Registro
goToRegister.addEventListener("click", (e) => {
  e.preventDefault();
  loginForm.classList.add("hidden");
  registerForm.classList.remove("hidden");
});

goToLogin.addEventListener("click", (e) => {
  e.preventDefault();
  registerForm.classList.add("hidden");
  loginForm.classList.remove("hidden");
});

// Lógica de Login
loginForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  const login = loginForm.login.value;
  const senha = loginForm.senha.value;

  try {
    const response = await usuarioService.login(login, senha);
    const result = await response.text();

    if (response.ok) {
      alert(result);
      window.location.href = "../trilha/index.html";
    } else {
      alert(result);
    }
  } catch (error) {
    console.error("Erro no login:", error);
  }
});

// Lógica de Registro
registerForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  const dados = {
    nome: registerForm.nome.value,
    login: registerForm.login.value,
    email: registerForm.email.value,
    senha: registerForm.senha.value,
  };

  try {
    const response = await usuarioService.cadastrar(dados);
    const result = await response.text();

    if (response.ok) {
      alert(result);
      location.reload(); // Volta para o login
    } else {
      alert(result);
    }
  } catch (error) {
    console.error("Erro no cadastro:", error);
  }
});
