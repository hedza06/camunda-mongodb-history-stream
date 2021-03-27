package com.camhistory.stream;import com.camhistory.stream.entity.TaskEventEntity;import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.stereotype.Repository;@Repositorypublic interface CustomTaskActivityRepository extends JpaRepository<TaskEventEntity, Integer>{    TaskEventEntity findByExecutionIdAndTaskId(String executionId, String taskId);    TaskEventEntity findFirstByProcessInstanceId(String processInstanceId);}