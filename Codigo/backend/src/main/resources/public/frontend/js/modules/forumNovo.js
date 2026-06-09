document.addEventListener("DOMContentLoaded", () => {
  const formTopico = document.getElementById("form-topico");
  if (!formTopico) return;

  formTopico.addEventListener("submit", async (e) => {
    e.preventDefault();

    let usuarioId = null;

    // Recupera o ID do cofre gerado no login (auth.js)
    const dadosUsuario = localStorage.getItem("usuarioAtual");
    if (dadosUsuario) {
      const usuario = JSON.parse(dadosUsuario);
      usuarioId = usuario.id;
    }

    if (!usuarioId) {
      alert("Você precisa fazer login para poder publicar um tópico!");
      return;
    }

    const params = new URLSearchParams();
    params.append("usuarioId", usuarioId);
    params.append("titulo", document.getElementById("titulo").value);
    params.append("imagemUrl", document.getElementById("imagemUrl").value);
    params.append("conteudo", document.getElementById("conteudo").value);

    try {
      const res = await fetch("http://localhost:8080/forum/insert", {
        method: "POST",
        body: params,
      });
      if (res.ok) {
        window.location.href = "index.html";
      } else {
        alert("Erro ao publicar o tópico no servidor.");
      }
    } catch (error) {
      console.error("Erro ao enviar o tópico:", error);
    }
  });
});
