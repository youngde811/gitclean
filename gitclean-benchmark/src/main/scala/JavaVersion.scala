/*
 * Copyright (c) 2020 David Young (youngde811@pobox.com)
 *
 * This file is part of Gitclean - a tool for removing large or troublesome blobs
 * from Git repositories. It is a fork from the original BFG Repo-Cleaner by
 * Roberto Tyley.
 * 
 * Gitclean is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gitclean is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

/*
 * Copyright (c) 2012, 2013 Roberto Tyley
 *
 * This file is part of 'BFG Repo-Cleaner' - a tool for removing large
 * or troublesome blobs from Git repositories.
 *
 * BFG Repo-Cleaner is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BFG Repo-Cleaner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.sys.process.{Process, ProcessLogger}

object JavaVersion {
  val VersionRegex = """(?:java|openjdk) version "(.*?)"""".r

  def version(javaCmd: String): Future[String] = {
    val resultPromise = Promise[String]()

    Future {
      val exitCode = Process(s"$javaCmd -version")!ProcessLogger(
        s => for (v <-versionFrom(s)) resultPromise.success(v)
      )

      resultPromise.tryFailure(new IllegalArgumentException(s"$javaCmd exited with code $exitCode, no Java version found"))
    }

    resultPromise.future
  }

  def versionFrom(javaVersionLine: String): Option[String] = {
    VersionRegex.findFirstMatchIn(javaVersionLine).map(_.group(1))
  }
}