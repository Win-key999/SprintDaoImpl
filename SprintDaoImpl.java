package com.pennant.prodmtr.Dao.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.LockTimeoutException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.QueryTimeoutException;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.prodmtr.Dao.Interface.SprintDao;
import com.pennant.prodmtr.model.Dto.ModuleDTO;
import com.pennant.prodmtr.model.Dto.UserDto;
import com.pennant.prodmtr.model.Entity.FunctionalUnit;
import com.pennant.prodmtr.model.Entity.Module;
import com.pennant.prodmtr.model.Entity.Sprint;
import com.pennant.prodmtr.model.Entity.SprintResource;
import com.pennant.prodmtr.model.Entity.SprintTasks;
import com.pennant.prodmtr.model.Entity.Task;
import com.pennant.prodmtr.model.Entity.User;
import com.pennant.prodmtr.service.Impl.SprintServiceImpl;

@Repository
@Transactional
public class SprintDaoImpl implements SprintDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	private static final Logger logger = LoggerFactory.getLogger(SprintServiceImpl.class);


	@Override
	public List<Sprint> getBaskLogs() throws IllegalArgumentException, IllegalStateException, PersistenceException,
	        TransactionRequiredException, QueryTimeoutException, NoResultException {
	    try {
	        // Create the JPQL query to retrieve the backlog sprints
	        String query = "SELECT s FROM Sprint s WHERE EXISTS (SELECT 1 FROM Task t WHERE t.module.id = s.moduleId.id AND t.taskCompletedDateTime IS NULL)";
	        // Execute the query and retrieve the result list
	        List<Sprint> backlogSprints = entityManager.createQuery(query, Sprint.class).getResultList();
	        // Log the successful retrieval of the backlog sprints
	        logger.info("Successfully retrieved backlog sprints");
	        return backlogSprints;
	    } catch (IllegalArgumentException e) {
	        String errorMessage = "Invalid request. Please provide a valid query parameter.";
	        // Log the error and throw an IllegalArgumentException
	        logger.error(errorMessage, e);
	        throw new IllegalArgumentException(errorMessage, e);
	    } catch (IllegalStateException e) {
	        String errorMessage = "Internal error. Please try again later.";
	        // Log the error and throw an IllegalStateException
	        logger.error(errorMessage, e);
	        throw new IllegalStateException(errorMessage, e);
	    } catch (QueryTimeoutException e) {
	        String errorMessage = "The execution of the query has exceeded the specified timeout.";
	        // Log the error and throw a QueryTimeoutException
	        logger.error(errorMessage, e);
	        throw new QueryTimeoutException(errorMessage, e);
	    } catch (PersistenceException e) {
	        String errorMessage = "An error occurred while retrieving data from the database.";
	        // Log the error and throw a PersistenceException
	        logger.error(errorMessage, e);
	        throw new PersistenceException(errorMessage, e);
	    }
	}


	@Override
	public Sprint getSprintDetails(int sprintId)
	        throws IllegalArgumentException, EntityNotFoundException, TransactionRequiredException,
	        QueryTimeoutException, PessimisticLockException, LockTimeoutException, PersistenceException {
	    try {
	        // Retrieve the sprint details using the provided sprintId
	        Sprint sprint = entityManager.find(Sprint.class, sprintId);
	        // Log the successful retrieval of the sprint details
	        logger.info("Successfully retrieved sprint details for sprint ID: " + sprintId);
	        return sprint;
	    } catch (IllegalArgumentException e) {
	        // Log the error and throw an IllegalArgumentException
	        logger.error("Invalid sprint ID. Please provide a valid ID.", e);
	        throw new IllegalArgumentException("Invalid sprint ID. Please provide a valid ID.", e);
	    } catch (EntityNotFoundException e) {
	        // Log the error and throw an EntityNotFoundException
	        logger.error("Sprint not found for the given ID.", e);
	        throw new EntityNotFoundException("Sprint not found for the given ID.");
	    } catch (TransactionRequiredException e) {
	        // Log the error and throw a TransactionRequiredException
	        logger.error("Transaction is required to fetch the sprint details.", e);
	        throw new TransactionRequiredException("Transaction is required to fetch the sprint details.");
	    } catch (QueryTimeoutException e) {
	        // Log the error and throw a QueryTimeoutException
	        logger.error("Fetching sprint details took longer than expected. Please try again later.", e);
	        throw new QueryTimeoutException("Fetching sprint details took longer than expected. Please try again later.", e);
	    } catch (PessimisticLockException e) {
	        // Log the error and throw a PessimisticLockException
	        logger.error("Unable to fetch sprint details due to a lock conflict.", e);
	        throw new PessimisticLockException("Unable to fetch sprint details due to a lock conflict.", e);
	    } catch (LockTimeoutException e) {
	        // Log the error and throw a LockTimeoutException
	        logger.error("Fetching sprint details exceeded the lock timeout. Please try again later.", e);
	        throw new LockTimeoutException("Fetching sprint details exceeded the lock timeout. Please try again later.", e);
	    } catch (PersistenceException e) {
	        // Log the error and throw a PersistenceException
	        logger.error("An error occurred while fetching the sprint details.", e);
	        throw new PersistenceException("An error occurred while fetching the sprint details.", e);
	    }
	}

	@Override
	public List<Task> getTasks(int modlId)
	        throws IllegalArgumentException, EntityNotFoundException, TransactionRequiredException,
	        QueryTimeoutException, PersistenceException, NoResultException, NonUniqueResultException {
	    try {
	        // Construct the query to retrieve tasks based on the module ID
	        String query = "SELECT t FROM Task t WHERE t.module.id = :modlId";
	        // Set the module ID parameter in the query
	        List<Task> tasks = entityManager.createQuery(query, Task.class)
	                .setParameter("modlId", modlId)
	                .getResultList();
	        // Log the successful retrieval of tasks
	        logger.info("Successfully retrieved tasks for module ID: " + modlId);
	        return tasks;
	    } catch (IllegalArgumentException e) {
	        // Log the error and throw an IllegalArgumentException
	        logger.error("Invalid module ID. Please provide a valid ID.", e);
	        throw new IllegalArgumentException("Invalid module ID. Please provide a valid ID.", e);
	    } catch (EntityNotFoundException e) {
	        // Log the error and throw an EntityNotFoundException
	        logger.error("Module not found for the given ID.", e);
	        throw new EntityNotFoundException("Module not found for the given ID.");
	    } catch (TransactionRequiredException e) {
	        // Log the error and throw a TransactionRequiredException
	        logger.error("Unable to fetch tasks. Please try again later.", e);
	        throw new TransactionRequiredException("Unable to fetch tasks. Please try again later.");
	    } catch (QueryTimeoutException e) {
	        // Log the error and throw a QueryTimeoutException
	        logger.error("Fetching tasks took longer than expected. Please try again later.", e);
	        throw new QueryTimeoutException("Fetching tasks took longer than expected. Please try again later.", e);
	    } catch (NoResultException e) {
	        // Log the error and throw a NoResultException
	        logger.error("No tasks found for the specified module ID.", e);
	        throw new NoResultException("No tasks found for the specified module ID.");
	    } catch (NonUniqueResultException e) {
	        // Log the error and throw a NonUniqueResultException
	        logger.error("Multiple tasks found for the specified module ID. Please contact support for assistance.", e);
	        throw new NonUniqueResultException("Multiple tasks found for the specified module ID. Please contact support for assistance.");
	    } catch (PersistenceException e) {
	        // Log the error and throw a PersistenceException
	        logger.error("An error occurred while fetching tasks. Please try again later.", e);
	        throw new PersistenceException("An error occurred while fetching tasks. Please try again later.", e);
	    }
	}

	@Override
	public List<Sprint> getAllSprints() throws IllegalArgumentException, TransactionRequiredException,
	        QueryTimeoutException, PersistenceException, NoResultException, NonUniqueResultException {
	    try {
	        // Construct the query to retrieve all sprints
	        String query = "SELECT s FROM Sprint s";
	        // Execute the query and get the list of sprints
	        List<Sprint> sprints = entityManager.createQuery(query, Sprint.class).getResultList();
	        // Log the successful retrieval of sprints
	        logger.info("Successfully retrieved all sprints.");
	        return sprints;
	    } catch (IllegalArgumentException e) {
	        // Log the error and throw an IllegalArgumentException
	        logger.error("Invalid query. Please provide a valid query.", e);
	        throw new IllegalArgumentException("Invalid query. Please provide a valid query.", e);
	    } catch (TransactionRequiredException e) {
	        // Log the error and throw a TransactionRequiredException
	        logger.error("Transaction is required to perform the query operation.", e);
	        throw new TransactionRequiredException("Transaction is required to perform the query operation.");
	    } catch (QueryTimeoutException e) {
	        // Log the error and throw a QueryTimeoutException
	        logger.error("The execution of the query has exceeded the specified timeout.", e);
	        throw new QueryTimeoutException("The execution of the query has exceeded the specified timeout.", e);
	    } catch (NoResultException e) {
	        // Log the error and throw a NoResultException
	        logger.error("No sprints found.", e);
	        throw new NoResultException("No sprints found.");
	    } catch (NonUniqueResultException e) {
	        // Log the error and throw a NonUniqueResultException
	        logger.error("Multiple sprints found. Please contact support for assistance.", e);
	        throw new NonUniqueResultException("Multiple sprints found. Please contact support for assistance.");
	    } catch (PersistenceException e) {
	        // Log the error and throw a PersistenceException
	        logger.error("An error occurred while fetching sprints.", e);
	        throw new PersistenceException("An error occurred while fetching sprints.", e);
	    }
	}


	@Override
	public Sprint storeSprint(Sprint sprint)
	        throws IllegalArgumentException, TransactionRequiredException, EntityExistsException, PersistenceException {
	    try {
	        if (sprint.getSprintId() == 0) {
	            entityManager.persist(sprint); // New entity, use persist
	        } else {
	            entityManager.merge(sprint); // Existing entity, use merge
	        }
	        
	        // Log the retrieved sprint from the database
	        logger.info("Retrieved from the database: {}", sprint);
	        
	        return sprint;
	    } catch (IllegalArgumentException e) {
	        // Log the error and throw an IllegalArgumentException
	        logger.error("Invalid sprint data. Please provide valid data.", e);
	        throw new IllegalArgumentException("Invalid sprint data. Please provide valid data.", e);
	    } catch (TransactionRequiredException e) {
	        // Log the error and throw a TransactionRequiredException
	        logger.error("Transaction is required to perform the store operation.", e);
	        throw new TransactionRequiredException("Transaction is required to perform the store operation.", e);
	    } catch (EntityExistsException e) {
	        // Log the error and throw an EntityExistsException
	        logger.error("Sprint already exists in the database.", e);
	        throw new EntityExistsException("Sprint already exists in the database.", e);
	    } catch (PersistenceException e) {
	        // Log the error and throw a PersistenceException
	        logger.error("An error occurred while storing the sprint.", e);
	        throw new PersistenceException("An error occurred while storing the sprint.", e);
	    }
	}


	@Override
	public List<SprintTasks> getAllTasksBySprintId(Sprint sprintId)
	        throws IllegalArgumentException, TransactionRequiredException, QueryTimeoutException, PersistenceException,
	        NoResultException, NonUniqueResultException {
	    try {
	        // Construct the query to retrieve all tasks by sprint ID
	        String query = "SELECT st FROM SprintTasks st WHERE st.id.sprnId = :sprintId";
	        // Set the sprint ID parameter in the query
	        List<SprintTasks> tasks = entityManager.createQuery(query, SprintTasks.class)
	                .setParameter("sprintId", sprintId).getResultList();
	        // Log the successful retrieval of tasks
	        logger.info("Successfully retrieved all tasks for sprint ID: " + sprintId);
	        return tasks;
	    } catch (IllegalArgumentException e) {
	        // Log the error and throw an IllegalArgumentException
	        logger.error("Invalid sprint ID. Please provide a valid ID.", e);
	        throw new IllegalArgumentException("Invalid sprint ID. Please provide a valid ID.", e);
	    } catch (TransactionRequiredException e) {
	        // Log the error and throw a TransactionRequiredException
	        logger.error("Transaction is required to perform the query operation.", e);
	        throw new TransactionRequiredException("Transaction is required to perform the query operation.");
	    } catch (QueryTimeoutException e) {
	        // Log the error and throw a QueryTimeoutException
	        logger.error("The execution of the query has exceeded the specified timeout.", e);
	        throw new QueryTimeoutException("The execution of the query has exceeded the specified timeout.", e);
	    } catch (NoResultException e) {
	        // Log the error and throw a NoResultException
	        logger.error("No tasks found for the specified sprint ID.", e);
	        throw new NoResultException("No tasks found for the specified sprint ID.");
	    } catch (NonUniqueResultException e) {
	        // Log the error and throw a NonUniqueResultException
	        logger.error("Multiple tasks found for the specified sprint ID.", e);
	        throw new NonUniqueResultException("Multiple tasks found for the specified sprint ID.");
	    } catch (PersistenceException e) {
	        // Log the error and throw a PersistenceException
	        logger.error("An error occurred while fetching tasks for the sprint.", e);
	        throw new PersistenceException("An error occurred while fetching tasks for the sprint.");
	    }
	}


	@Override
	public List<ModuleDTO> getSprintModulesByProjectId(int projectId)
	        throws IllegalArgumentException, QueryTimeoutException, PersistenceException, NoResultException {
	    try {
	        // Convert the projectId to short if necessary
	        short pid = (short) projectId;
	        // Construct the query to retrieve sprint modules by project ID
	        String query = "SELECT m FROM com.pennant.prodmtr.model.Entity.Module m WHERE m.moduleProject.projectId = :projectId AND m.moduleId NOT IN (SELECT s.moduleId.id FROM com.pennant.prodmtr.model.Entity.Sprint s)";
	        // Create a typed query and set the projectId parameter
	        TypedQuery<Module> typedQuery = entityManager.createQuery(query, Module.class);
	        typedQuery.setParameter("projectId", pid);
	        
	        // Execute the query and retrieve the list of modules
	        List<Module> moduleList = typedQuery.getResultList();
	        
	        // Log the retrieved modules
	        for (Module m : moduleList) {
	            logger.info("Retrieved module: " + m);
	        }
	        
	        // Convert the modules to ModuleDTO objects
	        List<ModuleDTO> moduleDTOList = new ArrayList<>();
	        for (Module m : moduleList) {
	            ModuleDTO md = ModuleDTO.fromEntity(m);
	            moduleDTOList.add(md);
	        }
	        
	        logger.info("Module: " + moduleList.get(0) + "  divider  " + moduleDTOList.get(0).getModl_id());
	        
	        return moduleDTOList;
	    } catch (IllegalArgumentException e) {
	        // Log the error and throw an IllegalArgumentException
	        logger.error("Invalid project ID. Please provide a valid ID.", e);
	        throw new IllegalArgumentException("Invalid project ID. Please provide a valid ID.", e);
	    } catch (QueryTimeoutException e) {
	        // Log the error and throw a QueryTimeoutException
	        logger.error("The execution of the query has exceeded the specified timeout.", e);
	        throw new QueryTimeoutException("The execution of the query has exceeded the specified timeout.", e);
	    } catch (NoResultException e) {
	        // Log the error and throw a NoResultException
	        logger.error("No sprint modules found for the specified project ID.", e);
	        throw new NoResultException("No sprint modules found for the specified project ID.");
	    } catch (PersistenceException e) {
	        // Log the error and throw a PersistenceException
	        logger.error("An error occurred while retrieving sprint modules.", e);
	        throw new PersistenceException("An error occurred while retrieving sprint modules.", e);
	    }
	}


	public List<FunctionalUnit> getFunctionalUnitsByModId(int modlId, int prjid)
	        throws IllegalArgumentException, QueryTimeoutException, PersistenceException {
	    try {
	        // Convert the modlId and prjid to short if necessary
	        short mId = (short) modlId;
	        short pId = (short) prjid;
	        String funstatus = null;
	        // Construct the query to retrieve functional units by module ID and project ID
	        String query = "SELECT fu FROM FunctionalUnit fu WHERE fu.id.module.id = :modlId AND fu.projectId.projectId = :prjid AND fu.funStatus is null";
	        
	        // Create a typed query and set the modlId and prjid parameters
	        TypedQuery<FunctionalUnit> typedQuery = entityManager.createQuery(query, FunctionalUnit.class);
	        typedQuery.setParameter("modlId", mId);
	        typedQuery.setParameter("prjid", pId);
	        
	        // Execute the query and retrieve the list of functional units
	        List<FunctionalUnit> functionalUnits = typedQuery.getResultList();
	        
	        // Log the retrieved functional units
	        for (FunctionalUnit fu : functionalUnits) {
	            logger.info("Retrieved functional unit: " + fu);
	        }
	        
	        return functionalUnits;
	    } catch (IllegalArgumentException e) {
	        // Log the error and throw an IllegalArgumentException
	        logger.error("Invalid module or project ID. Please provide valid IDs.", e);
	        throw new IllegalArgumentException("Invalid module or project ID. Please provide valid IDs.", e);
	    } catch (QueryTimeoutException e) {
	        // Log the error and throw a QueryTimeoutException
	        logger.error("The execution of the query has exceeded the specified timeout.", e);
	        throw new QueryTimeoutException("The execution of the query has exceeded the specified timeout.", e);
	    } catch (PersistenceException e) {
	        // Log the error and throw a PersistenceException
	        logger.error("An error occurred while retrieving functional units.", e);
	        throw new PersistenceException("An error occurred while retrieving functional units.", e);
	    }
	}


	public Task storeTask(Task task)
	        throws IllegalArgumentException, TransactionRequiredException, PersistenceException {
	    try {
	        if (task.getTaskId() == 0) {
	            entityManager.persist(task); // New entity, use persist
	            logger.info("Stored new task: " + task);
	        } else {
	            entityManager.merge(task); // Existing entity, use merge
	            logger.info("Updated task: " + task);
	        }
	        return task;
	    } catch (IllegalArgumentException e) {
	        // Log the error and throw an IllegalArgumentException
	        logger.error("Invalid task. Please provide a valid task.", e);
	        throw new IllegalArgumentException("Invalid task. Please provide a valid task.", e);
	    } catch (TransactionRequiredException e) {
	        // Log the error and throw a TransactionRequiredException
	        logger.error("Failed to store the task. Transaction is required.", e);
	        throw new TransactionRequiredException("Failed to store the task. Transaction is required.");
	    } catch (PersistenceException e) {
	        // Log the error and throw a PersistenceException
	        logger.error("An error occurred while storing the task. Please try again later.", e);
	        throw new PersistenceException("An error occurred while storing the task. Please try again later.", e);
	    }
	}


	@Override
	public List<UserDto> getAllResources() throws PersistenceException {
	    try {
	        String jpql = "SELECT r FROM User r";
	        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
	        List<User> users = query.getResultList();

	        List<UserDto> userDtos = users.stream().map(UserDto::fromEntity).collect(Collectors.toList());

	        // Log the number of resources retrieved
	        logger.info("Retrieved {} resources.", userDtos.size());

	        return userDtos;
	    } catch (IllegalArgumentException e) {
	        // Log the error and throw an IllegalArgumentException
	        logger.error("Invalid query. Please provide a valid JPQL query.", e);
	        throw new IllegalArgumentException("Invalid query. Please provide a valid JPQL query.", e);
	    } catch (PersistenceException e) {
	        // Log the error and throw a PersistenceException
	        logger.error("An error occurred while retrieving resources. Please try again later.", e);
	        throw new PersistenceException("An error occurred while retrieving resources. Please try again later.", e);
	    }
	}

	@Override
	public void storeSprintResource(SprintResource src) throws PersistenceException {
	    try {
	        // Store the sprint resource using the entity manager
	        entityManager.persist(src);

	        // Log a success message
	        logger.info("Sprint resource stored successfully.");
	    } catch (IllegalArgumentException e) {
	        // Log the error and throw an IllegalArgumentException
	        logger.error("Invalid sprint resource. Please provide a valid sprint resource.", e);
	        throw new IllegalArgumentException("Invalid sprint resource. Please provide a valid sprint resource.", e);
	    } catch (PersistenceException e) {
	        // Log the error and throw a PersistenceException
	        logger.error("An error occurred while storing the sprint resource. Please try again later.", e);
	        throw new PersistenceException("An error occurred while storing the sprint resource. Please try again later.", e);
	    }
	}

	public void storeSprintTasks(SprintTasks sprintTask) throws IllegalArgumentException, PersistenceException {
	    try {
	        // Store the sprint task using the entity manager
	        entityManager.persist(sprintTask);

	        // Log a success message
	        logger.info("Sprint task stored successfully.");
	    } catch (IllegalArgumentException e) {
	        // Log the error and throw an IllegalArgumentException
	        logger.error("Invalid sprint task. Please provide a valid sprint task.", e);
	        throw new IllegalArgumentException("Invalid sprint task. Please provide a valid sprint task.", e);
	    } catch (PersistenceException e) {
	        // Log the error and throw a PersistenceException
	        logger.error("An error occurred while storing the sprint task. Please try again later.", e);
	        throw new PersistenceException("An error occurred while storing the sprint task. Please try again later.", e);
	    }
	}

	@Override
	public List<Sprint> getSprintByProjId(int projId) throws IllegalArgumentException, PersistenceException {
	    try {
	        // Create a TypedQuery to retrieve sprints by project ID
	        TypedQuery<Sprint> query = entityManager.createQuery(
	                "SELECT s FROM Sprint s WHERE projectId.projectId = :projId", Sprint.class);
	        query.setParameter("projId", (short) projId);

	        // Execute the query and retrieve the list of sprints
	        List<Sprint> sprints = query.getResultList();

	        // Log a success message
	        logger.info("Retrieved sprints by project ID successfully.");

	        return sprints;
	    } catch (IllegalArgumentException e) {
	        // Log the error and throw an IllegalArgumentException
	        logger.error("Invalid project ID. Please provide a valid ID.", e);
	        throw new IllegalArgumentException("Invalid project ID. Please provide a valid ID.", e);
	    } catch (PersistenceException e) {
	        // Log the error and throw a PersistenceException
	        logger.error("An error occurred while retrieving sprints by project ID.", e);
	        throw new PersistenceException("An error occurred while retrieving sprints by project ID.", e);
	    }
	}

	public void updateFunctionalstatus(int funit)
	        throws IllegalArgumentException, TransactionRequiredException, PersistenceException, QueryTimeoutException,
	        LockTimeoutException, PessimisticLockException, OptimisticLockException, NoResultException {
	    String status = "Task";
	    String qry = "UPDATE FunctionalUnit f SET f.funStatus = :status WHERE f.id.funitid = :funit";

	    try {
	        // Execute the update query to update the functional status
	        entityManager.createQuery(qry)
	                .setParameter("funit", funit)
	                .setParameter("status", status)
	                .executeUpdate();

	        // Log a success message
	        logger.info("Functional status updated successfully.");
	    } catch (IllegalArgumentException e) {
	        // Log the error and throw an IllegalArgumentException
	        logger.error("Invalid functional unit. Please provide a valid functional unit ID.", e);
	        throw new IllegalArgumentException("Invalid functional unit. Please provide a valid functional unit ID.", e);
	    } catch (TransactionRequiredException e) {
	        // Log the error and throw a TransactionRequiredException
	        logger.error("Transaction is required to update the functional status.", e);
	        throw new TransactionRequiredException("Transaction is required to update the functional status.");
	    } catch (QueryTimeoutException e) {
	        // Log the error and throw a QueryTimeoutException
	        logger.error("The query execution has exceeded the specified timeout.", e);
	        throw new QueryTimeoutException("The query execution has exceeded the specified timeout.", e);
	    } catch (LockTimeoutException e) {
	        // Log the error and throw a LockTimeoutException
	        logger.error("Lock acquisition has timed out.", e);
	        throw new LockTimeoutException("Lock acquisition has timed out.", e);
	    } catch (PessimisticLockException e) {
	        // Log the error and throw a PessimisticLockException
	        logger.error("Pessimistic lock acquisition failed.", e);
	        throw new PessimisticLockException("Pessimistic lock acquisition failed.", e);
	    } catch (OptimisticLockException e) {
	        // Log the error and throw an OptimisticLockException
	        logger.error("Optimistic lock acquisition failed.", e);
	        throw new OptimisticLockException("Optimistic lock acquisition failed.", e);
	    } catch (NoResultException e) {
	        // Log the error and throw a NoResultException
	        logger.error("No result found for the provided functional unit ID.", e);
	        throw new NoResultException("No result found for the provided functional unit ID.");
	    } catch (PersistenceException e) {
	        // Log the error and throw a PersistenceException
	        logger.error("An error occurred while updating the functional status.", e);
	        throw new PersistenceException("An error occurred while updating the functional status.", e);
	    }
	}
}
