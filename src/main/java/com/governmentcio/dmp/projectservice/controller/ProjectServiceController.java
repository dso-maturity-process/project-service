/**
 * 
 */
package com.governmentcio.dmp.projectservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.governmentcio.dmp.model.Project;
import com.governmentcio.dmp.projectservice.exception.ProjectServiceException;
import com.governmentcio.dmp.projectservice.service.ProjectService;
import com.governmentcio.dmp.utility.ServiceHealth;

/**
 * 
 * @author <a href=mailto:support@governmentcio.com>support</a>
 *
 */
@RestController
@RequestMapping("/project")
public class ProjectServiceController {

	/**
	 * Logger instance.
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(ProjectServiceController.class.getName());

	ProjectService projectService;

	/**
	 * @return the projectService
	 */
	public ProjectService getProjectService() {
		return projectService;
	}

	/**
	 * @param projectService the projectService to set
	 */
	@Autowired
	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.governmentcio.dmp.projectservice.controller.ProjectService#
	 * getAllProjects()
	 */
	@GetMapping("/getAll")
	public Iterable<Project> getAllProjects() {
		return projectService.getAllProjects();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.governmentcio.dmp.projectservice.controller.ProjectService#addProject(
	 * java.lang.String, java.lang.String)
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Project addProject(
			@RequestParam(value = "projectName", required = true) String projectName,
			@RequestParam(value = "description", required = true) String description) {
		return projectService.addProject(projectName, description);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	@GetMapping("/get")
	public Project getProjectByName(
			@RequestParam(value = "projectName", required = true) String projectName) {
		return projectService.getProjectByName(projectName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.governmentcio.dmp.projectservice.controller.ProjectService#
	 * updateProject(com.governmentcio.dmp.model.Project)
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public void updateProject(@RequestBody(required = true) Project project)
			throws ProjectServiceException {

		if (null == project) {
			throw new IllegalArgumentException("Project parameter was null.");
		}

		projectService.updateProject(project);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.governmentcio.dmp.projectservice.controller.ProjectService#
	 * removeProject(java.lang.String)
	 */
	@DeleteMapping("/remove")
	public void removeProject(
			@RequestParam(value = "projectName", required = true) String projectName) {
		projectService.removeProject(projectName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.governmentcio.dmp.projectservice.controller.ProjectService#
	 * addUserToProject(java.lang.String, java.lang.String)
	 */
	@RequestMapping(value = "/addUserToProject", method = RequestMethod.POST)
	public void addUserToProject(
			@RequestParam(value = "userName", required = true) String userName,
			@RequestParam(value = "projectName", required = true) String projectName)
			throws ProjectServiceException {
		projectService.addUserToProject(userName, projectName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.governmentcio.dmp.projectservice.controller.ProjectService#
	 * removeUserFromProject(java.lang.String, java.lang.String)
	 */
	@RequestMapping(value = "/removeUserFromProject", method = RequestMethod.POST)
	public void removeUserFromProject(
			@RequestParam(value = "userName", required = true) String userName,
			@RequestParam(value = "projectName", required = true) String projectName)
			throws ProjectServiceException {
		projectService.removeUserFromProject(userName, projectName);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	@GetMapping("/healthz")
	public ServiceHealth healthz() {

		LOG.info("Checking health...");

		return new ServiceHealth("Project", true); // TODO: Replace canned response.
	}

}
