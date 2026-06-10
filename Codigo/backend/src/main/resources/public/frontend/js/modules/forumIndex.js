document.addEventListener("DOMContentLoaded", async () => {
  const feed = document.getElementById("feed-forum");
  if (!feed) return;

  // Busca quem está logado para permitir exibir botão de edição
  let usuarioLogadoId = null;
  let isAdm = false;
  try {
    const resUser = await fetch("http://localhost:8080/usuario/atual");
    if (resUser.ok) {
      const user = await resUser.json();
      usuarioLogadoId = user.id;
      isAdm = user.adm;
    }
  } catch (e) {}

  try {
    const response = await fetch("http://localhost:8080/forum");
    const topicos = await response.json();

    feed.innerHTML = "";
    topicos.forEach((t) => {
      let conteudoHTML =
        t.imagemUrl && t.imagemUrl.trim() !== ""
          ? `<img src="${t.imagemUrl}" style="max-width: 100%; border-radius: 8px; margin-top: 10px;" alt="Imagem do tópico">`
          : `<p style="margin-top: 10px; color: #555;">${t.conteudo.substring(0, 150)}...</p>`;

      // Se for dono do post ou Adm, mostra o botão de edição
      let btnEdit = "";
      if (usuarioLogadoId === t.usuarioId || isAdm) {
        btnEdit = `<button onclick="editarTopico(event, ${t.id})" style="position: absolute; top: 15px; right: 15px; background: none; border: none; cursor: pointer; font-size: 18px;" title="Editar Tópico">✏️</button>`;
      }

      feed.innerHTML += `
                <div class="forum-card" onclick="window.location.href='topico.html?id=${t.id}'" style="position: relative;">
                    ${btnEdit}
                    <h2 style="margin: 0; padding-right: 30px;">${t.titulo}</h2>
                    ${conteudoHTML}
                    <div class="forum-actions" style="margin-top: 15px; border-top: 1px solid #eee; padding-top: 10px; display: flex; align-items: center;">
                        <button onclick="interagir('topico', ${t.id}, 'like', event)">👍 <span id="like-topico-${t.id}">${t.likes}</span></button>
                        <button onclick="interagir('topico', ${t.id}, 'dislike', event)">👎 <span id="dislike-topico-${t.id}">${t.dislikes}</span></button>
                        <span style="color: #666; font-size: 14px;">💬 ${t.quantidadeComentarios} Comentários</span>
                    </div>
                </div>`;
    });
  } catch (error) {
    console.error("Erro ao carregar o feed do fórum:", error);
  }
});

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

window.editarTopico = (event, id) => {
  event.stopPropagation();
  window.location.href = `novo_topico.html?edit=${id}`;
};
