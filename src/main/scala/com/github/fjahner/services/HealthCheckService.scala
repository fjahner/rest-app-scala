package com.github.fjahner.services

import cats.effect.IO
import doobie.Transactor
import doobie.implicits.*

trait HealthCheckService:
  def checkReadiness: IO[Map[String, Boolean]]

object HealthCheckService:
  def make(transactorOpt: Option[Transactor[IO]] = None): HealthCheckService =
    new HealthCheckService:
      override def checkReadiness: IO[Map[String, Boolean]] =
        val appCheck = IO.pure(Map("app" -> true))

        transactorOpt match
          case Some(xa) =>
            val dbCheck = sql"SELECT 1"
              .query[Int]
              .unique
              .transact(xa)
              .map(_ => Map("database" -> true))
              .handleError(_ => Map("database" -> false))

            for
              app <- appCheck
              db  <- dbCheck
            yield app ++ db

          case None =>
            appCheck
