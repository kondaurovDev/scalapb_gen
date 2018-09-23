package json_schema

import com.google.protobuf.Descriptors._
import com.google.protobuf.Descriptors.FieldDescriptor._
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse
import org.json4s.JValue
import org.json4s.JsonAST.{JArray, JObject}
import org.json4s.JsonDSL._
import scalapb.compiler._

import scala.collection.JavaConverters._
import org.json4s.jackson._

class JsonSchemaBuilder(
  implicits: DescriptorImplicits
) {

  import implicits._

  def joinAllSchemas(fileDescriptorList: List[FileDescriptor]): CodeGeneratorResponse.File = {

    val resp = CodeGeneratorResponse.File.newBuilder()

    val all = fileDescriptorList.flatMap(getAllSchemas)

    resp.setContent(txt.ts(all).body)
    resp.setName("jsonSchemas.ts")

    resp.build()

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
    "$ref" -> s"#/definitions/${getDefId(d)}"
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