package com.mkhwang.wantedcqrs.product.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

public class ProductDto {
  @Setter
  @Getter
  public static class Product {

    private Long id;
    private String name;
    private String slug;
    private Instant createdAt;
    private Instant updatedAt;
  }

  @Setter
  @Getter
  public static class Option {

    private Long id;
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

  @Setter
  @Getter
  public static class Image {

    private Long id;
    @NotEmpty
    private String url;
    @JsonProperty("alt_text")
    private String altText;
    @JsonProperty("is_primary")
    private boolean primary;
    @JsonProperty("display_order")
    private int displayOrder;
    @JsonProperty("option_id")
    private Integer optionId;
  }
}
