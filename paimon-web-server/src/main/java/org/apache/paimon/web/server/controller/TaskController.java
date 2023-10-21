package org.apache.paimon.web.server.controller;

import org.apache.paimon.web.common.data.vo.SubmitResult;
import org.apache.paimon.web.server.data.enums.TaskType;
import org.apache.paimon.web.task.SubmitTask;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/task")
@AllArgsConstructor
public class TaskController {
    @PostMapping("/executeSql")
    public SubmitResult submitExecuteSql(String sql, String taskType) {
        SubmitTask task = TaskType.valueOf(taskType.toUpperCase()).getTask();
        try {
            return task.execute(sql);
        } catch (Exception e) {
            return null;
        }
    }
}
