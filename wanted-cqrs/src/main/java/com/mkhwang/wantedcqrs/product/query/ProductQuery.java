package com.mkhwang.wantedcqrs.product.query;

import com.mkhwang.wantedcqrs.common.dto.PageRequestDto;
import com.mkhwang.wantedcqrs.product.domain.ProductStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

public class ProductQuery {
  @Data
  @Builder
  public static class GetProduct {
    private Long productId;
  }

  @Data
  @Builder
  public static class ListProducts {
    private ProductStatus status;
    private Integer minPrice;
    private Integer maxPrice;
    private List<Long> category;
    private Long seller;
    private Long brand;
    private Boolean inStock;
    private String search;

    private LocalDate createdFrom;
    private LocalDate createdTo;

    private PageRequestDto pagination;
  }
}
