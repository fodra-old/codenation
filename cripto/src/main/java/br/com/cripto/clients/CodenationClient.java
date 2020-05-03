package br.com.cripto.clients;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.cripto.config.BeanBuild;
import br.com.cripto.dto.AnswerDto;
import br.com.cripto.utils.CodenationCripto;
import io.micrometer.core.instrument.util.IOUtils;

@Component
public class CodenationClient {

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private RestTemplate rest;
	
	public void post() {

		try (CloseableHttpClient client = HttpClientBuilder.create().build()){
			
			File file = new File("answer.json");
			
			String url = "https://api.codenation.dev/v1/challenge/"
					+ "dev-ps/submit-solution?token=" + BeanBuild.TOKEN;

			HttpPost post = new HttpPost(url);
			
			StringBody body = new StringBody(mapper.writeValueAsString(file), ContentType.MULTIPART_FORM_DATA);
			FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);
			
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addPart("answer", body);
			builder.addPart("answer", fileBody);
			
			HttpEntity entity = builder.build();
			post.setEntity(entity);
			
			HttpResponse response = client.execute(post);
			HttpEntity responseEntity = response.getEntity();
			System.out.println(IOUtils.toString(responseEntity.getContent()));
		} catch (Exception e) {

			e.printStackTrace();
		}
		
	}
	public void get() {

		try (FileOutputStream fileOutputStream = 
				new FileOutputStream("answer.json")) {
			
			AnswerDto answer = rest.getForObject(
					"https://api.codenation.dev/v1/challenge/dev-ps/generate-data?token=c16f69cede9fd74764d37f6c74a8e34a7d0c59f6", AnswerDto.class);
			
			answer.setDecifrado(
					CodenationCripto.decriptar(
							(Integer.valueOf(answer.getNumero_casas())*-1), 
							answer.getCifrado()));
			answer.setResumo_criptografico(
					CodenationCripto.convertToSha1(answer.getDecifrado()));
			
			mapper.writeValue(fileOutputStream, answer);
		} catch (JsonGenerationException e) {
			
			e.printStackTrace();
		} catch (JsonMappingException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}