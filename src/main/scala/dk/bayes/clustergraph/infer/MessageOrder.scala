package dk.bayes.clustergraph.infer

import dk.bayes.clustergraph.Cluster

/**
 * Specifies order of clusters in which messages are sent for a single iteration of Belief Propagation.
 * 
 * @author Daniel Korzekwa
 */
trait MessageOrder {

  /**
   * Returns order of clusters in which messages are sent for a single iteration of Belief Propagation.
   */
  def ordered(clusters:Seq[Cluster]):Seq[Cluster]
}