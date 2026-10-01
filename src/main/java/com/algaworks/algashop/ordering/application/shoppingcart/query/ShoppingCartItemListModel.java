package com.algaworks.algashop.ordering.application.shoppingcart.query;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ShoppingCartItemListModel {
    private List<ShoppingCartItemOutput> items = new ArrayList<>();
}
