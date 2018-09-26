package json_schema

import org.json4s.JsonAST.JValue

object Models {

  case class GeneratedFile(
    name: String,
    content: String
  )

  case class JsonSchema(
    id: String,
    schema: JValue
  )

}
