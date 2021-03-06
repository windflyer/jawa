/*******************************************************************************
 * Copyright (c) 2013 - 2016 Fengguo Wei and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Detailed contributors are listed in the CONTRIBUTOR.md
 ******************************************************************************/
package org.sireum.jawa.classpath

import java.io.File
import java.net.URL
import FileUtils.AbstractFileOps
import org.sireum.jawa.io.FileZipArchive
import org.sireum.jawa.io.AbstractFile

/**
 * A trait allowing to look for classpath entries of given type in zip and jar files.
 * It provides common logic for classes handling class and source files.
 * It's aware of things like e.g. META-INF directory which is correctly skipped.
 */
trait ZipArchiveFileLookup[FileEntryType <: ClassRepClasspathEntry] extends FlatClasspath {
  val zipFile: File

  assert(zipFile != null, "Zip file in ZipArchiveFileLookup cannot be null")

  override def asURLs: Seq[URL] = Seq(zipFile.toURI.toURL)
  override def asClasspathStrings: Seq[String] = Seq(zipFile.getPath)

  private val archive = new FileZipArchive(zipFile)

  override private[jawa] def packages(inPackage: String): Seq[PackageEntry] = {
    val prefix = PackageNameUtils.packagePrefix(inPackage)
    for {
      dirEntry <- findDirEntry(inPackage).toSeq
      entry <- dirEntry.iterator if entry.isPackage
    } yield PackageEntryImpl(prefix + entry.name)
  }

  protected def files(inPackage: String): Seq[FileEntryType] =
    for {
      dirEntry <- findDirEntry(inPackage).toSeq
      entry <- dirEntry.iterator if isRequiredFileType(entry)
    } yield createFileEntry(entry)

  override private[jawa] def list(inPackage: String): FlatClasspathEntries = {
    val foundDirEntry = findDirEntry(inPackage)

    foundDirEntry map { dirEntry =>
      val pkgBuf = collection.mutable.ArrayBuffer.empty[PackageEntry]
      val fileBuf = collection.mutable.ArrayBuffer.empty[FileEntryType]
      val prefix = PackageNameUtils.packagePrefix(inPackage)

      for (entry <- dirEntry.iterator) {
        if (entry.isPackage)
          pkgBuf += PackageEntryImpl(prefix + entry.name)
        else if (isRequiredFileType(entry))
          fileBuf += createFileEntry(entry)
      }
      FlatClasspathEntries(pkgBuf, fileBuf)
    } getOrElse FlatClasspathEntries(Seq.empty, Seq.empty)
  }

  private def findDirEntry(pkg: String) = {
    val dirName = s"${FileUtils.dirPath(pkg)}/"
    archive.allDirs.get(dirName)
  }

  protected def createFileEntry(file: FileZipArchive#Entry): FileEntryType
  protected def isRequiredFileType(file: AbstractFile): Boolean
}
