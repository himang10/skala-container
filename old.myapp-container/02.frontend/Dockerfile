# nginx 기반 이미지 사용
FROM nginx:alpine

# 기본 nginx 설정 파일 제거
RUN rm /etc/nginx/conf.d/default.conf

# 커스텀 nginx 설정 파일 복사
COPY default.conf /etc/nginx/conf.d/

# 웹 파일들을 nginx 기본 디렉토리로 복사
COPY src/ /usr/share/nginx/html/

# nginx 포트 노출
EXPOSE 80

# nginx 실행 (기본 CMD가 이미 설정되어 있음)
CMD ["nginx", "-g", "daemon off;"]
