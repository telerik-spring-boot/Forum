package com.telerik.forum.repositories.utilities;

import com.telerik.forum.models.filters.Sortable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class SortingHelper {


    public static <Filter extends Sortable, Entity> void sortingHelper(CriteriaBuilder cb, Root<Entity> root, CriteriaQuery<Entity> entityCriteria, Filter options) {
        options.getSortBy().ifPresent(sortBy -> {
            String sortOrder = options.getSortOrder().orElse("asc");

            if (sortOrder.equalsIgnoreCase("desc")) {
                entityCriteria.orderBy(cb.desc(root.get(sortBy)));
            } else {
                entityCriteria.orderBy(cb.asc(root.get(sortBy)));
            }
        });
    }
}
