package com.afa.devicer.back.mappers;

import com.afa.core.dto.customers.CustomerDto;
import com.afa.core.dto.persons.PersonShortDto;
import com.afa.devicer.back.entities.customers.Customer;
import com.afa.devicer.back.utils.persons.PersonHelper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@SuppressWarnings({"PMD.LawOfDemeter"})
public interface CustomerMapper {

    @Mapping(target = "userAdded", expression = "java(getUserAdded(customer))")
    CustomerDto fromCustomer(Customer customer);

    default List<CustomerDto> fromCustomers(
            final List<Customer> customers) {
        return customers.stream()
                .map(this::fromCustomer)
                .toList();
    }

    default PersonShortDto getUserAdded(final Customer customer) {
        return PersonHelper.fromPerson(customer.getUserAdded());
    }
}