package com.mindmirror.service;

import com.mindmirror.dto.response.ChallengeResponse;
import com.mindmirror.entity.Challenge;
import com.mindmirror.entity.enums.ChallengeStatus;
import com.mindmirror.entity.enums.NotificationType;
import com.mindmirror.exception.NotFoundException;
import com.mindmirror.repository.ChallengeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class ChallengeService {

    private record Template(String type, String titleEn, String titleMk,
                            String descEn, String descMk, int xp) { }

    /** One challenge is generated per day, rotating deterministically by date. */
    private static final List<Template> TEMPLATES = List.of(
            new Template("hydration", "Drink 2L of water", "Испиј 2 литри вода",
                    "Stay hydrated throughout the day.", "Остани хидриран во текот на денот.", 20),
            new Template("activity", "Walk 30 minutes", "Прошетај 30 минути",
                    "Get moving outdoors for at least 30 minutes.", "Движи се на отворено барем 30 минути.", 25),
            new Template("sleep", "Sleep before 23:00", "Легни пред 23:00",
                    "Give your body enough rest tonight.", "Дај си доволно одмор вечерва.", 25),
            new Template("screen", "No phone after 21:00", "Без телефон по 21:00",
                    "Put screens away in the evening.", "Тргни ги екраните навечер.", 20),
            new Template("stress", "Meditate 10 minutes", "Медитирај 10 минути",
                    "Take 10 minutes to breathe and relax.", "Одвои 10 минути за дишење и релаксација.", 20),
            new Template("social", "Spend time with a friend", "Помини време со пријател",
                    "Connect with someone you care about.", "Поврзи се со некој до кого ти е гајле.", 20),
            new Template("productivity", "Take a break every study hour", "Паузирај на секој час учење",
                    "Short breaks keep you focused.", "Кратките паузи те одржуваат фокусиран.", 15)
    );

    private final ChallengeRepository challengeRepository;
    private final UserStatsService statsService;
    private final NotificationService notificationService;
    private final AchievementService achievementService;

    public ChallengeService(ChallengeRepository challengeRepository, UserStatsService statsService,
                            NotificationService notificationService, AchievementService achievementService) {
        this.challengeRepository = challengeRepository;
        this.statsService = statsService;
        this.notificationService = notificationService;
        this.achievementService = achievementService;
    }

    @Transactional
    public Challenge ensureDailyChallenge(Long userId, LocalDate date) {
        return challengeRepository.findByUserIdAndChallengeDate(userId, date)
                .orElseGet(() -> {
                    Template t = TEMPLATES.get((int) (Math.floorMod(date.toEpochDay(), TEMPLATES.size())));
                    Challenge c = new Challenge();
                    c.setUserId(userId);
                    c.setChallengeDate(date);
                    c.setType(t.type());
                    c.setTitleEn(t.titleEn());
                    c.setTitleMk(t.titleMk());
                    c.setDescriptionEn(t.descEn());
                    c.setDescriptionMk(t.descMk());
                    c.setXpReward(t.xp());
                    c.setStatus(ChallengeStatus.ASSIGNED);
                    return challengeRepository.save(c);
                });
    }

    @Transactional
    public ChallengeResponse today(Long userId) {
        return ChallengeResponse.from(ensureDailyChallenge(userId, LocalDate.now()));
    }

    @Transactional(readOnly = true)
    public List<ChallengeResponse> list(Long userId) {
        return challengeRepository.findByUserIdOrderByChallengeDateDesc(userId).stream()
                .map(ChallengeResponse::from).toList();
    }

    @Transactional
    public ChallengeResponse complete(Long userId, Long challengeId) {
        Challenge c = challengeRepository.findById(challengeId)
                .filter(x -> x.getUserId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Challenge not found"));

        if (c.getStatus() == ChallengeStatus.COMPLETED) {
            return ChallengeResponse.from(c);
        }
        c.setStatus(ChallengeStatus.COMPLETED);
        c.setCompletedAt(Instant.now());
        challengeRepository.save(c);

        statsService.addXp(userId, c.getXpReward());
        notificationService.create(userId, NotificationType.CHALLENGE_COMPLETED,
                "Challenge completed! +" + c.getXpReward() + " XP",
                "Предизвикот е завршен! +" + c.getXpReward() + " XP",
                c.getTitleEn(), c.getTitleMk());
        achievementService.evaluate(userId);
        return ChallengeResponse.from(c);
    }
}
