package org.sireum.jawa.alir.interProcedural.reachability

import org.sireum.jawa.JawaProcedure
import org.sireum.jawa.alir.interProcedural.pointsToAnalysis.PointerAssignmentGraph
import org.sireum.jawa.alir.interProcedural.controlFlowGraph.InterproceduralControlFlowGraph
import org.sireum.jawa.alir.interProcedural.pointsToAnalysis.PtaNode
import org.sireum.jawa.alir.interProcedural.controlFlowGraph.CGNode
import org.sireum.jawa.alir.interProcedural.pointsToAnalysis.InterproceduralPointsToAnalysis

object ReachabilityAnalysis {
  
  /**
	 * Get all reachable procedures of given procedure set.
	 * @param procedureUris Initial procedures set
	 * @param wholeProgram Building call graph in whole program mode or not
	 * @return Set of reachable procedure resource uris from initial set
	 */
	def getReachableProcedures(procedures : Set[JawaProcedure], wholeProgram : Boolean) : Set[JawaProcedure] = {
    val pag = new PointerAssignmentGraph[PtaNode]()
    val cg = new InterproceduralControlFlowGraph[CGNode]
    new InterproceduralPointsToAnalysis().pta(pag, cg, procedures, wholeProgram)
    cg.getReachableProcedures(procedures)
	}
	
	def getReachableProceduresBySBCG(procedures : Set[JawaProcedure], wholeProcs : Set[JawaProcedure], par : Boolean) : Set[JawaProcedure] = {
	  SignatureBasedCallGraph.getReachableProcedures(procedures, wholeProcs, par)
	}
	
	def getBackwardReachability(apiSigs : Set[String], par : Boolean) : Map[String, Set[JawaProcedure]] = {
	  BackwardCallChain.getReachableProcedures(apiSigs, par)
	}
	
	def getBackwardReachability(apiSig : String, par : Boolean) : Set[JawaProcedure] = {
	  BackwardCallChain.getReachableProcedures(apiSig, par)
	}
	
	def getBackwardReachabilityForSubSig(apiSubSig : String, par : Boolean) : Set[JawaProcedure] = {
	  BackwardCallChain.getReachableProceduresBySubSig(apiSubSig, par)
	}
}