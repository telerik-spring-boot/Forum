package com.telerik.forum.repositories.utilities;

import com.telerik.forum.exceptions.InvalidSortParameterException;
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

    public static void validateSortOrderField(String type){
        if(!type.equalsIgnoreCase("asc") && !type.equalsIgnoreCase("desc")){
            throw new InvalidSortParameterException(type);
        }
    }

    public static void validateSortByFieldPost(String type){
        if(!type.equalsIgnoreCase("title") &&
                !type.equalsIgnoreCase("content") &&
                !type.equalsIgnoreCase("likes")){
            throw new InvalidSortParameterException(type);
        }
    }

    public static void validateSortByFieldUser(String type){
        if(!type.equalsIgnoreCase("firstname") &&
                !type.equalsIgnoreCase("lastname") &&
                !type.equalsIgnoreCase("username")){
            throw new InvalidSortParameterException(type);
        }
    }

    public static void validateSortByFieldComment(String type) {
        if(!type.equalsIgnoreCase("commentContent")){
            throw new InvalidSortParameterException(type);
        }
    }
}
