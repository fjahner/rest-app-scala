package com.github.fjahner.services

import munit.CatsEffectSuite

class HealthCheckServiceTest extends CatsEffectSuite:

  test("checkReadiness should return app healthy when no database is configured"):
    val service = HealthCheckService.make(None)

    service.checkReadiness.map: result =>
      assertEquals(result.size, 1)
      assertEquals(result.get("app"), Some(true))
      assert(!result.contains("database"))
