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
//*
//*
//****************************************************************************

package com.wolery.owl.utils

import org.slf4j.Logger

/**
 * Initializes a dedicated logger that instances of this class can write to.
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
    org.slf4j.LoggerFactory.getLogger(logName)           // Initialize logger
  }
}

//****************************************************************************
