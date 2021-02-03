/**
 * 
 */
package com.governmentcio.dmp.projectservice.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.governmentcio.dmp.dao.DomainFactory;
import com.governmentcio.dmp.dao.ProjectDao;
import com.governmentcio.dmp.dao.ProjectUserDao;
import com.governmentcio.dmp.dao.UserDao;
import com.governmentcio.dmp.model.Project;
import com.governmentcio.dmp.projectservice.exception.ProjectServiceException;
import com.governmentcio.dmp.repository.ProjectRepository;
import com.governmentcio.dmp.repository.ProjectUserRepository;
import com.governmentcio.dmp.repository.UserRepository;

/**
 * 
 * @author <a href=mailto:support@governmentcio.com>support</a>
 *
 */
@Service
public class ProjectServiceImpl implements ProjectService {

	/**
	 * Logger instance.
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(ProjectServiceImpl.class.getName());

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ProjectUserRepository projectUserRepository;

	/**
	 * @param projectRepository the projectRepository to set
	 */
	@Autowired
	public void setProjectRepository(ProjectRepository projectRepository) {
		this.projectRepository = projectRepository;
	}

	/**
	 * @param userRepository the userRepository to set
	 */
	@Autowired
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * @param projectUserRepository the projectUserRepository to set
	 */
	@Autowired
	public void setProjectUserRepository(
			ProjectUserRepository projectUserRepository) {
		this.projectUserRepository = projectUserRepository;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.governmentcio.dmp.projectservice.controller.ProjectService#
	 * getAllProjects()
	 */
	@Override
	public Iterable<Project> getAllProjects() {

		Iterable<ProjectDao> iterableProjectDaos = projectRepository.findAll();

		Set<Project> projects = new HashSet<Project>();

		for (ProjectDao nextDao : iterableProjectDaos) {
			Project project = DomainFactory.createProject(nextDao);
			if (null != project) {
				projects.add(project);
			}
		}

		return projects;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.governmentcio.dmp.projectservice.controller.ProjectService#addProject(
	 * java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public Project addProject(
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "description", required = true) String description) {

		if (null == name) {
			throw new IllegalArgumentException("Name was null");
		}
		if (null == description) {
			throw new IllegalArgumentException("Description was null");
		}

		LOG.info("Adding project [" + name + "]-[" + description + "].");

		ProjectDao newProjectDao = projectRepository
				.save(new ProjectDao(name, description));

		Project project = DomainFactory.createProject(newProjectDao);

		LOG.info("Project [" + project + "] added.");

		return project;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	@Override
	public Project getProjectByName(@PathVariable String name) {

		if ((name == null) || (name.length() == 0)) {
			throw new IllegalArgumentException("Project name was null or empty");
		}

		return DomainFactory.createProject(projectRepository.findByName(name));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.governmentcio.dmp.projectservice.controller.ProjectService#
	 * updateProject(com.governmentcio.dmp.model.Project)
	 */
	@Override
	@Transactional
	public void updateProject(@RequestBody(required = true) Project project)
			throws ProjectServiceException {

		if (null == project) {
			throw new IllegalArgumentException("Project parameter was null.");
		}

		Optional<ProjectDao> retrievedProjectDao = projectRepository
				.findById(project.getId());

		if (!retrievedProjectDao.isPresent()) {
			throw new ProjectServiceException(
					"Project [" + project.getName() + "] not found for update.");
		}

		retrievedProjectDao.get().setName(project.getName());
		retrievedProjectDao.get().setDescription(project.getDescription());

		projectRepository.save(retrievedProjectDao.get());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.governmentcio.dmp.projectservice.controller.ProjectService#
	 * removeProject(java.lang.String)
	 */
	@Override
	public void removeProject(@PathVariable String name) {

		if ((name == null) || (name.length() == 0)) {
			throw new IllegalArgumentException("Nname parameter was null or empty");
		}

		ProjectDao retrievedProjectDao = projectRepository.findByName(name);

		if (null != retrievedProjectDao) {
			projectRepository.delete(retrievedProjectDao);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.governmentcio.dmp.projectservice.controller.ProjectService#
	 * addUserToProject(java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public void addUserToProject(
			@RequestParam(value = "userName", required = true) String userName,
			@RequestParam(value = "projectName", required = true) String projectName)
			throws ProjectServiceException {

		if (null == userName) {
			throw new IllegalArgumentException("User name was null");
		}
		if (null == projectName) {
			throw new IllegalArgumentException("Project name was null");
		}

		LOG.info("Adding user [" + userName + " to project [" + projectName + "].");

		ProjectDao projectDao = projectRepository.findByName(projectName);

		if (null == projectDao) {
			throw new ProjectServiceException(
					"Project [" + projectName + "] not found.");
		}

		UserDao userDao = userRepository.findByuserName(userName);

		if (null == userDao) {
			throw new ProjectServiceException("User [" + userName + "] not found.");
		}

		ProjectUserDao projectUserDao = new ProjectUserDao(projectDao, userDao);

		projectUserRepository.save(projectUserDao);

		userDao.addProjectUserDao(projectUserDao);

		userRepository.save(userDao);

		projectDao.getProjectUserDaos().add(projectUserDao);

		projectRepository.save(projectDao);

		LOG.info("User [" + userName + "] added to project [" + projectName + "].");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.governmentcio.dmp.projectservice.controller.ProjectService#
	 * removeUserFromProject(java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public void removeUserFromProject(
			@RequestParam(value = "userName", required = true) String userName,
			@RequestParam(value = "projectName", required = true) String projectName)
			throws ProjectServiceException {

		if (null == userName) {
			throw new IllegalArgumentException("User name was null");
		}
		if (null == projectName) {
			throw new IllegalArgumentException("Project name was null");
		}

		LOG.info(
				"Removing user [" + userName + " from project [" + projectName + "].");

		ProjectDao projectDao = projectRepository.findByName(projectName);

		if (null == projectDao) {
			throw new ProjectServiceException(
					"Project [" + projectName + "] not found.");
		}

		UserDao userDao = userRepository.findByuserName(userName);

		if (null == userDao) {
			throw new ProjectServiceException("User [" + userName + "] not found.");
		}

		Set<ProjectUserDao> projectUserDaos = new HashSet<>(
				userDao.getProjectUserDaos());

		for (ProjectUserDao projectUserDao : projectUserDaos) {
			if (projectUserDao.getUserDao().equals(userDao)
					&& projectUserDao.getProjectDao().equals(projectDao)) {

				userDao.getProjectUserDaos().remove(projectUserDao);
				userRepository.save(userDao);

				projectDao.getProjectUserDaos().remove(projectUserDao);
				projectRepository.save(projectDao);

				projectUserRepository.delete(projectUserDao);
			}
		}

		LOG.info("User [" + userName + "] removed from [" + projectName + "].");

	}

}
