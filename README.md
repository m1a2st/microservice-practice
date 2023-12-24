# microservice-practice

## Architecture
![big_picture.jpg](pic%2Fbig_picture.jpg)

### Modules
1. twitter-to-kafka-service
2. kafka
   1. kafka-model: Create and hold Java Objects for Kafka in Avro format
   2. kafka-admin: Create and verify Kafka topics programmatically
   3. kafka-producer: Use spring-kafka to write Kafka producer implementation


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

# Build docker image 

- `spring-boot:build-image`: Create docker image for a spring boot application
- Layered approach: Prevents single fat jar and using cache image update

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <image>
                    <name>${project.groupId}/twitter.to.kafka.service:${project.version}</name>
                </image>
            </configuration>
            <executions>
                <execution>
                    <phase>install</phase>
                    <goals>
                        <goal>build-image</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

# config server repository
- this repository is used to store configuration files for all microservices

# config server

- application.yml: basic config server properties
- bootstrap.yml: Required in case of loading configuration in bootstrap phase which has priority over application.yaml

    - `config.server.uri`: For high availability and repository caching: Use ssh with shared file system or http with remote git repository

### Encrypt password

- Jasypt: Java Simplified Encryption

    - `jasypt.encryptor.password`: Password to encrypt/ decrypt properties
    - use `ENC()` to encrypt a property value
    - PBKDF2: Password-Based Key Derivation Function 2, reduces the speed of brute-force attacks
- JCE: Java Cryptography Extension
    
    - `encryptor.key`: Key to encrypt/ decrypt properties
    - `{cipher}encrypted_value`: Encrypted value of a property
    - AES: Advanced Encryption Standard
    - More secure than Jasypt

#### Asymmetric vs Symmetric Encryption

- Asymmetric Encryption

    - More secure as it has 2 keys
    - Has a private secret key, and a shared public key
    - Message is encrypted with public key and decrypted with private key
    - Slower than Symmetric approach because it has a more complex logic 
    - Provides confidentiality and data integrity (digital signature)
    - Ex: RSA, DSA, ECDSA, ECDH, Diffie-Hellman
- Symmetric Encryption

    - Less secure as it has only one key
    - Faster than Asymmetric approach as it uses the sane key in encryption and decryption
    - Message is encrypted and decrypted with the same shared key
    - Provides confidentiality
    - Sharing the key securely is a challenge
    - Ex: AES, DES, 3DES, RC4, RC5, Blowfish, Twofish

# Kafka consumer basics

- Kafka relies on logs which are known as partitions. Kafka Producers write to the end of specific partition
  and Kafka Consumers read the logs starting from beginning while holding an offset to remember where to leave.
- Kafka distributes partitions to consumers based ib the consumer group identifier.
- Each partition is assigned to single consumer in a consumer group.
- Different consumer groups can read the same data from the same partition.
- Set partition number equal to consumer number. Note that more consumers than partition number will be idle.
- Set poll time out to a value not too big to prevent blocking indefinitely, not too small to prevent cpu call.
- To start from beginning with a new consumer group we can set the auto.offset.reset property to earliest which has 
  default value of latest which is the last committed offset. You can use assign method to set tracing offset explicitly.
  You can also use custom ConsumerRebalanceListener when subscribing and override on PartitionsAssigned method and use 
  consumer.seekToBeginning method.
- Multithreading in a single consumer will not use parallel partition consuming. But you can still use multiple thread 
  in each consumer thread to gain some throughput which you scale according to year need.
- Scaling per partition in consumer level is limit to partition number. Can add additional threads on each consumer 
  but multi thread will break ordering in single partition you need to take care of it. With multi thread consumer per 
  partition of course we have more TCP connections to leader and brokers.
- Delivery semantics: At least once/ At most once/ Exactly once. Rely on consumer commits & acks.

  - If you commit after processing at least once, before processing at most once.
  - Exactly once requires to coordinate between producer and consumer and using transactions staring from producer part.
- With spring kafka, you can give concurrency level and also can set appropriate properties.
![kafka_comsumer.png](pic%2Fkafka_comsumer.png)

## Kafka consumer properties

- Key/ Value Deserializers
- Consumer group id
- auto.offset.reset: earliest, latest, none
- Specific Avro Reader
- Auto Startup
- Concurrency level
- Session Timeout Ms
- Heartbeat Interval Ms
- Max Poll Interval Ms
- Max Poll Records
- Max Partition Fetch Bytes
- Max Poll Duration Ms

# ElasticSearch Basics

- Open source search engine
- Apache Lucene search engine - high performance, full-text search engine
- Organize your data and make it easily accessible
- Easy RESTful API based on JSON
- Easy scale
- Type guessing, dynamic mapping and Lucene standard analysers
- Query DSL for complex queries

## Inverted Index

- Elastic uses a structure called an inverted index, which is designed to allow very fast full text searches.
- An inverted index consist of a list of all the unique words that appear in any document, and for each word,
  a list of the documents in which it appears.
- You can find only terms that exist in your index. so both indexed text and the query string must be normalized into 
  the same form.

## ElasticSearch Analyzers

- Analysing consist of 3 steps:

  - Character filters: Remove HTML tags, convert & to and, ...
  - Tokenizer: Split text into tokens
  - Token filters: Lowercase, remove stop words, remove punctuation, ...
- ElasticSearch provides many character filter. tokenizer and token filter out of the box. Theses can be combined to create
  custom analyzers suitable for different purposes.
