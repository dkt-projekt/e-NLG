package de.dkt.eservices.enlg;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.dkt.common.feedback.InteractionManagement;
import eu.freme.common.conversion.rdf.RDFConversionService;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.rest.BaseRestController;

@RestController
public class ENLGRestController {//extends BaseRestController{
    
	Logger logger = Logger.getLogger(ENLGRestController.class);

	@Autowired
	ENLGService service;
	
//	@Autowired
//	RDFConversionService rdfconversionservice;
		
	@RequestMapping(value = "/e-nlg/testURL", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> testURL(
			@RequestParam(value = "preffix", required = false) String preffix,
			@RequestBody(required = false) String postBody) throws Exception {
	    HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.add("Content-Type", "text/plain");
	    ResponseEntity<String> response = new ResponseEntity<String>("The restcontroller is working properly", responseHeaders, HttpStatus.OK);
	    return response;
	}

	@RequestMapping(value = "/e-nlg/generateDescription", method = {RequestMethod.POST})
	public ResponseEntity<String> generateDescription(
			HttpServletRequest request, 
			@RequestParam(value = "productType", required = false) String type,
			@RequestParam(value = "productName", required = false) String name,
			@RequestParam(value = "productFeatures", required = false) String features,
			@RequestParam(value = "language", required = false) String language,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
            @RequestParam Map<String, String> allParams,
			@RequestBody(required = false) String postBody) throws Exception {
		try {
            String result = service.generateDescription(type, name, features, language);
//			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "usage", "e-NLG/generateTemplate", "Success", "", "Exception", "", "");
            HttpHeaders responseHeaders = new HttpHeaders();
//			responseHeaders.add("Content-Type", "text/plain; charset=utf-8");
			responseHeaders.add("Access-Control-Allow-Origin", "*");
			ResponseEntity<String> response = new ResponseEntity<String>(result, responseHeaders, HttpStatus.OK);
			return response;
		} catch (Exception e) {
			logger.error("EXCEPTION: "+e.getMessage());
//			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLG/generateTemplate", e.getMessage(), "", "Exception", e.getMessage(), "");
			throw e;
		}
	}
	
	@RequestMapping(value = "/e-nlg/generateTemplate", method = {RequestMethod.POST})
	public ResponseEntity<String> generateTemplate(
			HttpServletRequest request, 
			@RequestParam(value = "input", required = false) String input,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
            @RequestParam Map<String, String> allParams,

			@RequestParam(value = "encodingG", required = false) String encoding,
			@RequestParam(value = "language", required = false) String language,
			@RequestParam(value = "algorithm", required = false) String algorithm,
			@RequestBody(required = false) String postBody) throws Exception {
		
		try {
			if(input==null || input.equalsIgnoreCase("")){
				input=postBody;
				if(input==null || input.equalsIgnoreCase("")){
					String msg = "No input found: nor file, neither input, neither body content.";
					logger.error(msg);
					InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLG/generateTemplate", msg, "", "Exception", msg, "");
					throw new BadRequestException(msg);
				}
			}
            JSONObject outObject;
            outObject = service.generateTemplate(input, algorithm, language);
            String result = outObject.toString(1);
//			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "usage", "e-NLG/generateTemplate", "Success", "", "Exception", "", "");
            HttpHeaders responseHeaders = new HttpHeaders();
//			responseHeaders.add("Content-Type", "text/plain; charset=utf-8");
			ResponseEntity<String> response = new ResponseEntity<String>(result, responseHeaders, HttpStatus.OK);
			return response;
		} catch (Exception e) {
			logger.error("EXCEPTION: "+e.getMessage());
//			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLG/generateTemplate", e.getMessage(), "", "Exception", e.getMessage(), "");
			throw e;
		}
	}
	
	@RequestMapping(value = "/e-nlg/generateTextFromTemplate", method = {RequestMethod.POST, RequestMethod.GET })
 	public ResponseEntity<String> generateTextFromTemplate(
			HttpServletRequest request, 
			@RequestParam(value = "input", required = false) String input,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
            @RequestParam Map<String, String> allParams,
			@RequestParam(value = "features", required = false) String sFeatures,
			@RequestBody(required = false) String postBody) throws Exception {
		try {
			if(input==null || input.equalsIgnoreCase("")){
				input=postBody;
				if(input==null || input.equalsIgnoreCase("")){
					String msg = "No input found: nor file, neither input, neither body content.";
					logger.error(msg);
//					InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLG/generateTextFromTemplate", msg, "", "Exception", msg, "");
					throw new BadRequestException(msg);
				}
			}
            JSONObject outObject;
            outObject = service.generateTextFromTemplate(input, sFeatures);
            String result = outObject.toString(1);
			HttpHeaders responseHeaders = new HttpHeaders();
//			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "usage", "e-NLG/generateTextFromTemplate", "Success", "", "Exception", "", "");
			return new ResponseEntity<String>(result, responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("EXCEPTION: "+e.getMessage());
//			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLG/generateTextFromTemplate", e.getMessage(), "", "Exception", e.getMessage(), "");
			throw e;
		}
	}

	@RequestMapping(value = "/e-nlg/generateGrammar", method = {RequestMethod.POST})
	public ResponseEntity<String> generateGrammar(
			HttpServletRequest request, 
			@RequestParam(value = "input", required = false) String input,
			@RequestParam(value = "domain", required = false) String domain,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
            @RequestParam Map<String, String> allParams,
			@RequestBody(required = false) String postBody) throws Exception {
		
		try {
			if(input==null || input.equalsIgnoreCase("")){
				input=postBody;
				if(input==null || input.equalsIgnoreCase("")){
					String msg = "No input found: nor file, neither input, neither body content.";
					logger.error(msg);
//					InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLG/generateTemplate", msg, "", "Exception", msg, "");
					throw new BadRequestException(msg);
				}
			}
            String result = service.generateGrammar(input, domain);
            System.out.println(result);
//			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "usage", "e-NLG/generateTemplate", "Success", "", "Exception", "", "");
            HttpHeaders responseHeaders = new HttpHeaders();
//			responseHeaders.add("Content-Type", "text/plain; charset=utf-8");
			responseHeaders.add("Access-Control-Allow-Origin", "*");
			ResponseEntity<String> response = new ResponseEntity<String>(result, responseHeaders, HttpStatus.OK);
			return response;
		} catch (Exception e) {
			logger.error("EXCEPTION: "+e.getMessage());
//			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLG/generateTemplate", e.getMessage(), "", "Exception", e.getMessage(), "");
			throw e;
		}
	}

	@RequestMapping(value = "/e-nlg/generateTextFromGrammar", method = {RequestMethod.POST})
	public ResponseEntity<String> generateTextFromGrammar(
			HttpServletRequest request, 
			@RequestParam(value = "input", required = false) String input,
			@RequestParam(value = "domain", required = false) String domain,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
            @RequestParam Map<String, String> allParams,
			@RequestBody(required = false) String postBody) throws Exception {
		try {
			if(input==null || input.equalsIgnoreCase("")){
				input=postBody;
				if(input==null || input.equalsIgnoreCase("")){
					String msg = "No input found: nor file, neither input, neither body content.";
					logger.error(msg);
//					InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLG/generateTemplate", msg, "", "Exception", msg, "");
					throw new BadRequestException(msg);
				}
			}
            System.out.println("input: "+input);
            String result = service.generateTextFromGrammar(input, domain);
            System.out.println("RESULT: "+result);
//			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "usage", "e-NLG/generateTemplate", "Success", "", "Exception", "", "");
            HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add("Access-Control-Allow-Origin", "*");
			ResponseEntity<String> response = new ResponseEntity<String>(result, responseHeaders, HttpStatus.OK);
			return response;
		} catch (Exception e) {
			logger.error("EXCEPTION: "+e.getMessage());
//			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-NLG/generateTemplate", e.getMessage(), "", "Exception", e.getMessage(), "");
			throw e;
		}
	}
}
