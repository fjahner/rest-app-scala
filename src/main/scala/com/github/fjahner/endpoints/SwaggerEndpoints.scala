package com.github.fjahner.endpoints

import cats.effect.IO
import com.github.fjahner.BuildInfo
import sttp.tapir.*
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.swagger.bundle.SwaggerInterpreter

object SwaggerEndpoints:

  def routes(endpoints: List[AnyEndpoint]): List[ServerEndpoint[Any, IO]] =
    SwaggerInterpreter()
      .fromEndpoints[IO](
        endpoints,
        "Scala REST API",
        BuildInfo.version,
      )
