import { usuarioService } from "../services/usuarioService.js";

document.addEventListener("DOMContentLoaded", async () => {
    const container = document.getElementById("quiz-container");
    
    try {
        let isAdm = false;
        try {
            const userResponse = await usuarioService.getUsuarioAtual();
            if (userResponse.ok) {
                const userData = await userResponse.json();
                isAdm = userData.adm; 
            }
        } catch (e) {
            console.error("Erro ao verificar status de ADM", e);
        }

        const response = await fetch('/trilha/quizzes');
        
        if (!response.ok) {
            console.error("Erro ao carregar trilha. Talvez usuário não logado.");
            return;
        }

        const quizzes = await response.json();
        container.innerHTML = ""; 
        
        quizzes.forEach(quiz => {
            let iconeSVG = "";
            if (quiz.status === "feito") {
                iconeSVG = '<path d="M5 13l4 4L19 7" stroke="#333" fill="none" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>';
            } else if (quiz.status === "liberado") {
                iconeSVG = '<path d="M7 11h10a2 2 0 0 1 2 2v6a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2v-6a2 2 0 0 1 2-2zM9 11V7.5a3.5 3.5 0 0 1 7 0" stroke="#333" fill="none" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>';
            } else if (quiz.status === "bloqueado") {
                iconeSVG = '<path d="M5 11h14a2 2 0 0 1 2 2v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-6a2 2 0 0 1 2-2zM7 11V7a5 5 0 0 1 10 0v4" stroke="#333" fill="none" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>';
            }

            // NOVO: Botão de editar dinâmico para Administradores
            let btnEdit = "";
            if (isAdm) {
                btnEdit = `
                    <button class="btn-edit-quiz" data-id="${quiz.id}" data-titulo="${quiz.titulo}" title="Editar Quiz" style="position: absolute; top: 15px; right: 15px; background: none; border: none; cursor: pointer;">
                        <svg viewBox="0 0 24 24" width="22" height="22">
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke="#002d5b" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke="#002d5b" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                    </button>
                `;
            }

            // Alterado de quiz.nivel_id para quiz.id para refletir a nossa nova base de dados sequencial
            const cardHTML = `
                <div class="quiz-card ${quiz.status}" data-id="${quiz.id}" style="position: relative;">
                    <div class="quiz-card-icon-container">
                        <svg class="quiz-card-icon" viewBox="0 0 24 24">${iconeSVG}</svg>
                    </div>
                    <div class="quiz-card-info">
                        <h3>Quiz ${quiz.id}</h3>
                        <p>${quiz.titulo}</p>
                    </div>
                    ${btnEdit}
                </div>
            `;
            
            container.innerHTML += cardHTML;
        });

        if (isAdm) {
            container.innerHTML += `
                <button class="btn-add-quiz" id="btn-add-quiz" title="Cadastrar Novo Quiz">
                    <svg class="btn-add-quiz-plus" viewBox="0 0 24 24">
                        <path d="M12 5v14M5 12h14" stroke="#333" fill="none" stroke-width="1.5" stroke-linecap="round"/>
                    </svg>
                </button>
            `;

            document.getElementById("btn-add-quiz").addEventListener('click', () => {
                window.location.href = "../admin/cadastro_quiz.html";
            });

            // NOVO: Adiciona o evento de clique aos botões de editar
            document.querySelectorAll('.btn-edit-quiz').forEach(btn => {
                btn.addEventListener('click', (e) => {
                    e.stopPropagation(); // Impede que clicar no lápis entre no quiz como jogador
                    const qId = btn.getAttribute('data-id');
                    const qTitulo = btn.getAttribute('data-titulo');
                    // Passa os dados via URL
                    window.location.href = `../admin/cadastro_quiz.html?edit=${qId}&titulo=${encodeURIComponent(qTitulo)}`;
                });
            });
        }

        // Eventos de clique para JOGAR os quizzes (Apenas liberados e feitos)
        document.querySelectorAll('.quiz-card.liberado, .quiz-card.feito').forEach(card => {
            card.addEventListener('click', () => {
                const quizId = card.getAttribute('data-id');
                window.location.href = `./quizzes/index.html?id=${quizId}`;
            });
        });

        document.querySelectorAll('.quiz-card.bloqueado').forEach(card => {
            card.addEventListener('click', () => {
                alert("Este quiz está bloqueado. Complete o anterior primeiro!");
            });
        });

    } catch (error) {
        console.error("Erro na requisição:", error);
    }
});