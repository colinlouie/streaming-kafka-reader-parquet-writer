# Streaming Kafka reader, Parquet writer.

This Apache Spark Streaming job reads from Kafka, in time-based intervals, to
save to Parquet files for offline processing.

This was tested on: Apache Spark 2.4.4, and AWS MSK.

```
Welcome to
      ____              __
     / __/__  ___ _____/ /__
    _\ \/ _ \/ _ `/ __/  '_/
   /___/ .__/\_,_/_/ /_/\_\   version 2.4.4
      /_/

Using Scala version 2.11.12 (OpenJDK 64-Bit Server VM, Java 1.8.0_242)
```

# How to run.

Create the FAT/Uber JAR.

```
shell$ sbt assembly
```

Run the Spark job.
```
shell$ spark-submit --driver-memory 8g \
    --class StreamingKafkaReaderParquetWriter \
    --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.4 \
    target/scala-2.11/StreamingKafkaReaderParquetWriter-assembly-1.0.0.jar
```

# Why?

There are examples of reading from Kafka, and writing to Parquet format. Why
write this? Most examples are from the context of `spark-shell`, or assume you
know the Apache Spark ecosystem well. This is a fully working example (once
you configure it) that should compile, and execute without a hitch.
