package json_schema

import example.hero.HeroProto
import example.weapon.WeaponProto
import org.json4s.JsonAST.{JObject, JValue}
import org.json4s.JsonDSL._
import org.json4s.jackson._
import org.scalatest.{FlatSpec, Matchers}
import scalapb.compiler.{DescriptorImplicits, GeneratorParams}

import scala.collection.JavaConverters._

class JsonSchemaBuilderTest extends FlatSpec with Matchers {

  val implicits = new DescriptorImplicits(
    params = GeneratorParams(),
    files = Seq(
      HeroProto.javaDescriptor,
      WeaponProto.javaDescriptor
    )
  )

  val builder = new JsonSchemaBuilder(implicits)

  val heroMsg = HeroProto.javaDescriptor.getMessageTypes.asScala.find(_.getName == "Hero").get

  "Hero" should "get required fields" in {

    val actual = builder.getRequiredFields(heroMsg)
    val expected = ("required" -> List("name", "power", "weapon")) ~ JObject()

    prettyJson(actual) shouldBe prettyJson(expected)

  }

  "Hero" should "return id" in {
    builder.getDefId(heroMsg) shouldBe "example.hero.Hero"
  }

  "Hero" should "get schema object" in {

    val actual = builder.getObjectSchema(heroMsg)

    val expected = {
      ("$id" -> "example.hero.Hero") ~
      ("type" -> "object") ~
      ("properties" -> (
        ("name" -> ("type" -> "string")) ~
        ("active" -> ("type" -> "boolean")) ~
        ("power" -> (
          ("type" -> "string") ~
          ("enum" -> List("HIGH_IQ", "VERY_FAST", "VERY_STRONG"))
        )) ~
        ("hobby" -> (
          ("type" -> "array") ~
          ("items" -> (
            ("type" -> "string") ~
            ("enum" -> List("FOOTBALL", "CHESS", "SKI"))
          ))
        )) ~
        ("weapon" -> (
          "$ref" -> "#/definitions/example.weapon.Weapon"
        ))
      )) ~
      ("required" -> List("name", "power", "weapon"))
    }

    prettyJson(actual.schema) shouldBe prettyJson(expected)

  }

  "all schemas" should "be returned from Weapon.proto" in {

    val actual = builder.getAllSchemas(WeaponProto.javaDescriptor).map(_.schema)

    val expected = List(
      {
        ("$id" -> "example.weapon.Weapon") ~
        ("oneOf" -> List(
          "$ref" -> "#/definitions/example.weapon.Knife",
          "$ref" -> "#/definitions/example.weapon.Ax"
        ))
      },
      {
        ("$id" -> "example.weapon.Knife") ~
        ("type" -> "object") ~
        ("properties" -> (
          ("size" -> (
            "type" -> "integer"
          )) ~
          ("color" -> (
            "type" -> "string"
          ))
        )) ~
        ("required" -> List("size", "color"))
      },
      {
        ("$id" -> "example.weapon.Ax") ~
        ("type" -> "object") ~
        ("properties" -> (
          ("weight_kilo" -> (
            "type" -> "number"
          )) ~ JObject()
        )) ~
        ("required" -> List("weight_kilo"))
      },
      {
        ("$id" -> "example.weapon.Gun") ~
        ("type" -> "object") ~
        ("properties" -> (
          ("magazine_size" -> (
            "type" -> "integer"
            )) ~ JObject()
        )) ~
        ("required" -> List("magazine_size"))
      }

    )

    prettyJson(actual) shouldBe prettyJson(expected)

  }

}
