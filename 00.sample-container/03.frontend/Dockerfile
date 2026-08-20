# nginx 기반 이미지 사용 (정적 파일만 서빙하므로 별도의 빌드 단계가 필요 없다)
FROM nginx:alpine

# 기본 nginx 설정 파일 제거
RUN rm /etc/nginx/conf.d/default.conf

# 커스텀 nginx 설정 파일 복사 (정적 서빙 + /api 프록시)
COPY default.conf /etc/nginx/conf.d/

# 정적 파일들을 nginx 기본 디렉토리로 복사
COPY src/ /usr/share/nginx/html/

# nginx 포트 노출
EXPOSE 80

# nginx 실행 (기본 CMD가 이미 설정되어 있음)
CMD ["nginx", "-g", "daemon off;"]
