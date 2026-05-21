export async function listarFaqs() {

    const response =
        await fetch("http://localhost:4567/faq/listar");

    const texto = await response.text();

    console.log(texto);

    return [];
}