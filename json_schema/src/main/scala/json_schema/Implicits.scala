package json_schema

import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse

object Implicits {

  implicit class GeneratedFileWrapper(val underlying: Models.GeneratedFile) extends AnyVal {

    def toCodeGeneratorResponseFile: CodeGeneratorResponse.File = {
      val resp = CodeGeneratorResponse.File.newBuilder()
      resp.setContent(underlying.content)
      resp.setName(underlying.name)
      resp.build()
    }

  }


}
