package com.afa.devicer.back.mappers;

import com.afa.core.dto.customers.CustomerDto;
import com.afa.core.dto.people.PersonShortDto;
import com.afa.devicer.back.entities.customers.Customer;
import com.afa.devicer.back.utils.PersonHelper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@SuppressWarnings({"PMD.LawOfDemeter"})
public interface CustomerMapper {

    @Mapping(target = "userAdded", expression = "java(getUserAdded(entity))")
    CustomerDto fromCustomer(Customer entity);

    default List<CustomerDto> fromCustomers(
            final List<Customer> entities) {
        return entities.stream()
                .map(this::fromCustomer)
                .toList();
    }

    default PersonShortDto getUserAdded(final Customer entity) {
        return PersonHelper.fromPerson(entity.getUserAdded());
    }
}