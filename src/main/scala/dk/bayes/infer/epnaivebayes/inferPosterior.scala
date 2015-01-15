package dk.bayes.infer.epnaivebayes

/**
 * Computes posterior of X for a naive bayes net. Variables: X, Y1|X, Y2|X,...Yn|X
 *
 * @param bn
 * @param paralllelMessagePassing If true then messages between X variable and Y variables are sent in parallel
 *
 * @author Daniel Korzekwa
 */
object inferPosterior {

  def apply[X, Y](bn: EPBayesianNet[X, Y],paralllelMessagePassing: Boolean = false): X = {

    val factorGraph = EPNaiveBayesFactorGraph(bn,paralllelMessagePassing)
    factorGraph.calibrate(100, 1e-5)
    
    val posterior = factorGraph.getPosterior()
    posterior
  }
}