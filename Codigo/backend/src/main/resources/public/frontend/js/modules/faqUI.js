import { listarFaqs } from "../services/faqService.js";

document.addEventListener('DOMContentLoaded', async () => {
    verificarPermissaoAdmin();
    await carregarFaqs();
});

// Mantém a tua função de deletar original, mas com um alerta de confirmação
async function deletarFaq(id) {
    if (confirm("Tem certeza que deseja excluir esta pergunta?")) {
        await fetch(`/faq/delete/${id}`);
        location.reload();
    }
}
window.deletarFaq = deletarFaq;

function verificarPermissaoAdmin() {
    const usuarioLogado = JSON.parse(localStorage.getItem('usuario')) || JSON.parse(sessionStorage.getItem('usuario'));
    
    // Altera para a tua propriedade de verificação de admin
    if (usuarioLogado && (usuarioLogado.tipo === 'ADMIN' || usuarioLogado.isAdmin === true)) {
        const btnCadastro = document.getElementById('btn-cadastro-faq');
        if (btnCadastro) {
            btnCadastro.style.display = 'inline-flex';
        }
        document.body.classList.add('is-admin');
    }
}

async function carregarFaqs() {
    const container = document.getElementById("faq-list"); // Container do Accordion
    const isAdmin = document.body.classList.contains('is-admin');

    try {
        const faqs = await listarFaqs(); // Puxa do teu backend usando a tua função
        container.innerHTML = "";

        if (faqs.length === 0) {
            container.innerHTML = '<p>Nenhuma pergunta cadastrada no momento.</p>';
            return;
        }

        faqs.forEach(faq => {
            const faqItem = document.createElement('div');
            faqItem.className = 'faq-item';
            
            // Renderiza o botão de excluir apenas se for admin
            const btnExcluir = isAdmin 
                ? `<button onclick="deletarFaq(${faq.id})" style="background: #ef4444; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; margin-left: 10px;"><i class="fas fa-trash"></i> Excluir</button>` 
                : '';

            faqItem.innerHTML = `
                <div class="faq-question">
                    <span style="flex-grow: 1;">${faq.pergunta}</span>
                    ${btnExcluir}
                    <i class="fas fa-chevron-down" style="margin-left: 15px;"></i>
                </div>
                <div class="faq-answer">
                    <p>${faq.resposta}</p>
                </div>
            `;

            // Lógica para abrir/fechar a sanfona
            const questionDiv = faqItem.querySelector('.faq-question');
            questionDiv.addEventListener('click', (e) => {
                // Impede que clicar no botão de excluir abra/feche a sanfona
                if (e.target.closest('button')) return;

                const isActive = faqItem.classList.contains('active');
                document.querySelectorAll('.faq-item').forEach(item => item.classList.remove('active'));
                
                if (!isActive) {
                    faqItem.classList.add('active');
                }
            });

            container.appendChild(faqItem);
        });

    } catch (erro) {
        console.error("Erro ao carregar FAQs:", erro);
        container.innerHTML = "<p>Erro ao carregar as perguntas.</p>";
    }
}