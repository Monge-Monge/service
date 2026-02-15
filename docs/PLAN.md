# 몽게몽게(MongeMonge) 백엔드 기능 개발 플랜

> 📅 작성일: 2025-02-15
> 🛠 스택: Kotlin + Spring Boot 4.0 + JPA + PostgreSQL + Clerk 인증
> 🏗 아키텍처: 헥사고날 아키텍처

---

## 🏋️ Phase 1: 몸무게 CRUD & 그래프 (핵심 기능)

### 1-1. Weight 도메인 설계

```kotlin
// weight/domain/Weight.kt
@Entity
@Table(name = "weights")
class Weight(
    val accountId: Long,
    val value: BigDecimal,        // 몸무게 (kg)
    val recordedAt: LocalDate,    // 기록 날짜
    val memo: String? = null,     // 간단 메모 (선택)
    @Id @GeneratedValue
    val id: Long? = null,
)
```

### 1-2. Weight CRUD API

| Method   | Endpoint           | 설명                              |
|----------|--------------------|-----------------------------------|
| `POST`   | `/api/weights`     | 몸무게 기록 생성                  |
| `GET`    | `/api/weights`     | 내 몸무게 목록 조회 (페이징, 기간 필터) |
| `GET`    | `/api/weights/{id}`| 단건 조회                         |
| `PUT`    | `/api/weights/{id}`| 몸무게 기록 수정                  |
| `DELETE` | `/api/weights/{id}`| 몸무게 기록 삭제                  |

### 1-3. 그래프용 통계 API

| Method | Endpoint                          | 설명               |
|--------|-----------------------------------|--------------------|
| `GET`  | `/api/weights/graph?period=WEEK`  | 주간 그래프 데이터 |
| `GET`  | `/api/weights/graph?period=MONTH` | 월간 그래프 데이터 |
| `GET`  | `/api/weights/graph?period=YEAR`  | 연간 그래프 데이터 |
| `GET`  | `/api/weights/stats`              | 통계 (최고/최저/평균/변화량) |

### 1-4. 패키지 구조 (기존 헥사고날 패턴 유지)

```
weight/
├── adapter/
│   └── web/
│       ├── WeightController.kt
│       ├── WeightRequest.kt
│       └── WeightResponse.kt
├── application/
│   ├── WeightService.kt
│   ├── provided/
│   │   └── WeightRecorder.kt
│   └── required/
│       └── WeightRepository.kt
└── domain/
    ├── Weight.kt
    └── WeightStat.kt
```

---

## 📱 Phase 2: SNS 핵심 기능

### 2-1. 프로필 (Profile)

```kotlin
// profile/domain/Profile.kt
@Entity
class Profile(
    val accountId: Long,
    val nickname: String,
    val profileImageUrl: String? = null,
    val goalWeight: BigDecimal? = null,  // 목표 체중
    val height: BigDecimal? = null,      // 키 (BMI 계산용)
    val bio: String? = null,             // 자기소개
    val isPublic: Boolean = false,       // 공개/비공개
)
```

| Method | Endpoint                      | 설명                 |
|--------|-------------------------------|----------------------|
| `GET`  | `/api/profiles/me`            | 내 프로필 조회       |
| `PUT`  | `/api/profiles/me`            | 내 프로필 수정       |
| `GET`  | `/api/profiles/{accountId}`   | 다른 유저 프로필 조회 |

### 2-2. 게시글 / 피드 (Post)

몸무게 변화 인증, 식단, 운동 등을 공유하는 SNS 피드

```kotlin
// post/domain/Post.kt
@Entity
class Post(
    val accountId: Long,
    val content: String,
    val imageUrl: String? = null,
    val weightAtPost: BigDecimal? = null,  // 게시 시점 몸무게
    val category: PostCategory,            // DIET, EXERCISE, PROGRESS, FREE
    val createdAt: LocalDateTime,
)
```

| Method   | Endpoint            | 설명                     |
|----------|---------------------|--------------------------|
| `POST`   | `/api/posts`        | 게시글 작성              |
| `GET`    | `/api/posts/feed`   | 피드 조회 (팔로잉 기반)  |
| `GET`    | `/api/posts/{id}`   | 게시글 상세              |
| `PUT`    | `/api/posts/{id}`   | 게시글 수정              |
| `DELETE` | `/api/posts/{id}`   | 게시글 삭제              |

### 2-3. 팔로우 (Follow)

```kotlin
// follow/domain/Follow.kt
@Entity
class Follow(
    val followerId: Long,
    val followingId: Long,
    val createdAt: LocalDateTime,
)
```

| Method   | Endpoint                    | 설명         |
|----------|-----------------------------|--------------|
| `POST`   | `/api/follows/{accountId}`  | 팔로우       |
| `DELETE` | `/api/follows/{accountId}`  | 언팔로우     |
| `GET`    | `/api/follows/followers`    | 팔로워 목록  |
| `GET`    | `/api/follows/followings`   | 팔로잉 목록  |

### 2-4. 좋아요 & 댓글

| Method   | Endpoint                        | 설명        |
|----------|---------------------------------|-------------|
| `POST`   | `/api/posts/{postId}/likes`     | 좋아요      |
| `DELETE` | `/api/posts/{postId}/likes`     | 좋아요 취소 |
| `POST`   | `/api/posts/{postId}/comments`  | 댓글 작성   |
| `DELETE` | `/api/comments/{commentId}`     | 댓글 삭제   |

---

## 🎯 Phase 3: 동기부여 & 게이미피케이션

### 3-1. 목표 & 챌린지 (Goal / Challenge)

```kotlin
// goal/domain/Goal.kt
@Entity
class Goal(
    val accountId: Long,
    val targetWeight: BigDecimal,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: GoalStatus,  // IN_PROGRESS, ACHIEVED, FAILED
)
```

| Method | Endpoint              | 설명           |
|--------|-----------------------|----------------|
| `POST` | `/api/goals`         | 목표 설정      |
| `GET`  | `/api/goals/current` | 현재 목표 조회 |
| `GET`  | `/api/goals/history` | 목표 히스토리  |

### 3-2. 뱃지 & 리워드

- 🏅 연속 기록 뱃지 (7일, 30일, 100일 연속 기록)
- 🎉 목표 달성 뱃지
- 📉 첫 감량 성공 뱃지
- 💪 커뮤니티 활동 뱃지

### 3-3. 알림 (Notification)

| Method | Endpoint                        | 설명       |
|--------|---------------------------------|------------|
| `GET`  | `/api/notifications`            | 알림 목록  |
| `PUT`  | `/api/notifications/{id}/read`  | 읽음 처리  |

알림 유형: 팔로우, 좋아요, 댓글, 목표 리마인더, 기록 독려

---

## 🔧 Phase 4: 부가 기능

| 기능               | 설명                                          |
|--------------------|-----------------------------------------------|
| **이미지 업로드**  | S3/CloudFront 연동 (프로필, 게시글 이미지)    |
| **신고/차단**      | 유저 신고, 차단 기능                          |
| **검색**           | 유저 검색, 게시글 해시태그 검색               |
| **BMI 계산**       | 키 + 몸무게 기반 BMI 자동 계산 및 트래킹      |
| **데이터 내보내기**| 몸무게 기록 CSV/PDF 다운로드                  |
| **푸시 알림**      | FCM 연동 (기록 리마인더, 소셜 알림)           |

---

## 📋 추천 개발 우선순위

| 순서  | 기능                    | 예상 기간 |
|-------|-------------------------|-----------|
| **1** | Weight CRUD + 그래프 API | 1주      |
| **2** | Profile 설정            | 3일       |
| **3** | 목표 설정 (Goal)        | 3일       |
| **4** | 게시글 + 피드           | 1주       |
| **5** | 팔로우                  | 3일       |
| **6** | 좋아요 + 댓글           | 3일       |
| **7** | 알림                    | 3~5일     |
| **8** | 뱃지/게이미피케이션     | 1주       |
| **9** | 이미지 업로드           | 3일       |
| **10**| 검색, 신고/차단, 내보내기 등 | 1주+  |

---

> 💡 **팁**: Phase 1(Weight CRUD)부터 시작하되, 기존 `account` 패키지의 헥사고날 구조(`adapter/web`, `application/provided,required`, `domain`)를 그대로 따라가면 일관성 있는 코드베이스를 유지할 수 있습니다.
