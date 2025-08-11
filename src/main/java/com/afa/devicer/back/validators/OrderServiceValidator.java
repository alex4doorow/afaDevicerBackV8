package com.afa.devicer.back.validators;

import com.afa.devicer.back.dto.orders.OrderSaveRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.CouplingBetweenObjects", "PMD.ExcessiveImports", "PMD.TooManyMethods",
        "PMD.GodClass", "PMD.CyclomaticComplexity"})
public class OrderServiceValidator {

//    private final IEmployee iEmployee;

    // check status = for project draft
    public void validateOrderCreating(final OrderSaveRequest request) {
//        if (project.getStatus() != VacancyProjectStatuses.DRAFT) {
//            throw new RedPlanException(RedPlanErrors.VACANCY_PROJECT_ACTION_PROHIBITED, project.getId(), project.getStatus());
//        }
//        validateVacancyRates(request);
    }

}