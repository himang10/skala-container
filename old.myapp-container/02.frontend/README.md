# 03.frontend

## 이 코드는 무엇을 위한 실습인가?
- 순수 HTML/CSS/JavaScript 정적 웹을 컨테이너로 배포하는 기초 실습 코드입니다.
- Nginx 정적 서빙, 간단한 API 호출/페이징 UI, Kubernetes 배포를 연습합니다.

## 이 디렉토리 기준 구조/파일 설명
- `src/`: 정적 웹 파일(`main.html`, `styles.css`, `app.js`)
- `default.conf`: Nginx 설정
- `Dockerfile`: 정적 파일을 Nginx 이미지에 포함하는 설정
- `run.sh`: 로컬/컨테이너 실행 보조 스크립트
- `docker-build.sh`, `docker-push.sh`: 이미지 빌드/푸시
- `deploy/`: Deployment/Service 템플릿 및 환경값
- `my-first-app/`: 동일 성격의 하위 실습 앱(별도 Dockerfile/deploy 포함)

## 학습 가이드(추천 순서)
- 1) `src/main.html`과 `src/app.js`로 화면/동작 확인
- 2) `default.conf`와 `Dockerfile`로 정적 서빙 구조 이해
- 3) `docker-build.sh`로 이미지 생성
- 4) `deploy/` 매니페스트로 Kubernetes 배포
- 5) `my-first-app/`에서 동일 패턴 반복 실습
