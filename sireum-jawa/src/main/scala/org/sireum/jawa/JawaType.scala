/*
Copyright (c) 2013-2014 Fengguo Wei & Sankardas Roy, Kansas State University.        
All rights reserved. This program and the accompanying materials      
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at              
http://www.eclipse.org/legal/epl-v10.html                             
*/
package org.sireum.jawa

import org.sireum.util._

object JawaType {
  def generateType(typ: String, dimensions: Int): JawaType = {
    new JawaType(typ, dimensions)
  }
  
  def addDimensions(typ: JawaType, dimensions: Int): JawaType = {
    if(typ.dimensions + dimensions >= 0) {
      JawaType(typ.baseType, typ.dimensions + dimensions)
    } else typ
  }
}

final case class JawaBaseType(pkg: Option[JawaPackage], name: String, unknown: Boolean = false) {
  /**
   * This is the internal representation for type (with package name).
   * e.g. (None, int) -> int, (java.lang, Object) -> java.lang.Object
   */
  def typ: String = {
    val namePart = name + {if(unknown) "_unknown" else ""}
    if(!pkg.isDefined) namePart
    else pkg.get.toPkgString(".") + "." + namePart
  }
  def toUnknown: JawaBaseType = JawaBaseType(pkg, name, true)
  def removeUnknown: JawaBaseType = JawaBaseType(pkg, name, false)
  override def toString: String = typ
}

/**
 * @author <a href="mailto:fgwei@k-state.edu">Fengguo Wei</a>
 * @author <a href="mailto:sroy@k-state.edu">Sankardas Roy</a>
 */ 
case class JawaType(baseType: JawaBaseType, dimensions: Int) extends JavaKnowledge with PropertyProvider {
  def this(baseType: JawaBaseType) = this(baseType, 0)
  def this(pkg: Option[JawaPackage], typ: String) = this(JawaBaseType(pkg, typ), 0)
  def this(pkgAndTyp: String, dimensions: Int) = this(JavaKnowledge.separatePkgAndTyp(pkgAndTyp), dimensions)
  def this(pkgAndTyp: String) = this(pkgAndTyp, 0)
  
  val propertyMap = mlinkedMapEmpty[Property.Key, Any]
  /**
   * Package will be None if it's array, primitive, no package class. e.g. int -> None, java.lang.Object -> Some("java.lang")
   * java.lang.Object[] -> None
   */
  def getPackage: Option[JawaPackage] = if(isArray) None else baseType.pkg
  /**
   * Type is the name of the primitive or class or arrays base class, such as: int -> int, java.lang.Object -> Object,
   * int[] -> int
   */
  def getType = baseType.typ
  def isArray = dimensions > 0
  def isPrimitive = baseType.pkg == None && isJavaPrimitive(baseType.typ) && dimensions == 0
  def isObject = !isPrimitive
  def toUnknown: JawaType = JawaType(baseType.toUnknown, dimensions)
  def removeUnknown: JawaType = JawaType(baseType.removeUnknown, dimensions)
  /**
   * This is the internal representation for type or array base type (with package name).
   * e.g. int -> int, java.lang.Object -> java.lang.Object, java.lang.Object[] -> java.lang.Object
   * It's very tricky, use it carefully.
   */
  def baseTyp: String = {
    baseType.typ
  }
  def name: String = formatTypeToName(this)
  def simpleName: String = {
    canonicalName.substring(canonicalName.lastIndexOf(".") + 1)
  }
  def jawaName: String = {
    val base = baseTyp
    assign(base, dimensions, "[]", false)
  }
  def canonicalName: String = {
    val base = baseTyp.replaceAll("\\$", ".")
    assign(base, dimensions, "[]", false)
  }

  /**
   * The result looks like:
   * input: java.lang.wfg.W$F$G$H
   * output: List(java.lang.wfg.W$F$G, java.lang.wfg.W$F, java.lang.wfg.W)
   */
  def getEnclosingTypes: List[JawaType] = {
    val result: MList[JawaType] = mlistEmpty
    if(isPrimitive || isArray) return result.toList
    else if(baseTyp.contains("$")){
      var outer = baseTyp.substring(0, baseTyp.lastIndexOf("$"))
      while(outer.contains("$")) {
        result += new JawaType(outer)
        outer = outer.substring(0, outer.lastIndexOf("$"))
      } 
      result += new JawaType(outer, 0)
    }
    result.toList
  }
  
  override def toString: String = {
    name
  }
}

//final case class PrimitiveType(typ: String) extends JawaType {
//  require(isJavaPrimitive(this))
//  def name: String = typ
//  def pkg: JawaPackage = null
//  def simpleName: String = name
//  def jawaName: String = name
//  def canonicalName: String = name
//  def dimensions: Int = 0
//  def isArray: Boolean = false
//}
//
///**
// * @author <a href="mailto:fgwei@k-state.edu">Fengguo Wei</a>
// * @author <a href="mailto:sroy@k-state.edu">Sankardas Roy</a>
// */ 
//final case class ObjectType(pkgAndTyp: (JawaPackage, String), dimensions: Int) extends JawaType {
//  def this(pkg: JawaPackage, typ: String) = this((pkg, typ), 0)
//  def this(pkgAndTyp: String, dimensions: Int) = this(JavaKnowledge.separatePkgAndTyp(pkgAndTyp), dimensions)
//  def this(pkgAndTyp: String) = this(pkgAndTyp, 0)
//  def pkg = if(dimensions > 0) null else pkgAndTyp._1
//  def package
//  def typ = pkgAndTyp._2
//  require(!isJavaPrimitive(typ) || dimensions != 0)
//  def isArray = dimensions > 0
//  def name: String = formatObjectTypeToObjectName(this)
//  def simpleName: String = {
//    var res = canonicalName.substring(canonicalName.lastIndexOf(".") + 1)
//    res
//  }
//  def jawaName: String = {
//    val base = typ
//    assign(base, dimensions, "[]", false)
//  }
//  def canonicalName: String = {
//    val base = typ.replaceAll("\\$", ".")
//    assign(base, dimensions, "[]", false)
//  }
//
//  def toUnknown: ObjectType = ObjectType((pkg, typ + "*"), dimensions)
//  /**
//   * The result looks like:
//   * input: java.lang.wfg.W$F$G$H
//   * output: List(java.lang.wfg.W$F$G, java.lang.wfg.W$F, java.lang.wfg.W)
//   */
//  def getEnclosingTypes: List[ObjectType] = {
//    val result: MList[ObjectType] = mlistEmpty
//    if(isArray) return result.toList
//    else if(typ.contains("$")){
//      var outer = typ.substring(0, typ.lastIndexOf("$"))
//      while(outer.contains("$")) {
//        result += ObjectType((pkg, outer), 0)
//        outer = outer.substring(0, outer.lastIndexOf("$"))
//      } 
//      result += ObjectType((pkg, outer), 0)
//    }
//    result.toList
//  }
//  
//  override def toString: String = {
//    name
//  }
//}

case class InvalidTypeException(msg: String) extends RuntimeException(msg)