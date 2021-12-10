package org.apache.spark.sql.arangodb.datasource.write

import com.arangodb.ArangoCollection
import com.arangodb.entity.CollectionType
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.arangodb.commons.ArangoDBConf
import org.apache.spark.sql.arangodb.datasource.BaseSparkTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource


class CreateCollectionTest extends BaseSparkTest {

  private val collectionName = "chessPlayersCreateCollection"
  private val collection: ArangoCollection = db.collection(collectionName)

  import spark.implicits._

  private val df = Seq(
    ("a/1", "b/1"),
    ("a/2", "b/2"),
    ("a/3", "b/3"),
    ("a/4", "b/4"),
    ("a/5", "b/5"),
    ("a/6", "b/6")
  ).toDF("_from", "_to")
    .repartition(3)

  @BeforeEach
  def beforeEach(): Unit = {
    if (collection.exists()) {
      collection.drop()
    }
  }

  @ParameterizedTest
  @MethodSource(Array("provideProtocolAndContentType"))
  def saveModeAppend(protocol: String, contentType: String): Unit = {
    df.write
      .format(BaseSparkTest.arangoDatasource)
      .mode(SaveMode.Append)
      .options(options + (
        ArangoDBConf.COLLECTION -> collectionName,
        ArangoDBConf.PROTOCOL -> protocol,
        ArangoDBConf.CONTENT_TYPE -> contentType,
        ArangoDBConf.NUMBER_OF_SHARDS -> "5",
        ArangoDBConf.COLLECTION_TYPE -> org.apache.spark.sql.arangodb.commons.CollectionType.EDGE.name
      ))
      .save()

    if (isCluster) {
      assertThat(collection.getProperties.getNumberOfShards).isEqualTo(5)
    }
    assertThat(collection.getProperties.getType.getType).isEqualTo(CollectionType.EDGES.getType)
  }

  @ParameterizedTest
  @MethodSource(Array("provideProtocolAndContentType"))
  def saveModeOverwrite(protocol: String, contentType: String): Unit = {
    df.write
      .format(BaseSparkTest.arangoDatasource)
      .mode(SaveMode.Overwrite)
      .options(options + (
        ArangoDBConf.COLLECTION -> collectionName,
        ArangoDBConf.PROTOCOL -> protocol,
        ArangoDBConf.CONTENT_TYPE -> contentType,
        ArangoDBConf.CONFIRM_TRUNCATE -> "true",
        ArangoDBConf.NUMBER_OF_SHARDS -> "5",
        ArangoDBConf.COLLECTION_TYPE -> org.apache.spark.sql.arangodb.commons.CollectionType.EDGE.name
      ))
      .save()

    if (isCluster) {
      assertThat(collection.getProperties.getNumberOfShards).isEqualTo(5)
    }
    assertThat(collection.getProperties.getType.getType).isEqualTo(CollectionType.EDGES.getType)
  }

}
