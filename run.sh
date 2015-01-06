mvn clean compile
mvn package

echo "Let's run the Rest service for Destination Finder"

java -jar target/gs-rest-service-1.0.jar --server.port=9080
