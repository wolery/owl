//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose :
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl

import org.scalactic.{Prettifier,source}
import org.scalatest.{Assertion,FunSuite}
import org.scalatest.prop.PropertyChecks

/**
 * TODO
 */
trait OwlSuite extends FunSuite with PropertyChecks
{
  /**
   * TODO
   */
  def reject(expression: Boolean)(implicit α: Prettifier,β: source.Position): Assertion =
  {
    assert(!expression)(α,β)
  }

  /**
   * TODO
   */
  def reject(expression: Boolean,clue: String)(implicit α: Prettifier,β: source.Position): Assertion =
  {
    assert(!expression,clue)(α,β)
  }
}

//****************************************************************************
