Description
===========

This sample illustrates a RESTful service that comprises of three resources and how you can build the business logic for each resource.

- ecommerceservice
    /products
    /orders
    /customers



Note:
ToDo: Once we have the support for service and resource level annotations, this sample has to be further improve with to reflect all the RESTful service concepts.




How to run this sample
======================
bin$ ./ballerina service ../samples/restfulService/ecommerceService.bal

This starts the EcommerceService as well as the other backend services required to run the sample.



Invoking the service
====================


curl -X GET "http://localhost:9090/ecommerceservice/products/123456"

curl -X POST -d '{ "Product": { "ID": "123456", "Name": "XYZ", "Description": "Sample product."}}'  "http://localhost:9090/ecommerceservice/products"



curl -X GET "http://localhost:9090/ecommerceservice/orders/123456"

curl -X POST -d '{ "Order": { "ID": "111999", "Name": "XYZ", "Description": "Sample order."}}'  "http://localhost:9090/ecommerceservice/orders"


curl -X GET "http://localhost:9090/ecommerceservice/customers/987654"

curl -X POST -d '{"Customer": {"ID": "987654", "Name": "ABC PQR","Description": "Sample Customer."}}'  "http://localhost:9090/ecommerceservice/customers"
