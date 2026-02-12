package com.recursify.recursify.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.recursify.recursify.model.User;
import com.recursify.recursify.repository.UserRepository;

@Service
public class StreakScheduler {

    private final UserRepository userRepository;

    public StreakScheduler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?") 
    public void resetMissedStreaks() {

        List<User> users = userRepository.findAll();
        LocalDate today = LocalDate.now();

        for (User user : users) {

            if (user.getLastSolvedDate() == null) continue;

            if (user.getLastSolvedDate().plusDays(1).isBefore(today)) {
                user.setStreakCount(0);
                userRepository.save(user);
            }
        }
    }
}
