package com.github.fjahner.endpoints

import cats.effect.IO
import com.github.fjahner.models.HealthResponse
import com.github.fjahner.services.HealthCheckService
import io.circe.Json
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.ServerEndpoint

class SystemEndpoints(healthCheckService: HealthCheckService):

  val livenessEndpoint: PublicEndpoint[Unit, Unit, Json, Any] =
    endpoint.get
      .in("_system" / "alive")
      .out(jsonBody[Json])
      .description("Liveness probe - returns empty JSON if application is running")

  val readinessEndpoint: PublicEndpoint[Unit, HealthResponse, HealthResponse, Any] =
    endpoint.get
      .in("_system" / "ready")
      .out(jsonBody[HealthResponse])
      .errorOut(
        statusCode(StatusCode.ServiceUnavailable)
          .and(jsonBody[HealthResponse]),
      )
      .description("Readiness probe - checks if application is ready to serve traffic")

  val livenessServerEndpoint: ServerEndpoint[Any, IO] =
    livenessEndpoint.serverLogicSuccess[IO](_ => IO.pure(Json.obj()))

  val readinessServerEndpoint: ServerEndpoint[Any, IO] =
    readinessEndpoint.serverLogic[IO] { _ =>
      healthCheckService.checkReadiness.map { checks =>
        val allHealthy    = checks.values.forall(identity)
        val checksStrings = checks.map { case (k, v) => k -> (if (v) "healthy" else "unhealthy") }

        if (allHealthy) {
          Right(HealthResponse.healthy(checksStrings))
        } else {
          Left(HealthResponse.unhealthy(checksStrings))
        }
      }
    }

  val routes: List[ServerEndpoint[Any, IO]] =
    List(livenessServerEndpoint, readinessServerEndpoint)

  val allEndpoints: List[PublicEndpoint[?, ?, ?, ?]] =
    List(livenessEndpoint, readinessEndpoint)

object SystemEndpoints:

  def apply(healthCheckService: HealthCheckService): SystemEndpoints =
    new SystemEndpoints(healthCheckService)
