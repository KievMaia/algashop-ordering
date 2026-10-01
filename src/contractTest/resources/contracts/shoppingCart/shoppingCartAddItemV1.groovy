package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method POST()
        headers {
            accept 'application/json'
            contentType 'application/json'
        }
        urlPath("/api/v1/shopping-carts/277297bf-e586-4389-9f21-b3ce0c3f6580/items")
        body([
                productId: value(
                        test("6a6ca34c-e65c-496e-adaf-f872e1784003"),
                        stub(anyUuid())
                ),
                quantity : value(
                        test(2),
                        stub(anyNumber())
                )
        ])
    }
    response {
        status 204
    }
}
