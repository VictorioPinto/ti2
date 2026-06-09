document.addEventListener("DOMContentLoaded", async () => {
  const feed = document.getElementById("feed-forum");
  if (!feed) return;

  try {
    const response = await fetch("http://localhost:8080/forum");
    const topicos = await response.json();

    feed.innerHTML = "";
    topicos.forEach((t) => {
      let conteudoHTML =
        t.imagemUrl && t.imagemUrl.trim() !== ""
          ? `<img src="${t.imagemUrl}" style="max-width: 100%; border-radius: 8px; margin-top: 10px;" alt="Imagem do tópico">`
          : `<p style="margin-top: 10px; color: #555;">${t.conteudo.substring(0, 150)}...</p>`;

      feed.innerHTML += `
                <div class="forum-card" onclick="window.location.href='topico.html?id=${t.id}'">
                    <h2 style="margin: 0;">${t.titulo}</h2>
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
  event.stopPropagation(); // Impede de abrir a página ao dar like/dislike
  const url =
    entidade === "topico"
      ? `http://localhost:8080/forum/topico/${id}/${tipo}`
      : `http://localhost:8080/forum/comentario/${id}/${tipo}`;

  try {
    const res = await fetch(url, { method: "POST" });
    if (res.ok) {
      const el = document.getElementById(`${tipo}-${entidade}-${id}`);
      if (el) {
        el.innerText = parseInt(el.innerText) + 1;
      }
    }
  } catch (error) {
    console.error("Erro ao registrar interação:", error);
  }
}

// Torna a função visível para os cliques inline do HTML
window.interagir = interagir;
