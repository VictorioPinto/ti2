import { usuarioService } from "../services/usuarioService.js";

const urlParams = new URLSearchParams(window.location.search);
const topicoId = urlParams.get("id");

document.addEventListener("DOMContentLoaded", async () => {
  if (!topicoId) return;

  // 1. Carregar Tópico Principal
  try {
    const resTopico = await fetch(`http://localhost:8080/forum/${topicoId}`);
    const topico = await resTopico.json();

    let htmlTopico = `<h1 style="margin-bottom: 10px;">${topico.titulo}</h1>`;
    if (topico.imagemUrl) {
      htmlTopico += `<img src="${topico.imagemUrl}" style="max-width: 100%; border-radius: 8px; margin-bottom: 15px;" alt="Imagem do Post">`;
    }
    htmlTopico += `<p style="font-size: 16px; line-height: 1.6; color: #333;">${topico.conteudo}</p>`;
    document.getElementById("post-principal").innerHTML = htmlTopico;
  } catch (error) {
    console.error("Erro ao carregar o tópico:", error);
  }

  // 2. Carregar Comentários
  carregarComentarios();

  // 3. Monitorizar o envio de novos comentários
  const formComentario = document.getElementById("form-comentario");
  if (formComentario) {
    formComentario.addEventListener("submit", async (e) => {
      e.preventDefault();
      let usuarioId = null;

      const dadosUsuario = localStorage.getItem("usuarioAtual");
      if (dadosUsuario) {
        const usuario = JSON.parse(dadosUsuario);
        usuarioId = usuario.id;
      } else {
        try {
          const response = await usuarioService.getUsuarioAtual();
          if (response.ok) {
            const usuario = await response.json();
            usuarioId = usuario.id;
          }
        } catch (error) {
          console.error("Erro ao buscar sessão ativa:", error);
        }
      }

      if (!usuarioId) {
        alert("Você precisa fazer login para poder comentar!");
        return;
      }

      const params = new URLSearchParams();
      params.append("usuarioId", usuarioId);
      params.append(
        "conteudo",
        document.getElementById("texto-comentario").value,
      );

      try {
        const res = await fetch(
          `http://localhost:8080/forum/${topicoId}/comentarios/insert`,
          {
            method: "POST",
            body: params,
          },
        );

        if (res.ok) {
          document.getElementById("texto-comentario").value = "";
          carregarComentarios();
        } else {
          alert("Erro ao salvar o comentário.");
        }
      } catch (err) {
        console.error("Erro ao enviar comentário:", err);
      }
    });
  }
});

async function carregarComentarios() {
  const lista = document.getElementById("lista-comentarios");
  if (!lista) return;
  lista.innerHTML = "";

  try {
    const resComentarios = await fetch(
      `http://localhost:8080/forum/${topicoId}/comentarios`,
    );
    const comentarios = await resComentarios.json();

    comentarios.forEach((c) => {
      lista.innerHTML += `
                <div class="comentario">
                    <p style="margin: 0 0 10px 0; color: #444;">${c.conteudo}</p>
                    <div>
                        <button class="btn-interagir" onclick="interagir('comentario', ${c.id}, 'like', event)">👍 <span id="like-comentario-${c.id}">${c.likes}</span></button>
                        <button class="btn-interagir" onclick="interagir('comentario', ${c.id}, 'dislike', event)">👎 <span id="dislike-comentario-${c.id}">${c.dislikes}</span></button>
                    </div>
                </div>`;
    });
  } catch (error) {
    console.error("Erro ao carregar comentários:", error);
  }
}

async function interagir(entidade, id, tipo, event) {
  if (event) event.stopPropagation();
  
  const url = entidade === "topico"
      ? `http://localhost:8080/forum/topico/${id}/${tipo}`
      : `http://localhost:8080/forum/comentario/${id}/${tipo}`;

  try {
    const res = await fetch(url, { method: "POST" });
    const result = await res.json();
    
    if (res.ok && result.success) {
      // Recarrega a página para exibir os novos valores contabilizados pelo banco
      location.reload(); 
    } else {
      if (res.status === 401) {
         alert("Você precisa fazer login para interagir!");
      }
    }
  } catch (error) {
    console.error("Erro ao registrar interação:", error);
  }
}

window.interagir = interagir;
