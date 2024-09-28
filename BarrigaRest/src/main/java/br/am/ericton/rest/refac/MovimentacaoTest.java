package br.am.ericton.rest.refac;

import br.am.com.ericton.rest.BaseTest;
import br.am.ericton.rest.tests.Movimentacao;
import br.am.ericton.rest.utils.DataUtils;
import io.restassured.RestAssured;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MovimentacaoTest extends BaseTest {

    public Integer getIdContaPelaNome(String nome) {
        return RestAssured.get("contas?nome="+nome).then().extract().path("id[0]");
    }

    public Integer getIdMovPelaDescricao(String desc) {
        return RestAssured.get("transacoes?descricao="+desc).then().extract().path("id[0]");
    }

    private Movimentacao getMovimentacaoValida() {

        Movimentacao mov = new Movimentacao();

        mov.setConta_id(getIdContaPelaNome("Conta para movimentacoes"));
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

    @Test
    public void deveInserirMovimentacaoSucesso () {

        Movimentacao mov = getMovimentacaoValida();

        given()
                .body(mov)
                .when()
                .post("transacoes")
                .then()
                .statusCode(201)
        ;
    }

    @Test
    public void deveValidarCamposObrigatorioMovimentacao () {

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
                        "Situação é obrigatório"));
    }

    @Test
    public void naoDeveInserirMovimentacaoSucessoComDataFutura () {

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
    public void naoDeveRemoverContaComMovimentacao () {

        Integer CONTA_ID = getIdContaPelaNome("Conta com movimentacao");

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
    public void deveRemoverMovimentacao () {

        Integer MOV_ID = getIdMovPelaDescricao("Movimentacao para exclusao");
        given()
                .pathParam("id", MOV_ID)
                .when()
                .delete("transacoes/{id}")
                .then()
                .statusCode(204)
        ;
    }
}
