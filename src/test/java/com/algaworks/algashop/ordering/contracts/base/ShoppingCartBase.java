package com.algaworks.algashop.ordering.contracts.base;

import com.algaworks.algashop.ordering.application.shoppingcart.ShoppingCartManagementApplicationService;
import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartOutputTestDataBuilder;
import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.presentation.ShoppingCartController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@WebMvcTest(controllers = ShoppingCartController.class)
public class ShoppingCartBase {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private ShoppingCartQueryService shoppingCartQueryService;

    @MockitoBean
    private ShoppingCartManagementApplicationService shoppingCartManagementApplicationService;

    public static final UUID VALID_SHOPPING_CART_ID = UUID.fromString("277297bf-e586-4389-9f21-b3ce0c3f6580");
    public static final UUID NOT_FOUND_SHOPPING_CART_ID = UUID.fromString("d70864cd-671c-4ec2-a3d2-0cc8f5dc55ba");
    public static final UUID CREATED_SHOPPING_CART_ID = UUID.fromString("5c9f8a3e-2b17-4d64-8e9a-1f3d5b7c9a21");

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(
                MockMvcBuilders.webAppContextSetup(context)
                        .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                        .build()
        );
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();

        mockFindById();
        mockCreate();
    }

    private void mockFindById() {
        Mockito.when(shoppingCartQueryService.findById(VALID_SHOPPING_CART_ID))
                .thenReturn(ShoppingCartOutputTestDataBuilder.existing());

        Mockito.when(shoppingCartQueryService.findById(NOT_FOUND_SHOPPING_CART_ID))
                .thenThrow(new ShoppingCartNotFoundException());
    }

    private void mockCreate() {
        Mockito.when(shoppingCartManagementApplicationService.createNew(Mockito.any(UUID.class)))
                .thenReturn(new ShoppingCartId(CREATED_SHOPPING_CART_ID));

        Mockito.when(shoppingCartQueryService.findById(CREATED_SHOPPING_CART_ID))
                .thenReturn(ShoppingCartOutputTestDataBuilder.existing());
    }
}
