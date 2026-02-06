package com.github.fjahner.system.models

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import sttp.tapir.Schema

case class HealthResponse(
  status: String,
  checks: Map[String, String],
)

object HealthResponse:
  given encoder: Encoder[HealthResponse] = deriveEncoder[HealthResponse]
  given decoder: Decoder[HealthResponse] = deriveDecoder[HealthResponse]
  given schema: Schema[HealthResponse]   = Schema.derived[HealthResponse]

  def healthy(checks: Map[String, String] = Map.empty): HealthResponse =
    HealthResponse("ready", checks + ("app" -> "healthy"))

  def unhealthy(checks: Map[String, String]): HealthResponse =
    HealthResponse("not ready", checks)
