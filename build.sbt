name := "StreamingKafkaReaderParquetWriter"
version := "1.0.0"
scalaVersion := "2.11.12"
scalacOptions := Seq("-unchecked", "-deprecation")

val sparkVersion = "2.4.4"

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion % Provided

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion % Provided

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql-kafka-0-10
libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion % Provided
