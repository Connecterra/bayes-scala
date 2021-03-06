package dk.bayes.factorgraph.ep.calibrate.fb
import org.junit._
import org.junit.Assert._
import dk.bayes.factorgraph.ep.util.TennisFactorGraph._
import org.junit.Assert._
import dk.bayes.factorgraph.factor.GaussianFactor
import dk.bayes.math.linear._
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import dk.bayes.factorgraph.factor.BivariateGaussianFactor
import dk.bayes.factorgraph.ep.GenericEP
import dk.bayes.factorgraph.ep.calibrate.fb.ForwardBackwardEPCalibrate

/**
 * This is a test for a skill update with TrueSkill rating system in a two-person game, like Tennis.
 *
 * Bayesian inference is performed with Expectation Propagation algorithm.
 *
 * @author Daniel Korzekwa
 */
class TrueSkillOnlineTennisEPTest {

  private def progress(currIter: Int) = println("EP iteration: " + currIter)

  /**
   * Tests for variable marginals.
   */

  @Test(expected = classOf[NoSuchElementException]) def variable_marginal_var_id_not_found:Unit = {

    val tennisFactorGraph = createTennisFactorGraphAfterPlayer1Won()

    val epCalibrate = ForwardBackwardEPCalibrate(tennisFactorGraph)
    assertEquals(EPSummary(1, 44), epCalibrate.calibrate(10, progress))

    val ep = GenericEP(tennisFactorGraph)
    val outcomeMarginal = ep.marginal(123)
  }

  /**http://atom.research.microsoft.com/trueskill/rankcalculator.aspx*/
  @Test def variable_marginal_no_result_set:Unit = {

    val tennisFactorGraph = createTennisFactorGraphAfterPlayer1Won()

    val epCalibrate = ForwardBackwardEPCalibrate(tennisFactorGraph)
    assertEquals(EPSummary(1, 44), epCalibrate.calibrate(10, progress))

    val ep = GenericEP(tennisFactorGraph)

    val outcomeMarginal = ep.marginal(outcomeVarId)
    assertEquals(0.24463, outcomeMarginal.getValue((outcomeVarId, 0)), 0.00001)
    assertEquals(0.75537, outcomeMarginal.getValue((outcomeVarId, 1)), 0.00001)

    val skill1Marginal = ep.marginal(skill1VarId).asInstanceOf[GaussianFactor]
    assertEquals(27.1742, skill1Marginal.m, 0.0001)
    assertEquals(37.5013, skill1Marginal.v, 0.0001)

    val skill2Marginal = ep.marginal(skill2VarId).asInstanceOf[GaussianFactor]
    assertEquals(33.846, skill2Marginal.m, 0.0001)
    assertEquals(20.861, skill2Marginal.v, 0.0001)

    assertEquals(EPSummary(1, 44), epCalibrate.calibrate(10, progress))

    val outcomeMarginal2 = ep.marginal(outcomeVarId)
    assertEquals(0.24463, outcomeMarginal2.getValue((outcomeVarId, 0)), 0.00001)
    assertEquals(0.75537, outcomeMarginal2.getValue((outcomeVarId, 1)), 0.00001)

    val skill1MarginalLater = ep.marginal(skill1VarId).asInstanceOf[GaussianFactor]
    assertEquals(27.1742, skill1MarginalLater.m, 0.0001)
    assertEquals(37.5013, skill1MarginalLater.v, 0.0001)

    val skill2MarginalLater = ep.marginal(skill2VarId).asInstanceOf[GaussianFactor]
    assertEquals(33.846, skill2MarginalLater.m, 0.0001)
    assertEquals(20.861, skill2MarginalLater.v, 0.0001)
  }

  /**http://atom.research.microsoft.com/trueskill/rankcalculator.aspx*/
  @Test def variable_marginal_player1_wins:Unit = {
    val tennisFactorGraph = createTennisFactorGraph()

    val ep = GenericEP(tennisFactorGraph)
    ep.setEvidence(outcomeVarId, true)

    val epCalibrate = ForwardBackwardEPCalibrate(tennisFactorGraph)
    assertEquals(EPSummary(2, 88), epCalibrate.calibrate(70, progress))

    val outcomeMarginal = ep.marginal(outcomeVarId)
    assertEquals(1, outcomeMarginal.getValue((outcomeVarId, 0)), 0.0001)
    assertEquals(0, outcomeMarginal.getValue((outcomeVarId, 1)), 0.0001)

    val skill1Marginal = ep.marginal(skill1VarId).asInstanceOf[GaussianFactor]
    assertEquals(27.1744, skill1Marginal.m, 0.0001)
    assertEquals(37.4973, skill1Marginal.v, 0.0001)

    val skill2Marginal = ep.marginal(skill2VarId).asInstanceOf[GaussianFactor]
    assertEquals(33.8473, skill2Marginal.m, 0.0001)
    assertEquals(20.8559, skill2Marginal.v, 0.0001)
  }

  /**http://atom.research.microsoft.com/trueskill/rankcalculator.aspx*/
  @Test def variable_marginal_player1_looses:Unit = {

    val tennisFactorGraph = createTennisFactorGraphAfterPlayer1Won()
    val ep = GenericEP(tennisFactorGraph)
    ep.setEvidence(outcomeVarId, false)

    val epCalibrate = ForwardBackwardEPCalibrate(tennisFactorGraph)
    assertEquals(EPSummary(2, 88), epCalibrate.calibrate(100, progress))

    val outcomeMarginal = ep.marginal(outcomeVarId)
    assertEquals(0, outcomeMarginal.getValue((outcomeVarId, 0)), 0.0001)
    assertEquals(1, outcomeMarginal.getValue((outcomeVarId, 1)), 0.0001)

    val skill1Marginal = ep.marginal(skill1VarId).asInstanceOf[GaussianFactor]
    assertEquals(25.558, skill1Marginal.m, 0.0001)
    assertEquals(30.5446, skill1Marginal.v, 0.0001)

    val skill2Marginal = ep.marginal(skill2VarId).asInstanceOf[GaussianFactor]
    assertEquals(34.745, skill2Marginal.m, 0.0001)
    assertEquals(18.7083, skill2Marginal.v, 0.0001)
  }

  /**
   * Tests for factor marginals.
   */

  @Test(expected = classOf[NoSuchElementException]) def factor_marginal_var_id_not_found:Unit = {

    val tennisFactorGraph = createTennisFactorGraphAfterPlayer1Won()
    val ep = GenericEP(tennisFactorGraph)

    val perfMarginal = ep.marginal(skill1VarId, 345)
  }

  @Test(expected = classOf[NoSuchElementException]) def factor_marginal_var_id_not_found2:Unit = {

    val tennisFactorGraph = createTennisFactorGraphAfterPlayer1Won()
    val ep = GenericEP(tennisFactorGraph)

    val perfMarginal = ep.marginal(skill1VarId, perf1VarId, 45)
  }

  /**http://atom.research.microsoft.com/trueskill/rankcalculator.aspx*/
  @Ignore @Test def factor_marginal_no_result_set = {

    val tennisFactorGraph = createTennisFactorGraphAfterPlayer1Won()
    val ep = GenericEP(tennisFactorGraph)

    val perfMarginal = ep.marginal(skill1VarId, perf1VarId).asInstanceOf[BivariateGaussianFactor]
    assertEquals(Vector(1, 3), perfMarginal.getVariableIds())
    assertEquals(DenseVector(Double.NaN, Double.NaN).toString, perfMarginal.mean.toString)
    assertEquals(new DenseMatrix(2, 2, Array(Double.NaN, Double.NaN, Double.NaN, Double.NaN)).toString, perfMarginal.variance.toString)

    val epCalibrate = ForwardBackwardEPCalibrate(tennisFactorGraph)
    assertEquals(EPSummary(1, 44), epCalibrate.calibrate(10, progress))

    val perfMarginal2 = ep.marginal(skill1VarId, perf1VarId).asInstanceOf[BivariateGaussianFactor]
    assertEquals(Vector(1, 3), perfMarginal2.getVariableIds())
    assertEquals(DenseVector(27.174, 27.174).toString, perfMarginal2.mean.toString)
    assertEquals(new DenseMatrix(2, 2, Array(37.501, 37.501, 37.501, 54.862)).toString, perfMarginal2.variance.toString)
  }

  /**http://atom.research.microsoft.com/trueskill/rankcalculator.aspx*/
  @Test def factor_marginal_player1_wins = {
    val tennisFactorGraph = createTennisFactorGraph()
    val ep = GenericEP(tennisFactorGraph)
    ep.setEvidence(outcomeVarId, true)

    val epCalibrate = ForwardBackwardEPCalibrate(tennisFactorGraph)
    assertEquals(EPSummary(2, 88), epCalibrate.calibrate(70, progress))

    val perfFactorMarginal = ep.marginal(skill1VarId, perf1VarId).asInstanceOf[BivariateGaussianFactor]
    assertEquals(Vector(1, 3), perfFactorMarginal.getVariableIds())
    assertTrue(isIdentical(DenseVector(27.174, 32.142), perfFactorMarginal.mean, 0.001))
    assertTrue(isIdentical(new DenseMatrix(2, 2, Array(37.497, 28.173, 28.173, 34.212)), perfFactorMarginal.variance, 0.001))

    val player1PerfMarginal = ep.marginal(perf1VarId).asInstanceOf[GaussianFactor]
    assertEquals(32.1415, player1PerfMarginal.m, 0.0001)
    assertEquals(34.2117, player1PerfMarginal.v, 0.0001)
  }

  /**http://atom.research.microsoft.com/trueskill/rankcalculator.aspx*/
  @Test def factor_marginal_player1_looses = {

    val tennisFactorGraph = createTennisFactorGraphAfterPlayer1Won()
    val ep = GenericEP(tennisFactorGraph)
    ep.setEvidence(outcomeVarId, false)

    val epCalibrate = ForwardBackwardEPCalibrate(tennisFactorGraph)
    assertEquals(EPSummary(2, 88), epCalibrate.calibrate(100, progress))

    val perfFactorMarginal = ep.marginal(skill1VarId, perf1VarId).asInstanceOf[BivariateGaussianFactor]
    assertEquals(Vector(1, 3), perfFactorMarginal.getVariableIds)
    assertTrue(isIdentical(DenseVector(25.558, 24.810), perfFactorMarginal.mean, 0.001))
    assertTrue(isIdentical(new DenseMatrix(2, 2, Array(30.545, 27.324, 27.324, 39.974)), perfFactorMarginal.variance, 0.001))

    val player1PerfMarginal = ep.marginal(perf1VarId).asInstanceOf[GaussianFactor]
    assertEquals(24.8097, player1PerfMarginal.m, 0.0001)
    assertEquals(39.9735, player1PerfMarginal.v, 0.0001)
  }

  /**
   * Testing inference including GenericFactor
   */
  @Test def genericfactor_test_factor_marginal_player1_wins:Unit = {
    val tennisFactorGraph = createTennisFactorGraphWithGenFactor()
    val ep = GenericEP(tennisFactorGraph)
    ep.setEvidence(outcomeVarId, true)

    val epCalibrate = ForwardBackwardEPCalibrate(tennisFactorGraph)
    assertEquals(EPSummary(2, 88), epCalibrate.calibrate(70, progress))

    val perfFactorMarginal = ep.marginal(skill1VarId, perf1VarId).asInstanceOf[BivariateGaussianFactor]
    assertEquals(Vector(1, 3), perfFactorMarginal.getVariableIds())
    assertTrue(isIdentical(DenseVector(27.174, 32.142), perfFactorMarginal.mean, 0.001))
    assertTrue(isIdentical(new DenseMatrix(2, 2, Array(37.497, 28.173, 28.173, 34.212)), perfFactorMarginal.variance, 0.001))

    val player1PerfMarginal = ep.marginal(perf1VarId).asInstanceOf[GaussianFactor]
    assertEquals(32.1415, player1PerfMarginal.m, 0.0001)
    assertEquals(34.2117, player1PerfMarginal.v, 0.0001)
  }
}