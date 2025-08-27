package com.afa.devicer.back.services;

import com.afa.core.dto.UserInfoDto;
import com.afa.core.dto.customers.*;
import com.afa.devicer.back.entities.companies.Company;
import com.afa.devicer.back.entities.companies.ICompany;
import com.afa.devicer.back.entities.customers.*;
import com.afa.devicer.back.entities.dictionaries.Address;
import com.afa.devicer.back.entities.dictionaries.Country;
import com.afa.devicer.back.entities.people.Person;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.exceptions.DevicerException;
import com.afa.devicer.back.mappers.CustomerMapper;
import com.afa.devicer.back.validators.CustomerServiceValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.CouplingBetweenObjects", "PMD.ExcessiveImports", "PMD.TooManyMethods",
        "PMD.GodClass", "PMD.CyclomaticComplexity"})
public class CustomerService {

    private final ICustomer iCustomer;
    private final ICompany iCompany;

    private final CustomerServiceValidator validator;
    private final CustomerMapper mapper;

    private final UserInfoService userInfoService;
    private final DictionaryCountryService dictionaryCountryService;
    private final PersonService personService;
    private final AddressService addressService;

    public Optional<Customer> findByIdOptional(final Long id) {
        return iCustomer.findById(id);
    }

    public Customer findByIdOrThrow(final Long id) {
        return findByIdOptional(id).orElseThrow(() ->
                new DevicerException(DevicerErrors.DB_ENTITY_NOT_FOUND, Customer_.class_, id)
        );
    }

    public CustomerSingleResponse getCustomer(
            @NotNull final UserInfoDto user,
            @NotNull final Long customerId) {

        final Customer customer = findByIdOrThrow(customerId);
        final CustomerDto result = mapper.fromCustomer(customer);
        return new CustomerSingleResponse(result);
    }

    @Transactional
    public Customer create(final UserInfoDto userInfo, final CustomerSaveRequest request) {

        validator.validateCustomerCreating(request);

        final Person originator = userInfoService.findByIdOrThrow(userInfo.getPersonId());
        final Country country = dictionaryCountryService.findByIdOrThrow(request.getCountryId());

        Company company = null;
        Person person = null;
        if (request.getCompany() != null) {

            company = Company.builder()
                    .inn(request.getCompany().getInn())
                    .shortName(request.getCompany().getShortName())
                    .longName(request.getCompany().getLongName())
                    .email(request.getCompany().getEmail())
                    .phoneNumber(request.getCompany().getPhoneNumber())
                    .country(country)
                    .build();
            company = iCompany.save(company);

        } else if (request.getPerson() != null) {

            person = Person.builder()
                    .firstName(request.getPerson().getFirstName())
                    .middleName(request.getPerson().getMiddleName())
                    .lastName(request.getPerson().getLastName())
                    .country(country)
                    .build();
        }

        final Customer customer = new Customer();
        customer.setType(request.getType());
        customer.setCompany(company);
        customer.setPerson(person);
        customer.setUserAdded(originator);

        final Set<CustomerContact> contacts = new HashSet<>();
        if (request.getContacts() != null) {
            for (final CustomerContactSaveRequest c : request.getContacts()) {

                Person contact = personService.findByPhoneNumber(c.getPerson().getPhoneNumber());
                if (contact == null) {
                    contact = personService.create(Person.builder()
                            .country(country)
                            .firstName(c.getPerson().getFirstName())
                            .middleName(c.getPerson().getMiddleName())
                            .lastName(c.getPerson().getLastName())
                            .phoneNumber(c.getPerson().getPhoneNumber())
                            .email(c.getPerson().getEmail())
                            .build());
                }
                contacts.add(CustomerContact.builder()
                        .type(c.getType())
                        .customer(customer)
                        .userAdded(originator)
                        .person(contact)
                        .build());
            }
        }

        final Set<CustomerAddress> addresses = new HashSet<>();
        if (request.getAddresses() != null) {

            for (final CustomerAddressSaveRequest a : request.getAddresses()) {

                final Address address = addressService.create(Address.builder()
                        .country(country)
                        .type(a.getAddress().getType())
                        .postCode(a.getAddress().getPostCode())
                        .street(a.getAddress().getStreet())
                        .house(a.getAddress().getHouse())
                        .flat(a.getAddress().getFlat())
                        .addressLine(a.getAddress().getAddressLine())
                        .userAdded(originator)
                        .build());
                addresses.add(CustomerAddress.builder()
                        .customer(customer)
                        .address(address)
                        .userAdded(originator)
                        .build());
            }
        }

        customer.setContacts(contacts);
        customer.setAddresses(addresses);
        return iCustomer.save(customer);
    }
}
