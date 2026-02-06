package com.github.fjahner

import cats.effect.*
import com.comcast.ip4s.*
import com.github.fjahner.endpoints.{SwaggerEndpoints, SystemEndpoints}
import com.github.fjahner.services.HealthCheckService
import org.http4s.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import sttp.tapir.server.http4s.Http4sServerInterpreter

object Main extends IOApp.Simple:

  private val healthCheckService = HealthCheckService.make(None)

  private val systemEndpoints = SystemEndpoints(healthCheckService)

  private val swaggerRoutes = SwaggerEndpoints.routes(systemEndpoints.allEndpoints)

  private val allRoutes: HttpRoutes[IO] =
    Http4sServerInterpreter[IO]().toRoutes(
      systemEndpoints.routes ++ swaggerRoutes,
    )

  private def server: Resource[IO, Server] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(allRoutes.orNotFound)
      .build

  override def run: IO[Unit] =
    server.use { _ =>
      IO.println("Server started at http://localhost:8080") *>
        IO.never
    }
