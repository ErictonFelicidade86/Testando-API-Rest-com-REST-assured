package br.am.ericton.rest.suite;

import br.am.com.ericton.rest.BaseTest;
import br.am.ericton.rest.refac.AuthTest;
import br.am.ericton.rest.refac.ContasTest;
import br.am.ericton.rest.refac.MovimentacaoTest;
import br.am.ericton.rest.refac.SaldoTest;
import org.junit.BeforeClass;
import io.restassured.RestAssured;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@RunWith(org.junit.runners.Suite.class)
@Suite.SuiteClasses({
        ContasTest.class,
        MovimentacaoTest.class,
        SaldoTest.class,
        AuthTest.class
})
public class SuiteTest extends BaseTest {

    @BeforeClass
    public static void login () {
        System.out.println("Fuck Year: Before Conta");
        Map<String, String> login = new HashMap<>();
        login.put("email","ericton@brito");
        login.put("senha", "123456");

        String TOKEN = given()
                .contentType("application/json")
                .body(login)
                .when()
                .post("signin")
                .then()
                .statusCode(200)
                .extract().path("token");

        if (TOKEN == null || TOKEN.isEmpty()) {
            throw new RuntimeException("Token não foi retornado na resposta.");
        }

        RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);

        RestAssured.get("reset").then().statusCode(200);
    }
}
