package de.dkt.eservices.enlg;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.context.ApplicationContext;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import eu.freme.bservices.testhelper.TestHelper;
import eu.freme.bservices.testhelper.ValidationHelper;
import eu.freme.bservices.testhelper.api.IntegrationTestSetup;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ENLGTest {

	TestHelper testHelper;
	ValidationHelper validationHelper;

	@Before
	public void setup() {
		ApplicationContext context = IntegrationTestSetup
				.getContext(TestConstants.pathToPackage);
		testHelper = context.getBean(TestHelper.class);
		validationHelper = context.getBean(ValidationHelper.class);
	}

	private HttpRequestWithBody baseRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-nlg/testURL";
		return Unirest.post(url);
	}

	private HttpRequestWithBody generateTextRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-nlg/generateTextFromTemplate";
		return Unirest.post(url);
	}

	private HttpRequestWithBody generateTemplateRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-nlg/generateTemplate";
		return Unirest.post(url);
	}

	private HttpRequestWithBody generateGrammarRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-nlg/generateGrammar";
		return Unirest.post(url);
	}

	private HttpRequestWithBody generateTextFromGrammarRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-nlg/generateTextFromGrammar";
		return Unirest.post(url);
	}

	@Test
	public void test_0_SanityCheck() throws UnirestException, IOException,
	Exception {
		HttpResponse<String> response = baseRequest()
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle").asString();
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
	}

//	@Test
//	public void test_1_generateText() throws UnirestException, IOException,
//	Exception {
//		
//		String inputText = "{"
//				+ "}";
//		HttpResponse<String> response = generateTextRequest()
//				.queryString("input", "{\"name\":\"template1\",\"features\":{\"color\":\"silver\",\"weight\":\"light\",\"size\":\"big\"}}")
//				.queryString("features", "{\"color\":\"silver\",\"weight\":\"light\",\"size\":\"big\"}")
//				.asString();
//		assertTrue(response.getStatus() == 200);
//		assertTrue(response.getBody().length() > 0);
//		System.out.println("Text output:");
//		System.out.println(response.getBody());
//	}

//	@Test
//	public void test_2_generateTemplate() throws UnirestException, IOException,
//	Exception {
//		String input = "{\"texts\":"
//				+ "[\"Built-in microphone delivers clear sound when you are calling, makes the communication a feeling of face to face.\\nAnti-winding line and soft touch feeling, these wired earbuds are extremely comfortable in your ear.\\nMultifunctional control button controls microphone, one-button call control and music play.\\n3.5mm jack fits for all 3.5mm interface electronic devices.\\nHigh definition stereo earphone provides extremely exact and natural sound than traditional stereo.\""
//				+ "]"
//				+ "}";
//		HttpResponse<String> response = generateTemplateRequest()
//				.queryString("input", input)
//				.asString();
//		assertTrue(response.getStatus() == 200);
//		assertTrue(response.getBody().length() > 0);
//		System.out.println("Template output:");
//		System.out.println(response.getBody());
//	}

//	@Test
//	public void test_3_generateGrammar() throws UnirestException, IOException,
//	Exception {
//		HttpResponse<String> response = generateGrammarRequest()
//				.queryString("input", "the flight leaves today")
//				.queryString("domain", "flights")
//				.asString();
//		assertTrue(response.getStatus() == 200);
//		assertTrue(response.getBody().length() > 0);
//		System.out.println("Template output:");
//		System.out.println(response.getBody());
//	}
//
//	@Test
//	public void test_4_generateTextFromGrammar() throws UnirestException, IOException,Exception {
//		String gram = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
//				+ "<regression>"
//				+ "<item numOfParses=\"1\" string=\"the flight leaves today\" info=\"s1\" test=\"true\">"
//				+ "<lf>"
//				+ "<node id=\"w2:motion\" pred=\"leave\" tense=\"pres\">"
//				+ "<rel name=\"Theme\">"
//				+ "<node id=\"w1:phys-obj\" pred=\"flight\" det=\"the\" num=\"sg\" />"
//				+ "</rel>"
//				+ "<rel name=\"HasProp\">"
//				+ "<node id=\"w3:proposition\" pred=\"today\" />"
//				+ "</rel>"
//				+ "</node>"
//				+ "</lf>"
//				+ "<full-words>&lt;s&gt; the:S-the:P-Det:T-np/n flight:S-flight:P-N:T-n:C-phys&amp;#45;obj leaves:S-leave:P-V:T-s[dcl]\\np today:S-today:P-Adv:T-s\\s &lt;/s&gt;</full-words>"
//				+ "<pred-info data=\"w1:n:N:flight w2:s[dcl]\\np:V:leave w3:s\\s:Adv:today\" />"
//				+ "</item>"
//				+ "</regression>";
//		HttpResponse<String> response = generateTextFromGrammarRequest()
//				.queryString("input", gram)
//				.queryString("domain", "flights")
//				.asString();
//		assertTrue(response.getStatus() == 200);
//		assertTrue(response.getBody().length() > 0);
//		System.out.println("Template output:");
//		System.out.println(response.getBody());
//	}

}
