package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method DELETE()
        headers {
            accept 'application/json'
        }
        url("/api/v1/shopping-carts/277297bf-e586-4389-9f21-b3ce0c3f6580")
    }
    response {
        status 204
    }
}
