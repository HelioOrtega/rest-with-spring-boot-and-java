package br.com.helio.integrationtests.controller.withyaml;

import br.com.helio.configs.TestConfigs;
import br.com.helio.integrationtests.controller.withyaml.mapper.YMLMapper;
import br.com.helio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.helio.integrationtests.vo.AccountCredentialsVO;
import br.com.helio.integrationtests.vo.TokenVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class AuthControllerYamlTest extends AbstractIntegrationTest {

    private static YMLMapper objectMapper;
    private static TokenVO tokenVO;

    @BeforeAll
    public static void setUp (){
        objectMapper = new YMLMapper();
    }

    @Test
    @Order(1)
    public void testSignin()  {

        AccountCredentialsVO user =
                new AccountCredentialsVO("leandro", "admin123");

        RequestSpecification specification = new RequestSpecBuilder()
                    .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                    .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        tokenVO = given().spec(specification)
                .config(
                        RestAssuredConfig.config()
                        .encoderConfig(
                            EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(
                                        TestConfigs.CONTENT_TYPE_YML,
                                        ContentType.TEXT)))
                    .accept(TestConfigs.CONTENT_TYPE_YML)
                .basePath("/auth/signin")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_YML)
                .body(user, objectMapper)
                .when()
                    .post()
                .then()
                    .statusCode(200)
                .extract()
                    .body()
                        .as(TokenVO.class, objectMapper);

        Assertions.assertNotNull(tokenVO.getAccessToken());
        Assertions. assertNotNull(tokenVO.getRefreshToken());
    }

    @Test
    @Order(2)
    public void testRefresh() {

        var newTokenVO = given()
                .config(
                    RestAssuredConfig.config()
                        .encoderConfig(
                            EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(
                                    TestConfigs.CONTENT_TYPE_YML,
                                    ContentType.TEXT)))
                    .accept(TestConfigs.CONTENT_TYPE_YML).basePath("/auth/refresh")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                    .pathParam("username", tokenVO.getUsername())
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVO.getRefreshToken())
                .when()
                    .put("{username}")
                .then()
                    .statusCode(200)
                .extract()
                    .body()
                        .as(TokenVO.class, objectMapper);

        Assertions.assertNotNull(newTokenVO.getAccessToken());
        Assertions.assertNotNull(newTokenVO.getRefreshToken());
    }
}
