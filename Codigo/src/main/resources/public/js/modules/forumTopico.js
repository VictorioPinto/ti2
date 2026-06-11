import { usuarioService } from "../services/usuarioService.js";

const urlParams = new URLSearchParams(window.location.search);
const topicoId = urlParams.get("id");

// Variáveis globais para guardar quem está logado
let usuarioLogadoId = null;
let isAdm = false;

document.addEventListener("DOMContentLoaded", async () => {
  if (!topicoId) return;

  // Busca sessão
  try {
    const response = await usuarioService.getUsuarioAtual();
    if (response.ok) {
      const usuario = await response.json();
      usuarioLogadoId = usuario.id;
      isAdm = usuario.adm;
    }
  } catch (error) {}

  // 1. Carregar Tópico Principal
  try {
    const resTopico = await fetch(`http://localhost:8080/forum/${topicoId}`);
    const topico = await resTopico.json();

    // Se for dono do tópico, permite editar o Post principal
    // Se for dono do tópico, permite editar ou excluir o Post principal
    let btnEditTopico = "";
    let btnDeleteTopico = "";
    if (usuarioLogadoId === topico.usuarioId || isAdm) {
      btnEditTopico = `<button onclick="window.location.href='novo_topico.html?edit=${topico.id}'" style="float: right; background: none; border: none; cursor: pointer; font-size: 18px; margin-left: 10px;" title="Editar Tópico">✏️</button>`;
      btnDeleteTopico = `<button onclick="deletarTopicoPost(${topico.id})" style="float: right; background: none; border: none; cursor: pointer; font-size: 18px; color: red;" title="Excluir Tópico">🗑️</button>`;
    }

    let htmlTopico = `<div style="overflow: auto;">${btnDeleteTopico}${btnEditTopico}</div><h1 style="margin-bottom: 10px; padding-right: 30px;">${topico.titulo}</h1>`;
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

      if (!usuarioLogadoId) {
        alert("Você precisa fazer login para poder comentar!");
        return;
      }

      const params = new URLSearchParams();
      params.append("usuarioId", usuarioLogadoId);
      params.append(
        "conteudo",
        document.getElementById("texto-comentario").value,
      );

      try {
        const res = await fetch(
          `http://localhost:8080/forum/${topicoId}/comentarios/insert`,
          { method: "POST", body: params },
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
      // Cria o botão de editar o comentário se for do dono
      let btnEditComentario = "";
      if (usuarioLogadoId === c.usuarioId || isAdm) {
        // Escapamos as aspas para evitar erro no HTML
        btnEditComentario = `<button onclick="editarComentario(${c.id}, this)" data-texto="${c.conteudo.replace(/"/g, "&quot;")}" style="background: none; border: none; cursor: pointer; font-size: 14px; margin-left: 10px; color: #0056b3;" title="Editar Comentário">✏️ Editar</button>`;
      }

      lista.innerHTML += `
                <div class="comentario" style="margin-bottom: 15px;">
                    <p style="margin: 0 0 10px 0; color: #444;">${c.conteudo}</p>
                    <div style="display: flex; align-items: center;">
                        <button class="btn-interagir" onclick="interagir('comentario', ${c.id}, 'like', event)">👍 <span id="like-comentario-${c.id}">${c.likes}</span></button>
                        <button class="btn-interagir" onclick="interagir('comentario', ${c.id}, 'dislike', event)">👎 <span id="dislike-comentario-${c.id}">${c.dislikes}</span></button>
                        ${btnEditComentario}
                    </div>
                </div>`;
    });
  } catch (error) {
    console.error("Erro ao carregar comentários:", error);
  }
}

async function interagir(entidade, id, tipo, event) {
  if (event) event.stopPropagation();
  const url =
    entidade === "topico"
      ? `http://localhost:8080/forum/topico/${id}/${tipo}`
      : `http://localhost:8080/forum/comentario/${id}/${tipo}`;

  try {
    const res = await fetch(url, { method: "POST" });
    const result = await res.json();
    if (res.ok && result.success) {
      location.reload();
    } else {
      if (res.status === 401) alert("Você precisa fazer login para interagir!");
    }
  } catch (error) {
    console.error("Erro ao registrar interação:", error);
  }
}

window.interagir = interagir;

// Função para Editar Comentário de forma simples (com Pop-up)
window.editarComentario = async (id, btnElement) => {
  const textoAtual = btnElement.getAttribute("data-texto");
  const novoTexto = prompt("Edite o seu comentário:", textoAtual);

  if (novoTexto && novoTexto.trim() !== "" && novoTexto !== textoAtual) {
    const params = new URLSearchParams();
    params.append("conteudo", novoTexto);
    try {
      const res = await fetch(
        `http://localhost:8080/forum/comentario/${id}/atualizar`,
        {
          method: "POST",
          body: params,
        },
      );
      if (res.ok) {
        location.reload();
      } else {
        alert("Erro ao atualizar o comentário.");
      }
    } catch (e) {
      console.error(e);
    }
  }
};
