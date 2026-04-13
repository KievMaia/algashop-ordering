package com.algaworks.algashop.ordering.presentation;

import com.algaworks.algashop.ordering.application.commons.AddressData;
import com.algaworks.algashop.ordering.application.customer.management.CustomerInput;
import com.algaworks.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.algaworks.algashop.ordering.application.customer.query.CustomerFilter;
import com.algaworks.algashop.ordering.application.customer.query.CustomerOutput;
import com.algaworks.algashop.ordering.application.customer.query.CustomerOutputTestDataBuilder;
import com.algaworks.algashop.ordering.application.customer.query.CustomerQueryService;
import com.algaworks.algashop.ordering.application.customer.query.CustomerSummaryOutputTestDataBuilder;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@WebMvcTest(CustomerController.class)
class CustomerControllerContractTest {

    @Autowired
    public WebApplicationContext context;

    @MockitoBean
    private CustomerManagementApplicationService customerManagementApplicationService;

    @MockitoBean
    private CustomerQueryService customerQueryService;

    @BeforeEach
    public void setupAll() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build()
        );
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void createCustomerContract() {
        var customerOutput = CustomerOutputTestDataBuilder.existing().build();

        var customerId = customerOutput.getId();

        Mockito.when(customerManagementApplicationService.create(Mockito.any(CustomerInput.class))).thenReturn(customerId);
        Mockito.when(customerQueryService.findById(Mockito.any(UUID.class))).thenReturn(
                customerOutput
        );

        var jsonInput = """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "email": "johndoe@email.com",
                  "document": "12345",
                  "phone": "1191234564",
                  "birthDate": "1987-11-05",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "Bourbon Street",
                    "number": "2000",
                    "complement": "apt 122",
                    "neighborhood": "North Ville",
                    "city": "Yostfort",
                    "state": "South Carolina",
                    "zipCode": "12321"
                  }
                }
                """;

        RestAssuredMockMvc
                .given()
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .body(jsonInput)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .post("/api/v1/customers")
                .then()
                    .assertThat()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .statusCode(HttpStatus.CREATED.value())
                    .header("Location", Matchers.containsString("/api/v1/customers/" + customerId))
                    .body("id", Matchers.notNullValue(),
                            "registeredAt", Matchers.notNullValue(),
                            "firstName", Matchers.is("John"),
                            "lastName", Matchers.is("Doe"),
                            "email", Matchers.is("johndoe@email.com"),
                            "document", Matchers.is("12345"),
                            "phone", Matchers.is("1191234564"),
                            "birthDate", Matchers.is("1987-11-05"),
                            "promotionNotificationsAllowed", Matchers.is(false),
                            "loyaltyPoints", Matchers.is(0),
                            "address.street", Matchers.is("Bourbon Street"),
                            "address.number", Matchers.is("2000"),
                            "address.complement", Matchers.is("apt 122"),
                            "address.neighborhood", Matchers.is("North Ville"),
                            "address.city", Matchers.is("Yostfort"),
                            "address.state", Matchers.is("South Carolina"),
                            "address.zipCode", Matchers.is("12321")
                            );
    }

    @Test
    public void createCustomerErrorContract() {

        var jsonInput = """
                {
                  "firstName": "",
                  "lastName": "",
                  "email": "johndoe@email.com",
                  "document": "12345",
                  "phone": "1191234564",
                  "birthDate": "1987-11-05",
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "Bourbon Street",
                    "number": "2000",
                    "complement": "apt 122",
                    "neighborhood": "North Ville",
                    "city": "Yostfort",
                    "state": "South Carolina",
                    "zipCode": "12321"
                  }
                }
                """;

        RestAssuredMockMvc.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonInput)
                .when()
                    .post("/api/v1/customers")
                .then()
                    .assertThat()
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(
                            "status", Matchers.is(HttpStatus.BAD_REQUEST.value()),
                            "type", Matchers.is("/errors/invalid-fields"),
                            "title", Matchers.notNullValue(),
                            "detail", Matchers.notNullValue(),
                            "instance", Matchers.notNullValue(),
                            "fields", Matchers.notNullValue()
                    );
    }

    @Test
    public void findCustomersContract() {
        int sizeLimit = 5;
        int pageNumber = 0;

        var customer1 = CustomerSummaryOutputTestDataBuilder.existing().build();
        var customer2 = CustomerSummaryOutputTestDataBuilder.existingAlt1().build();

        Mockito.when(customerQueryService.filter(Mockito.any(CustomerFilter.class)))
                .thenReturn(new PageImpl<>(List.of(customer1, customer2)));

        var formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        RestAssuredMockMvc
                .given()
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .queryParam("size", sizeLimit)
                    .queryParam("page", pageNumber)
                .when()
                    .get("/api/v1/customers")
                .then()
                    .assertThat()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .statusCode(HttpStatus.OK.value())
                    .body(
                            "number", Matchers.equalTo(pageNumber),
                            "size", Matchers.equalTo(2),
                            "totalPages", Matchers.equalTo(1),
                            "totalElements", Matchers.equalTo(2),
                            "content[0].id", Matchers.equalTo(customer1.getId().toString()),
                            "content[0].firstName", Matchers.equalTo(customer1.getFirstName()),
                            "content[0].lastName", Matchers.equalTo(customer1.getLastName()),
                            "content[0].email", Matchers.equalTo(customer1.getEmail()),
                            "content[0].document", Matchers.equalTo(customer1.getDocument()),
                            "content[0].phone", Matchers.equalTo(customer1.getPhone()),
                            "content[0].birthDate", Matchers.equalTo(customer1.getBirthDate().toString()),
                            "content[0].loyaltyPoints", Matchers.equalTo(customer1.getLoyaltyPoints()),
                            "content[0].promotionNotificationsAllowed", Matchers.equalTo(customer1.getPromotionNotificationsAllowed()),
                            "content[0].archived", Matchers.is(customer1.getArchived()),
                            "content[0].registeredAt", Matchers.is(formatter.format(customer1.getRegisteredAt())),

                            "content[1].id", Matchers.equalTo(customer2.getId().toString()),
                            "content[1].firstName", Matchers.equalTo(customer2.getFirstName()),
                            "content[1].lastName", Matchers.equalTo(customer2.getLastName()),
                            "content[1].email", Matchers.equalTo(customer2.getEmail()),
                            "content[1].document", Matchers.equalTo(customer2.getDocument()),
                            "content[1].phone", Matchers.equalTo(customer2.getPhone()),
                            "content[1].birthDate", Matchers.equalTo(customer2.getBirthDate().toString()),
                            "content[1].loyaltyPoints", Matchers.equalTo(customer2.getLoyaltyPoints()),
                            "content[1].promotionNotificationsAllowed", Matchers.equalTo(customer2.getPromotionNotificationsAllowed()),
                            "content[1].archived", Matchers.is(customer2.getArchived()),
                            "content[1].registeredAt", Matchers.is(formatter.format(customer2.getRegisteredAt()))
                    );
    }

    @Test
    public void findByIdContract() {
        var customer = CustomerOutputTestDataBuilder.existing().build();
        var address = customer.getAddress();

        var formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        Mockito.when(customerQueryService.findById(customer.getId())).thenReturn(customer);


        RestAssuredMockMvc
                .given()
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .get("/api/v1/customers/{customerId}", customer.getId())
                .then()
                    .assertThat()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .statusCode(HttpStatus.OK.value())
                    .body(
                            "id", Matchers.equalTo(customer.getId().toString()),
                            "firstName", Matchers.equalTo(customer.getFirstName()),
                            "lastName", Matchers.equalTo(customer.getLastName()),
                            "email", Matchers.equalTo(customer.getEmail()),
                            "document", Matchers.equalTo(customer.getDocument()),
                            "phone", Matchers.equalTo(customer.getPhone()),
                            "birthDate", Matchers.equalTo(customer.getBirthDate().toString()),
                            "loyaltyPoints", Matchers.equalTo(customer.getLoyaltyPoints()),
                            "promotionNotificationsAllowed", Matchers.equalTo(customer.getPromotionNotificationsAllowed()),
                            "archived", Matchers.is(customer.getArchived()),
                            "registeredAt", Matchers.is(formatter.format(customer.getRegisteredAt())),
                            "address.street", Matchers.is(address.getStreet()),
                            "address.number", Matchers.is(address.getNumber()),
                            "address.complement", Matchers.is(address.getComplement()),
                            "address.neighborhood", Matchers.is(address.getNeighborhood()),
                            "address.city", Matchers.is(address.getCity()),
                            "address.state", Matchers.is(address.getState()),
                            "address.zipCode", Matchers.is(address.getZipCode())
                    );
    }
}