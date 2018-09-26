package json_schema

import com.google.protobuf.Descriptors.FileDescriptor
import com.google.protobuf.ExtensionRegistry
import com.google.protobuf.compiler.PluginProtos.{CodeGeneratorRequest, CodeGeneratorResponse}
import scalapb.compiler.{DescriptorImplicits, GeneratorException, GeneratorParams, ProtoValidation}
import scalapb.options.compiler.Scalapb

import scala.util.Try
import scala.collection.JavaConverters._

import Implicits._

class JsonSchemaCodeGenerator extends protocbridge.ProtocCodeGenerator {

  override def run(req: Array[Byte]): Array[Byte] = {

    val registry = ExtensionRegistry.newInstance()
    Scalapb.registerAllExtensions(registry)
    val request = CodeGeneratorRequest.parseFrom(req, registry)
    handleGeneratorRequest(request).toByteArray

  }

  def handleGeneratorRequest(request: CodeGeneratorRequest): CodeGeneratorResponse = {

    val b = CodeGeneratorResponse.newBuilder

    Try {

      val filesByName: Map[String, FileDescriptor] =
        request.getProtoFileList.asScala.foldLeft[Map[String, FileDescriptor]](Map.empty) {
          case (acc, fp) =>
            val deps = fp.getDependencyList.asScala.map(acc)
            acc + (fp.getName -> FileDescriptor.buildFrom(fp, deps.toArray))
        }

      val implicits = new DescriptorImplicits(GeneratorParams(), filesByName.values.toVector)
      val generator = new JsonSchemaBuilder(implicits)

      val allFiles = request.getFileToGenerateList.asScala.map(filesByName).toList

      b.addFile(generator.getJsonFile(allFiles).toCodeGeneratorResponseFile)
      b.addFile(generator.getScalaFile(allFiles).toCodeGeneratorResponseFile)

    }.failed.foreach {
      case e: GeneratorException =>
        b.setError(e.message)
      case e => throw e
    }

    b.build

  }

}
