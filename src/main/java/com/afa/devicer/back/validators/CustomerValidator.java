package com.afa.devicer.back.validators;

import com.afa.core.dto.customers.CustomerSaveRequest;
import com.afa.core.enums.CustomerTypes;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.exceptions.DevicerException;
import com.afa.devicer.back.entities.companies.Company;
import com.afa.devicer.back.entities.companies.ICompany;
import com.afa.devicer.back.entities.people.IPerson;
import com.afa.devicer.back.entities.people.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
public class CustomerValidator {

    private final IPerson iPerson;
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
        if (request.getType() == CustomerTypes.PERSON) {
            final Optional<Person> findingPerson = iPerson.findByPhoneNumber(request.getPerson().getPhoneNumber());
            if (findingPerson.isPresent()) {
                throw new DevicerException(DevicerErrors.CUSTOMER_PERSON_BY_PHONE_EXIST, request.getPerson().getPhoneNumber());
            }
        }
    }

    public void validateCustomerEditing(final Long customerId, final CustomerSaveRequest request) {

    }
}
