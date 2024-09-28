package br.am.ericton.rest.refac;

import br.am.com.ericton.rest.BaseTest;
import io.restassured.RestAssured;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class ContasTest extends BaseTest {

    @Test
    public void deveIncluirContaComSucesso () {

        System.out.println("Fuck Year: Incluir");

        given()
                .body("{\"nome\": \"Conta inserida\"}")
                .when()
                .post("contas")
                .then()
                .statusCode(201)
        ;
    }

    @Test
    public void deveAlterarContaComSucesso () {

        System.out.println("Fuck Year: Alterar");

        Integer CONTA_ID = getIdContaPelaNome("Conta para alterar");

        System.out.println("******************************************");
        System.out.println(CONTA_ID);

        given()
                .body("{\"nome\": \"Conta alterada\"}")
                .pathParam("id", CONTA_ID)
                .when()
                .put("contas/{id}")
                .then()
                .statusCode(200)
                .body("nome", is("Conta alterada"))
        ;
    }

    @Test
    public void naoDeveInserirContaMesmoNome () {

        given()
                .body("{\"nome\": \"Conta mesmo nome\"}")
                .when()
                .post("contas")
                .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }

    public Integer getIdContaPelaNome(String nome) {
        return RestAssured.get("contas?nome="+nome).then().extract().path("id[0]");
    }
}
