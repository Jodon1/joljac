package com.example.dormmatching.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "user_profile")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id", nullable = false)
    private Integer profileId;

    @OneToOne
    @JoinColumn(name = "identifier", nullable = false)
    private UserEntity user;

    @Column(name = "mbti", length = 4)
    private String mbti;

    @Column(name = "sleep_time")
    private LocalTime sleepTime;

    @Column(name = "wake_time")
    private LocalTime wakeTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "sleep_habit", length = 10)
    private SleepHabit sleepHabit;

    @Enumerated(EnumType.STRING)
    @Column(name = "alarm_count", length = 10)
    private AlarmCount alarmCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "light_pref", length = 6)
    private LightPref lightPref;

    @Enumerated(EnumType.STRING)
    @Column(name = "cleaning_freq", length = 20)
    private CleaningFreq cleaningFreq;

    @Enumerated(EnumType.STRING)
    @Column(name = "shower_duration", length = 10)
    private ShowerDuration showerDuration;

    @Enumerated(EnumType.STRING)
    @Column(name = "shower_period", length = 10)
    private ShowerPeriod showerPeriod;

    @Enumerated(EnumType.STRING)
    @Column(name = "fragrance_sensitivity", length = 6)
    private FragranceSensitivity fragranceSensitivity;

    @Enumerated(EnumType.STRING)
    @Column(name = "perfume_usage", length = 10)
    private PerfumeUsage perfumeUsage;

    @Enumerated(EnumType.STRING)
    @Column(name = "summer_cooling_usage", length = 10)
    private FanHeaterUsage summerCoolingUsage;

    @Enumerated(EnumType.STRING)
    @Column(name = "winter_heating_usage", length = 10)
    private FanHeaterUsage winterHeatingUsage;

    @Column(name = "smoking")
    private Boolean smoking;

    @Enumerated(EnumType.STRING)
    @Column(name = "drinking_frequency", length = 10)
    private DrinkingFreq drinkingFrequency;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_frequency", length = 10)
    private Frequency deliveryFrequency;

    @Enumerated(EnumType.STRING)
    @Column(name = "night_snack_frequency", length = 10)
    private Frequency nightSnackFrequency;

    @Enumerated(EnumType.STRING)
    @Column(name = "study_location", length = 10)
    private StudyLocation studyLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "home_visit_frequency", length = 10)
    private HomeVisitFreq homeVisitFrequency;

    @Enumerated(EnumType.STRING)
    @Column(name = "talking_volume", length = 6)
    private TalkingVolume talkingVolume;

    @Enumerated(EnumType.STRING)
    @Column(name = "phone_call_policy", length = 10)
    private PhoneCallPolicy phoneCallPolicy;

    @Enumerated(EnumType.STRING)
    @Column(name = "insect_tolerance", length = 10)
    private InsectTolerance insectTolerance;

    @Enumerated(EnumType.STRING)
    @Column(name = "guest_visit_policy", length = 10)
    private GuestPolicy guestVisitPolicy;

    @Enumerated(EnumType.STRING)
    @Column(name = "indoor_food_policy", length = 15)
    private IndoorFoodPolicy indoorFoodPolicy;


    // —————————— ENUM 정의 ——————————

    public enum SleepHabit { 없음, 코골이, 이갈이, 잠꼬대, 혼합 }
    public enum AlarmCount { 없음, _5개이하, _10개이하, _10개초과 }
    public enum LightPref { 어두움, 중간, 밝음 }
    public enum CleaningFreq { 매일, 주마다, 달마다더러워지면 }
    public enum ShowerDuration { _5분, _10분, _15분, _20분, _30분, _40분, _1시간 }
    public enum ShowerPeriod { 아침, 저녁, 유동적, 둘다 }
    public enum FragranceSensitivity { 민감, 중간, 둔감 }
    public enum PerfumeUsage { 미사용, 가끔, 매일 }
    public enum FanHeaterUsage { 적게틀, 중간, 많이틀 }
    public enum DrinkingFreq { 안마심, 매달, 가끔, 자주, 매일 }
    public enum Frequency { 안먹음, 가끔, 자주 }
    public enum StudyLocation { 기숙사내, 기숙사밖, 유동적 }
    public enum HomeVisitFreq { 안감, 가끔, 매달, 격주, 매주 }
    public enum TalkingVolume { 작게, 보통, 크게 }
    public enum PhoneCallPolicy { 상관없음, 짧게, 밖에서 }
    public enum InsectTolerance { 못잡음, 작은것만, 전부잡음 }
    public enum GuestPolicy { 안됨, 허락받고, 상관없음 }
    public enum IndoorFoodPolicy { 상관없음, 냄새안나는것만, 환기필수 }
}
