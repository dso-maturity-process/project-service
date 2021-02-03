package com.governmentcio.dmp.projectservice.service;

import org.springframework.web.bind.annotation.PathVariable;

import com.governmentcio.dmp.model.Project;
import com.governmentcio.dmp.projectservice.exception.ProjectServiceException;

public interface ProjectService {

	/**
	 * 
	 * @return
	 */
	Iterable<Project> getAllProjects();

	/**
	 * 
	 * @param name
	 * @param description
	 * @return
	 */
	Project addProject(String name, String description);

	/**
	 * 
	 * @param user
	 * @throws Exception
	 */
	void updateProject(Project project) throws ProjectServiceException;

	/**
	 * 
	 * @param name
	 */
	void removeProject(String name);

	/**
	 * 
	 * @param userName
	 * @param roleType
	 * @param projectId
	 * @throws Exception
	 */
	void addUserToProject(String userName, String projectName)
			throws ProjectServiceException;

	/**
	 * 
	 * @param userName
	 * @param projectName
	 * @throws Exception
	 */
	void removeUserFromProject(String userName, String projectName)
			throws ProjectServiceException;

	/**
	 * 
	 * @param name
	 * @return
	 */
	public Project getProjectByName(@PathVariable String name);

}