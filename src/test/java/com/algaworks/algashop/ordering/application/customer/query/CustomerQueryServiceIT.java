package com.algaworks.algashop.ordering.application.customer.query;

import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CustomerQueryServiceIT {

    @Autowired
    private CustomerQueryService customerQueryService;

    @Autowired
    private Customers customers;

    @Test
    public void shouldFindByPage() {
        var customer = CustomerTestDataBuilder.existingCustomer().build();
        var customer1 = CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).build();
        var customer2 = CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).build();
        var customer3 = CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).build();
        var customer4 = CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).build();
        customers.add(customer);
        customers.add(customer1);
        customers.add(customer2);
        customers.add(customer3);
        customers.add(customer4);

        var page = customerQueryService.filter(new CustomerFilter(3, 0));

        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(5);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(3);
    }

    @Test
    public void shouldFindByFirstName() {
        var customer = CustomerTestDataBuilder.existingCustomer().build();
        var customer1 = CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).fullName(new FullName("Kiev", "Maia")).build();
        customers.add(customer);
        customers.add(customer1);

        var filter = new CustomerFilter();
        filter.setFirstName("Kiev");

        var page = customerQueryService.filter(filter);
        var customerSummaryOutput = page.getContent().getFirst();

        Assertions.assertThat(customerSummaryOutput.getFirstName()).isEqualTo(customer1.fullName().firstName());
        Assertions.assertThat(page.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(1);
    }

    @Test
    public void shouldFindByEmail() {
        var customer = CustomerTestDataBuilder.existingCustomer().build();
        var customer1 = CustomerTestDataBuilder.existingCustomer()
                .id(new CustomerId())
                .fullName(new FullName("Kiev", "Maia"))
                .email(new Email("kievmaia@gmail.com"))
                .build();
        customers.add(customer);
        customers.add(customer1);

        var filter = new CustomerFilter();
        filter.setEmail("kievmaia@gmail.com");

        var page = customerQueryService.filter(filter);
        var customerSummaryOutput = page.getContent().getFirst();

        Assertions.assertThat(customerSummaryOutput.getFirstName()).isEqualTo(customer1.fullName().firstName());
        Assertions.assertThat(customerSummaryOutput.getLastName()).isEqualTo(customer1.fullName().lastName());
        Assertions.assertThat(customerSummaryOutput.getEmail()).isEqualTo(customer1.email().value());
        Assertions.assertThat(page.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(1);
    }

    @Test
    public void givenInvalidName_whenFilter_shouldReturnEmptyPage() {
        var customer = CustomerTestDataBuilder.existingCustomer().build();
        var customer1 = CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).fullName(new FullName("Kiev", "Maia")).build();
        customers.add(customer);
        customers.add(customer1);

        var filter = new CustomerFilter();
        filter.setFirstName("Oreia seca");

        var page = customerQueryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(0);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(0);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(0);
    }

    @Test
    public void shouldFilterByEmailAndFirstName() {
        var target = CustomerTestDataBuilder.existingCustomer()
                .id(new CustomerId())
                .fullName(new FullName("Kiev", "Maia"))
                .email(new Email("kiev@gmail.com"))
                .build();

        var wrongName = CustomerTestDataBuilder.existingCustomer()
                .id(new CustomerId())
                .fullName(new FullName("Outro", "Nome"))
                .email(new Email("kiev@gmail.com"))
                .build();

        var wrongEmail = CustomerTestDataBuilder.existingCustomer()
                .id(new CustomerId())
                .fullName(new FullName("Kiev", "Maia"))
                .email(new Email("outro@gmail.com"))
                .build();

        customers.add(target);
        customers.add(wrongName);
        customers.add(wrongEmail);

        var filter = new CustomerFilter();
        filter.setFirstName("Kiev");
        filter.setEmail("kiev@gmail.com");

        var page = customerQueryService.filter(filter);

        Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(page.getContent().getFirst().getEmail())
                .isEqualTo("kiev@gmail.com");
    }

    @Test
    public void shouldPaginateCorrectlyAcrossPages() {
        for (int i = 0; i < 5; i++) {
            customers.add(CustomerTestDataBuilder.existingCustomer()
                    .id(new CustomerId())
                    .build());
        }

        var firstPage = customerQueryService.filter(new CustomerFilter(2, 0));
        var secondPage = customerQueryService.filter(new CustomerFilter(2, 1));

        Assertions.assertThat(firstPage.getNumberOfElements()).isEqualTo(2);
        Assertions.assertThat(secondPage.getNumberOfElements()).isEqualTo(2);
    }

    @Test
    public void shouldSortByFirstNameAsc() {
        var a = CustomerTestDataBuilder.existingCustomer()
                .id(new CustomerId())
                .fullName(new FullName("Ana", "Silva"))
                .build();

        var b = CustomerTestDataBuilder.existingCustomer()
                .id(new CustomerId())
                .fullName(new FullName("Bruno", "Souza"))
                .build();

        customers.add(b);
        customers.add(a);

        var filter = new CustomerFilter();
        filter.setSortByProperty(CustomerFilter.SortType.FIRST_NAME);
        filter.setSortDirection(Sort.Direction.ASC);

        var page = customerQueryService.filter(filter);

        Assertions.assertThat(page.getContent().get(0).getFirstName()).isEqualTo("Ana");
    }

    @Test
    public void shouldSortByFirstNameDesc() {
        var a = CustomerTestDataBuilder.existingCustomer()
                .id(new CustomerId())
                .fullName(new FullName("Ana", "Silva"))
                .build();

        var b = CustomerTestDataBuilder.existingCustomer()
                .id(new CustomerId())
                .fullName(new FullName("Bruno", "Souza"))
                .build();

        customers.add(a);
        customers.add(b);

        var filter = new CustomerFilter();
        filter.setSortByProperty(CustomerFilter.SortType.FIRST_NAME);
        filter.setSortDirection(Sort.Direction.DESC);

        var page = customerQueryService.filter(filter);

        Assertions.assertThat(page.getContent().get(0).getFirstName()).isEqualTo("Bruno");
    }

    @Test
    public void shouldSortByEmailAsc() {
        var a = CustomerTestDataBuilder.existingCustomer()
                .id(new CustomerId())
                .email(new Email("a@email.com"))
                .build();

        var b = CustomerTestDataBuilder.existingCustomer()
                .id(new CustomerId())
                .email(new Email("b@email.com"))
                .build();

        customers.add(b);
        customers.add(a);

        var filter = new CustomerFilter();
        filter.setSortByProperty(CustomerFilter.SortType.EMAIL);
        filter.setSortDirection(Sort.Direction.ASC);

        var page = customerQueryService.filter(filter);

        Assertions.assertThat(page.getContent().get(0).getEmail()).isEqualTo("a@email.com");
    }

    @Test
    public void shouldReturnEmptyPageWhenNoMatch() {
        customers.add(CustomerTestDataBuilder.existingCustomer().build());

        var filter = new CustomerFilter();
        filter.setEmail("naoexiste@email.com");

        var page = customerQueryService.filter(filter);

        Assertions.assertThat(page.getTotalElements()).isZero();
        Assertions.assertThat(page.getContent()).isEmpty();
    }
}