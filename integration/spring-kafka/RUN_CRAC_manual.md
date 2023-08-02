* Build a Docker image with CRaC JDK support:

build the spring-kafka project and run:

```
docker build -t tzolov/spring-kafka-crac-image:builder .
```

Run Kafka:

```
docker-compose -f ./docker-compose-fixed-ports.yml up
```

Run the image in a terminal
```
docker run -it  --privileged --rm --name=spring-kafka-crac --ulimit nofile=1024 -p 8080:8080 --network spring-kafka_compose_network -v $(pwd)/build:/opt/mnt tzolov/spring-kafka-crac-image:builder /bin/bash
```

Later mounts the local `build` folder to the `/opt/mnt` in the container to access the spring-kafka.jar.

while in the container copy the jar and run the spring-kafka app:

```
echo 128 > /proc/sys/kernel/ns_last_pid; java -XX:CRaCCheckpointTo=/opt/crac-files -jar /opt/app/spring-kafka.jar
```

After the application is warmed up, open a second terminal and use `jcmd` to trigger the checkpoint creation:
```
docker exec -it  --privileged -u root spring-kafka-crac jcmd /opt/app/spring-kafka.jar JDK.checkpoint
```

The checkpoints images are created under the `/opt/crac-files` folder in the `spring-kafka-crac` container.
After the checkpoint complete, CRaC stops/kills the running application.
