package org.apache.spark.sql.arangodb.commons.filter

import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.sources.EqualTo
import org.apache.spark.sql.types._
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EqualToFilterTest {
  private val schema = StructType(Array(
    // atomic types
    StructField("bool", BooleanType),
    StructField("double", DoubleType),
    StructField("float", FloatType),
    StructField("integer", IntegerType),
    StructField("long", LongType),
    StructField("date", DateType),
    StructField("timestamp", TimestampType),
    StructField("short", ShortType),
    StructField("byte", ByteType),
    StructField("string", StringType),

    // complex types
    StructField("stringArray", ArrayType(StringType)),
    StructField("dateArray", ArrayType(DateType)),
    StructField("intMap", MapType(StringType, IntegerType)),
    StructField("dateMap", MapType(StringType, DateType)),
    StructField("null", NullType),
    StructField("struct", StructType(Array(
      StructField("a", StringType),
      StructField("b", IntegerType)
    ))),
    StructField("structWithTimestamp", StructType(Array(
      StructField("a", StringType),
      StructField("b", IntegerType),
      StructField("c", DateType)
    )))
  ))

  @Test
  def equalToStringFilter(): Unit = {
    val field = "string"
    val value = "str"
    val filter = PushableFilter(EqualTo(field, value), schema: StructType)
    assertThat(filter.support()).isEqualTo(FilterSupport.FULL)
    assertThat(filter.aql("d")).isEqualTo(s"""`d`.`$field` == "$value"""")
  }

  @Test
  def equalToFilterTimestamp(): Unit = {
    val field = "timestamp"
    val value = "2001-01-02T15:30:45.678111Z"
    val filter = PushableFilter(EqualTo(field, value), schema: StructType)
    assertThat(filter.support()).isEqualTo(FilterSupport.PARTIAL)
    assertThat(filter.aql("d")).isEqualTo(s"""DATE_TIMESTAMP(`d`.`$field`) == DATE_TIMESTAMP("$value")""")
  }

  @Test
  def equalToFilterDate(): Unit = {
    val field = "date"
    val value = "2001-01-02"
    val filter = PushableFilter(EqualTo(field, value), schema: StructType)
    assertThat(filter.support()).isEqualTo(FilterSupport.FULL)
    assertThat(filter.aql("d")).isEqualTo(s"""DATE_TIMESTAMP(`d`.`$field`) == DATE_TIMESTAMP("$value")""")
  }

  @Test
  def equalToBoolFilter(): Unit = {
    val field = "bool"
    val value = false
    val filter = PushableFilter(EqualTo(field, value), schema: StructType)
    assertThat(filter.support()).isEqualTo(FilterSupport.FULL)
    assertThat(filter.aql("d")).isEqualTo(s"""`d`.`$field` == $value""")
  }

  @Test
  def equalToDoubleFilter(): Unit = {
    val field = "double"
    val value = 77.88
    val filter = PushableFilter(EqualTo(field, value), schema: StructType)
    assertThat(filter.support()).isEqualTo(FilterSupport.FULL)
    assertThat(filter.aql("d")).isEqualTo(s"""`d`.`$field` == $value""")
  }

  @Test
  def equalToFloatFilter(): Unit = {
    val field = "float"
    val value = 77.88F
    val filter = PushableFilter(EqualTo(field, value), schema: StructType)
    assertThat(filter.support()).isEqualTo(FilterSupport.FULL)
    assertThat(filter.aql("d")).isEqualTo(s"""`d`.`$field` == $value""")
  }

  @Test
  def equalToIntegerFilter(): Unit = {
    val field = "integer"
    val value = 22
    val filter = PushableFilter(EqualTo(field, value), schema: StructType)
    assertThat(filter.support()).isEqualTo(FilterSupport.FULL)
    assertThat(filter.aql("d")).isEqualTo(s"""`d`.`$field` == $value""")
  }

  @Test
  def equalToLongFilter(): Unit = {
    val field = "long"
    val value = 22L
    val filter = PushableFilter(EqualTo(field, value), schema: StructType)
    assertThat(filter.support()).isEqualTo(FilterSupport.FULL)
    assertThat(filter.aql("d")).isEqualTo(s"""`d`.`$field` == $value""")
  }

  @Test
  def equalToShortFilter(): Unit = {
    val field = "short"
    val value: Short = 22
    val filter = PushableFilter(EqualTo(field, value), schema: StructType)
    assertThat(filter.support()).isEqualTo(FilterSupport.FULL)
    assertThat(filter.aql("d")).isEqualTo(s"""`d`.`$field` == $value""")
  }

  @Test
  def equalToByteFilter(): Unit = {
    val field = "byte"
    val value: Byte = 22
    val filter = PushableFilter(EqualTo(field, value), schema: StructType)
    assertThat(filter.support()).isEqualTo(FilterSupport.FULL)
    assertThat(filter.aql("d")).isEqualTo(s"""`d`.`$field` == $value""")
  }

  @Test
  def equalToStringArrayFilter(): Unit = {
    val field = "stringArray"
    val value = Seq("a", "b", "c")
    val filter = PushableFilter(EqualTo(field, value), schema: StructType)
    assertThat(filter.support()).isEqualTo(FilterSupport.FULL)
    assertThat(filter.aql("d")).isEqualTo("""`d`.`stringArray` == ["a","b","c"]""")
  }

  @Test
  def equalToDateArrayFilter(): Unit = {
    val field = "dateArray"
    val value = Seq("2001-01-01", "2001-01-02", "2001-01-03")
    val filter = PushableFilter(EqualTo(field, value), schema: StructType)
    assertThat(filter.support()).isEqualTo(FilterSupport.NONE)
  }

  @Test
  def equalToIntMapFilter(): Unit = {
    val field = "intMap"
    val value = Map(
      "a" -> 1,
      "b" -> 2,
      "c" -> 3
    )
    val filter = PushableFilter(EqualTo(field, value), schema: StructType)
    assertThat(filter.support()).isEqualTo(FilterSupport.FULL)
    assertThat(filter.aql("d")).isEqualTo("""`d`.`intMap` == {"a":1,"b":2,"c":3}""")
  }

  @Test
  def equalToDateMapFilter(): Unit = {
    val field = "dateMap"
    val value = Map(
      "a" -> "2001-01-01",
      "b" -> "2001-01-02",
      "c" -> "2001-01-03"
    )
    val filter = PushableFilter(EqualTo(field, value), schema: StructType)
    assertThat(filter.support()).isEqualTo(FilterSupport.NONE)
  }

  @Test
  def equalToStructFilter(): Unit = {
    val field = "struct"
    val value = new GenericRowWithSchema(
      Array("str", 22),
      StructType(Array(
        StructField("a", StringType),
        StructField("b", IntegerType)
      ))
    )
    val filter = PushableFilter(EqualTo(field, value), schema: StructType)
    assertThat(filter.support()).isEqualTo(FilterSupport.FULL)
    assertThat(filter.aql("d")).isEqualTo("""`d`.`struct` == {"a":"str","b":22}""")
  }

  @Test
  def equalToStructWithTimestampFieldFilter(): Unit = {
    val field = "structWithTimestamp"
    val value = new GenericRowWithSchema(
      Array("str", "2001-01-02"),
      StructType(Array(
        StructField("a", StringType),
        StructField("b", IntegerType),
        StructField("c", DateType)
      ))
    )
    val filter = PushableFilter(EqualTo(field, value), schema: StructType)
    assertThat(filter.support()).isEqualTo(FilterSupport.NONE)
  }

}
