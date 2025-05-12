package com.mkhwang.wantedcqrs.product.query.handler;

import com.mkhwang.wantedcqrs.product.command.ProductDto;
import com.mkhwang.wantedcqrs.product.query.ProductQuery;

public interface ProductQueryHandler {
  ProductDto.Product getProduct(ProductQuery.GetProduct query);

  ProductListResponse getProducts(ProductQuery.ListProducts query);
}
