package json_schema

import com.google.protobuf.Descriptors._
import com.google.protobuf.Descriptors.FieldDescriptor._
import org.json4s.JValue
import org.json4s.JsonAST.{JArray, JObject}
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import scalapb.compiler._

import scala.collection.JavaConverters._

import Models._

class JsonSchemaBuilder(
  implicits: DescriptorImplicits
) {

  import implicits._

  def getScalaFile(fileDescriptorList: List[FileDescriptor]): GeneratedFile = {

    val all = fileDescriptorList.flatMap(getAllSchemas)

    GeneratedFile(
      name = "JsonSchema.scala",
      content = txt.jackson_scala(all).body
    )

  }

  def getJsonFile(fileDescriptorList: List[FileDescriptor]): GeneratedFile = {

    val all = fileDescriptorList.flatMap(getAllSchemas)

    GeneratedFile(
      name = "JsonSchema.js",
      content = pretty(all.map(_.schema))
    )

  }

  def getAllSchemas(fileDescriptor: FileDescriptor): List[JsonSchema] = {

    fileDescriptor.getMessageTypes.asScala.map(msg => {

      getObjectSchema(msg)

    }).toList

  }

  def getDefId(d: Descriptor): String = {
    d.getFullName
  }

  def getRef(d: Descriptor): JValue = {
    "$ref" -> s"#${getDefId(d)}"
  }

  def getObjectSchema(d: Descriptor): JsonSchema = {

    val id = getDefId(d)

    val schema = if (d.isSealedOneofType) {
      ("oneOf" -> d.sealedOneofCases.get.map(getRef)) ~ JObject()
    } else {
      ("type" -> "object") ~
      ("properties" -> JObject(
        d.getFields.asScala.toList.map(f => {
          f.getName -> {
            if (f.isRepeated) {
              ("type" -> "array") ~
              ("items" -> getFieldType(f))
            } else {
              getFieldType(f)
            }
          }
        })
      )) ~
      getRequiredFields(d)
    }

    JsonSchema(
      id = id,
      schema = ("$id" -> id) ~ schema
    )

  }

  def getFieldType(field: FieldDescriptor): JValue = {

    field.getJavaType match {

      case JavaType.MESSAGE => getRef(field.getMessageType)

      case JavaType.ENUM =>
        ("type" -> "string") ~
        ("enum" -> field.getEnumType.getValues.asScala.map(_.getName))

      case JavaType.INT  => "type" -> "integer"
      case JavaType.LONG => "type" -> "integer"
      case JavaType.BOOLEAN => "type" -> "boolean"
      case JavaType.DOUBLE => "type" -> "number"
      case JavaType.FLOAT => "type" -> "number"
      case JavaType.BYTE_STRING => "type" -> "string"
      case JavaType.STRING => "type" -> "string"
    }

  }

  def getRequiredFields(d: Descriptor): JObject = {
    val fields = d.getFields.asScala.toList.filter(_.isRequired)
    if (fields.nonEmpty) {
      "required" -> fields.map(f => f.getName)
    } else {
      JObject()
    }

  }

}