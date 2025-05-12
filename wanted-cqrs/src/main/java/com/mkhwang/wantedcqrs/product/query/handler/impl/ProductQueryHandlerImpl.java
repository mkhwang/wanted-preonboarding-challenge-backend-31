package com.mkhwang.wantedcqrs.product.query.handler.impl;

import com.mkhwang.wantedcqrs.product.command.ProductDto;
import com.mkhwang.wantedcqrs.product.infra.ProductSearchRepository;
import com.mkhwang.wantedcqrs.product.query.ProductQuery;
import com.mkhwang.wantedcqrs.product.query.handler.ProductListResponse;
import com.mkhwang.wantedcqrs.product.query.handler.ProductQueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductQueryHandlerImpl implements ProductQueryHandler {
  private final ProductSearchRepository productSearchRepository;


  @Override
  public ProductDto.Product getProduct(ProductQuery.GetProduct query) {
    return productSearchRepository.getProductDetailBaseById(query.getProductId());
  }

  @Override
  public ProductListResponse getProducts(ProductQuery.ListProducts query) {
    return null;
  }
}
