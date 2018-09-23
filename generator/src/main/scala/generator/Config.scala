package generator

import json_schema.JsonSchemaCodeGenerator
import protocbridge.ProtocCodeGenerator

object Config {

  private val CustomPathArgument = "--protoc="

  def processArgs(args: Array[String]): Config = {
    case class State(cfg: Config, passThrough: Boolean)

    args
      .foldLeft(State(Config(), passThrough = false)) {
        case (state, item) =>
          (state.passThrough, item) match {
            case (false, v) if v.startsWith("-v") => state.copy(cfg = state.cfg.copy(version = v))
            case (false, "--throw")               => state.copy(cfg = state.cfg.copy(throwException = true))
            case (false, p) if p.startsWith(CustomPathArgument) =>
              state.copy(
                cfg = state.cfg
                  .copy(customProtocLocation = Some(p.substring(CustomPathArgument.length)))
              )
            case (_, other) =>
              state.copy(passThrough = true, cfg = state.cfg.copy(args = state.cfg.args :+ other))
          }
      }
      .cfg
  }

}

case class Config(
  version: String = "-v360",
  throwException: Boolean = false,
  args: Seq[String] = Seq.empty,
  customProtocLocation: Option[String] = None,
  namedGenerators: Seq[(String, ProtocCodeGenerator)] = Seq("json-schema" -> new JsonSchemaCodeGenerator())
)