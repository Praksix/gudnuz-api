package com.gudnuz.api.scheduler;

import com.gudnuz.api.service.FlareService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlareScheduler {

    private final FlareService flareService;

   @Scheduled(cron = "0 0 8,12,20,00 * * *")
   //@Scheduled(cron = "0 */1 * * * *")
    public void runElection() {
        flareService.electFlare();
    }
}