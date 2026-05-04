package com.afa.devicer.back.services;

import com.afa.core.dto.UserInfoDto;
import com.afa.core.dto.customers.*;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.exceptions.DevicerException;
import com.afa.devicer.back.entities.companies.Company;
import com.afa.devicer.back.entities.companies.ICompany;
import com.afa.devicer.back.entities.customers.*;
import com.afa.devicer.back.entities.dictionaries.Address;
import com.afa.devicer.back.entities.dictionaries.Country;
import com.afa.devicer.back.entities.people.Person;
import com.afa.devicer.back.mappers.CustomerMapper;
import com.afa.devicer.back.validators.CustomerServiceValidator;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.CouplingBetweenObjects", "PMD.ExcessiveImports", "PMD.TooManyMethods",
        "PMD.GodClass", "PMD.CyclomaticComplexity", "PMD.NcssCount"})
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

    @Transactional(readOnly = true)
    public Optional<Company> findByInnOrOptional(final String inn) {
        return iCompany.findAllByInn(inn);
    }

    @Transactional(readOnly = true)
    public Customer findByIdOrThrow(final Long id) {
        return findByIdOptional(id).orElseThrow(() ->
                new DevicerException(DevicerErrors.DB_ENTITY_NOT_FOUND, Customer_.class_, id)
        );
    }

    @Transactional(readOnly = true)
    public CustomerSingleResponse getCustomer(
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

            final Optional<Company> findingCompany = findByInnOrOptional(request.getCompany().getInn());
            company = findingCompany.orElseGet(Company::new);

            company.setInn(request.getCompany().getInn());
            company.setShortName(request.getCompany().getShortName());
            company.setLongName(request.getCompany().getLongName());
            company.setEmail(request.getCompany().getEmail());
            company.setPhoneNumber(request.getCompany().getPhoneNumber());
            company.setCountry(country);
            company = iCompany.save(company);

        } else if (request.getPerson() != null) {
            final Optional<Person> findingPerson = personService.findByPhoneNumberOptional(request.getPerson().getPhoneNumber());
            person = findingPerson.orElseGet(Person::new);
            person.setCountry(country);
            person.setFirstName(request.getPerson().getFirstName());
            person.setMiddleName(request.getPerson().getMiddleName());
            person.setLastName(request.getPerson().getLastName());
        }

        final Customer customer = new Customer();
        customer.setType(request.getType());
        customer.setCompany(company);
        customer.setPerson(person);
        customer.setUserAdded(originator);

        final Set<CustomerContact> contacts = new HashSet<>();
        if (request.getContacts() != null) {
            for (final CustomerContactSaveRequest c : request.getContacts()) {

                final Optional<Person> findingPerson = personService.findByPhoneNumberOptional(c.getPerson().getPhoneNumber());
                Person contactPerson = findingPerson.orElseGet(Person::new);

                contactPerson.setCountry(country);
                contactPerson.setFirstName(c.getPerson().getFirstName());
                contactPerson.setMiddleName(c.getPerson().getMiddleName());
                contactPerson.setLastName(c.getPerson().getLastName());
                contactPerson.setPhoneNumber(c.getPerson().getPhoneNumber());
                contactPerson.setEmail(c.getPerson().getEmail());
                contactPerson = personService.create(contactPerson);

                contacts.add(CustomerContact.builder()
                        .type(c.getType())
                        .customer(customer)
                        .userAdded(originator)
                        .person(contactPerson)
                        .build());
            }
        }

        final Set<CustomerAddress> addresses = new HashSet<>();
        if (request.getAddresses() != null) {

            for (final CustomerAddressSaveRequest a : request.getAddresses()) {

                Address address = new Address();
                address.setCountry(country);
                address.setType(a.getAddress().getType());
                address.setPostCode(a.getAddress().getPostCode());
                address.setStreet(a.getAddress().getStreet());
                address.setHouse(a.getAddress().getHouse());
                address.setFlat(a.getAddress().getFlat());
                address.setAddressLine(a.getAddress().getAddressLine());
                address.setUserAdded(originator);
                address = addressService.create(address);

                final CustomerAddress customerAddress = new CustomerAddress();
                customerAddress.setCustomer(customer);
                customerAddress.setAddress(address);
                customerAddress.setUserAdded(originator);
                addresses.add(customerAddress);
            }
        }

        customer.setContacts(contacts);
        customer.setAddresses(addresses);
        return iCustomer.save(customer);
    }
}
