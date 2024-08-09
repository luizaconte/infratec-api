package br.com.infratec.service.sales;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

@Service
public class MeliSchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeliSchedulerService.class);

    Map<String, ScheduledFuture<?>> jobsMap = new HashMap<>();

    private final TaskScheduler taskScheduler;


    @Autowired
    public MeliSchedulerService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    private void adicionarJob(String jobId, Runnable tasklet, String cronExpression) {
        LOGGER.info("Agendando tarefa com id: " + jobId + " e expressão CRON: " + cronExpression);
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(tasklet, new CronTrigger(cronExpression, TimeZone.getTimeZone(TimeZone.getDefault().getID())));
        jobsMap.put(jobId, scheduledTask);
    }

    private void removerJob(String jobId) {
        LOGGER.info("Removendo tarefa com id: " + jobId);
        ScheduledFuture<?> scheduledTask = jobsMap.get(jobId);
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            jobsMap.remove(jobId);
        }
    }

    public void configurar() {
    }

}
