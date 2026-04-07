package com.vti.bevtilib.config;

import com.vti.bevtilib.model.CareLevel;
import com.vti.bevtilib.model.Product;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> filterBy(String keyword, Long categoryId, String availability,
                                                   Double minRating, Double priceMin, Double priceMax,
                                                   String careLevel) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            query.distinct(true);

            // Luôn lọc sản phẩm đã bị xóa mềm
            predicates.add(cb.isFalse(root.get("deleted")));

            if (StringUtils.hasText(keyword)) {
                String likePattern = "%" + keyword.toLowerCase() + "%";

                // Tìm trên name và origin (VARCHAR - an toàn với lower/like)
                Predicate namePred = cb.like(cb.lower(root.get("name")), likePattern);
                Predicate originPred = cb.like(cb.lower(root.get("origin")), likePattern);
                Predicate skuPred = cb.like(cb.lower(root.get("sku")), likePattern);

                // Tìm trên description (LONGTEXT) - cast sang String để tránh lỗi Hibernate với @Lob
                Expression<String> descAsString = root.get("description").as(String.class);
                Predicate descPred = cb.like(cb.lower(descAsString), likePattern);

                predicates.add(cb.or(namePred, originPred, skuPred, descPred));
            }

            if (categoryId != null && categoryId > 0) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            if (StringUtils.hasText(availability)) {
                if ("inStock".equalsIgnoreCase(availability)) {
                    predicates.add(cb.greaterThan(root.get("stockQuantity"), 0));
                } else if ("outOfStock".equalsIgnoreCase(availability)) {
                    predicates.add(cb.equal(root.get("stockQuantity"), 0));
                }
            }

            if (minRating != null && minRating > 0) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("averageRating"), minRating));
            }

            if (priceMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), BigDecimal.valueOf(priceMin)));
            }

            if (priceMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), BigDecimal.valueOf(priceMax)));
            }

            if (StringUtils.hasText(careLevel)) {
                try {
                    CareLevel level = CareLevel.valueOf(careLevel.toUpperCase());
                    predicates.add(cb.equal(root.get("careLevel"), level));
                } catch (IllegalArgumentException ignored) {
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
