package cage433.einsum

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class TensorTests extends AnyFreeSpec with Matchers {
  "Can build a tensor" in {
    val tensor = Tensor(shape = Array(1, 2), values = Array(1.0, 3.0))
  }
}

