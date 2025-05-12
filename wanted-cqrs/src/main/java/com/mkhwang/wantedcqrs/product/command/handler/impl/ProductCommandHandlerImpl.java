package com.mkhwang.wantedcqrs.product.command.handler.impl;

import com.mkhwang.wantedcqrs.config.GenericMapper;
import com.mkhwang.wantedcqrs.config.exception.ResourceNotFoundException;
import com.mkhwang.wantedcqrs.product.command.ProductCommand;
import com.mkhwang.wantedcqrs.product.command.ProductDto;
import com.mkhwang.wantedcqrs.product.command.handler.ProductCommandHandler;
import com.mkhwang.wantedcqrs.product.domain.*;
import com.mkhwang.wantedcqrs.product.domain.dto.product.*;
import com.mkhwang.wantedcqrs.product.infra.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductCommandHandlerImpl implements ProductCommandHandler {
  private final ProductRepository productRepository;
  private final ProductDetailRepository productDetailRepository;
  private final CategoryRepository categoryRepository;
  private final ProductCategoryRepository productCategoryRepository;
  private final ProductImageRepository productImageRepository;
  private final GenericMapper genericMapper;
  private final SellerRepository sellerRepository;
  private final BrandRepository brandRepository;
  private final ProductPriceRepository productPriceRepository;
  private final ProductTagRepository productTagRepository;
  private final TagRepository tagRepository;
  private final ProductOptionGroupRepository productOptionGroupRepository;
  private final ProductOptionRepository productOptionRepository;


  @Override
  @Transactional
  public ProductDto.Product createProduct(ProductCommand.CreateProduct command) {
    Product product = genericMapper.toEntity(command, Product.class);

    sellerRepository.findById(command.getSellerId()).ifPresent(product::setSeller);
    brandRepository.findById(command.getBrandId()).ifPresent(product::setBrand);

    productRepository.save(product);

    ProductDetail productDetail = genericMapper.toEntity(command.getDetail(), ProductDetail.class);
    productDetail.setProduct(product);
    productDetailRepository.save(productDetail);

    ProductPrice productPrice = genericMapper.toEntity(command.getPrice(), ProductPrice.class);
    productPrice.setProduct(product);
    productPriceRepository.save(productPrice);

    createProductCategories(command, product);

    createProductOptionGroups(command, product);

    createProductImages(command, product);

    createProductTags(command, product);

    return genericMapper.toDto(product, ProductDto.Product.class);
  }

  @Override
  @Transactional
  public ProductDto.Product updateProduct(ProductCommand.UpdateProduct command) {
    Product product = productRepository.findById(command.getId()).orElseThrow(
            () -> new ResourceNotFoundException("product.not.found")
    );
    product.setName(command.getName());
    product.setSlug(command.getSlug());
    product.setShortDescription(command.getShortDescription());
    product.setFullDescription(command.getFullDescription());
    product.setStatus(command.getStatus());
    sellerRepository.findById(command.getSellerId()).ifPresent(product::setSeller);
    brandRepository.findById(command.getBrandId()).ifPresent(product::setBrand);

    modifyProductDetail(command, product);

    modifyProductPrice(command, product);

    modifyProductCategories(command, product);

    modifyProductTags(command, product);

    return genericMapper.toDto(product, ProductDto.Product.class);
  }

  @Override
  @Transactional
  public void deleteProduct(ProductCommand.DeleteProduct command) {
    productRepository.findById(command.getId()).orElseThrow(
            () -> new ResourceNotFoundException("product.not.found")
    );
    productRepository.deleteById(command.getId());

  }

  @Override
  @Transactional
  public ProductDto.Option addProductOption(ProductCommand.AddProductOption command) {
    ProductOptionGroup optionGroup = productOptionGroupRepository.findById(command.getOptionGroupId())
            .orElseThrow(() -> new ResourceNotFoundException("product.option.group.not.found"));
    if (!optionGroup.getProductId().equals(command.getProductId())) {
      throw new ResourceNotFoundException("product.option.group.not.found");
    }
    ProductOption productOption = genericMapper.toEntity(command, ProductOption.class);
    productOption.setGroup(optionGroup);
    productOptionRepository.save(productOption);
    return genericMapper.toDto(productOption, ProductDto.Option.class);
  }

  @Override
  @Transactional
  public ProductDto.Option updateProductOption(ProductCommand.UpdateProductOption command) {
    ProductOption productOption = productOptionRepository.findById(command.getOptionId()).orElseThrow(
            () -> new ResourceNotFoundException("product.option.not.found")
    );
    if (!productOption.getProductId().equals(command.getProductId())) {
      throw new ResourceNotFoundException("product.option.not.found");
    }

    productOption.setName(command.getName());
    productOption.setAdditionalPrice(command.getAdditionalPrice());
    productOption.setSku(command.getSku());
    productOption.setStock(command.getStock());
    productOption.setDisplayOrder(command.getDisplayOrder());
    productOptionRepository.save(productOption);
    return genericMapper.toDto(productOption, ProductDto.Option.class);
  }

  @Override
  @Transactional
  public void deleteProductOption(ProductCommand.DeleteProductOption command) {
    ProductOption productOption = productOptionRepository.findById(command.getOptionId()).orElseThrow(
            () -> new ResourceNotFoundException("product.option.not.found")
    );
    if (!productOption.getProductId().equals(command.getProductId())) {
      throw new ResourceNotFoundException("product.option.not.found");
    }
    productOptionRepository.delete(productOption);
  }

  @Override
  @Transactional
  public ProductDto.Image addProductImage(ProductCommand.AddProductImage command) {
    Product product = productRepository.findById(command.getProductId()).orElseThrow(
            () -> new ResourceNotFoundException("product.not.found")
    );
    ProductOption productOption = null;
    if (command.getOptionId() != null) {
      productOption = productOptionRepository.findById(command.getOptionId()).orElseThrow(
              () -> new ResourceNotFoundException("product.option.not.found")
      );
    }

    ProductImage productImage = genericMapper.toEntity(command, ProductImage.class);
    productImage.setProduct(product);
    if (productOption != null) {
      productImage.setOption(productOption);
    }

    productImageRepository.save(productImage);
    return genericMapper.toDto(productImage, ProductDto.Image.class);
  }

  private void createProductTags(ProductCommand.CreateProduct command, Product product) {
    List<Long> tagIds = command.getTags();
    if (tagIds == null || tagIds.isEmpty()) {
      return;
    }

    List<Tag> allTags = tagRepository.findAllByIdIn(tagIds);
    allTags.forEach(
            tag -> {
              ProductTag productTag = new ProductTag();
              productTag.setProduct(product);
              productTag.setTag(tag);
              productTagRepository.save(productTag);
            }
    );
  }

  private void createProductImages(ProductCommand.CreateProduct command, Product product) {
    List<ProductImageCreateDto> images = command.getImages();
    if (images == null || images.isEmpty()) {
      return;
    }

    images.forEach(
            imageDto -> {
              ProductImage productImage = genericMapper.toEntity(imageDto, ProductImage.class);
              productImage.setProduct(product);
              productImageRepository.save(productImage);
            }
    );
  }

  private void createProductOptionGroups(ProductCommand.CreateProduct command, Product product) {
    List<ProductOptionGroupCreateDto> optionGroups = command.getOptionGroups();
    if (optionGroups == null || optionGroups.isEmpty()) {
      return;
    }

    optionGroups.forEach(
            optionGroupDto -> {
              ProductOptionGroup productOptionGroup = genericMapper.toEntity(optionGroupDto, ProductOptionGroup.class);
              productOptionGroup.setProduct(product);
              productOptionGroupRepository.save(productOptionGroup);

              List<ProductOptionCreateDto> options = optionGroupDto.getOptions();
              if (options != null) {
                options.forEach(
                        optionDto -> {
                          ProductOption productOption = genericMapper.toEntity(optionDto, ProductOption.class);
                          productOption.setGroup(productOptionGroup);
                          productOptionRepository.save(productOption);
                        }
                );
              }
            }
    );
  }

  private void createProductCategories(ProductCommand.CreateProduct command, Product product) {
    List<ProductCategoryCreateDto> categories = command.getCategories();
    if (categories == null || categories.isEmpty()) {
      return;
    }

    categories.forEach(
            categoryDto -> {
              Category category = categoryRepository.findById(categoryDto.getCategoryId())
                      .orElseThrow(() -> new ResourceNotFoundException("category.not.found"));
              ProductCategory productCategory = new ProductCategory();
              productCategory.setProduct(product);
              productCategory.setCategory(category);
              productCategory.setPrimary(categoryDto.isPrimary());
              productCategoryRepository.save(productCategory);
            }
    );
  }

  private void modifyProductPrice(ProductCommand.UpdateProduct command, Product product) {
    ProductPrice productPrice = productPriceRepository.findByProductId(product.getId()).orElseThrow(
            () -> new ResourceNotFoundException("product.price.not.found")
    );
    ProductPriceCreateDto price = command.getPrice();
    productPrice.setBasePrice(price.getBasePrice());
    productPrice.setSalePrice(price.getSalePrice());
    productPrice.setCostPrice(price.getCostPrice());
    productPrice.setCurrency(price.getCurrency());
    productPrice.setTaxRate(price.getTaxRate());
  }

  private void modifyProductDetail(ProductCommand.UpdateProduct command, Product product) {
    ProductDetail productDetail = productDetailRepository.findByProductId(product.getId()).orElseThrow(
            () -> new ResourceNotFoundException("product.detail.not.found")
    );
    ProductDetailCreateDto detail = command.getDetail();
    productDetail.setWeight(detail.getWeight());
    productDetail.setDimensions(detail.getDimensions());
    productDetail.setMaterials(detail.getMaterials());
    productDetail.setWarrantyInfo(detail.getWarrantyInfo());
    productDetail.setCareInstructions(detail.getCareInstructions());
    productDetail.setCountryOfOrigin(detail.getCountryOfOrigin());
    productDetail.setAdditionalInfo(detail.getAdditionalInfo());
  }

  private void modifyProductTags(ProductCommand.UpdateProduct command, Product product) {
    if (command.getTags() == null || command.getTags().isEmpty()) {
      productTagRepository.deleteByProductId(product.getId());
    } else {
      List<Tag> allTags = tagRepository.findAllByIdIn(command.getTags());
      List<ProductTag> existingTags = productTagRepository.findByProductId(product.getId());
      if (allTags != null) {
        for (Tag tag : allTags) {
          if (existingTags.stream().noneMatch(pt -> pt.getTag().getId().equals(tag.getId()))) {
            ProductTag productTag = new ProductTag();
            productTag.setProduct(product);
            productTag.setTag(tag);
            productTagRepository.save(productTag);
          }
        }
      }
      productTagRepository.deleteByProductIdAndTagNotIn(product.getId(), allTags);
    }
  }

  private void modifyProductCategories(ProductCommand.UpdateProduct command, Product product) {
    List<ProductCategory> productCategories = productCategoryRepository.findByProductId(product.getId());
    List<ProductCategoryCreateDto> categories = command.getCategories();

    if (categories != null) {
      for (ProductCategoryCreateDto categoryDto : categories) {
        Category category = categoryRepository.findById(categoryDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("category.not.found"));
        ProductCategory productCategory = productCategories.stream()
                .filter(pc -> pc.getCategory().getId().equals(category.getId()))
                .findFirst()
                .orElse(null);
        if (productCategory != null) {
          productCategory.setPrimary(categoryDto.isPrimary());
          productCategoryRepository.save(productCategory);
        } else {
          ProductCategory newProductCategory = new ProductCategory();
          newProductCategory.setProduct(product);
          newProductCategory.setCategory(category);
          newProductCategory.setPrimary(categoryDto.isPrimary());
          productCategoryRepository.save(newProductCategory);
        }
      }
    }
  }
}
