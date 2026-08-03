apiVersion: apps/v1
kind: Deployment
metadata:
  name: ${USER_NAME}-${SERVICE_NAME}
  namespace: ${NAMESPACE}
spec:
  replicas: 1
  selector:
    matchLabels:
      app: ${USER_NAME}-${SERVICE_NAME}
  template:
    metadata:
      annotations:
        prometheus.io/scrape: 'true'
        prometheus.io/port: '8080'
        prometheus.io/path: '/prometheus'
        update: e8c24298b888a2dc0795de1564bca2da12
      labels:
        app: ${USER_NAME}-${SERVICE_NAME}
    spec:
      containers:
      - name: ${USER_NAME}-${SERVICE_NAME}
        image: amdp-registry.skala-ai.com/skala25a/${USER_NAME}-posts-get.amd64:1.0
        imagePullPolicy: Always
