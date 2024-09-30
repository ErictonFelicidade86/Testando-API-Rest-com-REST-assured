package br.am.ericton.rest.refac;

import br.am.ericton.rest.core.BaseTest;
import io.restassured.RestAssured;
import static org.hamcrest.Matchers.*;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class SaldoTest extends BaseTest {

    public Integer getIdContaPelaNome(String nome) {
        return RestAssured.get("contas?nome="+nome).then().extract().path("id[0]");
    }

    @Test
    public void deveCalcularSaldoContas () {

        Integer CONTA_ID = getIdContaPelaNome("Conta para saldo");

        given()
                .when()
                .get("saldo")
                .then()
                .statusCode(200)
                .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("534.00"))
        ;
    }
}
