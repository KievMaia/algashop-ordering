package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method GET()
        headers {
            accept 'application/json'
        }
        url("/api/v1/shopping-carts/d70864cd-671c-4ec2-a3d2-0cc8f5dc55ba")
    }
    response {
        status 404
        headers {
            contentType 'application/problem+json'
        }
        body([
                instance: fromRequest().path(),
                type    : "/errors/not-found",
                title   : "Not found"
        ])
    }
}
