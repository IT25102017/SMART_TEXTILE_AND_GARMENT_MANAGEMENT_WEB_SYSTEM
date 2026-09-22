package com.lankatex.smarttextile.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "material_categories",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "category_name")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class MaterialCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @NotBlank(message = "Category name is required")
    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(name = "description")
    private String description;

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";


    // User-friendly category code for display purposes.
    // This value is not stored as a separate database column.
    @Transient
    public String getCategoryCode() {

        if (categoryId == null) {
            return "CAT-NEW";
        }

        return String.format(
                "CAT-%03d",
                categoryId
        );
    }
}