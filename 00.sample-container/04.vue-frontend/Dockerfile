# 1단계: Vue 프로젝트를 빌드해서 정적 파일(dist/)을 생성한다.
# (03.frontend와 달리 Vue는 빌드 과정이 필요하므로 멀티스테이지 빌드를 사용한다)
FROM node:22-alpine AS build
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm install
COPY . .
RUN npm run build

# 2단계: 빌드된 정적 파일만 nginx 이미지에 담아 서빙한다.
FROM nginx:alpine

# 기본 nginx 설정 파일 제거
RUN rm /etc/nginx/conf.d/default.conf

# 커스텀 nginx 설정 파일 복사 (SPA 라우팅 지원 + /api 프록시)
COPY default.conf /etc/nginx/conf.d/

# 1단계에서 만든 빌드 결과물만 nginx 기본 디렉토리로 복사
COPY --from=build /app/dist/ /usr/share/nginx/html/

# nginx 포트 노출
EXPOSE 80

# nginx 실행 (기본 CMD가 이미 설정되어 있음)
CMD ["nginx", "-g", "daemon off;"]
