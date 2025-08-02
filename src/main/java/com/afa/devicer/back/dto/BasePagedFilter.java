package com.afa.devicer.back.dto;

import com.afa.devicer.back.enums.DevicerErrors;
import com.afa.devicer.back.exceptions.DevicerException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasePagedFilter {
    private final static int MAX_RESULTS_ON_PAGE = 100;
    private final static String SORTED_ASC = "asc";
    private final static String SORTED_DESC = "desc";

    @Schema(description = "results on page", example = "10")
    private Integer resultsOnPage = 10;

    @Schema(description = "result's page number", example = "3")
    private Integer pageNumber = 1;

    @Schema(description = "sort results by desc if true")
    private boolean reverseOrder;

    @Schema(description = "list sorted fields", example = "tsCreated asc, name desc, status asc")
    private String sortedBy;

    public void setSortedBy(final String sortedBy) {
        this.sortedBy = sortedBy.trim().replaceAll(" +", " ");
    }

    @JsonIgnore
    public boolean isSortedByEmpty() {
        return StringUtils.isEmpty(sortedBy);
    }

    /**
     * Create {@link PageRequest} request from filter properties
     * @return {@link PageRequest} request
     */
    public PageRequest createPageRequest(final String... orders) {
        final Sort sort = orders == null || orders.length == 0
                ? Sort.unsorted()
                : Sort.by(orders);
        return PageRequest.of(Math.max(pageNumber - 1, 0),
                resultsOnPage,
                reverseOrder ? sort.descending() : sort.ascending());
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.UseVarargs"})
    public PageRequest createPageRequest(
            final String orderContext,
            final Field[] fields) {

        final Sort sort;
        if (StringUtils.isEmpty(orderContext)) {
            sort = Sort.unsorted();
        } else {
            final String[] stringOrders = orderContext.split(",");
            final Sort.Order[] sortOrders = new Sort.Order[stringOrders.length];
            for (int i = 0; i < stringOrders.length; i++) {

                final String[] sortOrder = stringOrders[i].trim().split(" ");

                final String fieldName = sortOrder[0].trim();
                if (!validateSortFields(fieldName, fields)) {
                    throw new DevicerException(DevicerErrors.DB_FIELD_SORT_NOT_FOUND, fieldName);
                }
                final String ascDesc = sortOrder.length > 1 ? sortOrder[1].trim().toLowerCase() : SORTED_ASC;

                switch (ascDesc) {
                    case SORTED_ASC:
                        sortOrders[i] = Sort.Order.asc(fieldName);
                        break;
                    case SORTED_DESC:
                        sortOrders[i] = Sort.Order.desc(fieldName);
                        break;
                    default: {
                        throw new DevicerException(DevicerErrors.DB_FIELD_ASC_DESC_SORT_NOT_FOUND, ascDesc);
                    }
                }
            }
            sort = Sort.by(sortOrders);
        }

        return PageRequest.of(Math.max(pageNumber - 1, 0),
                resultsOnPage,
                sort);
    }

    /**
     * Create {@link PageRequest} request from filter properties
     * @return {@link PageRequest} request
     */
    public PageRequest createPageRequest(final List<Sort.Order> orders) {
        final Sort sort = (orders == null || orders.isEmpty())
                ? Sort.unsorted()
                : Sort.by(orders);
        return PageRequest.of(Math.max(pageNumber - 1, 0),
                resultsOnPage,
                sort);
    }

    @SuppressWarnings("PMD.UseVarargs")
    private boolean validateSortFields(
            final String stringField,
            final Field[] fields) {

        for (final Field field : fields) {
            if (stringField.split("\\.").length > 1
                    && stringField.split("\\.")[0].equalsIgnoreCase(field.getName())) {
                return true;
            }
            if (field.getName().equals(stringField)) {
                return true;
            }
        }
        return false;
    }
}
