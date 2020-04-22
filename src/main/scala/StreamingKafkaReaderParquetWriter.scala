import scala.concurrent.duration._


object StreamingKafkaReaderParquetWriter {

  def main(args: Array[String]): Unit = {

    val appName = "Kafka reader, Parquet writer"

    val conf = new org.apache.spark.SparkConf()
      .setAppName(appName)

    val spark = org.apache.spark.sql.SparkSession
      .builder
      .config(conf)
      .getOrCreate

    // Create the Spark context object.
    val sc = spark.sparkContext

    import spark.implicits._

    // -----------------------------------------------------------------------
    // Details related to connecting to Kafka.

    val KAFKA_BOOTSTRAP_SERVERS = Seq[String](
      "broker-1.domain.tld:9094",
      "broker-2.domain.tld:9094",
      "broker-n.domain.tld:9094"
    ).mkString(",")

    // Required if connecting to brokers using TLS.
    val SSL_CLIENT_KEYSTORE = "./kafka-client-keystore.jks"
    val SSL_CLIENT_KEYSTORE_PASSWORD = "password-for-above-keystore"

    // -----------------------------------------------------------------------
    // Kafka Topic(s) to read from (one or more).

    val KAFKA_TOPICS = Seq[String](
      "topic-1",
      "topic-2",
      "topic-3"
    ).mkString(",")

    // Checkpoint where the Stream Reader last read from.
    val STREAM_READER_CHECK_POINT_LOCATION = "/path/to/foo.parquet.checkpoint"

    // -----------------------------------------------------------------------
    // Details related to writing output.

    val PARQUET_OUTPUT_LOCATION = "/path/to/foo..parquet"

    val OUTPUT_INTERVAL_TRIGGER = 1 hour

    // -----------------------------------------------------------------------
    // The work begins here.

    // Read from stream (Kafka topic(s) subscribed to), into a Spark
    // DataFrame.

    val df1 = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", KAFKA_BOOTSTRAP_SERVERS)
      .option("kafka.security.protocol", "ssl")
      .option("kafka.ssl.keystore.location", SSL_CLIENT_KEYSTORE)
      .option("kafka.ssl.keystore.password", SSL_CLIENT_KEYSTORE_PASSWORD)
      .option("subscribe", KAFKA_TOPICS)
      .load()

    // Specify the columnar data we want. If you want all columns, output df1
    // instead of df2.
    //
    // Available columns:
    // - offset
    // - value
    // - topic
    // - timestamp
    // - timestampType
    // - partition
    // - key

    val df2 = df1.selectExpr("value", "topic", "timestamp")

    // For debugging. Print the schema to confirm this is what we want.
    // root
    //  |-- value: binary (nullable = true)
    //  |-- topic: string (nullable = true)
    //  |-- timestamp: timestamp (nullable = true)
    df2.printSchema()

    // Write out to console for visual verification.
    val streamingConsoleWriter = df2
      .writeStream
      .outputMode("append")
      .format("console")
      .start()

    // Write out to Parquet format at specified intervals.
    val streamingParquetWriter = df2.writeStream
      .format("parquet")
      .option("checkpointLocation", STREAM_READER_CHECK_POINT_LOCATION)
      .option("path", PARQUET_OUTPUT_LOCATION)
      .trigger(
        org.apache.spark.sql.streaming
          .Trigger.ProcessingTime(OUTPUT_INTERVAL_TRIGGER)
      )
      .start()

    // Make sure all of these are last, otherwise you'll block other code from
    // executing.
    streamingConsoleWriter.awaitTermination()
    streamingParquetWriter.awaitTermination()

    // Stop the Spark Session.
    spark.stop

  } // def main

} // object StreamingKafkaReaderParquetWriter
