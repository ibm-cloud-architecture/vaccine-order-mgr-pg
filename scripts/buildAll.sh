./mvnw clean package -DskipTests
docker build -f src/main/docker/Dockerfile.jvm -t ibmcase/vaccineorderms .
docker push ibmcase/vaccineorderms
