package com.exemplo.produtoapi.service;

import com.exemplo.produtoapi.dto.DisponibilidadeResponse;
import com.exemplo.produtoapi.dto.ProductRequest;
import com.exemplo.produtoapi.dto.ProductResponse;
import com.exemplo.produtoapi.dto.ViaCepResponse;
import com.exemplo.produtoapi.exception.ProductNotFoundException;
import com.exemplo.produtoapi.model.Product;
import com.exemplo.produtoapi.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ViaCepService viaCepService;

    public ProductService(ProductRepository productRepository, ViaCepService viaCepService) {
        this.productRepository = productRepository;
        this.viaCepService = viaCepService;
    }

    public ProductResponse criar(ProductRequest request) {
        Product produto = Product.builder()
                .name(request.name())
                .category(request.category())
                .price(request.price())
                .distributionCenter(request.distributionCenter())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(productRepository.save(produto));
    }

    public List<ProductResponse> listar(String categoria) {
        List<Product> produtos = (categoria == null || categoria.isBlank())
                ? productRepository.findAll()
                : productRepository.findByCategoryIgnoreCaseAndActiveTrue(categoria);

        return produtos.stream().map(this::toResponse).toList();
    }

    public ProductResponse buscarPorId(Long id) {
        return toResponse(buscarProdutoOuLancarErro(id));
    }

    public ProductResponse atualizar(Long id, ProductRequest request) {
        Product produto = buscarProdutoOuLancarErro(id);
        produto.setName(request.name());
        produto.setCategory(request.category());
        produto.setPrice(request.price());
        produto.setDistributionCenter(request.distributionCenter());

        return toResponse(productRepository.save(produto));
    }

    public ProductResponse inativar(Long id) {
        Product produto = buscarProdutoOuLancarErro(id);
        produto.setActive(false);

        return toResponse(productRepository.save(produto));
    }

    public List<ProductResponse> top5PorPreco() {
        return productRepository.findByActiveTrueOrderByPriceDesc(PageRequest.of(0, 5))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DisponibilidadeResponse verificarDisponibilidade(Long produtoId, String cep) {
        Product produto = buscarProdutoOuLancarErro(produtoId);
        ViaCepResponse endereco = viaCepService.buscarEnderecoPorCep(cep);

        boolean disponivel = produto.getDistributionCenter().equalsIgnoreCase(endereco.localidade());

        return new DisponibilidadeResponse(
                produto.getId(),
                cep,
                endereco.localidade(),
                produto.getDistributionCenter(),
                disponivel
        );
    }

    private Product buscarProdutoOuLancarErro(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto com id " + id + " nao foi encontrado"));
    }

    private ProductResponse toResponse(Product produto) {
        return new ProductResponse(
                produto.getId(),
                produto.getName(),
                produto.getCategory(),
                produto.getPrice(),
                produto.getActive(),
                produto.getDistributionCenter()
        );
    }
}
