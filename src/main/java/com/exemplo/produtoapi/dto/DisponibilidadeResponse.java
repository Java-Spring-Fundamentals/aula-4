package com.exemplo.produtoapi.dto;

public record DisponibilidadeResponse(
        Long produtoId,
        String cepConsultado,
        String cidadeDoCep,
        String centroDistribuicaoDoProduto,
        boolean disponivelNaCidade
) {
}
