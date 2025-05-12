package com.mkhwang.wantedcqrs.product.command.handler;

import com.mkhwang.wantedcqrs.product.command.ProductCommand;
import com.mkhwang.wantedcqrs.product.command.ProductDto;

public interface ProductCommandHandler {

  ProductDto.Product createProduct(ProductCommand.CreateProduct command);

  ProductDto.Product updateProduct(ProductCommand.UpdateProduct command);

  void deleteProduct(ProductCommand.DeleteProduct command);

  ProductDto.Option addProductOption(ProductCommand.AddProductOption command);

  ProductDto.Option updateProductOption(ProductCommand.UpdateProductOption command);

  void deleteProductOption(ProductCommand.DeleteProductOption command);

  ProductDto.Image addProductImage(ProductCommand.AddProductImage command);
}
