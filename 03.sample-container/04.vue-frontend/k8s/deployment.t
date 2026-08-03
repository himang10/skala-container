# 04.vue-frontend Deployment
# - Vue 빌드 결과물(정적 파일)을 nginx로 서빙하는 컨테이너를 k8s에 배포하기 위한 정의
# - image 값은 docker-push.sh로 올린 이미지 주소로 바꿔서 사용한다.
apiVersion: apps/v1
kind: Deployment
metadata:
  annotations:
    update: {{HASHCODE}}
  name: {{USER_NAME}}-vue-frontend
  namespace: {{NAMESPACE}}
spec:
  replicas: 1
  selector:
    matchLabels:
      app: {{USER_NAME}}-vue-frontend
  template:
    metadata:
      labels:
        app: {{USER_NAME}}-vue-frontend
    spec:
      containers:
        - name: vue-frontend
          image: {{DOCKER_REGISTRY}}/{{PROJECT_NAME}}/{{USER_NAME}}-vue-frontend:1.0
          imagePullPolicy: Always
          ports:
            - containerPort: 80
          readinessProbe:
            httpGet:
              path: /
              port: 80
            initialDelaySeconds: 5
            periodSeconds: 10
          livenessProbe:
            httpGet:
              path: /
              port: 80
            initialDelaySeconds: 10
            periodSeconds: 10
