import sbt._

object Dependencies {
  object Versions {
    val scala3         = "3.3.4"
    val catsEffect     = "3.5.4"
    val doobie         = "1.0.0-RC5"
    val http4s         = "0.23.27"
    val circe          = "0.14.7"
    val postgresql     = "42.7.3"
    val jwtScala       = "10.0.1"
    val tapir          = "1.11.50"
    val sttpClient4    = "4.0.0-M11"
    val munit          = "1.0.0"
    val munitCatsEffect = "2.0.0"
    val testcontainers = "0.41.4"
    val logback        = "1.5.6"
  }

  // Cats Effect
  val catsEffect = "org.typelevel" %% "cats-effect" % Versions.catsEffect

  // Doobie - Database access
  val doobieCore     = "org.tpolecat" %% "doobie-core"     % Versions.doobie
  val doobieHikari   = "org.tpolecat" %% "doobie-hikari"   % Versions.doobie
  val doobiePostgres = "org.tpolecat" %% "doobie-postgres" % Versions.doobie

  // PostgreSQL Driver
  val postgresql = "org.postgresql" % "postgresql" % Versions.postgresql

  // HTTP4s - REST API
  val http4sDsl    = "org.http4s" %% "http4s-dsl"          % Versions.http4s
  val http4sServer = "org.http4s" %% "http4s-ember-server" % Versions.http4s
  val http4sClient = "org.http4s" %% "http4s-ember-client" % Versions.http4s
  val http4sCirce  = "org.http4s" %% "http4s-circe"        % Versions.http4s

  // Circe - JSON
  val circeCore    = "io.circe" %% "circe-core"    % Versions.circe
  val circeGeneric = "io.circe" %% "circe-generic" % Versions.circe
  val circeParser  = "io.circe" %% "circe-parser"  % Versions.circe

  // JWT
  val jwtCirce = "com.github.jwt-scala" %% "jwt-circe" % Versions.jwtScala

  // Tapir - API endpoints and documentation
  val tapirCore         = "com.softwaremill.sttp.tapir" %% "tapir-core"              % Versions.tapir
  val tapirHttp4s       = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"     % Versions.tapir
  val tapirJsonCirce    = "com.softwaremill.sttp.tapir" %% "tapir-json-circe"        % Versions.tapir
  val tapirSwaggerUi    = "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % Versions.tapir

  // Logging
  val logback = "ch.qos.logback" % "logback-classic" % Versions.logback

  // Testing
  val munit             = "org.scalameta" %% "munit"               % Versions.munit          % Test
  val munitCatsEffect   = "org.typelevel" %% "munit-cats-effect"   % Versions.munitCatsEffect % Test
  val doobieScalatest   = "org.tpolecat"  %% "doobie-munit"        % Versions.doobie         % Test
  val testcontainers    = "com.dimafeng"  %% "testcontainers-scala-munit"      % Versions.testcontainers % Test
  val testcontainersPostgres = "com.dimafeng" %% "testcontainers-scala-postgresql" % Versions.testcontainers % Test
  val tapirStubServer   = "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub4-server" % Versions.tapir % Test
  val sttpClientCore    = "com.softwaremill.sttp.client4" %% "core" % Versions.sttpClient4 % Test
  val sttpClientCats    = "com.softwaremill.sttp.client4" %% "cats" % Versions.sttpClient4 % Test

  // Dependency groups
  val coreDependencies: Seq[ModuleID] = Seq(
    catsEffect,
    doobieCore,
    doobieHikari,
    doobiePostgres,
    postgresql,
    http4sDsl,
    http4sServer,
    http4sClient,
    http4sCirce,
    circeCore,
    circeGeneric,
    circeParser,
    jwtCirce,
    tapirCore,
    tapirHttp4s,
    tapirJsonCirce,
    tapirSwaggerUi,
    logback
  )

  val testDependencies: Seq[ModuleID] = Seq(
    munit,
    munitCatsEffect,
    doobieScalatest,
    testcontainers,
    testcontainersPostgres,
    tapirStubServer,
    sttpClientCore,
    sttpClientCats
  )

  val allDependencies: Seq[ModuleID] = coreDependencies ++ testDependencies
}
