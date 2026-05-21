package service;

import dao.FaqDAO;
import model.Faq;
import spark.Request;
import spark.Response;

import java.util.List;

public class FaqService {

    private FaqDAO faqDAO = new FaqDAO();

    /**
     * CREATE
     */
    public Object insert(Request request, Response response) {

        String pergunta = request.queryParams("pergunta");
        String respostaTexto = request.queryParams("resposta");

        Faq faq = new Faq(-1, pergunta, respostaTexto);

        if (faqDAO.insert(faq)) {
            response.status(201);
            return "FAQ cadastrada com sucesso!";
        } else {
            response.status(500);
            return "Erro ao cadastrar FAQ.";
        }
    }

    /**
     * READ POR ID
     */
    public Object get(Request request, Response response) {

        int id = Integer.parseInt(request.params(":id"));

        Faq faq = faqDAO.get(id);

        if (faq != null) {
            response.status(200);

            return "Pergunta: " + faq.getPergunta()
                    + "\nResposta: " + faq.getResposta();

        } else {
            response.status(404);
            return "FAQ não encontrada.";
        }
    }

    /**
     * LISTAR TODAS AS FAQS
     */
    public Object listar(Request request, Response response) {

        List<Faq> faqs = faqDAO.get();

        StringBuilder retorno = new StringBuilder();

        for (Faq faq : faqs) {

            retorno.append("ID: ")
                   .append(faq.getId())
                   .append("\nPergunta: ")
                   .append(faq.getPergunta())
                   .append("\nResposta: ")
                   .append(faq.getResposta())
                   .append("\n------------------\n");
        }

        response.status(200);

        return retorno.toString();
    }

    /**
     * UPDATE
     */
    public Object update(Request request, Response response) {

        int id = Integer.parseInt(request.params(":id"));

        String pergunta = request.queryParams("pergunta");
        String respostaTexto = request.queryParams("resposta");

        Faq faqExistente = faqDAO.get(id);

        if (faqExistente == null) {
            response.status(404);
            return "FAQ não encontrada.";
        }

        Faq faqAtualizada =
                new Faq(id, pergunta, respostaTexto);

        if (faqDAO.update(faqAtualizada)) {

            response.status(200);

            return "FAQ atualizada com sucesso!";
        } else {

            response.status(500);

            return "Erro ao atualizar FAQ.";
        }
    }

    /**
     * DELETE
     */
    public Object delete(Request request, Response response) {

        int id = Integer.parseInt(request.params(":id"));

        Faq faq = faqDAO.get(id);

        if (faq == null) {
            response.status(404);
            return "FAQ não encontrada.";
        }

        if (faqDAO.delete(id)) {

            response.status(200);

            return "FAQ removida com sucesso!";
        } else {

            response.status(500);

            return "Erro ao remover FAQ.";
        }
    }
}