package br.am.ericton.rest.tests;

import br.am.ericton.rest.core.BaseTest;
import br.am.ericton.rest.utils.DataUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTest extends BaseTest {

//	private static String TOKEN;

    private static String CONTA_NAME = "Conta" + System.nanoTime();
    private static Integer CONTA_ID;
    private static Integer MOV_ID;

    @BeforeClass
    public static void login () {
        Map<String, String> login = new HashMap<>();
        login.put("email","ericton@brito");
        login.put("senha", "123456");

        String TOKEN = given()
                .body(login)
                .when()
                .post("signin")
                .then()
                .statusCode(200)
                .extract().path("token");

        RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
    }

    @Test

    public void deveIncluirContaComSucesso_primeiro () {

        CONTA_ID =	given()
                .body("{\"nome\": \""+CONTA_NAME+"\"}")
                .when()
                .post("contas")
                .then()
                .statusCode(201)
                .extract().path("id")
        ;
    }

    @Test
    public void deveAlterarContaComSucesso_segundo () {

        given()
                .body("{\"nome\": \""+CONTA_NAME+" conta do botao\"}")
                .pathParam("id", CONTA_ID)
                .when()
                .put("contas/{id}")
                .then()
                .statusCode(200)
                .body("nome", is(CONTA_NAME+" conta do botao"))
        ;
    }

    @Test
    public void naoDeveInserirContaMesmoNome_terceiro () {

        given()
                .body("{\"nome\": \""+CONTA_NAME+" conta do botao\"}")
                .when()
                .post("contas")
                .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }

    @Test
    public void deveInserirMovimentacaoSucesso_quarto () {
        Movimentacao mov = getMovimentacaoValida();

        MOV_ID = given()
                .body(mov)
                .when()
                .post("transacoes")
                .then()
                .statusCode(201).log().all()
                .extract().path("id")
        ;
    }

    @Test
    public void deveValidarCamposObrigatorioMovimentacao_quinto () {

        given()
                .body("{}")
                .when()
                .post("transacoes")
                .then()
                .statusCode(400)
                .body("$", hasSize(8))
                .body("msg", hasItems(
                        "Data da Movimentação é obrigatório",
                        "Data do pagamento é obrigatório",
                        "Descrição é obrigatório",
                        "Interessado é obrigatório",
                        "Valor é obrigatório",
                        "Valor deve ser um número",
                        "Conta é obrigatório",
                        "Situação é obrigatório"))
        ;
    }

    @Test
    public void naoDeveInserirMovimentacaoSucessoComDataFutura_sexto () {
        Movimentacao mov = getMovimentacaoValida();
        mov.setData_transacao(DataUtils.getDataDiferencaDias(2));

        given()
                .body(mov)
                .when()
                .post("transacoes")
                .then()
                .statusCode(400)
                .body("$", hasSize(1))
                .body("msg", hasItems("Data da Movimentação deve ser menor ou igual à data atual"))
        ;
    }

    @Test
    public void naoDeveRemoverContaComMovimentacao_setimo () {

        given()
                .pathParam("id", CONTA_ID)
                .when()
                .delete("contas/{id}")
                .then()
                .statusCode(500)
                .body("constraint", is("transacoes_conta_id_foreign"))
        ;
    }

    @Test
    public void deveCalcularSaldoContas_oitavo () {

        given()
                .when()
                .get("saldo")
                .then()
                .statusCode(200)
                .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("100.00")).log().all()
        ;
    }

    @Test
    public void deveRemoverMovimentacao_decimo () {

        given()
                .pathParam("id", MOV_ID)
                .when()
                .delete("transacoes/{id}")
                .then()
                .statusCode(204)
        ;
    }

    @Test
    public void naoDeveAcessarAPISemToken_decimo_primeiro () {

        FilterableRequestSpecification req =(FilterableRequestSpecification) RestAssured.requestSpecification;
        req.removeHeader("Authorization");

        given()
                .when()
                .get("contas")
                .then()
                .statusCode(401)
        ;
    }

    private Movimentacao getMovimentacaoValida() {

        Movimentacao mov = new Movimentacao();

        mov.setConta_id(CONTA_ID);
//		mov.setUsuario_id();
        mov.setDescricao("Descricao da movimentacao");
        mov.setEnvolvido("Envolvido na mov");
        mov.setTipo("REC");
        mov.setData_transacao(DataUtils.getDataDiferencaDias(-1));
        mov.setData_pagamento(DataUtils.getDataDiferencaDias(5));
        mov.setValor(100F);
        mov.setStatus(true);
        return mov;
    }

}


