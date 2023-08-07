* Build a Docker image with CRaC JDK support:

build the spring-kafka project and run:

```
./gradlew :integration:spring-amqp-rabbit:bootJar
```

```
docker build -t tzolov/spring-amqp-rabbit-crac-image:builder .

docker build -t tzolov/spring-amqp-rabbit-crac-image:builder integration/spring-amqp-rabbit/
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
export RABBITMQ_HOST=rabbitmq
echo 128 > /proc/sys/kernel/ns_last_pid; java -XX:CRaCCheckpointTo=/opt/crac-files -jar /opt/app/spring-amqp-rabbit.jar
```

After the application is warmed up, open a second terminal and use `jcmd` to trigger the checkpoint creation:
```
docker exec -it  --privileged -u root spring-amqp-rabbit-crac jcmd /opt/app/spring-amqp-rabbit.jar JDK.checkpoint
```

The checkpoints images are created under the `/opt/crac-files` folder in the `spring-amqp-rabbit-crac` container.
After the checkpoint complete, CRaC stops/kills the running application.


```
docker commit $(docker ps -aqf "name=spring-amqp-rabbit-crac") tzolov/spring-amqp-rabbit-crac:checkpoint
docker kill $(docker ps -aqf "name=spring-amqp-rabbit-crac")
```

```
docker run -it --privileged --rm --name my-spring-amqp-rabbit-crac tzolov/spring-amqp-rabbit-crac:checkpoint java -XX:CRaCRestoreFrom=/opt/crac-files
```