document.addEventListener("DOMContentLoaded", async () => {
    
    const urlParams = new URLSearchParams(window.location.search);
    const quizId = urlParams.get('id');

    if (!quizId) {
        alert("Nenhum quiz selecionado.");
        window.location.href = "../trilha/index.html";
        return;
    }

    const areaPergunta = document.getElementById("area-pergunta");
    const progresso = document.getElementById("progresso");
    const feedbackArea = document.getElementById("feedback-area");
    const btnResponder = document.getElementById("btn-responder");
    const btnProxima = document.getElementById("btn-proxima");
    const btnVoltarTrilha = document.getElementById("btn-voltar-trilha");

    let perguntas = [];
    let indiceAtual = 0;
    let acertos = 0;

    
    try {
        const response = await fetch(`http://localhost:8080/quiz/${quizId}/perguntas`);
        perguntas = await response.json();
        
        if (perguntas.length > 0) {
            renderizarPergunta();
            btnResponder.style.display = "block";
        } else {
            progresso.innerHTML = "Este quiz ainda não tem perguntas cadastradas.";
        }
    } catch (error) {
        console.error("Erro ao carregar quiz:", error);
        progresso.innerHTML = "Erro ao carregar o quiz.";
    }

    
    function renderizarPergunta() {
        const p = perguntas[indiceAtual];
        progresso.innerHTML = `<strong>Pergunta ${indiceAtual + 1} de ${perguntas.length}</strong>`;
        feedbackArea.style.display = "none";
        feedbackArea.className = "";
        btnResponder.style.display = "block";
        btnProxima.style.display = "none";
        
        let html = `<h2 class="pergunta-titulo">${p.pergunta}</h2>`;

        if (p.tipo === "FECHADA") {
            html += `<div class="opcoes-container">`;
            p.opcoes.forEach(opcao => {
                html += `
                    <label class="opcao-label">
                        <input type="radio" name="opcao" value="${opcao.id}">
                        ${opcao.texto}
                    </label>`;
            });
            html += `</div>`;
        } else if (p.tipo === "ABERTA") {
            html += `<textarea id="resposta-aberta" placeholder="Escreva a sua resposta de forma detalhada..."></textarea>`;
        }

        areaPergunta.innerHTML = html;
    }

    
    btnResponder.addEventListener("click", async () => {
        const p = perguntas[indiceAtual];
        let estaCorreto = false;

        btnResponder.disabled = true;
        btnResponder.innerHTML = 'Processando... <i class="fas fa-spinner fa-spin"></i>';

        if (p.tipo === "FECHADA") {
            const opcaoSelecionada = document.querySelector('input[name="opcao"]:checked');
            if (!opcaoSelecionada) {
                alert("Selecione uma opção.");
                resetarBotao();
                return;
            }

            if (parseInt(opcaoSelecionada.value) === p.correta) {
                estaCorreto = true;
                acertos++;
                mostrarFeedback(true, "Correto! " + (p.explicacao || ""));
            } else {
                mostrarFeedback(false, "Incorreto. " + (p.explicacao || ""));
            }
            avancarEtapa();

        } else if (p.tipo === "ABERTA") {
            const textoResposta = document.getElementById("resposta-aberta").value;
            if (textoResposta.trim().length < 10) {
                alert("Por favor, desenvolva melhor a sua resposta.");
                resetarBotao();
                return;
            }

           
            try {
                const res = await fetch("http://localhost:8080/questionario/avaliar-aberta", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: new URLSearchParams({
                        pergunta: p.pergunta,
                        resposta_aluno: textoResposta
                    })
                });

                const avaliacao = await res.json();
                
                if (avaliacao.aprovado) {
                    acertos++;
                    mostrarFeedback(true, `Excelente! Pontuação: ${avaliacao.porcentagem}%<br><br><strong>Feedback da IA:</strong> ${avaliacao.feedback}`);
                } else {
                    mostrarFeedback(false, `Ainda pode melhorar. Pontuação: ${avaliacao.porcentagem}%<br><br><strong>Feedback da IA:</strong> ${avaliacao.feedback}`);
                }
                avancarEtapa();

            } catch (error) {
                alert("Erro ao conectar com a IA corretora.");
                resetarBotao();
            }
        }
    });

    function resetarBotao() {
        btnResponder.disabled = false;
        btnResponder.innerHTML = "Verificar Resposta";
    }

    function mostrarFeedback(sucesso, mensagem) {
        feedbackArea.style.display = "block";
        feedbackArea.innerHTML = mensagem;
        if (sucesso) {
            feedbackArea.className = "feedback-sucesso";
        } else {
            feedbackArea.className = "feedback-erro";
        }
    }

    function avancarEtapa() {
        btnResponder.style.display = "none";
        resetarBotao();
        
        if (indiceAtual + 1 < perguntas.length) {
            btnProxima.style.display = "block";
        } else {
            
            btnVoltarTrilha.style.display = "block";
            const aprovacao = (acertos / perguntas.length) >= 0.7 ? "Aprovado!" : "Tente novamente.";
            areaPergunta.innerHTML += `<hr><h3 style="margin-top:20px; text-align:center;">Fim do Quiz! Acertou ${acertos} de ${perguntas.length}. ${aprovacao}</h3>`;
        }
    }

    btnProxima.addEventListener("click", () => {
        indiceAtual++;
        renderizarPergunta();
    });

    btnVoltarTrilha.addEventListener("click", () => {
        window.location.href = "../index.html";
        
    });
});