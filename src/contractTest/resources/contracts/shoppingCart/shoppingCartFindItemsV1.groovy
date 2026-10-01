package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method GET()
        headers {
            accept 'application/json'
        }
        url("/api/v1/shopping-carts/277297bf-e586-4389-9f21-b3ce0c3f6580/items")
    }
    response {
        status 200
        headers {
            contentType 'application/json'
        }
        body([
                items: [
                        [
                                id        : "177297bf-e586-4389-9f21-a3ce0c3f6580",
                                productId : "6a6ca34c-e65c-496e-adaf-f872e1784003",
                                name      : "Desktop Gamer Dive",
                                price     : 20.99,
                                quantity  : 2,
                                totalAmount: 41.98,
                                available : true
                        ]
                ]
        ])
    }
}
