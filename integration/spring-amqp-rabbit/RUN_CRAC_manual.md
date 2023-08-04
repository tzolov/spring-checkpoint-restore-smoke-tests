* Build a Docker image with CRaC JDK support:

build the spring-kafka project and run:

```
./gradlew :integration:spring-amqp-rabbit:bootJar
```

```
docker build -t tzolov/spring-amqp-rabbit-crac-image:builder .

docker build -f integration/spring-amqp-rabbit/Dockerfile -t tzolov/spring-amqp-rabbit-crac-image:builder integration/spring-amqp-rabbit/
```

Run Kafka:

```
docker-compose -f ./docker-compose-with-network.yml up
```

Run the image in a terminal
```
docker run -it  --privileged --rm --name=spring-amqp-rabbit-crac --ulimit nofile=1024 -p 8080:8080 --network spring-amqp-rabbit_compose_network -v $(pwd)/build:/opt/mnt tzolov/spring-amqp-rabbit-crac-image:builder /bin/bash
```

Later mounts the local `build` folder to the `/opt/mnt` in the container to access the spring-kafka.jar.

while in the container copy the jar and run the spring-kafka app:

```
echo 128 > /proc/sys/kernel/ns_last_pid; java -XX:CRaCCheckpointTo=/opt/crac-files -jar /opt/app/spring-amqp-rabbit.jar
```

After the application is warmed up, open a second terminal and use `jcmd` to trigger the checkpoint creation:
```
docker exec -it  --privileged -u root spring-amqp-rabbit-crac jcmd /opt/app/spring-amqp-rabbit.jar JDK.checkpoint
```

The checkpoints images are created under the `/opt/crac-files` folder in the `spring-amqp-rabbit-crac` container.
After the checkpoint complete, CRaC stops/kills the running application.


```
runtimeClasspath - Runtime classpath of source set 'main'.
+--- org.springframework.boot:spring-boot-dependencies:3.2.0-SNAPSHOT
|    +--- org.springframework.amqp:spring-rabbit:3.0.7 -> 3.0.8-SNAPSHOT (c)
|    +--- org.springframework.boot:spring-boot-starter-amqp:3.2.0-SNAPSHOT (c)
|    +--- org.springframework.amqp:spring-amqp:3.0.7 -> 3.0.8-SNAPSHOT (c)
|    +--- com.rabbitmq:amqp-client:5.18.0 (c)
|    +--- org.springframework:spring-context:6.1.0-M3 (c)
|    +--- org.springframework:spring-messaging:6.1.0-M3 (c)
|    +--- org.springframework:spring-tx:6.1.0-M3 (c)
|    +--- io.micrometer:micrometer-observation:1.12.0-M1 (c)
|    +--- org.springframework.boot:spring-boot:3.2.0-SNAPSHOT (c)
|    +--- org.springframework.boot:spring-boot-starter:3.2.0-SNAPSHOT (c)
|    +--- org.springframework:spring-core:6.1.0-M3 (c)
|    +--- org.springframework.retry:spring-retry:2.0.2 (c)
|    +--- org.slf4j:slf4j-api:2.0.7 (c)
|    +--- org.springframework:spring-aop:6.1.0-M3 (c)
|    +--- org.springframework:spring-beans:6.1.0-M3 (c)
|    +--- org.springframework:spring-expression:6.1.0-M3 (c)
|    +--- io.micrometer:micrometer-commons:1.12.0-M1 (c)
|    +--- org.springframework.boot:spring-boot-autoconfigure:3.2.0-SNAPSHOT (c)
|    +--- org.springframework.boot:spring-boot-starter-logging:3.2.0-SNAPSHOT (c)
|    +--- jakarta.annotation:jakarta.annotation-api:2.1.1 (c)
|    +--- org.yaml:snakeyaml:2.0 (c)
|    +--- org.springframework:spring-jcl:6.1.0-M3 (c)
|    +--- ch.qos.logback:logback-classic:1.4.8 (c)
|    +--- org.apache.logging.log4j:log4j-to-slf4j:2.20.0 (c)
|    +--- org.slf4j:jul-to-slf4j:2.0.7 (c)
|    +--- ch.qos.logback:logback-core:1.4.8 (c)
|    \--- org.apache.logging.log4j:log4j-api:2.20.0 (c)
+--- org.springframework.boot:spring-boot-starter-amqp -> 3.2.0-SNAPSHOT
|    +--- org.springframework.boot:spring-boot-starter:3.2.0-SNAPSHOT
|    |    +--- org.springframework.boot:spring-boot:3.2.0-SNAPSHOT
|    |    |    +--- org.springframework:spring-core:6.1.0-M3
|    |    |    |    \--- org.springframework:spring-jcl:6.1.0-M3
|    |    |    \--- org.springframework:spring-context:6.1.0-M3
|    |    |         +--- org.springframework:spring-aop:6.1.0-M3
|    |    |         |    +--- org.springframework:spring-beans:6.1.0-M3
|    |    |         |    |    \--- org.springframework:spring-core:6.1.0-M3 (*)
|    |    |         |    \--- org.springframework:spring-core:6.1.0-M3 (*)
|    |    |         +--- org.springframework:spring-beans:6.1.0-M3 (*)
|    |    |         +--- org.springframework:spring-core:6.1.0-M3 (*)
|    |    |         +--- org.springframework:spring-expression:6.1.0-M3
|    |    |         |    \--- org.springframework:spring-core:6.1.0-M3 (*)
|    |    |         \--- io.micrometer:micrometer-observation:1.12.0-M1
|    |    |              \--- io.micrometer:micrometer-commons:1.12.0-M1
|    |    +--- org.springframework.boot:spring-boot-autoconfigure:3.2.0-SNAPSHOT
|    |    |    \--- org.springframework.boot:spring-boot:3.2.0-SNAPSHOT (*)
|    |    +--- org.springframework.boot:spring-boot-starter-logging:3.2.0-SNAPSHOT
|    |    |    +--- ch.qos.logback:logback-classic:1.4.8
|    |    |    |    +--- ch.qos.logback:logback-core:1.4.8
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.7
|    |    |    +--- org.apache.logging.log4j:log4j-to-slf4j:2.20.0
|    |    |    |    +--- org.apache.logging.log4j:log4j-api:2.20.0
|    |    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.7
|    |    |    \--- org.slf4j:jul-to-slf4j:2.0.7
|    |    |         \--- org.slf4j:slf4j-api:2.0.7
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.1.1
|    |    +--- org.springframework:spring-core:6.1.0-M3 (*)
|    |    \--- org.yaml:snakeyaml:2.0
|    +--- org.springframework:spring-messaging:6.1.0-M3
|    |    +--- org.springframework:spring-beans:6.1.0-M3 (*)
|    |    \--- org.springframework:spring-core:6.1.0-M3 (*)
|    \--- org.springframework.amqp:spring-rabbit:3.0.7 -> 3.0.8-SNAPSHOT
|         +--- org.springframework.amqp:spring-amqp:3.0.8-SNAPSHOT
|         |    +--- org.springframework:spring-core:6.0.11 -> 6.1.0-M3 (*)
|         |    \--- org.springframework.retry:spring-retry:2.0.2
|         +--- com.rabbitmq:amqp-client:5.16.1 -> 5.18.0
|         |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.7
|         +--- org.springframework:spring-context:6.0.11 -> 6.1.0-M3 (*)
|         +--- org.springframework:spring-messaging:6.0.11 -> 6.1.0-M3 (*)
|         +--- org.springframework:spring-tx:6.0.11 -> 6.1.0-M3
|         |    +--- org.springframework:spring-beans:6.1.0-M3 (*)
|         |    \--- org.springframework:spring-core:6.1.0-M3 (*)
|         \--- io.micrometer:micrometer-observation:1.10.9 -> 1.12.0-M1 (*)
+--- org.springframework.amqp:spring-rabbit:3.0.8-SNAPSHOT (*)
+--- org.crac:crac:1.3.0
\--- project :cr-listener
     +--- org.springframework.boot:spring-boot-dependencies:3.2.0-SNAPSHOT (*)
     +--- org.springframework:spring-context -> 6.1.0-M3 (*)
     \--- org.springframework.boot:spring-boot -> 3.2.0-SNAPSHOT (*)
```