package br.com.helio.integrationtests.controller.withyaml;

import br.com.helio.configs.TestConfigs;
import br.com.helio.data.vo.v1.security.TokenVO;
import br.com.helio.integrationtests.controller.withyaml.mapper.YMLMapper;
import br.com.helio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.helio.integrationtests.vo.AccountCredentialsVO;
import br.com.helio.integrationtests.vo.PersonVO;
import br.com.helio.integrationtests.vo.pagedmodels.PagedModelPerson;
import br.com.helio.integrationtests.vo.wrappers.WrapperPersonVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerYamlTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static YMLMapper objectMapper;

	private static PersonVO person;

	@BeforeAll
	public static void setup() {
		objectMapper = new YMLMapper();
		person = new PersonVO();
	}

	@Test
	@Order(0)
	public void authorization() {

		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

		var accessToken = given()
				.config(
				RestAssuredConfig.config()
					.encoderConfig(
					EncoderConfig.encoderConfig()
						.encodeContentTypeAs(
							TestConfigs.CONTENT_TYPE_YML,
							ContentType.TEXT)))
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_YML)
						.accept(TestConfigs.CONTENT_TYPE_YML)
						.body(user, objectMapper)
					.when()
						.post()
					.then()
						.statusCode(200)
							.extract()
							.body()
								.as(TokenVO.class, objectMapper)
							.getAccessToken();

		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

	@Test
	@Order(1)
	public void testCreate() {

		mockPerson();

		var persistedPerson = given().spec(specification)
				.config(
					RestAssuredConfig.config()
						.encoderConfig(
							EncoderConfig.encoderConfig()
							.encodeContentTypeAs(
								TestConfigs.CONTENT_TYPE_YML,
								ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
					.accept(TestConfigs.CONTENT_TYPE_YML)
					.body(person, objectMapper)
				.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
				.body()
					.as(PersonVO.class, objectMapper);

		Assertions.assertNotNull(persistedPerson);
		Assertions.assertNotNull(persistedPerson.getId());
		Assertions.assertNotNull(persistedPerson.getFirstName());
		Assertions.assertNotNull(persistedPerson.getLastName());
		Assertions.assertNotNull(persistedPerson.getAddress());
		Assertions.assertNotNull(persistedPerson.getGender());

		assertTrue(persistedPerson.getId() > 0);

		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
		assertTrue((persistedPerson.getEnabled()));
	}

	@Test
	@Order(2)
	public void testUpdate() {
		person.setLastName("Piquet Souto Maior");

		var persistedPerson = given().spec(specification)
				.config(
					RestAssuredConfig
					.config()
						.encoderConfig(EncoderConfig.encoderConfig()
							.encodeContentTypeAs(
								TestConfigs.CONTENT_TYPE_YML,
								ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
					.accept(TestConfigs.CONTENT_TYPE_YML)
					.body(person, objectMapper)
				.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
						.as(PersonVO.class, objectMapper);

		person = persistedPerson;

		assertNotNull(persistedPerson);

		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());

		assertEquals(person.getId(), persistedPerson.getId());

		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
		assertTrue((persistedPerson.getEnabled()));

	}

	@Test
	@Order(3)
	public void testDisablePersonById() throws JsonProcessingException {

		var persistedPerson = given().spec(specification)
				.config(
					RestAssuredConfig
					.config()
						.encoderConfig(EncoderConfig.encoderConfig()
						.encodeContentTypeAs(
							TestConfigs.CONTENT_TYPE_YML,
							ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
					.accept(TestConfigs.CONTENT_TYPE_YML)
					.body(person, objectMapper)
				.pathParam("id", person.getId())
				.when()
					.patch("{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
						.as(PersonVO.class, objectMapper);

		person = persistedPerson;

		assertNotNull(persistedPerson);

		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());


		assertEquals(person.getId(), persistedPerson.getId());

		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
		assertFalse((persistedPerson.getEnabled()));
	}

	@Test
	@Order(4)
	public void testFindById() {
		mockPerson();

		var persistedPerson = given().spec(specification)
				.config(
					RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
							.encodeContentTypeAs(
							TestConfigs.CONTENT_TYPE_YML,
							ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
					.accept(TestConfigs.CONTENT_TYPE_YML)
					.pathParam("id", person.getId())
				.when()
					.get("{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
						.as(PersonVO.class, objectMapper);

		person = persistedPerson;

		assertNotNull(persistedPerson);

		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());

		assertEquals(person.getId(), persistedPerson.getId());

		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
		assertFalse((persistedPerson.getEnabled()));
	}

	@Test
	@Order(5)
	public void testDelete() {

		given().spec(specification)
				.config(
					RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
							.encodeContentTypeAs(
								TestConfigs.CONTENT_TYPE_YML,
								ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
					.accept(TestConfigs.CONTENT_TYPE_YML)
					.pathParam("id", person.getId())
				.when()
					.delete("{id}")
				.then()
					.statusCode(204);
}

	@Test
	@Order(6)
	public void testFindAll() throws JsonProcessingException {

		var wrapper = given().spec(specification)
				.config(
					RestAssuredConfig.config()
						.encoderConfig(
							EncoderConfig.encoderConfig()
							.encodeContentTypeAs(
								TestConfigs.CONTENT_TYPE_YML,
								ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.queryParams("page", 3, "size", 10, "direction", "asc")
				.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
				.body()
					.as(PagedModelPerson.class, objectMapper);

		var people = wrapper.getContent();

		PersonVO foundPersonOne = people.getFirst();

		Assertions.assertNotNull(foundPersonOne);
		Assertions.assertNotNull(foundPersonOne.getId());
		Assertions.assertNotNull(foundPersonOne.getFirstName());
		Assertions.assertNotNull(foundPersonOne.getLastName());
		Assertions.assertNotNull(foundPersonOne.getAddress());
		Assertions.assertNotNull(foundPersonOne.getGender());

		assertEquals(677, foundPersonOne.getId());

		assertEquals("Alic", foundPersonOne.getFirstName());
		assertEquals("Terbrug", foundPersonOne.getLastName());
		assertEquals("3 Eagle Crest Court", foundPersonOne.getAddress());
		assertEquals("Male", foundPersonOne.getGender());
		assertTrue(foundPersonOne.getEnabled());

		PersonVO foundPersonSix = people.get(5);

		Assertions.assertNotNull(foundPersonSix);
		Assertions.assertNotNull(foundPersonSix.getId());
		Assertions.assertNotNull(foundPersonSix.getFirstName());
		Assertions.assertNotNull(foundPersonSix.getLastName());
		Assertions.assertNotNull(foundPersonSix.getAddress());
		Assertions.assertNotNull(foundPersonSix.getGender());

		assertEquals("Allegra", foundPersonSix.getFirstName());
		assertEquals("Dome", foundPersonSix.getLastName());
		assertEquals("57 Roxbury Pass", foundPersonSix.getAddress());
		assertEquals("Female", foundPersonSix.getGender());
		assertTrue(foundPersonSix.getEnabled());
	}

	@Test
	@Order(7)
	public void testFindPersonByName() throws JsonProcessingException {

		var wrapper = given().spec(specification)
				.config(
					RestAssuredConfig.config()
					.encoderConfig(
						EncoderConfig.encoderConfig()
						.encodeContentTypeAs(
							TestConfigs.CONTENT_TYPE_YML,
							ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.pathParam("firstName", "fag")
				.queryParams("page", 0, "size", 6, "direction", "asc")
				.when()
					.get("findPersonByName/{firstName}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.as(PagedModelPerson.class, objectMapper);

		var people = wrapper.getContent();

		PersonVO foundPerson = people.getFirst();

		Assertions.assertNotNull(foundPerson);
		Assertions.assertNotNull(foundPerson.getId());
		Assertions.assertNotNull(foundPerson.getFirstName());
		Assertions.assertNotNull(foundPerson.getLastName());
		Assertions.assertNotNull(foundPerson.getAddress());
		Assertions.assertNotNull(foundPerson.getGender());

		assertEquals(7, foundPerson.getId());

		assertEquals("Fagner", foundPerson.getFirstName());
		assertEquals("Absynth", foundPerson.getLastName());
		assertEquals("Santo Andre", foundPerson.getAddress());
		assertEquals("Male", foundPerson.getGender());
		assertTrue(foundPerson.getEnabled());
	}

	@Test
	@Order(8)
	public void testFindAllWithoutToken() throws JsonProcessingException {

		RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();

		given().spec(specificationWithoutToken)
			.config(
				RestAssuredConfig.config()
				.encoderConfig(
					EncoderConfig.encoderConfig()
					.encodeContentTypeAs(
						TestConfigs.CONTENT_TYPE_YML,
						ContentType.TEXT)))
			.contentType(TestConfigs.CONTENT_TYPE_YML)
			.when()
				.get()
			.then()
				.statusCode(403)
					.extract()
					.body()
						.asString();
	}

	private void mockPerson() {
		person.setFirstName("Nelson");
		person.setLastName("Piquet");
		person.setAddress("Brasília - DF - Brasil");
		person.setGender("Male");
		person.setEnabled(true);
	}
}

