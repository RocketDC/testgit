package demo;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import resources.AddProductResponse;
import resources.Login;
import resources.LoginResponse;
import resources.Order;
import resources.OrderProductResponse;
import resources.Orders;

public class EcommerceApiTest {

	public static void main(String[] args) {
//		Login
		RequestSpecification req = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
				.setContentType(ContentType.JSON).build();
		Login login = new Login();
		login.setUserEmail("something3@someone.com");
		login.setUserPassword("Qwerty@123");
		RequestSpecification loginRequest = given().log().all().spec(req).body(login);
//		loginRes = new ResponseBuilder()
		LoginResponse loginResponse = loginRequest.when().post("/api/ecom/auth/login").then().extract().response()
				.as(LoginResponse.class);
		System.out.println("Token No ===> " + loginResponse.getToken());
		System.out.println("User Id ==> " + loginResponse.getUserId());

//		Add Product
		RequestSpecification addProductReq = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
				.addHeader("authorization", loginResponse.getToken()).build();
		AddProductResponse addProductResponse = given().log().all().spec(addProductReq).param("productName", "Fishy")
				.param("productAddedBy", loginResponse.getUserId()).param("productCategory", "fashion")
				.param("productSubCategory", "shirts").param("productPrice", "11500")
				.param("productDescription", "Addias Originals").param("productFor", "women")
				.multiPart("productImage", new File("C:\\Users\\diamo\\Postman\\files\\R2.jpg")).when()
				.post("/api/ecom/product/add-product").then().log().all().extract().response()
				.as(AddProductResponse.class);
		System.out.println("Product ID ===> " + addProductResponse.getProductId());

//		Create Order

		RequestSpecification createOrderReq = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
				.addHeader("authorization", loginResponse.getToken()).setContentType(ContentType.JSON).build();
		Order order1 = new Order();
		order1.setCountry("India");
		order1.setProductOrderedId(addProductResponse.getProductId());
		Orders orders = new Orders();
		List<Order> ls = new ArrayList<>();
		orders.setOrders(ls);
		String orderProductResponse = given().log().all().spec(createOrderReq).body(orders).when()
				.post("/api/ecom/order/create-order").then().log().all().extract().response().asString();
		JsonPath js2 = new JsonPath(orderProductResponse);
		System.out.println("OrderID == > " + js2.get("productOrderId"));

//		Delete Product

		
		  RequestSpecification delOrderPre = new
		  RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
		  .addHeader("authorization",
		  loginResponse.getToken()).setContentType(ContentType.JSON) .build();
		  
		  
		  String delResponse =
		  given().log().all().spec(delOrderPre).pathParam("productId",
		  addProductResponse.getProductId())
		  .when().delete("/api/ecom/product/delete-product/{productId}")
		  .then().log().all().extract().response().asString();
		  
		  JsonPath js1 = new JsonPath(delResponse);
		  System.out.println((String)js1.get("message"));
		 

//		Delete Order

	}

}
