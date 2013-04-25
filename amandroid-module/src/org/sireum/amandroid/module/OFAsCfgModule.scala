// Do not edit this file. It is auto-generated from org.sireum.amandroid.module.OFAsCfg
// by org.sireum.pipeline.gen.ModuleGenerator

package org.sireum.amandroid.module

import org.sireum.util._
import org.sireum.pipeline._
import java.lang.String
import org.sireum.alir.AlirIntraProceduralNode
import org.sireum.alir.ControlFlowGraph
import org.sireum.amandroid.AndroidSymbolResolver.AndroidVirtualMethodTables
import org.sireum.amandroid.cache.AndroidCacheFile
import org.sireum.amandroid.objectflowanalysis.ObjectFlowGraph
import org.sireum.amandroid.objectflowanalysis.OfaNode
import org.sireum.amandroid.scfg.CompressedControlFlowGraph
import org.sireum.pilar.ast.LocationDecl
import org.sireum.pilar.symbol.ProcedureSymbolTable
import org.sireum.pilar.symbol.SymbolTable
import scala.Function2
import scala.Option
import scala.collection.Seq
import scala.collection.mutable.Map

object OFAsCfgModule extends PipelineModule {
  def title = "System Control Flow Graph with OFA Builder"
  def origin = classOf[OFAsCfg]

  val globalOFAsCfgKey = "Global.OFAsCfg"
  val globalAndroidVirtualMethodTablesKey = "Global.androidVirtualMethodTables"
  val globalProcedureSymbolTablesKey = "Global.procedureSymbolTables"
  val globalAndroidCacheKey = "Global.androidCache"
  val globalCfgsKey = "Global.cfgs"
  val OFAsCfgKey = "OFAsCfg.OFAsCfg"

  def compute(job : PipelineJob, info : PipelineJobModuleInfo) : MBuffer[Tag] = {
    val tags = marrayEmpty[Tag]
    try {
      val module = Class.forName("org.sireum.amandroid.module.OFAsCfgModuleDef")
      val cons = module.getConstructors()(0)
      val params = Array[AnyRef](job, info)
      val inst = cons.newInstance(params : _*)
    } catch {
      case e : Throwable =>
        e.printStackTrace
        tags += PipelineUtil.genTag(PipelineUtil.ErrorMarker, e.getMessage);
    }
    return tags
  }

  override def initialize(job : PipelineJob) {
  }

  override def validPipeline(stage : PipelineStage, job : PipelineJob) : MBuffer[Tag] = {
    val tags = marrayEmpty[Tag]
    val deps = ilist[PipelineModule]()
    deps.foreach(d =>
      if(stage.modules.contains(d)){
        tags += PipelineUtil.genTag(PipelineUtil.ErrorMarker,
            "'" + this.title + "' depends on '" + d.title + "' yet both were found in stage '" + stage.title + "'"
        )
      }
    )
    return tags
  }

  def inputDefined (job : PipelineJob) : MBuffer[Tag] = {
    val tags = marrayEmpty[Tag]
    var _androidVirtualMethodTables : scala.Option[AnyRef] = None
    var _androidVirtualMethodTablesKey : scala.Option[String] = None

    val keylistandroidVirtualMethodTables = List(OFAsCfgModule.globalAndroidVirtualMethodTablesKey)
    keylistandroidVirtualMethodTables.foreach(key => 
      if(job ? key) { 
        if(_androidVirtualMethodTables.isEmpty) {
          _androidVirtualMethodTables = Some(job(key))
          _androidVirtualMethodTablesKey = Some(key)
        }
        if(!(job(key).asInstanceOf[AnyRef] eq _androidVirtualMethodTables.get)) {
          tags += PipelineUtil.genTag(PipelineUtil.ErrorMarker,
            "Input error for '" + this.title + "': 'androidVirtualMethodTables' keys '" + _androidVirtualMethodTablesKey.get + " and '" + key + "' point to different objects.")
        }
      }
    )

    _androidVirtualMethodTables match{
      case Some(x) =>
        if(!x.isInstanceOf[org.sireum.amandroid.AndroidSymbolResolver.AndroidVirtualMethodTables]){
          tags += PipelineUtil.genTag(PipelineUtil.ErrorMarker,
            "Input error for '" + this.title + "': Wrong type found for 'androidVirtualMethodTables'.  Expecting 'org.sireum.amandroid.AndroidSymbolResolver.AndroidVirtualMethodTables' but found '" + x.getClass.toString + "'")
        }
      case None =>
        tags += PipelineUtil.genTag(PipelineUtil.ErrorMarker,
          "Input error for '" + this.title + "': No value found for 'androidVirtualMethodTables'")       
    }
    var _androidCache : scala.Option[AnyRef] = None
    var _androidCacheKey : scala.Option[String] = None

    val keylistandroidCache = List(OFAsCfgModule.globalAndroidCacheKey)
    keylistandroidCache.foreach(key => 
      if(job ? key) { 
        if(_androidCache.isEmpty) {
          _androidCache = Some(job(key))
          _androidCacheKey = Some(key)
        }
        if(!(job(key).asInstanceOf[AnyRef] eq _androidCache.get)) {
          tags += PipelineUtil.genTag(PipelineUtil.ErrorMarker,
            "Input error for '" + this.title + "': 'androidCache' keys '" + _androidCacheKey.get + " and '" + key + "' point to different objects.")
        }
      }
    )

    _androidCache match{
      case Some(x) =>
        if(!x.isInstanceOf[org.sireum.amandroid.cache.AndroidCacheFile[java.lang.String]]){
          tags += PipelineUtil.genTag(PipelineUtil.ErrorMarker,
            "Input error for '" + this.title + "': Wrong type found for 'androidCache'.  Expecting 'org.sireum.amandroid.cache.AndroidCacheFile[java.lang.String]' but found '" + x.getClass.toString + "'")
        }
      case None =>
        tags += PipelineUtil.genTag(PipelineUtil.ErrorMarker,
          "Input error for '" + this.title + "': No value found for 'androidCache'")       
    }
    var _cfgs : scala.Option[AnyRef] = None
    var _cfgsKey : scala.Option[String] = None

    val keylistcfgs = List(OFAsCfgModule.globalCfgsKey)
    keylistcfgs.foreach(key => 
      if(job ? key) { 
        if(_cfgs.isEmpty) {
          _cfgs = Some(job(key))
          _cfgsKey = Some(key)
        }
        if(!(job(key).asInstanceOf[AnyRef] eq _cfgs.get)) {
          tags += PipelineUtil.genTag(PipelineUtil.ErrorMarker,
            "Input error for '" + this.title + "': 'cfgs' keys '" + _cfgsKey.get + " and '" + key + "' point to different objects.")
        }
      }
    )

    _cfgs match{
      case Some(x) =>
        if(!x.isInstanceOf[scala.collection.mutable.Map[java.lang.String, org.sireum.alir.ControlFlowGraph[java.lang.String]]]){
          tags += PipelineUtil.genTag(PipelineUtil.ErrorMarker,
            "Input error for '" + this.title + "': Wrong type found for 'cfgs'.  Expecting 'scala.collection.mutable.Map[java.lang.String, org.sireum.alir.ControlFlowGraph[java.lang.String]]' but found '" + x.getClass.toString + "'")
        }
      case None =>
        tags += PipelineUtil.genTag(PipelineUtil.ErrorMarker,
          "Input error for '" + this.title + "': No value found for 'cfgs'")       
    }
    var _procedureSymbolTables : scala.Option[AnyRef] = None
    var _procedureSymbolTablesKey : scala.Option[String] = None

    val keylistprocedureSymbolTables = List(OFAsCfgModule.globalProcedureSymbolTablesKey)
    keylistprocedureSymbolTables.foreach(key => 
      if(job ? key) { 
        if(_procedureSymbolTables.isEmpty) {
          _procedureSymbolTables = Some(job(key))
          _procedureSymbolTablesKey = Some(key)
        }
        if(!(job(key).asInstanceOf[AnyRef] eq _procedureSymbolTables.get)) {
          tags += PipelineUtil.genTag(PipelineUtil.ErrorMarker,
            "Input error for '" + this.title + "': 'procedureSymbolTables' keys '" + _procedureSymbolTablesKey.get + " and '" + key + "' point to different objects.")
        }
      }
    )

    _procedureSymbolTables match{
      case Some(x) =>
        if(!x.isInstanceOf[scala.collection.Seq[org.sireum.pilar.symbol.ProcedureSymbolTable]]){
          tags += PipelineUtil.genTag(PipelineUtil.ErrorMarker,
            "Input error for '" + this.title + "': Wrong type found for 'procedureSymbolTables'.  Expecting 'scala.collection.Seq[org.sireum.pilar.symbol.ProcedureSymbolTable]' but found '" + x.getClass.toString + "'")
        }
      case None =>
        tags += PipelineUtil.genTag(PipelineUtil.ErrorMarker,
          "Input error for '" + this.title + "': No value found for 'procedureSymbolTables'")       
    }
    return tags
  }

  def outputDefined (job : PipelineJob) : MBuffer[Tag] = {
    val tags = marrayEmpty[Tag]
    if(!(job ? OFAsCfgModule.OFAsCfgKey) && !(job ? OFAsCfgModule.globalOFAsCfgKey)) {
      tags += PipelineUtil.genTag(PipelineUtil.ErrorMarker,
        "Output error for '" + this.title + "': No entry found for 'OFAsCfg'. Expecting (OFAsCfgModule.OFAsCfgKey or OFAsCfgModule.globalOFAsCfgKey)") 
    }

    if(job ? OFAsCfgModule.OFAsCfgKey && !job(OFAsCfgModule.OFAsCfgKey).isInstanceOf[org.sireum.amandroid.objectflowanalysis.ObjectFlowGraph[org.sireum.amandroid.objectflowanalysis.OfaNode]]) {
      tags += PipelineUtil.genTag(PipelineUtil.ErrorMarker, 
        "Output error for '" + this.title + "': Wrong type found for OFAsCfgModule.OFAsCfgKey.  Expecting 'org.sireum.amandroid.objectflowanalysis.ObjectFlowGraph[org.sireum.amandroid.objectflowanalysis.OfaNode]' but found '" + 
        job(OFAsCfgModule.OFAsCfgKey).getClass.toString + "'")
    } 

    if(job ? OFAsCfgModule.globalOFAsCfgKey && !job(OFAsCfgModule.globalOFAsCfgKey).isInstanceOf[org.sireum.amandroid.objectflowanalysis.ObjectFlowGraph[org.sireum.amandroid.objectflowanalysis.OfaNode]]) {
      tags += PipelineUtil.genTag(PipelineUtil.ErrorMarker, 
        "Output error for '" + this.title + "': Wrong type found for OFAsCfgModule.globalOFAsCfgKey.  Expecting 'org.sireum.amandroid.objectflowanalysis.ObjectFlowGraph[org.sireum.amandroid.objectflowanalysis.OfaNode]' but found '" + 
        job(OFAsCfgModule.globalOFAsCfgKey).getClass.toString + "'")
    } 
    return tags
  }

  def getAndroidVirtualMethodTables (options : scala.collection.Map[Property.Key, Any]) : org.sireum.amandroid.AndroidSymbolResolver.AndroidVirtualMethodTables = {
    if (options.contains(OFAsCfgModule.globalAndroidVirtualMethodTablesKey)) {
       return options(OFAsCfgModule.globalAndroidVirtualMethodTablesKey).asInstanceOf[org.sireum.amandroid.AndroidSymbolResolver.AndroidVirtualMethodTables]
    }

    throw new Exception("Pipeline checker should guarantee we never reach here")
  }

  def setAndroidVirtualMethodTables (options : MMap[Property.Key, Any], androidVirtualMethodTables : org.sireum.amandroid.AndroidSymbolResolver.AndroidVirtualMethodTables) : MMap[Property.Key, Any] = {

    options(OFAsCfgModule.globalAndroidVirtualMethodTablesKey) = androidVirtualMethodTables

    return options
  }

  def getAndroidCache (options : scala.collection.Map[Property.Key, Any]) : org.sireum.amandroid.cache.AndroidCacheFile[java.lang.String] = {
    if (options.contains(OFAsCfgModule.globalAndroidCacheKey)) {
       return options(OFAsCfgModule.globalAndroidCacheKey).asInstanceOf[org.sireum.amandroid.cache.AndroidCacheFile[java.lang.String]]
    }

    throw new Exception("Pipeline checker should guarantee we never reach here")
  }

  def setAndroidCache (options : MMap[Property.Key, Any], androidCache : org.sireum.amandroid.cache.AndroidCacheFile[java.lang.String]) : MMap[Property.Key, Any] = {

    options(OFAsCfgModule.globalAndroidCacheKey) = androidCache

    return options
  }

  def getOFAsCfg (options : scala.collection.Map[Property.Key, Any]) : org.sireum.amandroid.objectflowanalysis.ObjectFlowGraph[org.sireum.amandroid.objectflowanalysis.OfaNode] = {
    if (options.contains(OFAsCfgModule.globalOFAsCfgKey)) {
       return options(OFAsCfgModule.globalOFAsCfgKey).asInstanceOf[org.sireum.amandroid.objectflowanalysis.ObjectFlowGraph[org.sireum.amandroid.objectflowanalysis.OfaNode]]
    }
    if (options.contains(OFAsCfgModule.OFAsCfgKey)) {
       return options(OFAsCfgModule.OFAsCfgKey).asInstanceOf[org.sireum.amandroid.objectflowanalysis.ObjectFlowGraph[org.sireum.amandroid.objectflowanalysis.OfaNode]]
    }

    throw new Exception("Pipeline checker should guarantee we never reach here")
  }

  def setOFAsCfg (options : MMap[Property.Key, Any], OFAsCfg : org.sireum.amandroid.objectflowanalysis.ObjectFlowGraph[org.sireum.amandroid.objectflowanalysis.OfaNode]) : MMap[Property.Key, Any] = {

    options(OFAsCfgModule.globalOFAsCfgKey) = OFAsCfg
    options(OFAsCfgKey) = OFAsCfg

    return options
  }

  def getCfgs (options : scala.collection.Map[Property.Key, Any]) : scala.collection.mutable.Map[java.lang.String, org.sireum.alir.ControlFlowGraph[java.lang.String]] = {
    if (options.contains(OFAsCfgModule.globalCfgsKey)) {
       return options(OFAsCfgModule.globalCfgsKey).asInstanceOf[scala.collection.mutable.Map[java.lang.String, org.sireum.alir.ControlFlowGraph[java.lang.String]]]
    }

    throw new Exception("Pipeline checker should guarantee we never reach here")
  }

  def setCfgs (options : MMap[Property.Key, Any], cfgs : scala.collection.mutable.Map[java.lang.String, org.sireum.alir.ControlFlowGraph[java.lang.String]]) : MMap[Property.Key, Any] = {

    options(OFAsCfgModule.globalCfgsKey) = cfgs

    return options
  }

  def getProcedureSymbolTables (options : scala.collection.Map[Property.Key, Any]) : scala.collection.Seq[org.sireum.pilar.symbol.ProcedureSymbolTable] = {
    if (options.contains(OFAsCfgModule.globalProcedureSymbolTablesKey)) {
       return options(OFAsCfgModule.globalProcedureSymbolTablesKey).asInstanceOf[scala.collection.Seq[org.sireum.pilar.symbol.ProcedureSymbolTable]]
    }

    throw new Exception("Pipeline checker should guarantee we never reach here")
  }

  def setProcedureSymbolTables (options : MMap[Property.Key, Any], procedureSymbolTables : scala.collection.Seq[org.sireum.pilar.symbol.ProcedureSymbolTable]) : MMap[Property.Key, Any] = {

    options(OFAsCfgModule.globalProcedureSymbolTablesKey) = procedureSymbolTables

    return options
  }

  object ConsumerView {
    implicit class OFAsCfgModuleConsumerView (val job : PropertyProvider) extends AnyVal {
      def androidVirtualMethodTables : org.sireum.amandroid.AndroidSymbolResolver.AndroidVirtualMethodTables = OFAsCfgModule.getAndroidVirtualMethodTables(job.propertyMap)
      def androidCache : org.sireum.amandroid.cache.AndroidCacheFile[java.lang.String] = OFAsCfgModule.getAndroidCache(job.propertyMap)
      def OFAsCfg : org.sireum.amandroid.objectflowanalysis.ObjectFlowGraph[org.sireum.amandroid.objectflowanalysis.OfaNode] = OFAsCfgModule.getOFAsCfg(job.propertyMap)
      def cfgs : scala.collection.mutable.Map[java.lang.String, org.sireum.alir.ControlFlowGraph[java.lang.String]] = OFAsCfgModule.getCfgs(job.propertyMap)
      def procedureSymbolTables : scala.collection.Seq[org.sireum.pilar.symbol.ProcedureSymbolTable] = OFAsCfgModule.getProcedureSymbolTables(job.propertyMap)
    }
  }

  object ProducerView {
    implicit class OFAsCfgModuleProducerView (val job : PropertyProvider) extends AnyVal {

      def androidVirtualMethodTables_=(androidVirtualMethodTables : org.sireum.amandroid.AndroidSymbolResolver.AndroidVirtualMethodTables) { OFAsCfgModule.setAndroidVirtualMethodTables(job.propertyMap, androidVirtualMethodTables) }
      def androidVirtualMethodTables : org.sireum.amandroid.AndroidSymbolResolver.AndroidVirtualMethodTables = OFAsCfgModule.getAndroidVirtualMethodTables(job.propertyMap)

      def androidCache_=(androidCache : org.sireum.amandroid.cache.AndroidCacheFile[java.lang.String]) { OFAsCfgModule.setAndroidCache(job.propertyMap, androidCache) }
      def androidCache : org.sireum.amandroid.cache.AndroidCacheFile[java.lang.String] = OFAsCfgModule.getAndroidCache(job.propertyMap)

      def OFAsCfg_=(OFAsCfg : org.sireum.amandroid.objectflowanalysis.ObjectFlowGraph[org.sireum.amandroid.objectflowanalysis.OfaNode]) { OFAsCfgModule.setOFAsCfg(job.propertyMap, OFAsCfg) }
      def OFAsCfg : org.sireum.amandroid.objectflowanalysis.ObjectFlowGraph[org.sireum.amandroid.objectflowanalysis.OfaNode] = OFAsCfgModule.getOFAsCfg(job.propertyMap)

      def cfgs_=(cfgs : scala.collection.mutable.Map[java.lang.String, org.sireum.alir.ControlFlowGraph[java.lang.String]]) { OFAsCfgModule.setCfgs(job.propertyMap, cfgs) }
      def cfgs : scala.collection.mutable.Map[java.lang.String, org.sireum.alir.ControlFlowGraph[java.lang.String]] = OFAsCfgModule.getCfgs(job.propertyMap)

      def procedureSymbolTables_=(procedureSymbolTables : scala.collection.Seq[org.sireum.pilar.symbol.ProcedureSymbolTable]) { OFAsCfgModule.setProcedureSymbolTables(job.propertyMap, procedureSymbolTables) }
      def procedureSymbolTables : scala.collection.Seq[org.sireum.pilar.symbol.ProcedureSymbolTable] = OFAsCfgModule.getProcedureSymbolTables(job.propertyMap)
    }
  }
}

trait OFAsCfgModule {
  def job : PipelineJob

  def androidVirtualMethodTables : org.sireum.amandroid.AndroidSymbolResolver.AndroidVirtualMethodTables = OFAsCfgModule.getAndroidVirtualMethodTables(job.propertyMap)

  def androidCache : org.sireum.amandroid.cache.AndroidCacheFile[java.lang.String] = OFAsCfgModule.getAndroidCache(job.propertyMap)


  def OFAsCfg_=(OFAsCfg : org.sireum.amandroid.objectflowanalysis.ObjectFlowGraph[org.sireum.amandroid.objectflowanalysis.OfaNode]) { OFAsCfgModule.setOFAsCfg(job.propertyMap, OFAsCfg) }
  def OFAsCfg : org.sireum.amandroid.objectflowanalysis.ObjectFlowGraph[org.sireum.amandroid.objectflowanalysis.OfaNode] = OFAsCfgModule.getOFAsCfg(job.propertyMap)

  def cfgs : scala.collection.mutable.Map[java.lang.String, org.sireum.alir.ControlFlowGraph[java.lang.String]] = OFAsCfgModule.getCfgs(job.propertyMap)

  def procedureSymbolTables : scala.collection.Seq[org.sireum.pilar.symbol.ProcedureSymbolTable] = OFAsCfgModule.getProcedureSymbolTables(job.propertyMap)
}