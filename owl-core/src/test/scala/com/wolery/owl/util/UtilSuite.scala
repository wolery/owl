//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Custom test suite trait for testing the 'util' package.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl
package util

//****************************************************************************

import org.scalatest.FunSuite
import org.scalatest.prop.PropertyChecks

//****************************************************************************

trait UtilSuite extends FunSuite with PropertyChecks
{
  def reject(e: Bool)           = assert(!e)
  def reject(e: Bool,m: String) = assert(!e,m)
}

//****************************************************************************
