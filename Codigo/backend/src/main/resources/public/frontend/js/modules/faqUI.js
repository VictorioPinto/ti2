import { listarFaqs } from "../services/faqService.js";

console.log("FAQ UI carregou");

async function carregarFaqs() {

    const container = document.getElementById("faq-container");

    try {

        const faqs = await listarFaqs();

        container.innerHTML = "";

        faqs.forEach(faq => {

            container.innerHTML += `
                <div class="faq-card">
                    <h3>${faq.pergunta}</h3>
                    <p>${faq.resposta}</p>
                </div>
            `;
        });

    } catch (erro) {

        console.error("Erro ao carregar FAQs:", erro);

        container.innerHTML =
            "<p>Erro ao carregar FAQs.</p>";
    }
}

carregarFaqs();