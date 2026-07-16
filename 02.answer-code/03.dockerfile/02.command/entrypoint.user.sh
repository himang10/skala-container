#!/bin/sh

echo "=================================================="
echo "[INFO] 실행 사용자 : $(id)"
echo "[INFO] 실행 시각   : $(date '+%Y-%m-%d %H:%M:%S')"
echo "=================================================="

# 1단계: /var/www/html 디렉토리 접근(실행) 권한 체크
echo "[CHECK] /var/www/html 디렉토리 접근 권한 확인 중..."
if [ ! -x /var/www/html ]; then
    echo "[ERROR] 권한 오류: '$(id -un)' 사용자는 /var/www/html 디렉토리에 접근할 수 없습니다."
    echo "[INFO]  디렉토리 권한 정보:"
    ls -la /var/www/ 2>&1
    echo "[INFO]  컨테이너를 종료합니다."
    exit 1
fi

# 2단계: index.html 파일 읽기 권한 체크
echo "[CHECK] /var/www/html/index.html 파일 읽기 권한 확인 중..."
if [ ! -r /var/www/html/index.html ]; then
    echo "[ERROR] 권한 오류: '$(id -un)' 사용자는 /var/www/html/index.html 파일을 읽을 수 없습니다."
    echo "[INFO]  파일 권한 정보:"
    ls -la /var/www/html/ 2>&1
    echo "[INFO]  컨테이너를 종료합니다."
    exit 1
fi

echo "[INFO] 권한 확인 완료. nginx를 시작합니다..."
exec nginx -g "daemon off;"
