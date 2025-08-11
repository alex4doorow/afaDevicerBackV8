package com.afa.devicer.back.validators;

import com.afa.devicer.back.dto.customers.CustomerSaveRequest;
import com.afa.devicer.back.entities.companies.Company;
import com.afa.devicer.back.entities.companies.ICompany;
import com.afa.devicer.back.enums.CustomerTypes;
import com.afa.devicer.back.enums.DevicerErrors;
import com.afa.devicer.back.exceptions.DevicerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.CyclomaticComplexity"})
public class CustomerServiceValidator {

    private final ICompany iCompany;

    public void validateCustomerCreating(final CustomerSaveRequest request) {

        if (request.getType() == CustomerTypes.COMPANY && request.getCompany() == null) {
            throw new DevicerException(DevicerErrors.CUSTOMER_COMPANY_TYPE_FORBIDDEN, request.getType());
        } else if (request.getType() == CustomerTypes.PERSON && request.getPerson() == null) {
            throw new DevicerException(DevicerErrors.CUSTOMER_COMPANY_TYPE_FORBIDDEN, request.getType());
        }

        if (request.getType() == CustomerTypes.COMPANY && request.getPerson() != null) {
            throw new DevicerException(DevicerErrors.CUSTOMER_PERSON_TYPE_MUST_NULL, request.getType());
        } else if (request.getType() == CustomerTypes.PERSON && request.getCompany() != null) {
            throw new DevicerException(DevicerErrors.CUSTOMER_COMPANY_TYPE_MUST_NULL, request.getType());
        }

        if (request.getType() == CustomerTypes.COMPANY) {
            final Optional<Company> optionalCompany = iCompany.findAllByInn(request.getCompany().getInn());
            if (optionalCompany.isPresent()) {
                throw new DevicerException(DevicerErrors.CUSTOMER_COMPANY_BY_INN_EXIST, request.getCompany().getInn());            }
        }
    }
}
