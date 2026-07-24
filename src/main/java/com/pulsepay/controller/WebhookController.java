package com.pulsepay.controller;

import com.pulsepay.dto.webhook.MockWorkoutPayload;
import com.pulsepay.service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLOutput;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final RewardService rewardService;

    public WebhookController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @PostMapping("/mock")
    public ResponseEntity<String> receiveMockWorkout(@RequestBody MockWorkoutPayload payload){
        System.out.println("🚨Incoming workout DETECTED 🚨");
        System.out.println("Athlete: " + payload.username());
        System.out.println("Distance: " + payload.distanceKm());
        System.out.println("Duration: " + payload.durationMinutes());

       rewardService.processWorkoutReward(payload.username(),payload.distanceKm());

        return ResponseEntity.ok("Workout received,rewarded and receipt generated");
    }
}
