document.addEventListener("DOMContentLoaded", () => {
  // Adiciona eventos para calcular automaticamente sempre que o utilizador altera um número
  const inputs = document.querySelectorAll(
    'input[type="number"], select, input[type="radio"]',
  );
  inputs.forEach((input) => {
    input.addEventListener("input", calcularInvestimento);
  });

  // Calcula a primeira vez ao carregar a página
  calcularInvestimento();
});

function calcularInvestimento() {
  const P = parseFloat(document.getElementById("inicial").value) || 0;
  const PMT = parseFloat(document.getElementById("mensal").value) || 0;
  const n = parseFloat(document.getElementById("meses").value) || 0;
  let r = parseFloat(document.getElementById("rentabilidade_valor").value) || 0;
  const periodo = document.getElementById("rentabilidade_periodo").value;

  // Converte a taxa para decimal (ex: 1% vira 0.01)
  r = r / 100;

  // Se a taxa for anual, converte para mensal (juros compostos)
  if (periodo === "anual") {
    r = Math.pow(1 + r, 1 / 12) - 1;
  }

  // Total Investido (Sem Juros) = Inicial + (Mensal * Meses)
  const totalInvestido = P + PMT * n;

  // Cálculo do Montante com Juros Compostos (Fórmula Matemática)
  let valorObtido = P * Math.pow(1 + r, n); // Juros sobre o inicial

  if (r > 0) {
    valorObtido += PMT * ((Math.pow(1 + r, n) - 1) / r); // Juros sobre os aportes mensais
  } else {
    valorObtido += PMT * n; // Se a taxa for 0
  }

  // Formata para Moeda (BRL) e injeta no HTML
  const formatador = new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  });

  document.getElementById("resultado-investido").innerText =
    formatador.format(totalInvestido);
  document.getElementById("resultado-obtido").innerText =
    formatador.format(valorObtido);
}
