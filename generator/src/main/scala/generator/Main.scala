package generator

import java.io.File

import protocbridge.ProtocBridge

object Main extends App {

  val config = Config.processArgs(args)

  val code = ProtocBridge.runWithGenerators(
    protoc = config.customProtocLocation match {
      case Some(path) =>
        val executable = new File(path)
        a =>
          com.github.os72.protocjar.Protoc
            .runProtoc(executable.getAbsolutePath, config.version +: a.toArray)
      case None =>
        a => com.github.os72.protocjar.Protoc.runProtoc(config.version +: a.toArray)
    },
    namedGenerators = config.namedGenerators,
    params = config.args
  )

  if (!config.throwException) {
    sys.exit(code)
  } else {
    if (code != 0) {
      throw new Exception(s"Exit with code $code")
    }
  }

}
