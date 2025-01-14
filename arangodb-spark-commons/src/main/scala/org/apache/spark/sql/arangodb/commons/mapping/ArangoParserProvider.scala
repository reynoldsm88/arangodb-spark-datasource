package org.apache.spark.sql.arangodb.commons.mapping

import org.apache.spark.sql.arangodb.commons.ContentType
import org.apache.spark.sql.types.DataType

import java.util.ServiceLoader

trait ArangoParserProvider {
  def of(contentType: ContentType, schema: DataType): ArangoParser
}

object ArangoParserProvider {
  def apply(): ArangoParserProvider = ServiceLoader.load(classOf[ArangoParserProvider]).iterator().next()
}
