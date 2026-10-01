package com.algaworks.algashop.ordering.presentation;

import com.algaworks.algashop.ordering.application.shoppingcart.ShoppingCartInput;
import com.algaworks.algashop.ordering.application.shoppingcart.ShoppingCartItemInput;
import com.algaworks.algashop.ordering.application.shoppingcart.ShoppingCartManagementApplicationService;
import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartItemListModel;
import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartOutput;
import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-carts")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartQueryService shoppingCartQueryService;
    private final ShoppingCartManagementApplicationService shoppingCartManagementApplicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartOutput create(@RequestBody @Valid ShoppingCartInput input) {
        var shoppingCartId = shoppingCartManagementApplicationService.createNew(input.getCustomerId());
        return shoppingCartQueryService.findById(shoppingCartId.value());
    }

    @GetMapping("/{shoppingCartId}")
    public ShoppingCartOutput findById(@PathVariable UUID shoppingCartId) {
        return shoppingCartQueryService.findById(shoppingCartId);
    }

    @GetMapping("/{shoppingCartId}/items")
    public ShoppingCartItemListModel findItems(@PathVariable UUID shoppingCartId) {
        var shoppingCart = shoppingCartQueryService.findById(shoppingCartId);

        var model = new ShoppingCartItemListModel();
        model.setItems(shoppingCart.getItems());
        return model;
    }

    @DeleteMapping("/{shoppingCartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID shoppingCartId) {
        shoppingCartManagementApplicationService.delete(shoppingCartId);
    }

    @DeleteMapping("/{shoppingCartId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void empty(@PathVariable UUID shoppingCartId) {
        shoppingCartManagementApplicationService.empty(shoppingCartId);
    }

    @PostMapping("/{shoppingCartId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addItem(@PathVariable UUID shoppingCartId, @RequestBody @Valid ShoppingCartItemInput input) {
        input.setShoppingCartId(shoppingCartId);
        shoppingCartManagementApplicationService.addItem(input);
    }

    @DeleteMapping("/{shoppingCartId}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable UUID shoppingCartId, @PathVariable UUID itemId) {
        shoppingCartManagementApplicationService.removeItem(shoppingCartId, itemId);
    }
}
