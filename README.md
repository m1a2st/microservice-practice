# microservice-practice

## Architecture
![big_picture.jpg](pic%2Fbig_picture.jpg)

### Modules
1. twitter-to-kafka-service

### twitter-to-kafka-service

#### Description
This service is responsible for fetching tweets from Twitter API and sending them to Kafka.
But I use a mock service instead of Twitter API.

---
##### Kafka Basics

- Immutable, append-only logs 
- Fast, resilient, scalable, high-throughput
- Relies on file system for storage and caching messages
- Resilient and fault-tolerant (replication)
- Disk caching, memory mapped files instead of GC eligible memory

  - Memory mapped files: Contains the contents of a file in virtual memory.
  - Disk caching: Consist of physical pages in RAM corresponds to physical blocks on DISK.
- Scale by partitions
- Ordered inside partition
- As an event store: A great match for event-driven architectures

##### Kafka Architecture
![kafka_arch.png](pic%2Fkafka_arch.png)

- Kafka Topic: Consists of one or more partitions to hold data/ events 
- Kafka Producer: Sends data to Kafka cluster (Thread safe for multiple-threading)
- Kafka consumer: A partition can have only one consumer


##### Kafka Producer

- Producer has buffers of records per topic-partition which are sized at `based.size` property
- Using a large `batch.size` makes compression more efficient 

##### Kafka Producer properties

- Key/ Value Serializer Class
- compressionType
- acks: 0, 1, all
- batchSize
- lingerMs
- requestTimeoutMs
- retryCount

### Kafkacat tool

we will use docker to run kafkacat tool to interact with Kafka cluster.

```bash
docker run -it --rm --network host confluentinc/cp-kafkacat:7.0.12 kafkacat -L -b localhost:19092
```
