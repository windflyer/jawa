/*
Copyright (c) 2013-2014 Fengguo Wei & Sankardas Roy, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/
package org.sireum.jawa.alir.pta.reachingFactsAnalysis.model

import org.sireum.jawa.JawaProcedure
import org.sireum.util._
import org.sireum.jawa.JawaRecord
import org.sireum.jawa.Type
import org.sireum.jawa.alir.Context
import org.sireum.jawa.Center
import org.sireum.jawa.NormalType
import org.sireum.alir.Slot
import org.sireum.jawa.alir.pta.Instance
import org.sireum.jawa.alir.pta.reachingFactsAnalysis._

/**
 * @author <a href="mailto:fgwei@k-state.edu">Fengguo Wei</a>
 */ 
trait ModelCallHandler {
  
  /**
   * return true if the given callee procedure needs to be modeled
   */
  def isModelCall(calleeProc : JawaProcedure) : Boolean = {
	  val r = calleeProc.getDeclaringRecord
	  StringBuilderModel.isStringBuilder(r) ||
	  StringModel.isString(r) || 
	  HashSetModel.isHashSet(r) || 
	  HashtableModel.isHashtable(r) ||
	  HashMapModel.isHashMap(r) ||
	  ClassModel.isClass(r) ||
	  NativeCallModel.isNativeCall(calleeProc) ||
    UnknownCallModel.isUnknownCall(calleeProc)
  }
      
  /**
   * instead of doing operation inside callee procedure's real code, we do it manually and return the result. 
   */
	def doModelCall(s : ISet[RFAFact], calleeProc : JawaProcedure, args : List[String], retVars : Seq[String], currentContext : Context) : ISet[RFAFact] = {
	  var (newFacts, delFacts, byPassFlag) = caculateResult(s, calleeProc, args, retVars, currentContext)
	  if(byPassFlag){
	  	val (newF, delF) = ReachingFactsAnalysisHelper.getUnknownObject(calleeProc, s, args, retVars, currentContext)
	  	newFacts ++= newF
	  	delFacts ++= delF
	  }
	  s ++ newFacts -- delFacts
	}
	
	def caculateResult(s : ISet[RFAFact], calleeProc : JawaProcedure, args : List[String], retVars : Seq[String], currentContext : Context) : (ISet[RFAFact], ISet[RFAFact], Boolean) = {
	  val r = calleeProc.getDeclaringRecord
	  if(StringModel.isString(r)) StringModel.doStringCall(s, calleeProc, args, retVars, currentContext)
	  else if(StringBuilderModel.isStringBuilder(r)) StringBuilderModel.doStringBuilderCall(s, calleeProc, args, retVars, currentContext)
	  else if(HashSetModel.isHashSet(r)) HashSetModel.doHashSetCall(s, calleeProc, args, retVars, currentContext)
	  else if(HashtableModel.isHashtable(r)) HashtableModel.doHashtableCall(s, calleeProc, args, retVars, currentContext)
	  else if(HashMapModel.isHashMap(r)) HashMapModel.doHashMapCall(s, calleeProc, args, retVars, currentContext)
	  else if(ClassModel.isClass(r)) ClassModel.doClassCall(s, calleeProc, args, retVars, currentContext)
	  else if(NativeCallModel.isNativeCall(calleeProc)) NativeCallModel.doNativeCall(s, calleeProc, args, retVars, currentContext)
    else if(UnknownCallModel.isUnknownCall(calleeProc)) UnknownCallModel.doUnknownCall(s, calleeProc, args, retVars, currentContext)
	  else throw new RuntimeException("given callee is not a model call: " + calleeProc)
	}
}

object NormalModelCallHandler extends ModelCallHandler