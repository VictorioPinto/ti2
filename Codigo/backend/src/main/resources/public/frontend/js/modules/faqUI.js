import { listarFaqs } from "../services/faqService.js";

console.log("FAQ UI carregou");
async function deletarFaq(id) {

    await fetch(`/faq/delete/${id}`);

    location.reload();
}

window.deletarFaq = deletarFaq;

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

			        <button onclick="deletarFaq(${faq.id})">
			            Excluir
			        </button>

			    </div>
			`;
        });

    } catch (erro) {

        console.error("Erro ao carregar FAQs:", erro);

        container.innerHTML =
            "<p>Erro ao carregar FAQs.</p>";
    }
}
async function cadastrarFaq() {

    const pergunta =
        document.getElementById("pergunta").value;

    const resposta =
        document.getElementById("resposta").value;

    await fetch("/faq/insert", {

        method: "POST",

        headers: {
            "Content-Type":
            "application/x-www-form-urlencoded"
        },

        body:
            `pergunta=${encodeURIComponent(pergunta)}&` +
            `resposta=${encodeURIComponent(resposta)}`
    });

    location.reload();
}

carregarFaqs();
document.getElementById("btnCadastrar")
.addEventListener("click", cadastrarFaq);