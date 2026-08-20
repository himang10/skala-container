# Step 1: Base image
FROM alpine:latest

# Step 2: Register label
LABEL app=demo

# Step 3: Install jq and curl
RUN apk add --no-cache jq curl

# Step 4: Add JSON data from URL
ADD https://springboot.skala25a.project.skala-ai.com/swagger/swagger-docs /tmp/swagger-docs.json

# Step 5: Use ENTRYPOINT to process and print JSON with jq
ENTRYPOINT ["sh", "-c"]

# Step 6: Print JSON data using jq
CMD ["echo 'Formatted JSON output:' && jq '.' /tmp/swagger-docs.json"]
