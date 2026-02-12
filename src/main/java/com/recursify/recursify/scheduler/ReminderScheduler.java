package com.recursify.recursify.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.recursify.recursify.model.Question;
import com.recursify.recursify.model.User;
import com.recursify.recursify.repository.QuestionRepository;
import com.recursify.recursify.repository.UserRepository;

@Service
public class ReminderScheduler {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    public ReminderScheduler(UserRepository userRepository,
                             QuestionRepository questionRepository) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
    }

    // Runs daily at 9 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDailyReminders() {

        List<User> users = userRepository.findAll();
        LocalDate today = LocalDate.now();

        for (User user : users) {

            List<Question> dueQuestions =
                    questionRepository.findByUserIdAndNextRevisionDateLessThanEqual(
                            user.getId(), today);

            if (!dueQuestions.isEmpty()) {

                System.out.println("Reminder → " + user.getEmail()
                        + " | Questions due today: " + dueQuestions.size());

                // Later → Email / Push notification here
            }
        }
    }
}
