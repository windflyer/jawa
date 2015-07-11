package org.sireum.jawa

import org.sireum.jawa.classpath.ClassFileLookup
import org.sireum.jawa.classpath.FlatClasspath
import org.sireum.jawa.classpath.Classpath
import org.sireum.jawa.backend.JavaPlatform

object ClasspathRepresentationType extends Enumeration {
  val Flat, Recursive = Value
}

/**
 * @author fgwei
 */
class Global(val projectName: String, val reporter: Reporter) extends {
  /* Is the compiler initializing? Early def, so that the field is true during the
   *  execution of the super constructor.
   */
  protected var initializing = true
} with JawaClassLoadManager
  with JawaClasspathManager {
  
  /**
   * reset the current Global
   */
  def reset = {
    this.classes.clear()
    this.applicationClasses.clear()
    this.systemLibraryClasses.clear()
    this.userLibraryClasses.clear()
    this.entryPoints.clear()
    this.hierarchy.reset
    this.applicationClassCodes.clear()
    this.userLibraryClassCodes.clear()
    this.cachedClassRepresentation.clear()
  }
  
}