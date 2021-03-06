package dk.bayes.factorgraph.factor

import org.junit.Assert.assertEquals
import org.junit.Test
import dk.bayes.factorgraph.factor.GaussianFactor

class GaussianFactorTest {

  private val gaussianFactor = GaussianFactor(varId = 1, m = 2, v = 3)

  /**
   * Tests for constructor.
   */
  @Test(expected = classOf[IllegalArgumentException]) def constructor_variance_is_NaN:Unit = {
    GaussianFactor(10, 0, Double.NaN)
  }
  @Test(expected = classOf[IllegalArgumentException]) def constructor_mean_is_NaN:Unit = {
    GaussianFactor(10, Double.NaN, 2)
  }

  @Test def getVariablesIds:Unit = {
    assertEquals(List(1), gaussianFactor.getVariableIds)
  }

  @Test(expected = classOf[IllegalArgumentException])
  def marginal_incorrect_var_id:Unit = {
    gaussianFactor.marginal(123)
  }

  @Test
  def marginal:Unit = {
    assertEquals(gaussianFactor, gaussianFactor.marginal(1))
  }

  @Test(expected = classOf[IllegalArgumentException])
  def productMarginal_incorrect_var_id:Unit = {
    gaussianFactor.productMarginal(123, Nil)
  }

  @Test(expected = classOf[IllegalArgumentException])
  def productMarginal_incorrect_factors_var_id:Unit = {
    val factors = GaussianFactor(varId = 1, m = 5, v = 7) :: GaussianFactor(varId = 2, m = 5, v = 7) :: Nil

    gaussianFactor.productMarginal(1, factors)
  }

  @Test def productMarginal:Unit = {
    val factors = GaussianFactor(varId = 1, m = 5, v = 7) :: GaussianFactor(varId = 1, m = 15, v = 17) :: Nil

    val marginal = gaussianFactor.productMarginal(1, factors)

    assertEquals(1, marginal.varId)
    assertEquals(4.2303, marginal.m, 0.0001)
    assertEquals(1.8691, marginal.v, 0.0001)
  }

  @Test(expected = classOf[IllegalArgumentException]) def product_var_id_do_not_match:Unit = {
    val f1 = GaussianFactor(varId = 1, m = 5, v = 7)
    val f2 = GaussianFactor(varId = 2, m = 15, v = 17)
    f1 * f2
  }

  @Test(expected = classOf[IllegalArgumentException]) def product_with_non_gaussian_factor:Unit = {
    val f1 = GaussianFactor(varId = 1, m = 5, v = 7)
    val f2 = SingleTableFactor(1, 2, valueProbs = Array(0.7, 0.3))
    f1 * f2
  }

  @Test def product:Unit = {
    val f1 = GaussianFactor(varId = 1, m = 5, v = 7)
    val f2 = GaussianFactor(varId = 1, m = 15, v = 17)
    val product = f1 * f2

    assertEquals(1, product.varId)
    assertEquals(7.9166, product.m, 0.0001)
    assertEquals(4.9583, product.v, 0.0001)
  }

  @Test(expected = classOf[IllegalArgumentException]) def divide_var_id_do_not_match:Unit = {
    val f1 = GaussianFactor(varId = 1, m = 5, v = 7)
    val f2 = GaussianFactor(varId = 2, m = 15, v = 17)
    f1 / f2
  }

  @Test(expected = classOf[IllegalArgumentException]) def divide_with_non_gaussian_factor:Unit = {
    val f1 = GaussianFactor(varId = 1, m = 5, v = 7)
    val f2 = SingleTableFactor(1, 2, valueProbs = Array(0.7, 0.3))
    f1 / f2
  }

  @Test def divide:Unit = {
    val f1 = GaussianFactor(varId = 1, m = 5, v = 7)
    val f2 = GaussianFactor(varId = 1, m = 15, v = 17)
    val product = f1 / f2

    assertEquals(1, product.varId)
    assertEquals(-1.9999, product.m, 0.0001)
    assertEquals(11.9, product.v, 0.0001)
  }

  @Test def equals:Unit = {
    val f1 = GaussianFactor(varId = 1, m = 5, v = 7)
    val f2 = GaussianFactor(varId = 1, m = 5.00000004, v = 7.00001)

    assertEquals(true, f1.equals(f2, 0.00001))
    assertEquals(true, f2.equals(f1, 0.00001))

    assertEquals(false, f1.equals(f2, 0.0000000001))
    assertEquals(false, f2.equals(f1, 0.0000000001))

    assertEquals(true, GaussianFactor(varId = 1, m = 0, v = Double.PositiveInfinity) equals(GaussianFactor(varId = 1, m = 0, v = Double.PositiveInfinity),0.0000001))
  }
}