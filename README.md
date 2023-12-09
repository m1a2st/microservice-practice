# microservice-practice

## Architecture
![big_picture.jpg](pic%2Fbig_picture.jpg)

### Modules
1. twitter-to-kafka-service

### twitter-to-kafka-service

#### Description
This service is responsible for fetching tweets from Twitter API and sending them to Kafka.
But I use a mock service instead of Twitter API.

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
- 
