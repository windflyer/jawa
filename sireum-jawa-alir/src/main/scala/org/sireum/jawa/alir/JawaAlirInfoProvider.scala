/*
Copyright (c) 2013-2014 Fengguo Wei & Sankardas Roy, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/
package org.sireum.jawa.alir

import org.sireum.util._
import org.sireum.jawa.symbolResolver.JawaSymbolTable
import org.sireum.pilar.symbol.SymbolTable
import org.sireum.alir.DefRef
import org.sireum.pilar.symbol.ProcedureSymbolTable
import org.sireum.pilar.ast.CatchClause
import org.sireum.alir.ControlFlowGraph
import org.sireum.jawa.ExceptionCenter
import org.sireum.pilar.ast.NamedTypeSpec
import org.sireum.pilar.parser.ChunkingPilarParser
import org.sireum.pilar.ast.Model
import org.sireum.jawa.symbolResolver.JawaSymbolTableBuilder
import org.sireum.alir.AlirIntraProceduralGraph
import org.sireum.jawa.alir.reachingDefinitionAnalysis.JawaReachingDefinitionAnalysis
import org.sireum.jawa.GlobalConfig
import org.sireum.jawa.Center
import org.sireum.jawa.JawaMethod
import org.sireum.jawa.JawaClass
import org.sireum.jawa.util.StringFormConverter
import org.sireum.jawa.Transform
import org.sireum.pilar.ast.NameExp
import org.sireum.jawa.alir.reachingDefinitionAnalysis.JawaDefRef
import org.sireum.jawa.alir.reachingDefinitionAnalysis.JawaVarAccesses
import org.sireum.jawa.alir.pta.ClassInstance

/**
 * @author <a href="mailto:fgwei@k-state.edu">Fengguo Wei</a>
 * @author <a href="mailto:sroy@k-state.edu">Sankardas Roy</a>
 */ 
object JawaAlirInfoProvider {
  
  final val CFG = "cfg"
  final val RDA = "rda"
  
  //for building cfg
  type VirtualLabel = String
  
  val ERROR_TAG_TYPE = MarkerType(
  "org.sireum.pilar.tag.error.symtab",
  None,
  "Pilar Symbol Resolution Error",
  MarkerTagSeverity.Error,
  MarkerTagPriority.Normal,
  ilist(MarkerTagKind.Problem, MarkerTagKind.Text))
  
  val WARNING_TAG_TYPE = MarkerType(
  "org.sireum.pilar.tag.error.symtab",
  None,
  "Pilar Symbol Resolution Warning",
  MarkerTagSeverity.Warning,
  MarkerTagPriority.Normal,
  ilist(MarkerTagKind.Problem, MarkerTagKind.Text))
  
  var dr : SymbolTable => DefRef = { st => new JawaDefRef(st, new JawaVarAccesses(st)) }
  
  val iopp : ProcedureSymbolTable => (ResourceUri => Boolean, ResourceUri => Boolean) = { pst =>
    val params = pst.params.toSet[ResourceUri]
    ({ localUri => params.contains(localUri) },
      { s => falsePredicate1[ResourceUri](s) })
  }
  
  val saom : Boolean = true
  
  def init(dr : SymbolTable => DefRef) = {
    this.dr = dr
  }
  
  def getExceptionName(cc : CatchClause) : String = {
    require(cc.typeSpec.isDefined)
    require(cc.typeSpec.get.isInstanceOf[NamedTypeSpec])
    cc.typeSpec.get.asInstanceOf[NamedTypeSpec].name.name
  }
  
  //for building cfg
  val siff : ControlFlowGraph.ShouldIncludeFlowFunction =
    { (loc, catchclauses) => 
      	var result = isetEmpty[CatchClause]
      	val thrownExcNames = ExceptionCenter.getExceptionsMayThrow(loc)
      	if(thrownExcNames.forall(_ != ExceptionCenter.ANY_EXCEPTION)){
	      	thrownExcNames.foreach{
	      	  thrownException=>
//	      	    val ccOpt = 
//		      	    catchclauses.find{
//				          catchclause =>
//				            val excName = if(getExceptionName(catchclause) == ExceptionCenter.ANY_EXCEPTION) "java.lang.Throwable" else getExceptionName(catchclause)
////				            val exc = Center.resolveClass(excName, Center.ResolveLevel.HIERARCHY)
//				          	 
//		      	    }
//	      	    ccOpt match{
//	      	      case Some(cc) => result += cc
//	      	      case None =>
//	      	    }
             result ++= catchclauses
	      	}
      	} else {
      	  result ++= catchclauses
      	}
      	
      	(result, false)
    } 
  
  def reporter = {
	  new org.sireum.pilar.parser.PilarParser.ErrorReporter {
      def report(source : Option[FileResourceUri], line : Int,
                 column : Int, message : String) =
        System.err.println("source:" + source + ".line:" + line + ".column:" + column + ".message:" + message)
    }
	}
  
	def getIntraMethodResult(code : String) : Map[ResourceUri, TransformIntraMethodResult] = {
	  val newModel = Transform.parseCodes(Set(code))
	  doGetIntraMethodResult(newModel)
	}
	
	def getIntraMethodResult(codes : Set[String]) : Map[ResourceUri, TransformIntraMethodResult] = {
	  val newModel = Transform.parseCodes(codes)
	  doGetIntraMethodResult(newModel)
	}
	
	private def doGetIntraMethodResult(model: Model) : Map[ResourceUri, TransformIntraMethodResult] = {
	  val result = JawaSymbolTableBuilder(List(model), Transform.fst, GlobalConfig.jawaResolverParallel)
	  result.procedureSymbolTables.map{
	    pst=>
	      val (pool, cfg) = buildCfg(pst)
	      val rda = buildRda(pst, cfg)
	      val procSig = 
	        pst.procedure.getValueAnnotation("signature") match {
			      case Some(exp : NameExp) =>
			        exp.name.name
			      case _ => throw new RuntimeException("Can not find signature")
			    }
	      (procSig, new TransformIntraMethodResult(pst, cfg, rda))
	  }.toMap
	}
  
  private def buildCfg(pst : ProcedureSymbolTable) = {
	  val ENTRY_NODE_LABEL = "Entry"
	  val EXIT_NODE_LABEL = "Exit"
	  val pool : AlirIntraProceduralGraph.NodePool = mmapEmpty
	  val result = ControlFlowGraph[VirtualLabel](pst, ENTRY_NODE_LABEL, EXIT_NODE_LABEL, pool, siff)
	  (pool, result)
	}
	
	private def buildRda (pst : ProcedureSymbolTable, cfg : ControlFlowGraph[VirtualLabel], initialFacts : ISet[JawaReachingDefinitionAnalysis.RDFact] = isetEmpty) = {
	  val iiopp = iopp(pst)
	  JawaReachingDefinitionAnalysis[VirtualLabel](pst,
	    cfg,
	    dr(pst.symbolTable),
	    first2(iiopp),
	    saom,
	    initialFacts)
	}
	
	/**
   * get cfg of current procedure
   */
  
  def getCfg(p : JawaMethod) = {
    if(!(p ? CFG)){
      this.synchronized{
	      val cfg = buildCfg(p.getMethodBody)._2
	      p.setProperty(CFG, cfg)
      }
    }
    p.getProperty(CFG).asInstanceOf[ControlFlowGraph[VirtualLabel]]
  }
	
	/**
   * get rda result of current procedure
   */
  
  def getRda(p : JawaMethod, cfg : ControlFlowGraph[VirtualLabel]) = {
    if(!(p ? RDA)){
      this.synchronized{
	      val rda = buildRda(p.getMethodBody, cfg)
	      p.setProperty(RDA, rda)
      }
    }
    p.getProperty(RDA).asInstanceOf[JawaReachingDefinitionAnalysis.Result]
  }
  
  def getClassInstance(r : JawaClass) : ClassInstance = {
    val mainContext = new Context(GlobalConfig.ICFG_CONTEXT_K)
    mainContext.setContext("Center", "L0000")
    ClassInstance(r.getName, mainContext)
  }
}

case class TransformIntraMethodResult(pst : ProcedureSymbolTable, cfg : ControlFlowGraph[String], rda : JawaReachingDefinitionAnalysis.Result)