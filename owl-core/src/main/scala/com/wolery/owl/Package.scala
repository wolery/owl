//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : Header:
//*
//*
//*  Purpose : TODO
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery

/**
 * TODO
 *
 * @author Jonathon Bell
 */
package object owl
{
  /**
   * Represents a natural - that is, non negative - number as a 32 bit integer.
   */
  type ℕ = Int

  /**
   * Represents an integer as a signed 32 bit integer.
   */
  type ℤ = Int

  /**
   * Represents a real number a double precision IEEE floating point number.
   */
  type ℝ = Double

  /**
   * Represents a boolean truth value as a native Scala boolean.
   */
  type Bool = Boolean

  /**
   * Represents the name of some entity as a Scala string.
   */
  type Name = String
}

//****************************************************************************
