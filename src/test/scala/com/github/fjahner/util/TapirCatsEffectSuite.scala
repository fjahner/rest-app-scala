package com.github.fjahner.util

import cats.effect.IO
import munit.CatsEffectSuite
import sttp.client4.impl.cats.CatsMonadAsyncError

class TapirCatsEffectSuite extends CatsEffectSuite:
  val monadError: sttp.monad.MonadError[IO] = new CatsMonadAsyncError[IO]()
