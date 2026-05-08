package com.afa.devicer.back.services;

import com.afa.core.dto.UserInfoDto;
import com.afa.core.dto.customers.*;
import com.afa.core.enums.CustomerTypes;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.exceptions.DevicerException;
import com.afa.devicer.back.entities.companies.Company;
import com.afa.devicer.back.entities.companies.Company_;
import com.afa.devicer.back.entities.companies.ICompany;
import com.afa.devicer.back.entities.customers.*;
import com.afa.devicer.back.entities.dictionaries.Address;
import com.afa.devicer.back.entities.dictionaries.Country;
import com.afa.devicer.back.entities.people.IPerson;
import com.afa.devicer.back.entities.people.Person;
import com.afa.devicer.back.entities.people.Person_;
import com.afa.devicer.back.mappers.CustomerMapper;
import com.afa.devicer.back.validators.CustomerValidator;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.CouplingBetweenObjects", "PMD.ExcessiveImports", "PMD.TooManyMethods",
        "PMD.GodClass", "PMD.CyclomaticComplexity", "PMD.NcssCount", "PMD.CognitiveComplexity"})
public class CustomerService {

    private final ICustomer iCustomer;
    private final ICompany iCompany;
    private final IPerson iPerson;
    private final ICustomerAddress iCustomerAddress;
    private final ICustomerContact iCustomerContact;

    private final CustomerValidator validator;
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

    @Transactional(readOnly = true)
    public CustomerSingleResponse getSingleSearchFiltered(final CustomerSearchPagedFilter filter) {

        final List<Customer> customers = iCustomer.findAll(fillCustomerSpecificationBySearchConditions(filter));
        if (customers.isEmpty()) {
            return new CustomerSingleResponse(null);
        }
        final CustomerDto customerDto = mapper.fromCustomer(customers.getFirst());
        return new CustomerSingleResponse(customerDto);
    }

    @Transactional(readOnly = true)
    public CustomerPagedResponse getSearchFiltered(
            final CustomerSearchPagedFilter filter) {

        final Page<Customer> page = iCustomer.findAll(fillCustomerSpecificationBySearchConditions(filter),
                filter.createPageRequest(filter.isSortedByEmpty() ? "id" : filter.getSortedBy(),
                        Customer_.class.getDeclaredFields()));
        final List<CustomerDto> customerDtos = mapper.fromCustomers(page.getContent());
        return new CustomerPagedResponse(
                page.getTotalElements(), page.getTotalPages(),
                page.hasPrevious(), page.hasNext(),
                customerDtos);
    }

    @SuppressWarnings({"PMD.CollapsibleIfStatements"})
    private Specification<Customer> fillCustomerSpecificationBySearchConditions(
            final CustomerSearchPagedFilter filter) {
        return (root, query, builder) -> {

            final List<Predicate> predicates = new ArrayList<>();

            // customer - person
            final CustomerTypes customerType;
            if (filter.getConditions() != null && filter.getConditions().getCustomerTypes() != null) {
                customerType = filter.getConditions().getCustomerTypes()
                        .stream()
                        .findFirst()
                        .orElse(CustomerTypes.PERSON);
            } else {
                customerType = CustomerTypes.PERSON;
            }

            if (customerType == CustomerTypes.PERSON) {
                if (filter.getConditions() != null && StringUtils.isNoneBlank(filter.getConditions().getPersonPhoneNumber())) {
                    predicates.add(builder.isNotNull(root.get(Customer_.PERSON)));
                    predicates.add(builder.equal(root.get(Customer_.PERSON).get(Person_.PHONE_NUMBER),
                            filter.getConditions().getPersonPhoneNumber()
                    ));
                }
            }
            if (customerType == CustomerTypes.COMPANY) {
                if (filter.getConditions() != null && StringUtils.isNoneBlank(filter.getConditions().getCompanyMainContactPhoneNumber())) {
                    predicates.add(builder.isNotNull(root.get(Customer_.COMPANY)));
                    predicates.add(builder.equal(root.get(Customer_.COMPANY).get(Company_.PHONE_NUMBER),
                            filter.getConditions().getCompanyMainContactPhoneNumber()
                    ));
                }
                if (filter.getConditions() != null && StringUtils.isNoneBlank(filter.getConditions().getCompanyMainContactEmail())) {
                    predicates.add(builder.isNotNull(root.get(Customer_.COMPANY)));
                    predicates.add(builder.equal(root.get(Customer_.COMPANY).get(Company_.EMAIL),
                            filter.getConditions().getCompanyMainContactEmail()
                    ));
                }
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }


    @Transactional
    public Customer create(
            final UserInfoDto userInfo,
            final CustomerSaveRequest request) {

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

            person = new Person();
            person.setCountry(country);
            person.setFirstName(request.getPerson().getFirstName());
            person.setMiddleName(request.getPerson().getMiddleName());
            person.setLastName(request.getPerson().getLastName());
            person.setEmail(request.getPerson().getEmail());
            person.setPhoneNumber(request.getPerson().getPhoneNumber());
            person = iPerson.save(person);
        }

        final Customer customer = new Customer();
        customer.setType(request.getType());
        customer.setCompany(company);
        customer.setPerson(person);
        customer.setUserAdded(originator);

        final Customer savedCustomer = iCustomer.save(customer);

        final Set<CustomerContact> contacts = new HashSet<>();
        if (request.isCompany() && request.getContacts() != null) {
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

                CustomerContact customerContact = new CustomerContact();
                customerContact.setType(c.getType());
                customerContact.setCustomer(customer);
                customerContact.setUserAdded(originator);
                customerContact.setPerson(contactPerson);
                customerContact = iCustomerContact.save(customerContact);

                contacts.add(customerContact);
            }
        }
        savedCustomer.setContacts(contacts);

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

                CustomerAddress customerAddress = new CustomerAddress();
                customerAddress.setCustomer(customer);
                customerAddress.setAddress(address);
                customerAddress.setUserAdded(originator);
                customerAddress = iCustomerAddress.save(customerAddress);
                addresses.add(customerAddress);
            }
        }
        savedCustomer.setAddresses(addresses);

        return iCustomer.save(savedCustomer);
    }

    @Transactional
    public Customer edit(
            final UserInfoDto userInfo,
            final Long customerId,
            final CustomerSaveRequest request) {

        final Customer customer = findByIdOrThrow(customerId);
        final Country country = dictionaryCountryService.findByIdOrThrow(request.getCountryId());
        final Person originator = userInfoService.findByIdOrThrow(userInfo.getPersonId());

        if (customer.isCompany()) {
            final Company company = customer.getCompany();
            company.setInn(request.getCompany().getInn());
            company.setShortName(request.getCompany().getShortName());
            company.setLongName(request.getCompany().getLongName());
            company.setEmail(request.getCompany().getEmail());
            company.setPhoneNumber(request.getCompany().getPhoneNumber());
            company.setCountry(country);
        } else if (customer.isPerson()) {
            final Person person = customer.getPerson();
            person.setCountry(country);
            person.setFirstName(request.getPerson().getFirstName());
            person.setMiddleName(request.getPerson().getMiddleName());
            person.setLastName(request.getPerson().getLastName());
        }

        customer.getContacts().clear();
        final Set<CustomerContact> contacts = customer.getContacts();
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

                final CustomerContact customerContact = new CustomerContact();
                customerContact.setType(c.getType());
                customerContact.setCustomer(customer);
                customerContact.setUserAdded(originator);
                customerContact.setPerson(contactPerson);
                contacts.add(customerContact);
            }
        }

        final Set<CustomerAddress> addresses = customer.getAddresses();
        addresses.clear();
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

        customer.setDateModified(Instant.now());
        return customer;
    }
}
