//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : A common trait for implementing unit tests.
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
 * A common trait for implementing unit tests.
 *
 * @author Jonathon Bell
 */
trait OwlSuite extends FunSuite with PropertyChecks
{
  /**
   * Asserts the given boolean is false, and throws an exception if not.
   *
   * @param bool  A boolean value to test.
   * @param clue  A value whose toString() method returns a message to include
   *              in the failure report.
   *
   * @throws TestFailedException   if ''bool'' is `false`.
   * @throws NullArgumentException if ''clue'' is `null`.
   */
  def reject(bool: Boolean)(implicit α: Prettifier,β: source.Position): Assertion =
  {
    assert(!bool)(α,β)
  }

  /**
   * Asserts the given boolean is false, and throws an exception if not.
   *
   * This variant includes the given clue in the generated failure report.
   *
   * @param bool  A boolean value to test.
   * @param clue  A value whose toString() method returns a message to include
   *              in the failure report.
   *
   * @throws TestFailedException   if ''bool'' is `false`.
   * @throws NullArgumentException if ''clue'' is `null`.
   */
  def reject(bool: Boolean,clue: Any)(implicit α: Prettifier,β: source.Position): Assertion =
  {
    assert(!bool,clue)(α,β)
  }
}

//****************************************************************************
