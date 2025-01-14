package org.apache.spark.sql.arangodb.datasource

import com.fasterxml.jackson.core.JsonFactory
import org.apache.spark.sql.arangodb.datasource.mapping.json.JSONOptions

package object mapping {
  private[mapping] def createOptions(jsonFactory: JsonFactory) = new JSONOptions(
    Map.empty[String, String],
    "UTC"
  ) {
    override def buildJsonFactory(): JsonFactory = jsonFactory
  }
}
