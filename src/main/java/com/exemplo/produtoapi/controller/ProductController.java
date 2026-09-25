package com.exemplo.produtoapi.controller;

import com.exemplo.produtoapi.dto.DisponibilidadeResponse;
import com.exemplo.produtoapi.dto.ProductRequest;
import com.exemplo.produtoapi.dto.ProductResponse;
import com.exemplo.produtoapi.service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/produtos")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> criar(
            @RequestBody @Valid ProductRequest request,
            @RequestHeader(value = "X-Origem-Requisicao", required = false, defaultValue = "desconhecida") String origem) {
        log.info("Recebendo requisicao de criacao de produto vinda de: {}", origem);
        ProductResponse response = productService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> listar(@RequestParam(required = false) String categoria) {
        return ResponseEntity.ok(productService.listar(categoria));
    }

    @GetMapping("/top5")
    public ResponseEntity<List<ProductResponse>> top5PorPreco() {
        return ResponseEntity.ok(productService.top5PorPreco());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> atualizar(@PathVariable Long id, @RequestBody @Valid ProductRequest request) {
        return ResponseEntity.ok(productService.atualizar(id, request));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<ProductResponse> inativar(@PathVariable Long id) {
        return ResponseEntity.ok(productService.inativar(id));
    }

    @GetMapping("/{id}/disponibilidade")
    public ResponseEntity<DisponibilidadeResponse> verificarDisponibilidade(
            @PathVariable Long id,
            @RequestParam String cep) {
        return ResponseEntity.ok(productService.verificarDisponibilidade(id, cep));
    }
}
