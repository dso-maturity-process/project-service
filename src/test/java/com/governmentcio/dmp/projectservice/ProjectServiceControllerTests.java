
package com.governmentcio.dmp.projectservice;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.governmentcio.dmp.Application;
import com.governmentcio.dmp.model.Project;
import com.governmentcio.dmp.model.User;
import com.governmentcio.dmp.utility.ServiceHealth;

/**
 * 
 * @author <a href=mailto:support@governmentcio.com>support</a>
 * 
 */
/**
 * 
 * @author <a href=mailto:support@governmentcio.com>support</a>
 * 
 *         Tests for Assessment service controller
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureStubRunner(stubsMode = StubRunnerProperties.StubsMode.LOCAL, ids = "com.governmentcio.dmp:assessment-service:+:stubs:8090")
class ProjectServiceControllerTests {

	@Value("${assessment.service.host}")
	private String assessmentServiceHost;

	@Value("${assessment.service.port}")
	private Long assessmentServicePort;

	@Value("${assessment.service.name}")
	private String assessmentServiceName;

	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();

	HttpHeaders headers = new HttpHeaders();

	private static final String PROJECT_URL = "/project";

	/**
	 * 
	 */
	@Test
	public void test_Getting_all_Projects() {

		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<Iterable<Project>> response = restTemplate.exchange(
				createProjectURLWithPort("/getAll"), HttpMethod.GET, entity,
				new ParameterizedTypeReference<Iterable<Project>>() {
				});

		assertNotNull(response);

		Iterable<Project> projects = response.getBody();

		String projectName = "VA-PARS";
		boolean found = false;

		for (Project project : projects) {
			assertNotNull(project);
			if (project.getName().equals(projectName)) {
				found = true;
			}
		}

		assertTrue(found);

	}

	/**
	 *
	 */
	@Test
	public void testHealth() {

		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<ServiceHealth> response = restTemplate.exchange(
				createProjectURLWithPort("/healthz"), HttpMethod.GET, entity,
				new ParameterizedTypeReference<ServiceHealth>() {
				});

		assertNotNull(response);

		assertTrue(response.getStatusCode() == HttpStatus.OK);

		ServiceHealth srvHealth = response.getBody();

		assertNotNull(srvHealth);

		assertTrue(srvHealth.isHealthy());

	}

	/**
	 * 
	 */
	@Test
	public void test_Project_CRUD_Functionality() {

		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		String projectName = "WEEMS";
		String description = "The description for the WEEMS project";

		String parameters = "?projectName=" + projectName + "&description="
				+ description;

		ResponseEntity<Project> response = restTemplate.exchange(
				createProjectURLWithPort("/add" + parameters), HttpMethod.POST, entity,
				new ParameterizedTypeReference<Project>() {
				});

		assertNotNull(response);

		assertTrue(response.getStatusCode() == HttpStatus.OK);

		Project newProject = response.getBody();

		assertNotNull(newProject);

		assertTrue(newProject.getName().equals(projectName));
		assertTrue(newProject.getDescription().equals(description));

		// Get the Project just added

		parameters = "?projectName=" + newProject.getName();

		response = restTemplate.exchange(
				createProjectURLWithPort("/get" + parameters), HttpMethod.GET, entity,
				new ParameterizedTypeReference<Project>() {
				});

		assertNotNull(response);

		assertTrue(response.getStatusCode() == HttpStatus.OK);

		Project retrievedProject = response.getBody();

		assertNotNull(retrievedProject);

		assertTrue(retrievedProject.getId().equals(newProject.getId()));
		assertTrue(retrievedProject.getName().equals(newProject.getName()));
		assertTrue(
				retrievedProject.getDescription().equals(newProject.getDescription()));

		// Update the description of the Project

		String updatedDescription = "This is an update to the project description";

		retrievedProject.setDescription(updatedDescription);

		// Prepare acceptable media type
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_JSON);

		// Prepare header
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(acceptableMediaTypes);
		HttpEntity<Project> updatedQuestionEntity = new HttpEntity<Project>(
				retrievedProject, headers);

		response = restTemplate.exchange(createProjectURLWithPort("/update"),
				HttpMethod.POST, updatedQuestionEntity, Project.class);

		assertNotNull(response);

		assertTrue(response.getStatusCode() == HttpStatus.OK);

		assertTrue(retrievedProject.getName().equals(projectName));
		assertTrue(retrievedProject.getDescription().equals(updatedDescription));

		// Remove the Project

		parameters = "?projectName=" + retrievedProject.getName();

		ResponseEntity<Void> responseVoid = restTemplate.exchange(
				createProjectURLWithPort("/remove" + parameters), HttpMethod.DELETE,
				entity, new ParameterizedTypeReference<Void>() {
				});

		assertNotNull(responseVoid);

		assertTrue(responseVoid.getStatusCode() == HttpStatus.OK);

		// Ensure Project removed

		parameters = "?projectName=" + retrievedProject.getName();

		response = restTemplate.exchange(
				createProjectURLWithPort("/get" + parameters), HttpMethod.GET, entity,
				new ParameterizedTypeReference<Project>() {
				});

		assertNotNull(response);

		assertTrue(response.getStatusCode() == HttpStatus.OK);

		retrievedProject = response.getBody();

		assertNull(retrievedProject);

	}

	/**
	 * 
	 */
	@Test
	public void test_Adding_then_Removing_User_from_Project() {

		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		String userName = "wdrew@governmentcio.com";
		String projectName = "IAM";

		String parameters = "?projectName=" + projectName;

		// Get the Project just added

		ResponseEntity<Project> response = restTemplate.exchange(
				createProjectURLWithPort("/get" + parameters), HttpMethod.GET, entity,
				new ParameterizedTypeReference<Project>() {
				});

		assertNotNull(response);

		assertTrue(response.getStatusCode() == HttpStatus.OK);

		Project retrievedProject = response.getBody();

		assertNotNull(retrievedProject);

		// Add User to Project

		parameters = "?userName=" + userName + "&projectName="
				+ retrievedProject.getName();

		ResponseEntity<Void> responseVoid = restTemplate.exchange(
				createProjectURLWithPort("/addUserToProject" + parameters),
				HttpMethod.POST, entity, new ParameterizedTypeReference<Void>() {
				});

		assertNotNull(responseVoid);

		assertTrue(responseVoid.getStatusCode() == HttpStatus.OK);

		// Get the Project just updated with new User

		parameters = "?projectName=" + retrievedProject.getName();

		response = restTemplate.exchange(
				createProjectURLWithPort("/get" + parameters), HttpMethod.GET, entity,
				new ParameterizedTypeReference<Project>() {
				});

		assertNotNull(response);

		assertTrue(response.getStatusCode() == HttpStatus.OK);

		Project retrievedProjectWithUserAdded = response.getBody();

		assertNotNull(retrievedProjectWithUserAdded);

		Set<User> projectUsers = retrievedProjectWithUserAdded.getMembers();

		assertFalse(projectUsers.isEmpty());

		// Check for User just added to Project

		for (User member : projectUsers) {
			assertNotNull(member);
			assertTrue(member.getUserName().equals(userName));
		}

		// Now remove the User from the Project

		parameters = "?userName=" + userName + "&projectName="
				+ retrievedProject.getName();

		responseVoid = restTemplate.exchange(
				createProjectURLWithPort("/removeUserFromProject" + parameters),
				HttpMethod.POST, entity, new ParameterizedTypeReference<Void>() {
				});

		assertNotNull(responseVoid);

		assertTrue(responseVoid.getStatusCode() == HttpStatus.OK);

		// Get the Project to check User was removed

		parameters = "?projectName=" + retrievedProject.getName();

		response = restTemplate.exchange(
				createProjectURLWithPort("/get" + parameters), HttpMethod.GET, entity,
				new ParameterizedTypeReference<Project>() {
				});

		assertNotNull(response);

		assertTrue(response.getStatusCode() == HttpStatus.OK);

		Project retrievedProjectWithUserRemoved = response.getBody();

		assertNotNull(retrievedProjectWithUserRemoved);

		Set<User> projectUsersShouldBeEmpty = retrievedProjectWithUserRemoved
				.getMembers();

		assertTrue(projectUsersShouldBeEmpty.isEmpty());

	}

	/**
	 * 
	 * @param uri
	 * @return
	 */
	private String createProjectURLWithPort(String uri) {
		return "http://localhost:" + port + PROJECT_URL + uri;
	}

	/**
	 * 
	 * @param uri
	 * @return
	 */
	private String createAssessmentURLWithPort(String uri) {
		return "http://" + assessmentServiceHost + ":" + assessmentServicePort + "/"
				+ assessmentServiceName + uri;
	}
}
