//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Miscellaneous utility functions for working with a manifest.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery
package owl
package util

import java.util.jar.Manifest

/**
 * Miscellaneous utility functions for working with a manifest.
 *
 * @author Jonathon Bell
 */
trait manifest
{
  /**
   * Returns the main attributes of the parent executable manifest as a map of
   * string pairs.
   */
  def attributes: Map[String,String] =
  {
    val manifest = new Manifest(Thread.currentThread
                               .getContextClassLoader
                               .getResourceAsStream("META-INF/MANIFEST.MF"))
    val map      = collection.mutable.Map[String,String]()

    manifest.getMainAttributes.forEach                   // For each (key,val)
    {
      case (k,v) ⇒ map += k.toString → v.toString        // ...add string pair
    }

    map.toMap                                            // As immutable map
  }
}

/**
 * Miscellaneous utility functions for working with a manifest.
 *
 * @author Jonathon Bell
 */
object manifest extends manifest

//****************************************************************************
