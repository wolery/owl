//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Miscellaneous utility functions for use within the core.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core

//****************************************************************************

//private[core]
object utilities
{
  def subscript(char: Char): Char = char match
  {
    case '-'              ⇒ '₋'
    case '+'              ⇒ '₊'
    case  c  if c.isDigit ⇒ ('₀' + c - '0').toChar
    case  c               ⇒ c
  }

  /**
   * @see  https://en.wikipedia.org/wiki/Unicode_subscripts_and_superscripts
   */
  def superscript(char : Char) : Char = char match
  {
    case '1'              ⇒ '¹'
    case '2'              ⇒ '²'
    case '3'              ⇒ '³'
    case '-'              ⇒ '⁻'
    case '+'              ⇒ '⁺'
    case  c  if c.isDigit ⇒ ('⁰' + c - '0').toChar
    case  c               ⇒ c
  }

  def subscript  (s: String): String = s.map(subscript)
  def superscript(s: String): String = s.map(superscript)

  def mod12(i: ℤ): ℤ =
  {
    Math.floorMod(i,12)
  }

  def rol12(bits: Int,by: ℤ): Int =
  {
    require((bits & ~0xFFF) == 0,"bad bits")

    val i = mod12(by)

    0xFFF & (bits << i | bits >> 12-i)
  }

  def ror12(bits: Int,by: ℤ): Int =
  {
    require((bits & ~0xFFF) == 0,"bad bits")

    val i = mod12(by)

    0xFFF & (bits >> i | bits << 12-i)
  }
}

//****************************************************************************
