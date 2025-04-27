package com.example.dormmatching.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "desired_roommate_profile")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DesiredRoommateProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "desire_id", nullable = false)
    private Integer desireId;

    @OneToOne
    @JoinColumn(name = "identifier", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_gender", length = 6)
    private Gender preferredGender;

    @Column(name = "preferred_mbti", length = 4)
    private String preferredMbti;

    @Column(name = "birth_year_from")
    private Integer birthYearFrom;

    @Column(name = "birth_year_to")
    private Integer birthYearTo;

    @Column(name = "grade_from")
    private Integer gradeFrom;

    @Column(name = "grade_to")
    private Integer gradeTo;

    @Column(name = "allow_smoking")
    private Boolean allowSmoking;

    @Enumerated(EnumType.STRING)
    @Column(name = "cleaning_requirement", length = 20)
    private CleaningFreq cleaningRequirement;

    @Enumerated(EnumType.STRING)
    @Column(name = "sleep_habit_tolerance", length = 10)
    private Tolerance sleepHabitTolerance;

    @Enumerated(EnumType.STRING)
    @Column(name = "noise_tolerance", length = 10)
    private NoiseTolerance noiseTolerance;

    @Enumerated(EnumType.STRING)
    @Column(name = "alarm_tolerance", length = 10)
    private AlarmTolerance alarmTolerance;

    @Enumerated(EnumType.STRING)
    @Column(name = "light_tolerance", length = 10)
    private LightTolerance lightTolerance;

    @Enumerated(EnumType.STRING)
    @Column(name = "temperature_pref", length = 10)
    private TempPref temperaturePref;

    @Enumerated(EnumType.STRING)
    @Column(name = "drinking_tolerance", length = 10)
    private DrinkingTolerance drinkingTolerance;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_tolerance", length = 10)
    private DeliveryTolerance deliveryTolerance;

    @Enumerated(EnumType.STRING)
    @Column(name = "night_snack_tolerance", length = 10)
    private NightSnackTolerance nightSnackTolerance;

    @Enumerated(EnumType.STRING)
    @Column(name = "study_location_pref", length = 10)
    private StudyLocationPref studyLocationPref;

    @Enumerated(EnumType.STRING)
    @Column(name = "guest_visit_pref", length = 10)
    private GuestPolicy guestVisitPref;

    @Enumerated(EnumType.STRING)
    @Column(name = "indoor_food_pref", length = 15)
    private IndoorFoodPolicy indoorFoodPref;

    @Lob
    @Column(name = "extra_requests")
    private String extraRequests;


    // —————————— ENUM 정의 ——————————

    public enum Gender { 남, 여, 무관 }
    public enum CleaningFreq { 매일, 주마다, 달마다더러워지면 }
    public enum Tolerance { 없었으면, 약간은괜찮음, 상관없음 }
    public enum NoiseTolerance { 이어폰만, 작게, 상관없음 }
    public enum AlarmTolerance { _1개, _5개이하, 상관없음 }
    public enum LightTolerance { 어두움, 중간, 밝음, 상관없음 }
    public enum TempPref { 적게틀, 중간, 많이틀, 상관없음 }
    public enum DrinkingTolerance { 안마심, 가끔, 자주, 매일, 상관없음 }
    public enum DeliveryTolerance { 안먹음, 가끔, 자주, 상관없음 }
    public enum NightSnackTolerance { 안먹음, 가끔, 자주, 상관없음 }
    public enum StudyLocationPref { 기숙사내, 기숙사밖, 유동적, 상관없음 }
    public enum GuestPolicy { 안됨, 허락받고, 상관없음 }
    public enum IndoorFoodPolicy { 상관없음, 냄새안나는것만, 환기필수 }
}
