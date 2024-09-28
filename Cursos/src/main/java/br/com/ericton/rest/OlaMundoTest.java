package br.com.ericton.rest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;


public class OlaMundoTest {

    @Test
    public void testOlaMundo() {
        Response response = RestAssured.request(Method.GET, "https://restapi.wcaquino.me/ola");
        Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
        Assert.assertTrue(response.statusCode() == 200);
        Assert.assertEquals(response.statusCode() == 200 , "O status Code Deveria ser 200");
        Assert.assertEquals(200, response.statusCode());
        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);
    }

    @Test
    public void devoConhcerOutrasFormasRestAssured() {
        Response response = RestAssured.request(Method.GET, "https://restapi.wcaquino.me/ola");
        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);


        RestAssured.given()
                //Pré condições
                .when()
                // As Ações
                .get("https://restapi.wcaquino.me/ola")
                // As Assertivas
                .then().statusCode(200);
    }

    @Test
    public void devoConhecerMatchersHamcrest() {
        assertThat("Maria", Matchers.is("Maria"));
    }

    @Test
    public void devoValidarBody() {
        RestAssured.given()
                //Pré condições
                .when()
                // As Ações
                .get("https://restapi.wcaquino.me/ola")
                // As Assertivas
                .then()
                .statusCode(200)
        ;
    }
}
