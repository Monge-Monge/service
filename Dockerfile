# 1. 실행 환경 (BellSoft Liberica JDK 17 lite 버전 사용)
# Alpine 기반의 lite 버전은 용량이 매우 작고 가볍습니다.
FROM bellsoft/liberica-openjdk-alpine:17

# 2. 컨테이너 내부 작업 디렉토리 설정
WORKDIR /app

# 3. 빌드된 jar 파일 복사
# build/libs 폴더에서 plain이 붙지 않은 실행 가능한 jar만 가져옵니다.
COPY build/libs/app.jar app.jar

# 4. 타임존 설정 (한국 시간)
# Alpine 리눅스이므로 tzdata를 설치해줍니다.
RUN apk add --no-cache tzdata
ENV TZ=Asia/Seoul

# 5. 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "app.jar"]