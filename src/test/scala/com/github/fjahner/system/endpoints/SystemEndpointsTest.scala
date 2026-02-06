package com.github.fjahner.system.endpoints

import cats.effect.IO
import com.github.fjahner.system.models.HealthResponse
import com.github.fjahner.system.services.HealthCheckService
import com.github.fjahner.util.TapirCatsEffectSuite
import io.circe.Json
import io.circe.parser.decode
import sttp.client4.*
import sttp.client4.testing.BackendStub
import sttp.model.StatusCode
import sttp.tapir.server.stub4.TapirStubInterpreter

class SystemEndpointsTest extends TapirCatsEffectSuite:

  private def mockHealthCheckService(checks: Map[String, Boolean]) = new HealthCheckService:
    override def checkReadiness: IO[Map[String, Boolean]] = IO.pure(checks)

  test("livenessEndpoint should return 200 with an empty JSON object"):
    val systemEndpoints = SystemEndpoints(mockHealthCheckService(Map.empty))
    val backendStub = TapirStubInterpreter(BackendStub[IO](monadError))
      .whenServerEndpoint(systemEndpoints.livenessServerEndpoint)
      .thenRunLogic()
      .backend()

    basicRequest
      .get(uri"https://test.com/_system/alive")
      .send(backendStub)
      .map: response =>
        assertEquals(response.code, StatusCode.Ok)
        assertEquals(response.contentType, Some("application/json"))

        val bodyString = response.body.merge
        val json       = decode[Json](bodyString).toOption.get
        assertEquals(json, Json.obj())

  test("readinessEndpoint should return 200 with healthy status when all checks pass"):
    val systemEndpoints = SystemEndpoints(
      mockHealthCheckService(
        Map(
          "app"      -> true,
          "database" -> true,
          "cache"    -> true,
        ),
      ),
    )
    val backendStub = TapirStubInterpreter(BackendStub[IO](monadError))
      .whenServerEndpoint(systemEndpoints.readinessServerEndpoint)
      .thenRunLogic()
      .backend()

    basicRequest
      .get(uri"https://test.com/_system/ready")
      .send(backendStub)
      .map: response =>
        assertEquals(response.code, StatusCode.Ok)
        assertEquals(response.contentType, Some("application/json"))

        val bodyString     = response.body.merge
        val healthResponse = decode[HealthResponse](bodyString).toOption.get
        assertEquals(healthResponse.status, "ready")
        assertEquals(healthResponse.checks.size, 3)
        assertEquals(healthResponse.checks.get("app"), Some("healthy"))
        assertEquals(healthResponse.checks.get("database"), Some("healthy"))
        assertEquals(healthResponse.checks.get("cache"), Some("healthy"))

  test("readinessEndpoint should handle empty checks as healthy"):
    val systemEndpoints = SystemEndpoints(mockHealthCheckService(Map.empty))
    val backendStub = TapirStubInterpreter(BackendStub[IO](monadError))
      .whenServerEndpoint(systemEndpoints.readinessServerEndpoint)
      .thenRunLogic()
      .backend()

    basicRequest
      .get(uri"https://test.com/_system/ready")
      .send(backendStub)
      .map: response =>
        assertEquals(response.code, StatusCode.Ok)
        assertEquals(response.contentType, Some("application/json"))

        val bodyString     = response.body.merge
        val healthResponse = decode[HealthResponse](bodyString).toOption.get
        assertEquals(healthResponse.status, "ready")
        // HealthResponse.healthy() always adds "app" -> "healthy"
        assertEquals(healthResponse.checks.size, 1)
        assertEquals(healthResponse.checks.get("app"), Some("healthy"))

  test("readinessEndpoint should return 503 with unhealthy status when any check fails"):
    val systemEndpoints = SystemEndpoints(mockHealthCheckService(Map("app" -> true, "database" -> false)))
    val backendStub = TapirStubInterpreter(BackendStub[IO](monadError))
      .whenServerEndpoint(systemEndpoints.readinessServerEndpoint)
      .thenRunLogic()
      .backend()

    basicRequest
      .get(uri"https://test.com/_system/ready")
      .send(backendStub)
      .map: response =>
        assertEquals(response.code, StatusCode.ServiceUnavailable)
        assertEquals(response.contentType, Some("application/json"))

        val bodyString     = response.body.merge
        val healthResponse = decode[HealthResponse](bodyString).toOption.get
        assertEquals(healthResponse.status, "not ready")
        assertEquals(healthResponse.checks.get("app"), Some("healthy"))
        assertEquals(healthResponse.checks.get("database"), Some("unhealthy"))

  test("readinessEndpoint should return 503 when all checks fail"):
    val systemEndpoints = SystemEndpoints(
      mockHealthCheckService(
        Map(
          "app"      -> false,
          "database" -> false,
        ),
      ),
    )
    val backendStub = TapirStubInterpreter(BackendStub[IO](monadError))
      .whenServerEndpoint(systemEndpoints.readinessServerEndpoint)
      .thenRunLogic()
      .backend()

    basicRequest
      .get(uri"https://test.com/_system/ready")
      .send(backendStub)
      .map: response =>
        assertEquals(response.code, StatusCode.ServiceUnavailable)
        assertEquals(response.contentType, Some("application/json"))

        val bodyString     = response.body.merge
        val healthResponse = decode[HealthResponse](bodyString).toOption.get
        assertEquals(healthResponse.status, "not ready")
        assertEquals(healthResponse.checks.get("app"), Some("unhealthy"))
        assertEquals(healthResponse.checks.get("database"), Some("unhealthy"))
