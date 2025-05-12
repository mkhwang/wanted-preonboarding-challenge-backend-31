package com.mkhwang.wantedcqrs.product.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mkhwang.wantedcqrs.product.domain.ProductStatus;
import com.mkhwang.wantedcqrs.product.domain.dto.product.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

public class ProductCommand {

  @Data
  @Builder
  public static class CreateProduct {
    @NotEmpty
    private String name;
    @NotEmpty
    private String slug;
    @JsonProperty("short_description")
    private String shortDescription;
    @JsonProperty("full_description")
    private String fullDescription;
    @NotNull
    @JsonProperty("seller_id")
    private Long sellerId;
    @NotNull
    @JsonProperty("brand_id")
    private Long brandId;

    private ProductStatus status = ProductStatus.ACTIVE;

    private ProductDetailCreateDto detail;
    private ProductPriceCreateDto price;
    private List<ProductCategoryCreateDto> categories;
    @JsonProperty("option_groups")
    private List<ProductOptionGroupCreateDto> optionGroups;
    private List<ProductImageCreateDto> images;
    private List<Long> tags;
  }

  @Data
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true, value = {"id"})
  public static class UpdateProduct {
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String slug;
    @JsonProperty("short_description")
    private String shortDescription;
    @JsonProperty("full_description")
    private String fullDescription;
    @NotNull
    @JsonProperty("seller_id")
    private Long sellerId;
    @NotNull
    @JsonProperty("brand_id")
    private Long brandId;

    private ProductStatus status = ProductStatus.ACTIVE;

    private ProductDetailCreateDto detail;
    private ProductPriceCreateDto price;
    private List<ProductCategoryCreateDto> categories;
    private List<Long> tags;
  }

  @Data
  @Builder
  public static class DeleteProduct {
    private Long id;
  }

  @Data
  @Builder
  public static class AddProductOption {
    private Long productId;
    @JsonProperty("option_group_id")
    private Long optionGroupId;
    private String name;
    @JsonProperty("additional_price")
    private BigDecimal additionalPrice;
    private String sku;
    private Integer stock;
    @JsonProperty("display_order")
    private int displayOrder;
  }

  @Data
  @Builder
  public static class UpdateProductOption {
    private Long productId;
    private Long optionId;
    private String name;
    @JsonProperty("additional_price")
    private BigDecimal additionalPrice;
    private String sku;
    private Integer stock;
    @JsonProperty("display_order")
    private int displayOrder;
  }

  @Data
  @Builder
  public static class DeleteProductOption {
    private Long productId;
    private Long optionId;
  }

  @Data
  @Builder
  public static class AddProductImage {
    private Long productId;
    @NotEmpty
    private String url;
    @JsonProperty("alt_text")
    private String altText;
    @JsonProperty("is_primary")
    private boolean primary;
    @Min(1)
    @JsonProperty("display_order")
    private int displayOrder;
    @JsonProperty("option_id")
    private Long optionId;
  }
}
