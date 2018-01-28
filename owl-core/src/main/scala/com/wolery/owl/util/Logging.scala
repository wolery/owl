//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Initializes a dedicated logger that instances can write to.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery
package owl
package util

import org.slf4j.{Logger,LoggerFactory}

/**
 * Initializes a dedicated logger that instances of this class can write to.
 *
 * @see    [[https://www.slf4j.org SL4FJ]] for more details on using the SLF4J
 *         logging framework.
 *
 * @author Jonathon Bell
 */
trait Logging
{
  /**
   * The name of the logger that instances of this class can write to.
   */
  def logName: String =
  {
    this.getClass.getName.stripSuffix("$")               // For Scala objects
  }

  /**
   * A dedicated logger that instances of this class can write to.
   */
  lazy val log: Logger =
  {
    LoggerFactory.getLogger(logName)                     // Initialize logger
  }
}

//****************************************************************************
