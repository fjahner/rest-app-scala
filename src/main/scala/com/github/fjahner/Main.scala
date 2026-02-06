package com.github.fjahner

import cats.effect.*
import com.comcast.ip4s.*
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server

object Main extends IOApp.Simple:

  private val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "health" =>
      Ok("Server is running")
  }

  private def server: Resource[IO, Server] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(routes.orNotFound)
      .build

  override def run: IO[Unit] =
    server.use { _ =>
      IO.println("Server started at http://localhost:8080") *>
        IO.println("Press CTRL+C to stop...") *>
        IO.never
    }
