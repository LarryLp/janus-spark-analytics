# Building
`mvn clean package -DskipTests`

# Running
Open `analytics.yml` and set the `graphConfigPath` to point to your `read-cassandra.properties` file.

`java -jar target/api-1.0-SNAPSHOT.jar server analytics.yml`

Submit a query to the endpoint:
`http://localhost:8080/analytics?query=g.V().count()`
