package org.apache.spark.sql.arangodb.datasource.reader

import com.arangodb.velocypack.VPackSlice
import org.apache.spark.sql.arangodb.commons.{ArangoClient, ArangoOptions}
import org.apache.spark.sql.arangodb.commons.mapping.ArangoParser
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.connector.read.PartitionReader
import org.apache.spark.sql.sources.Filter
import org.apache.spark.sql.types._


class ArangoCollectionPartitionReader(inputPartition: ArangoCollectionPartition, schema: StructType, filters: Array[Filter], opts: ArangoOptions)
  extends PartitionReader[InternalRow] {

  // override endpoints with partition endpoint
  private val options = opts.updated(ArangoOptions.ENDPOINTS, inputPartition.endpoint)
  private val parser = ArangoParser.of(options.readOptions.contentType, schema)
  private lazy val client = ArangoClient(options)
  private lazy val iterator = client.readCollectionPartition(inputPartition.shardId, schema, filters)

  private var current: VPackSlice = _

  override def next: Boolean =
    if (iterator.hasNext) {
      current = iterator.next()
      true
    } else {
      false
    }

  override def get: InternalRow = parser.parse(current.toByteArray).head

  override def close(): Unit = {
    iterator.close()
    client.shutdown()
  }

}
