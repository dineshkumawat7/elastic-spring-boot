package com.example.elastic.config;

import com.example.elastic.service.DataSyncService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class ScheduledTask {
    @Autowired
    private DataSyncService dataSyncService;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

//    @Scheduled(fixedDelay = 1000 * 10, initialDelay = 1000 * 5)
    public void currentTime(){
        log.info("The time is now {}", dateFormat.format(new Date()));
    }

    @Scheduled(fixedRate = 1000 * 60)
    public void performSync(){
        dataSyncService.syncData();
        log.info("User data sync at: {}", new Date());
    }
}
