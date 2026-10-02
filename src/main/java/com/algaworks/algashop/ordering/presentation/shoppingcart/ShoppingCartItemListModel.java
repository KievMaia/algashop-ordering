package com.algaworks.algashop.ordering.presentation.shoppingcart;

import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartItemOutput;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ShoppingCartItemListModel {
    private List<ShoppingCartItemOutput> items = new ArrayList<>();
}
