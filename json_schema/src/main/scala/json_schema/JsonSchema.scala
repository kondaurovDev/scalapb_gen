package json_schema

import org.json4s.JsonAST.JValue

case class JsonSchema(
  id: String,
  schema: JValue
)
